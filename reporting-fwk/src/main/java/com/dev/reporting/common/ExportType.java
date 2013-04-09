package com.dev.reporting.common;

/**
 * The Enum ExportType.
 */
public enum ExportType {

	/** The pdf. */
	PDF("PDF", "pdf"),
	/** The html. */
	HTML("HTML", "html"),
	/** The xhtml. */
	XHTML("XHTML", "xhtml"),
	/** The doc. */
	DOC("DOC", "doc"),
	/** The docx. */
	DOCX("DOCX", "docx"),
	/** The pptx. */
	PPTX("PPTX", "pptx"),
	/** The xsl. */
	XLS("XLS", "xls"),
	/** The xslx. */
	XLSX("XLSX", "xlsx"),
	/** The image jpeg. */
	IMAGE_JPEG("IMAGE_JPEG", "jpeg"),
	/** The image bmp. */
	IMAGE_BMP("IMAGE_BMP", "bmp"),
	/** The image png. */
	IMAGE_PNG("IMAGE_PNG", "png"),
	/** The txt. */
	TXT("TXT", "txt"),
	/** The rtf. */
	RTF("RTF", "rtf");

	/** The type. */
	private String type;

	/** The extension. */
	private String extension;

	/**
	 * Instantiates a new export type.
	 * 
	 * @param type
	 *            the type
	 * @param extension
	 *            the extension
	 */
	private ExportType(String type, String extension) {
		this.type = type;
		this.extension = extension;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the extension.
	 * 
	 * @return the extension
	 */
	public String getExtension() {
		return "." + extension;
	}

	/**
	 * Sets the extension.
	 * 
	 * @param extension
	 *            the new extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

}
