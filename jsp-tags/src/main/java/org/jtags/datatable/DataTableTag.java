package org.jtags.datatable;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.el.PropertyNotFoundException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class DataTable.
 */
public class DataTableTag extends SimpleTagSupport implements DynamicAttributes {

	/** The collection. */
	private String name;

	/** The items. */
	private String items;

	/** The row colors. */
	private String rowColors;

	/** The columns. */
	private Collection<ColumnTag> columns;

	/** The paginator. */
	private PaginatorTag paginator;

	/** The tag attributes. */
	private Map<String, Object> tagAttributes = new HashMap<String, Object>(1);

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

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
	 * Gets the items.
	 * 
	 * @return the items
	 */
	public String getItems() {
		return items;
	}

	/**
	 * Sets the items.
	 * 
	 * @param items
	 *            the new items
	 */
	public void setItems(String items) {
		this.items = items;
	}

	/**
	 * Gets the row colors.
	 * 
	 * @return the row colors
	 */
	public String getRowColors() {
		return rowColors;
	}

	/**
	 * Sets the row colors.
	 * 
	 * @param rowColors
	 *            the new row colors
	 */
	public void setRowColors(String rowColors) {
		this.rowColors = rowColors;
	}

	/**
	 * Gets the columns.
	 * 
	 * @return the columns
	 */
	public Collection<ColumnTag> getColumns() {
		return columns;
	}

	/**
	 * Sets the columns.
	 * 
	 * @param columns
	 *            the new columns
	 */
	public void setColumns(Collection<ColumnTag> columns) {
		this.columns = columns;
	}

	/**
	 * Gets the paginator.
	 * 
	 * @return the paginator
	 */
	public PaginatorTag getPaginator() {
		return paginator;
	}

	/**
	 * Sets the paginator.
	 * 
	 * @param paginator
	 *            the new paginator
	 */
	public void setPaginator(PaginatorTag paginator) {
		this.paginator = paginator;
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
		columns = new ArrayList<>();
		getJspBody().invoke(null);
		try {
			render(super.getJspContext());
		} catch (Exception e) {
			throw new JspException(e.getMessage(), e);
		}// try-catch
	}// doTag()

	/**
	 * Render.
	 * 
	 * @param jspContext
	 *            the jsp context
	 * @throws Exception
	 *             the exception
	 */
	private void render(final JspContext jspContext) throws Exception {
		final boolean isPaginator = (getPaginator() != null);
		final List<String> paginatorLocations = new CopyOnWriteArrayList<>();
		if (isPaginator && StringUtils.isNotEmpty(paginator.getLocation())) {
			paginatorLocations.addAll(Arrays.asList(StringUtils.split(paginator.getLocation(), ",")));

			for (int i = 0; i < paginatorLocations.size(); i++) {
				paginatorLocations.set(i, StringUtils.upperCase(paginatorLocations.get(i)));
			}// for

		}// if

		jspContext.getOut().write("<DIV >");
		if (isPaginator && paginatorLocations.contains("HEADER")) {
			renderPaginator(jspContext);
		}// if

		renderTable(jspContext);

		if (isPaginator && paginatorLocations.contains("FOOTER")) {
			renderPaginator(jspContext);
		}// if

		jspContext.getOut().write("</DIV >");
	}

