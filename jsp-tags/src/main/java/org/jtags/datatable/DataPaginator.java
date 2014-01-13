package org.jtags.datatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * The Class DataListHandler.
 * 
 * @param <E>
 *           the element type
 */
public class DataPaginator<E> implements Serializable{

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 1L;

	/** The list. */
	private final List<E> list;

	/** The list iterator. */
	private ListIterator<E> listIterator;

	/** The rows per page. */
	private int rowsPerPage;

	/** The current page index. */
	private int currentPageIndex;

	/** The number of pages. */
	private int numberOfPages = 1;

	/**
	 * Instantiates a new data list handler.
	 * 
	 * @param list
	 *           the list
	 */
	public DataPaginator(List<E> list) {
		super();
		this.list = list;
		this.listIterator = this.list.listIterator();
	}

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
	 *           the new rows per page
	 */
	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
		numberOfPages = (int) Math.ceil(((double) this.list.size() / getRowsPerPage()) / 1.0);
	}

	/**
	 * Gets the current page index.
	 * 
	 * @return the current page index
	 */
	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	/**
	 * Sets the current page index.
	 * 
	 * @param currentPageIndex
	 *           the new current page index
	 */
	public void setCurrentPageIndex(int currentPageIndex) {
		this.currentPageIndex = currentPageIndex;
	}

	/**
	 * Gets the number of pages.
	 * 
	 * @return the number of pages
	 */
	public int getNumberOfPages() {
		return numberOfPages;
	}

	/**
	 * Sets the number of pages.
	 * 
	 * @param numberOfPages
	 *           the new number of pages
	 */
	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return this.list.size();
	}

	/**
	 * Page.
	 * 
	 * @param pageNumber
	 *           the page number
	 * @return the list
	 * @throws DataPaginatorException
	 *            the data paginator exception
	 */
	public List<E> page(final int pageNumber) throws DataPaginatorException {

		if (pageNumber <= 0 || pageNumber > getNumberOfPages()) {
			throw new DataPaginatorException("Invalid page number " + pageNumber);
		}

		if (pageNumber == 1) {
			return first();
		} else
			if (pageNumber == getNumberOfPages()) {
				return last();
			}// if

		first();

		for (int ctr = 1; ctr < (pageNumber - 1); ctr++) {
			next();
		}
		currentPageIndex = pageNumber;
		return next();
	}

	/**
	 * First.
	 * 
	 * @return the list
	 * @throws DataPaginatorException
	 *            the data paginator exception
	 */
	public List<E> first() throws DataPaginatorException {
		if (this.listIterator == null) {
			throw new DataPaginatorException("No data found");
		}// if

		currentPageIndex = 1;

		// resetting list iterator
		this.listIterator = this.list.listIterator();

		final List<E> localList = new LinkedList<E>();
		int ctr = 0;
		while (listIterator.hasNext() && ctr < getRowsPerPage()) {
			localList.add(listIterator.next());
			ctr++;
		}// while
		return localList;
	}

	/**
	 * First.
	 * 
	 * @return the list
	 * @throws DataPaginatorException
	 *            the data paginator exception
	 */
	public List<E> last() throws DataPaginatorException {
		if (this.listIterator == null) {
			throw new DataPaginatorException("No data found");
		}// if

		// resetting list iterator

		int records = this.list.size() % getRowsPerPage();

		while (listIterator.hasNext()) {
			listIterator.next();
		}// while

		if (records == 0) {
			records = getRowsPerPage();
			records++;
		} else {
			records++;
		}

		while (listIterator.hasPrevious()) {
			if (listIterator.previousIndex() == this.list.size() - records) {
				break;
			}// if
			listIterator.previous();
		}// while

		currentPageIndex = getNumberOfPages();

		final List<E> localList = new LinkedList<E>();
		int ctr = 0;
		while (listIterator.hasNext() && ctr < getRowsPerPage()) {
			localList.add(listIterator.next());
			ctr++;
		}// while

		return localList;
	}

	/**
	 * Next.
	 * 
	 * @return the list
	 * @throws DataPaginatorException
	 *            the data paginator exception
	 */
	public List<E> next() throws DataPaginatorException {
		if (this.listIterator == null) {
			throw new DataPaginatorException("No data found");
		}// if

		final List<E> localList = new LinkedList<E>();
		int ctr = 0;
		while (listIterator.hasNext() && ctr < getRowsPerPage()) {
			localList.add(listIterator.next());
			ctr++;
		}// while

		currentPageIndex++;

		return localList;
	}// next()

	/**
	 * Previous.
	 * 
	 * @return the list
	 * @throws DataPaginatorException
	 *            the data paginator exception
	 */
	public List<E> previous() throws DataPaginatorException {
		if (this.listIterator == null) {
			throw new DataPaginatorException("No data found");
		}// if

		if (this.currentPageIndex == 1) {
			return first();
		}

		final List<E> localList = new LinkedList<E>();
		int ctr = 0;
		while (listIterator.hasPrevious() && ctr < getRowsPerPage()) {
			localList.add(listIterator.previous());
			ctr++;
		}// while

		currentPageIndex--;
		Collections.reverse(localList);
		return localList;
	}// next()

	/**
	 * Reset.
	 */
	public void reset() {
		this.listIterator = this.list.listIterator();
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *           the arguments
	 * @throws DataPaginatorException
	 */
	public static void main(String[] args) throws DataPaginatorException {

		List<String> strings = new ArrayList<String>(1);
		for (int i = 1; i <= 50; i++) {
			strings.add("" + i);
		}
		final DataPaginator<String> dataPaginator = new DataPaginator<String>(strings);
		dataPaginator.setRowsPerPage(11);
		System.out.println("# of pages " + dataPaginator.getNumberOfPages());

		List<String> list = dataPaginator.last();
		for (String string : list) {
			System.out.println(string);
		}// for

		System.out.println("---------------------");

		list = dataPaginator.first();
		for (String string : list) {
			System.out.println(string);
		}// for

		System.out.println("---------------------");

		list = dataPaginator.page(0);
		for (String string : list) {
			System.out.println(string);
		}// for

		// List<String> list = dataPaginator.page(4);
		// for (String string : list) {
		// System.out.println(string);
		// }// for

	}
}// class
