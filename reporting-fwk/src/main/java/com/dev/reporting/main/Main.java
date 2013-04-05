package com.dev.reporting.main;

import java.util.HashMap;
import java.util.Map;

import com.dev.reporting.common.ExportType;
import com.dev.reporting.exception.ReportingException;
import com.dev.reporting.template.ReportTemplate;

/**
 * The Class Main.
 */
public class Main {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws ReportingException
	 *             the reporting exception
	 */
	public static void main(String[] args) throws ReportingException {

		final ReportTemplate reportTemplate = new ReportTemplate();
		reportTemplate.setConnection(null);
		reportTemplate.setFileName("");

		final Map<String, Object> parameters = new HashMap<>(1);
		reportTemplate.setParameters(parameters);
		reportTemplate.compile();
		reportTemplate.export(ExportType.PDF);
		reportTemplate.export(ExportType.PDF, "path" + ExportType.PDF.getExtension());

	}
}
