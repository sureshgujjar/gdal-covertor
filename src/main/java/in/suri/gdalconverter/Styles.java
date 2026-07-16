package in.suri.gdalconverter;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Styles {
	String geometry;
	String color;
	String width;
	String fill;
    String lineDash;
    String font;
    String text;
    String fontSize;
    String offsetX;
    String offsetY;
    
    
	public Styles() {
		
	}
	
	public Styles(String geometry, String color, String width, String fill, String lineDash, String font, String text,
			String fontSize, String offsetX, String offsetY) {
	
		this.geometry = geometry;
		this.color = color;
		this.width = width;
		this.fill = fill;
		this.lineDash = lineDash;
		this.font = font;
		this.text = text;
		this.fontSize = fontSize;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
    
	public String getGeometry() {
		return geometry;
	}
	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getFill() {
		return fill;
	}
	public void setFill(String fill) {
		this.fill = fill;
	}
	public String getLineDash() {
		return lineDash;
	}
	public void setLineDash(String lineDash) {
		this.lineDash = lineDash;
	}
	public String getFont() {
		return font;
	}
	public void setFont(String font) {
		this.font = font;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFontSize() {
		return fontSize;
	}
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}
	public String getOffsetX() {
		return offsetX;
	}
	public void setOffsetX(String offsetX) {
		this.offsetX = offsetX;
	}
	public String getOffsetY() {
		return offsetY;
	}
	public void setOffsetY(String offsetY) {
		this.offsetY = offsetY;
	}
	@Override
	public String toString() {
		return "Styles [geometry=" + geometry + ", color=" + color + ", width=" + width + ", fill=" + fill
				+ ", lineDash=" + lineDash + ", font=" + font + ", text=" + text + ", fontSize=" + fontSize
				+ ", offsetX=" + offsetX + ", offsetY=" + offsetY + "]";
	}
	
    
    

}
