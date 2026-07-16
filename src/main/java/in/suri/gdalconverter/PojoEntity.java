package in.suri.gdalconverter;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PojoEntity {
	String text;
	String wkt;
	String styleString;
	Styles styles;

	public PojoEntity() {

	}

	public PojoEntity(String text, String wkt, String styleString, Styles styles) {
		super();
		this.text = text;
		this.wkt = wkt;
		this.styleString = styleString;
		this.styles = styles;
	}

//	public PojoEntity(String text, String wkt, Styles styles) {
//		this.text = text;
//		this.wkt = wkt;
//		this.styles = styles;
//	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getWkt() {
		return wkt;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

	public Styles getStyles() {
		return styles;
	}

	public void setStyles(Styles styles) {
		this.styles = styles;
	}

	public String getStyleString() {
		return styleString;
	}

	public void setStyleString(String styleString) {
		this.styleString = styleString;
	}

	@Override
	public String toString() {
		return "PojoEntity [text=" + text + ", wkt=" + wkt + ", styleString=" + styleString + ", styles=" + styles
				+ "]";
	}

//	@Override
//	public String toString() {
//		return "PojoEntity [text=" + text + ", wkt=" + wkt + ", styles=" + styles + "]";
//	}

}
