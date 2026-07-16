package in.suri.gdalconverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.TranslateOptions;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.StyleTable;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class GeotiffConvertor {

	public static void main(String[] args) {
		// Register GDAL
		gdal.AllRegister();

		// Input JPEG file path
		String inputJpegPath = "/home/suresh/Downloads/india.jpg";
		String fileName = inputJpegPath.substring(inputJpegPath.lastIndexOf("/") + 1, inputJpegPath.lastIndexOf("."));

		String outputGeoTiffPath = fileName.concat(".tif");
		// Input DXF file path
		String inputDxfPath = "/home/suresh/Downloads/DxfData/gargoti_new.dxf";
		String outputShpPath = "output.shp";
		String outputWktPath = "output.wkt";
		String outputGpkgPath = "output.gpkg";
		convertDxfToWkt(inputDxfPath, outputWktPath);
		convertDxfToGeoPackage(inputDxfPath, outputGpkgPath);
//		convertDXFtoSHP(inputDxfPath, outputShpPath);
	}

	public static void convertToGeoTiff(String inputPath, String outputPath) {
		// Open the JPEG dataset
		Dataset inputDataset = gdal.Open(inputPath, gdalconst.GA_ReadOnly);

		if (inputDataset == null) {
			System.err.println("Error: Could not open the input JPEG file.");
			return;
		}

		// Translate options
		String crs = "EPSG:4326";

		Vector<String> options = new Vector<>();

		// Provided data
		double[] imageCenter = { 82.35898541145323, 23.222196437186188 };
		double imageRotate = 0.0018922976621204895;
		double[] imageScale = { 0.10131876708322392, 0.09598900664850321 };
		double[] imageExtent = { 66.45242414028459, 4.816051056357431, 97.80112422654591, 40.50112762257587 };

		options.add("-of");
		options.add("GTiff");
		options.add("-co");
		options.add("COMPRESS=JPEG");
		options.add("-a_srs");
		options.add(crs); // Set CRS
//       
		options.add("-gcp");
		options.add("0"); // GCP index
		options.add("0"); // Pixel x
		options.add("0"); // Pixel y
		options.add(Double.toString(imageCenter[0])); // Easting (longitude)
		options.add(Double.toString(imageCenter[1])); // Northing (latitude)
		// Set upper left and lower right corners (extent)
		options.add("-a_ullr");
		options.add(Double.toString(imageExtent[0])); // upper-left-x
		options.add(Double.toString(imageExtent[3])); // upper-left-y
		options.add(Double.toString(imageExtent[2])); // lower-right-x
		options.add(Double.toString(imageExtent[1])); // lower-right-y
		// Set rotation and scale as metadata items
		inputDataset.SetMetadataItem("imageRotate", Double.toString(imageRotate));
		inputDataset.SetMetadataItem("imageScaleX", Double.toString(imageScale[0]));
		inputDataset.SetMetadataItem("imageScaleY", Double.toString(imageScale[1]));

		// Translate (convert) to GeoTIFF
		Dataset outputDataset = gdal.Translate(outputPath, inputDataset, new TranslateOptions(options));
		// Set metadata on the output GeoTIFF dataset
//        for (Map.Entry<String, String> entry : metadata.entrySet()) {
//        	System.out.println(entry.getKey()+" :"+entry.getValue());
//            outputDataset.SetMetadataItem(entry.getKey(), entry.getValue(), "TIFF");
//        }

		if (outputDataset == null) {
			System.err.println("Error: Could not create the output GeoTIFF file.");
		} else {
			System.out.println("Conversion successful!");
			// Close datasets
			inputDataset.delete();
			outputDataset.delete();
		}
	}

	public static void convertDxfToShp(String inputDxfPath, String outputShpPath) {
		String outputCrs = "EPSG:32643";

		// Open the DXF dataset
		DataSource inputDxfDataset = ogr.Open(inputDxfPath);
		if (inputDxfDataset == null) {
			System.err.println("Error: Could not open the input DXF file.");
			return;
		}

		// Create a new SHP dataset for output with mixed geometry collection type
		DataSource outputShpDataset = ogr.GetDriverByName("ESRI Shapefile").CreateDataSource(outputShpPath);
		if (outputShpDataset == null) {
			System.err.println("Error: Could not create the output SHP file.");
			inputDxfDataset.delete();
			return;
		}
		// Set the CRS for the output layer
		SpatialReference spatialReference = new SpatialReference();
		spatialReference.SetFromUserInput(outputCrs);

		// Create layer with mixed geometry collection type
		Layer outputLayer = outputShpDataset.CreateLayer("Layer", spatialReference, ogr.wkbGeometryCollection);

		// Loop through layers and copy all geometries to the SHP file
		int layerCount = inputDxfDataset.GetLayerCount();
		for (int i = 0; i < layerCount; i++) {
			Layer inputLayer = inputDxfDataset.GetLayerByIndex(i);

			// Loop through features and copy all geometries
			Feature inputFeature;
			while ((inputFeature = inputLayer.GetNextFeature()) != null) {
				Geometry geometry = inputFeature.GetGeometryRef();
				if (geometry != null) {
					Feature outputFeature = new Feature(outputLayer.GetLayerDefn());
					outputFeature.SetGeometry(geometry);
					outputLayer.CreateFeature(outputFeature);
				}
				inputFeature.delete();
			}
		}

		// Close datasets
		inputDxfDataset.delete();
		outputShpDataset.delete();

		System.out.println("DXF to mixed geometry SHP conversion successful!");

	}

//Method for Converting DXF into WKT and Serialize into JSON file 
	public static void convertDxfToWkt(String inputDxfPath, String outputWktPath) {
		// Open the DXF dataset
		DataSource inputDxfDataset = ogr.Open(inputDxfPath);
		List<PojoEntity> pojoList = new ArrayList<>();
		if (inputDxfDataset == null) {
			System.err.println("Error: Could not open the input DXF file.");
			return;
		}

		try {
			// Create a FileWriter for the output WKT file
			FileWriter writer = new FileWriter(outputWktPath);

			// Loop through layers and write WKT to the file
			int layerCount = inputDxfDataset.GetLayerCount();
			System.out.println(layerCount);
			for (int i = 0; i < layerCount; i++) {
				Layer inputLayer = inputDxfDataset.GetLayerByIndex(i);
				System.out.println(inputLayer.GetName() + " is layer name");
				StyleTable st = inputLayer.GetStyleTable();
				int fieldCount = inputLayer.GetLayerDefn().GetFieldCount();
				for (int j = 0; j < fieldCount; j++) {
					FieldDefn fieldDefn = inputLayer.GetLayerDefn().GetFieldDefn(j);
					String fieldName = fieldDefn.GetName();
					System.out.println(fieldName);
				}
				// Loop through features and write WKT to the file
				Feature inputFeature;

				while ((inputFeature = inputLayer.GetNextFeature()) != null) {
					System.out.println("Feature Type: " + inputFeature.GetNativeMediaType());
					System.out.println(inputFeature.GetStyleString());
					Geometry inputGeometry = inputFeature.GetGeometryRef();
					String wkt = inputGeometry.ExportToWkt();
					String value = inputFeature.GetFieldAsString("Text");
					String styleString = inputFeature.GetStyleString();
					PojoEntity entity = new PojoEntity();
					Styles styles = new Styles();
					entity.setWkt(wkt);
					if (!value.equals(""))
						entity.setText(value);
					styles.setGeometry(wkt.substring(0, wkt.indexOf(" ")));
					// Define a regular expression pattern to match key-value pairs from styleString
					Pattern pattern = Pattern.compile("(f|t|p|w|dx|dy|c|a|s|fc):([^,\\\\)]*)");
					Matcher matcher = pattern.matcher(styleString);

					// Iterate through matches and add key-value pairs
					while (matcher.find()) {

						String key = matcher.group(1);
						String style = matcher.group(2);
						// Filtering and setting style by Tool and Geometry type
						if (styleString.contains("PEN") && (wkt.contains("POINT") || wkt.contains("LINESTRING"))) {

							switch (key) {
							case "c":
								styles.setColor(style);
								break;
							case "w":
								styles.setWidth(style);
								break;
							case "p":
								styles.setLineDash(style);
								break;

							default:
								break;
							}
						}
						if (styleString.contains("BRUSH") && (wkt.contains("POLYGON") || wkt.contains("TRIANGLE"))) {
							if (key.equals("fc")) {
								styles.setFill(style);
							}
						}
						if (styleString.contains("SYMBOL") && wkt.contains("POINT")) {
							styles.setColor(style);
						}
						if (styleString.contains("LABEL") && wkt.contains("POINT")) {
							switch (key) {
							case "t":
								// removing \" from text
								styles.setText(style.replace("\"", ""));
								break;
							case "s":
								styles.setFontSize(style);
								break;
							case "c":
								styles.setColor(style);
								break;
							case "f":
								// removing \" from font
								styles.setFont(style.replace("\"", ""));
								break;
							case "dx":
								styles.setOffsetX(style);
								break;
							case "dy":
								styles.setOffsetY(style);
								break;
							default:
								break;
							}
						}
					}
					// Setting style obj into wkt pojo
					entity.setStyles(styles);
					entity.setStyleString(styleString);
					// Adding wkt pojo into pojo list
					pojoList.add(entity);
					writer.write(wkt + "\n");
					inputFeature.delete();
				}
			}
			// Converting POJO into JSON
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			File file = new File("wktData.json");
			objectMapper.writeValue(file, pojoList);

			// Close the FileWriter
			writer.close();

			System.out.println("DXF to WKT conversion successful!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Close the dataset
			inputDxfDataset.delete();
		}
	}

	public static boolean convertDXFtoSHP(String inputDXFFile, String outputSHPFile) {
		// Open DXF file
		DataSource dataSource = ogr.Open(inputDXFFile);
		if (dataSource == null) {
			System.err.println("Failed to open input DXF file.");
			return false;
		}

		// Create output SHP file
		DataSource outputDataSource = ogr.GetDriverByName("ESRI Shapefile").CreateDataSource(outputSHPFile);
		if (outputDataSource == null) {
			System.err.println("Failed to create output SHP file.");
			dataSource.delete();
			return false;
		}

		// Loop through layers in DXF file
		int layerCount = dataSource.GetLayerCount();
		for (int i = 0; i < layerCount; i++) {
			org.gdal.ogr.Layer layer = dataSource.GetLayer(i);

			// Create corresponding layer in SHP file
			org.gdal.ogr.Layer outputLayer = outputDataSource.CreateLayer(layer.GetName(), null, ogr.wkbUnknown);

			// Copy fields from DXF layer to SHP layer
			FeatureDefn featureDefn = layer.GetLayerDefn();
			for (int j = 0; j < featureDefn.GetFieldCount(); j++) {
				outputLayer.CreateField(featureDefn.GetFieldDefn(j));
			}

			// Copy features from DXF layer to SHP layer
			layer.ResetReading();
			org.gdal.ogr.Feature feature;
			while ((feature = layer.GetNextFeature()) != null) {
				outputLayer.CreateFeature(feature);
				feature.delete();
			}
		}

		// Close data sources
		dataSource.delete();
		outputDataSource.delete();

		return true;
	}

	public static void convertDxfToGeoPackage(String inputDxfPath, String outputGpkgPath) {
		// Open the DXF dataset
		DataSource inputDxfDataset = ogr.Open(inputDxfPath);

		if (inputDxfDataset == null) {
			System.err.println("Error: Could not open the input DXF file.");
			return;
		}

		// Create a GeoPackage dataset for output
		DataSource outputGpkgDataset = ogr.GetDriverByName("GPKG").CreateDataSource(outputGpkgPath);

		// Loop through layers and copy them to the GeoPackage file
		int layerCount = inputDxfDataset.GetLayerCount();
		for (int i = 0; i < layerCount; i++) {
			Layer inputLayer = inputDxfDataset.GetLayerByIndex(i);

			// Create a new layer in the GeoPackage dataset
			Layer outputLayer = outputGpkgDataset.CreateLayer(inputLayer.GetName(), null, ogr.wkbUnknown);

			// Loop through fields and create corresponding fields in the GeoPackage layer
			int fieldCount = inputLayer.GetLayerDefn().GetFieldCount();
			for (int j = 0; j < fieldCount; j++) {
				FieldDefn fieldDefn = inputLayer.GetLayerDefn().GetFieldDefn(j);
				outputLayer.CreateField(fieldDefn);
			}

			// Loop through features and copy them to the GeoPackage layer
			Feature inputFeature;
			while ((inputFeature = inputLayer.GetNextFeature()) != null) {
				// Copy the feature to the GeoPackage layer
				Feature outputFeature = new Feature(outputLayer.GetLayerDefn());
				outputFeature.SetFrom(inputFeature);

				// Create the geometry and set it in the GeoPackage feature
				Geometry inputGeometry = inputFeature.GetGeometryRef();
				Geometry outputGeometry = inputGeometry.Clone();
				outputFeature.SetGeometry(outputGeometry);

				// Create the feature in the GeoPackage layer
				outputLayer.CreateFeature(outputFeature);

				inputFeature.delete();
				outputFeature.delete();
			}
		}

		// Close datasets
		inputDxfDataset.delete();
		outputGpkgDataset.delete();

		System.out.println("DXF to GeoPackage conversion successful!");
	}

}
