# GDAL Converter

A Java-based utility built using the GDAL/OGR Java bindings to convert and process geospatial data.

## Features

The project currently supports the following operations:

- Convert JPEG images to GeoTIFF
- Convert DXF files to WKT
- Convert DXF files to GeoPackage (GPKG)
- Convert DXF files to ESRI Shapefile (SHP)
- Extract DXF geometry and styling information into JSON format

---

## Current Project Structure

```
src/
 ├── GeotiffConvertor.java   # Main application and conversion logic
 ├── PojoEntity.java         # Represents extracted geometry object
 └── Styles.java             # Stores parsed style information
```

---

# Current Functionality

## 1. JPEG → GeoTIFF

Converts a normal JPEG image into a GeoTIFF.

Current implementation:

- Opens JPEG using GDAL
- Assigns CRS (EPSG:4326)
- Sets image extent
- Adds Ground Control Point (GCP)
- Stores rotation and scale as metadata
- Generates compressed GeoTIFF

Output

```
input.jpg
      ↓
output.tif
```

---

## 2. DXF → WKT

Reads every feature from the DXF file.

For each feature:

- Extracts geometry
- Converts geometry into WKT
- Reads DXF text values
- Reads DXF style string
- Writes WKT into

```
output.wkt
```

Example

```
POINT(...)
LINESTRING(...)
POLYGON(...)
```

---

## 3. DXF Style Extraction

The converter parses DXF StyleString and extracts styling properties.

Currently supported styles:

### PEN

For lines and polylines

Extracts:

- Color
- Width
- Line Dash Pattern

Example

```
PEN(c:#FF0000,w:2px,p:5px)
```

---

### BRUSH

For polygons

Extracts:

- Fill Color

Example

```
BRUSH(fc:#00FF00)
```

---

### SYMBOL

For point features

Extracts:

- Symbol Color

---

### LABEL

For text entities

Extracts:

- Font
- Font Size
- Text
- Color
- Offset X
- Offset Y

Example

```
LABEL(
    t:"Road",
    f:"Arial",
    s:12,
    c:#000000
)
```

---

## 4. JSON Generation

Every processed feature is converted into a Java object.

```
PojoEntity
    |
    |-- WKT
    |-- Text
    |-- StyleString
    |-- Styles
```

Finally all objects are serialized into

```
wktData.json
```

Example output

```json
{
  "text": "Building",
  "wkt": "POLYGON((...))",
  "styleString": "BRUSH(fc:#00FF00)",
  "styles": {
    "geometry": "POLYGON",
    "fill": "#00FF00"
  }
}
```

---

## 5. DXF → GeoPackage

Copies every layer from the DXF file into a GeoPackage.

Current implementation:

- Creates GPKG file
- Creates corresponding layers
- Copies all fields
- Copies all geometries

Output

```
output.gpkg
```

---

## 6. DXF → Shapefile

Two implementations currently exist:

### convertDxfToShp()

Creates a GeometryCollection shapefile.

### convertDXFtoSHP()

Copies layers and features directly into SHP format.

Output

```
output.shp
```

---

# Data Model

## PojoEntity

Represents one extracted DXF feature.

Fields

| Field | Description |
|--------|-------------|
| text | DXF text entity |
| wkt | Geometry in WKT format |
| styleString | Original DXF Style String |
| styles | Parsed style object |

---

## Styles

Contains parsed style properties.

| Property | Description |
|----------|-------------|
| geometry | Geometry Type |
| color | Stroke/Text color |
| width | Line width |
| fill | Polygon fill |
| lineDash | Dash pattern |
| font | Label font |
| text | Label text |
| fontSize | Font size |
| offsetX | Label X Offset |
| offsetY | Label Y Offset |

---

# Technologies

- Java
- GDAL Java Bindings
- OGR
- Jackson
- GeoTIFF
- DXF
- WKT
- GeoPackage
- ESRI Shapefile

---

# Current Workflow

```
DXF
 │
 │
 ▼
Read Layers
 │
 ▼
Read Features
 │
 ▼
Extract Geometry
 │
 ▼
Export WKT
 │
 ▼
Parse Style String
 │
 ▼
Create Styles Object
 │
 ▼
Create PojoEntity
 │
 ▼
Serialize JSON
 │
 ├────────────► output.wkt
 │
 └────────────► wktData.json
```

---

# Future Improvements

- Support coordinate system transformation
- Export GeoJSON
- Export KML
- Support DXF blocks
- Better style parsing
- Handle hatches and complex entities
- CLI support for input/output paths
- Maven executable JAR packaging