	/**
	 * Render.
	 * 
	 * @param jspContext
	 *            the jsp context
	 * @throws Exception
	 *             the exception
	 */
	private void renderTable(final JspContext jspContext) throws Exception {
		final StringBuffer output = new StringBuffer();
		output.append(MessageFormat.format("<DIV ID=\"{0}Div\">", getName()));

		final boolean isPaginator = (getPaginator() != null);

		output.append(MessageFormat.format("<TABLE ID=\"{0}\"  ", StringUtils.defaultString(getName()), "1", ""));

		for (Map.Entry<String, Object> entry : this.tagAttributes.entrySet()) {
			output.append(MessageFormat.format(" {0}=\"{1}\" ", entry.getKey(), entry.getValue()));
		}

		output.append(">");

		output.append("<TR> ");
		for (ColumnTag column : getColumns()) {
			
			if (column.isSortable()) {
				output.append(MessageFormat.format("<TH width=\"{0}\" style=\"{1}\" ><a href=\"javascript:void(0)\">{2}</a></TH>", StringUtils.defaultString(column.getWidth()),
						StringUtils.defaultString(column.getStyle()), StringUtils.defaultString(column.getTitle())));				
			} else {
				output.append(MessageFormat.format("<TH width=\"{0}\" style=\"{1}\" >{2}</TH>", StringUtils.defaultString(column.getWidth()),
						StringUtils.defaultString(column.getStyle()), StringUtils.defaultString(column.getTitle())));				
			}
			

		}// for
		output.append("</TR> ");

		final Collection<?> collection = (Collection<?>) jspContext.findAttribute(items);

		int rowCtr = 1;
		String oddRowColor = "#EEEEEE";
		String evenRowColor = "#ffffff";

		if (StringUtils.isNotEmpty(rowColors)) {
			String[] colors = StringUtils.split(rowColors, ",");
			if (colors != null && colors.length == 2) {
				oddRowColor = colors[0];
				evenRowColor = colors[1];
			}// if
		}// if

		for (Object object : collection) {
			output.append(MessageFormat.format("<TR bgcolor=\"{0}\"> ", ((rowCtr % 2 == 0) ? evenRowColor : oddRowColor)));
			for (ColumnTag column : getColumns()) {
				if (StringUtils.isNotEmpty(column.getBodyContent())) {
					output.append(String.format("<TD>%s</TD>", column.getBodyContent()));
					continue;
				}// if

				final Method method = MethodUtils.getMatchingAccessibleMethod(object.getClass(), "get" + StringUtils.capitalize(column.getProperty()), new Class[]{});
				if (method == null) {
					throw new PropertyNotFoundException("No property found with name " + column.getProperty() + " in " + object.getClass());
				}
				Object value = method.invoke(object, new Object[] {});
				output.append(String.format("<TD>%s</TD>", value));
			}// for
			output.append("</TR> ");
			rowCtr++;

			if (isPaginator && rowCtr > paginator.getRowsPerPage()) {
				break;
			}// if
		}// for

		output.append("</TABLE> ");
		output.append("</DIV> ");

		jspContext.getOut().write(output.toString());
	}

	/**
	 * Render paginator.
	 * 
	 * @param jspContext
	 *            the jsp context
	 * @throws Exception
	 *             the exception
	 */
	private void renderPaginator(final JspContext jspContext) throws Exception {
		final StringBuffer output = new StringBuffer();
		final String currentPage = (String) jspContext.findAttribute("currentPage");
		final String totalItems = (String) jspContext.findAttribute("totalItems");
		final String totalPages = (String) jspContext.findAttribute("totalPages");

		output.append("<div ");
		for (Map.Entry<String, Object> entry : getPaginator().getTagAttributes().entrySet()) {
			output.append(MessageFormat.format(" {0}=\"{1}\" ", entry.getKey(), entry.getValue()));
		}//for
		output.append("> ");
		
		output.append("<a href=\"?page=first\">first</a>&nbsp;|&nbsp;");
		output.append(MessageFormat.format("<a href=\"?page=next&currentpage={0}\">next</a>&nbsp;|&nbsp;", currentPage));
		output.append(MessageFormat.format("<a href=\"?page=previous&currentpage={0}\">previous</a>&nbsp;|&nbsp;", currentPage));
		output.append("<a href=\"?page=last\">last</a>");
		output.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		output.append(MessageFormat.format("page {0} of {1}", currentPage, totalPages));
		output.append("</div>");
		jspContext.getOut().write(output.toString());
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
	 */
	@Override
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		tagAttributes.put(name, value);
	}
}
