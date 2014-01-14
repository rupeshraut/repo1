package org.jtags.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jtags.datatable.DataPaginator;
import org.jtags.datatable.DataPaginatorException;

/**
 * Servlet implementation class ListServlet.
 */
public class ListServlet extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new list servlet.
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public ListServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Do get.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(request.getQueryString());
		doPost(request, response);
	}

	/**
	 * Do post.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			final HttpSession session = request.getSession(true);
			@SuppressWarnings("unchecked")
			DataPaginator<Items> dataPaginator = (DataPaginator<Items>) session.getAttribute("SESSION_ITEMS");
			final String page = (String) request.getParameter("page");
			
			if (StringUtils.isEmpty(page)) {
				dataPaginator = null;
				session.removeAttribute("SESSION_ITEMS");
			}
			
			if (dataPaginator != null) {
				if (StringUtils.isNotEmpty(page)) {
					if (StringUtils.equalsIgnoreCase(page, "first")) {
						final List list = dataPaginator.first();
						request.setAttribute("items", list);
						request.setAttribute("from", 1);
						request.setAttribute("to",  (list.size() % dataPaginator.getRowsPerPage()) == 0 ? dataPaginator.getCurrentPageIndex() * dataPaginator.getRowsPerPage() : (dataPaginator.getSize() % dataPaginator.getRowsPerPage()));
						request.setAttribute("currentPage", dataPaginator.getCurrentPageIndex());
					} else if (StringUtils.equalsIgnoreCase(page, "next")) {
						final List list = dataPaginator.next();
						request.setAttribute("items", list);
						request.setAttribute("currentPage", dataPaginator.getCurrentPageIndex());
						request.setAttribute("from", ( dataPaginator.getCurrentPageIndex() * dataPaginator.getRowsPerPage()) - dataPaginator.getRowsPerPage()+1);
						request.setAttribute("to",  (list.size() % dataPaginator.getRowsPerPage()) == 0 ? dataPaginator.getCurrentPageIndex() * dataPaginator.getRowsPerPage() : dataPaginator.getSize());						
					} else if (StringUtils.equalsIgnoreCase(page, "previous")) {
						final List list = dataPaginator.previous();
						request.setAttribute("items", list);
						request.setAttribute("currentPage", dataPaginator.getCurrentPageIndex());
						request.setAttribute("from", ( dataPaginator.getCurrentPageIndex() * dataPaginator.getRowsPerPage()) - dataPaginator.getRowsPerPage() + 1);
						request.setAttribute("to",  (list.size() % dataPaginator.getRowsPerPage()) == 0 ? dataPaginator.getCurrentPageIndex() * dataPaginator.getRowsPerPage() : (dataPaginator.getSize() % dataPaginator.getRowsPerPage()));
					} else if (StringUtils.equalsIgnoreCase(page, "last")) {
						final List list = dataPaginator.last();
						request.setAttribute("items", dataPaginator.last());
						request.setAttribute("currentPage", dataPaginator.getCurrentPageIndex());
						request.setAttribute("from", ( dataPaginator.getCurrentPageIndex() * dataPaginator.getRowsPerPage()) - dataPaginator.getRowsPerPage()+1);
						request.setAttribute("to",  (list.size() % dataPaginator.getRowsPerPage()) == 0 ? dataPaginator.getCurrentPageIndex() * dataPaginator.getRowsPerPage() : dataPaginator.getSize());
					}// if-else-if
				}// if

				request.setAttribute("totalItems", dataPaginator.getSize());
				request.setAttribute("totalPages", dataPaginator.getNumberOfPages());
				
			} else {
				List<Items> items = new ArrayList<>();
				Random random = new Random();
				for (int i = 1; i <= 20000; i++) {
					items.add(new Items("item#" + i, random.nextDouble()));
				}// for
				dataPaginator = new DataPaginator<>(items);
				dataPaginator.setRowsPerPage(20);
				session.setAttribute("SESSION_ITEMS", dataPaginator);
				request.setAttribute("items", dataPaginator.first());
				request.setAttribute("currentPage", dataPaginator.getCurrentPageIndex());
				request.setAttribute("totalItems", dataPaginator.getSize());
				request.setAttribute("totalPages", dataPaginator.getNumberOfPages());
				request.setAttribute("from", 1);
				request.setAttribute("to",  (dataPaginator.getSize() % dataPaginator.getRowsPerPage()) == 0 ? dataPaginator.getCurrentPageIndex() * dataPaginator.getRowsPerPage() : (dataPaginator.getSize() % dataPaginator.getRowsPerPage()));
			}// if-else

			request.getRequestDispatcher("/WEB-INF/views/datatable.jsp").forward(request, response);

		} catch (DataPaginatorException e) {
			throw new ServletException(e.getMessage(), e);
		}
	}

}
