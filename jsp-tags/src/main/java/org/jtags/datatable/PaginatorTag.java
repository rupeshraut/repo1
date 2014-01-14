package org.jtags.datatable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

// TODO: Auto-generated Javadoc
/**
 * The Class PaginatorTag.
 */
public class PaginatorTag extends SimpleTagSupport implements DynamicAttributes {

	/** The tag attributes. */
	final private Map<String, Object> tagAttributes = new HashMap<String, Object>(1);

	/** The rows per page. */
	private int rowsPerPage;

	/** The location. */
	private String location = "header";

	/**
	 * Gets the rows per page.
	 * 
	 * @return the rows per page
	 */
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	/**
	 * Sets the rows per page.
	 * 
	 * @param rowsPerPage
	 *            the new rows per page
	 */
	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * Sets the location.
	 * 
	 * @param location
	 *            the new location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Gets the tag attributes.
	 * 
	 * @return the tag attributes
	 */
	public Map<String, Object> getTagAttributes() {
		return tagAttributes;
	}

	/**
	 * Sets the dynamic attribute.
	 * 
	 * @param uri
	 *            the uri
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @throws JspException
	 *             the jsp exception
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang .String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		tagAttributes.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		DataTableTag dataTableTag = (DataTableTag) getParent();
		dataTableTag.setPaginator(this);
	}

}
