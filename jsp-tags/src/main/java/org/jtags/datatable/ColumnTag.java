package org.jtags.datatable;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

// TODO: Auto-generated Javadoc
/**
 * The Class ColumnTag.
 */
public class ColumnTag extends SimpleTagSupport implements DynamicAttributes {

	/** The id. */
	private String name;

	/** The title. */
	private String title;

	/** The property. */
	private String property;

	/** The css class. */
	private String cssClass;

	/** The style. */
	private String style;

	/** The width. */
	private String width;

	/** The sortable. */
	private boolean sortable;

	/** The body content. */
	private String bodyContent;

	/** The tag attributes. */
	private Map<String, Object> tagAttributes = new HashMap<String, Object>(1);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#getId()
	 */
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#setId(java.lang.String)
	 */
	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the property.
	 * 
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Sets the property.
	 * 
	 * @param property
	 *            the new property
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Gets the css class.
	 * 
	 * @return the css class
	 */
	public String getCssClass() {
		return cssClass;
	}

	/**
	 * Sets the css class.
	 * 
	 * @param cssClass
	 *            the new css class
	 */
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	/**
	 * Gets the style.
	 * 
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets the style.
	 * 
	 * @param style
	 *            the new style
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width
	 *            the new width
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * Checks if is sortable.
	 * 
	 * @return true, if is sortable
	 */
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * Sets the sortable.
	 * 
	 * @param sortable
	 *            the new sortable
	 */
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	/**
	 * Gets the body content.
	 * 
	 * @return the body content
	 */
	public String getBodyContent() {
		return bodyContent;
	}

	/**
	 * Sets the body content.
	 * 
	 * @param bodyContent
	 *            the new body content
	 */
	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}

	/**
	 * Do tag.
	 * 
	 * @throws JspException
	 *             the jsp exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		DataTableTag dataTableTag = (DataTableTag) getParent();
		JspFragment jspFragment = getJspBody();

		if (jspFragment != null) {
			final StringWriter stringWriter = new StringWriter();
			try {
				jspFragment.invoke(stringWriter);
				setBodyContent(stringWriter.getBuffer().toString());
			} finally {
				stringWriter.close();
			}// try-finally
		}// if

		dataTableTag.getColumns().add(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang
	 * .String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		tagAttributes.put(name, value);
	}

}
