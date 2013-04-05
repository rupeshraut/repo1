package com.dev.reporting.template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dev.reporting.common.ExportType;
import com.dev.reporting.exception.ReportingException;

/**
 * The Class ReportTemplate.
 */
public class ReportTemplate {

	/** The Constant LOG. */
	private static final Log LOG = LogFactory.getLog(ReportTemplate.class);

	/** The file name. */
	private String fileName;

	/** The connection. */
	private Connection connection;

	/** The parameters. */
	private Map<String, Object> parameters = new HashMap<>(1);

	/** The jasper print. */
	private JasperPrint jasperPrint;

	/**
	 * Gets the file name.
	 * 
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param fileName
	 *            the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the connection.
	 * 
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Sets the connection.
	 * 
	 * @param connection
	 *            the new connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Gets the parameters.
	 * 
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 * 
	 * @param parameters
	 *            the parameters
	 */
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Gets the jasper print.
	 * 
	 * @return the jasper print
	 */
	public JasperPrint getJasperPrint() {
		return jasperPrint;
	}

	/**
	 * Sets the jasper print.
	 * 
	 * @param jasperPrint
	 *            the new jasper print
	 */
	public void setJasperPrint(JasperPrint jasperPrint) {
		this.jasperPrint = jasperPrint;
	}

	/**
	 * Load report.
	 * 
	 * @return the input stream
	 * @throws ReportingException
	 *             the reporting exception
	 */
	private InputStream loadReport() throws ReportingException {

		if (LOG.isInfoEnabled()) {
			LOG.info("loading report file " + getFileName());
		}

		if (StringUtils.isBlank(getFileName())) {
			throw new ReportingException("report design file does not exists");
		}// if

		InputStream inputStream = null;

		try {
			if (StringUtils.startsWithIgnoreCase(getFileName(), "file:")) {
				inputStream = JRLoader.getFileInputStream(StringUtils.substringAfter(this.getFileName(), "file:"));
			} else {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getFileName());
			}// if-else
		} catch (JRException e) {
			throw new ReportingException("error occurred while loading report " + getFileName(), e);
		}// try-catch

		if (inputStream == null) {
			throw new ReportingException( String.format("report design file %s not found", getFileName()));
		}// if

		return inputStream;
	}// loadReport()

	/**
	 * Compile.
	 * 
	 * @throws ReportingException
	 */
	public void compile() throws ReportingException {

		if (LOG.isInfoEnabled()) {
			LOG.info("compliling report file " + getFileName());
		}// if

		if (!StringUtils.endsWithIgnoreCase(getFileName(), ".jasper") && !StringUtils.endsWithIgnoreCase(getFileName(), ".jrxml")) {
			throw new ReportingException("Not a vaild jasper design file " + getFileName());
		}// if

		InputStream inputStream = null;

		try {

			inputStream = loadReport();

			if (StringUtils.endsWithIgnoreCase(getFileName(), ".jasper")) {
				this.jasperPrint = JasperFillManager.fillReport(inputStream, this.getParameters(), this.getConnection());
			} else if (StringUtils.endsWithIgnoreCase(getFileName(), ".jrxml")) {
				JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
				jasperPrint = JasperFillManager.fillReport(jasperReport, this.getParameters(), this.getConnection());
			}// if-else-if

			if (this.jasperPrint == null || this.jasperPrint.getPages() == null || this.jasperPrint.getPages().size() == 0) {
				throw new ReportingException("jasper print output is empty.");
			}// if

		} catch (ReportingException re) {
			throw re;
		} catch (Exception e) {
			throw new ReportingException("error occurred while compiling report", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}// try-catch-finally

	}// compile()

	/**
	 * Export.
	 * 
	 * @param exportType
	 *            the export type
	 * @param destination
	 *            the destination
	 * @throws ReportingException
	 *             the reporting exception
	 */
	public void export(final ExportType exportType, final String destination) throws ReportingException {

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("exporting report file %s as %s to %s", getFileName(), exportType.getType(), destination));
		}// if

		try {

			byte[] data = export(exportType);
			FileUtils.writeByteArrayToFile(new File(destination), data);

		} catch (ReportingException e) {
			throw e;
		} catch (Exception e) {
			throw new ReportingException("error occurred while exporting " + getFileName() + " report to " + exportType.getType(), e);
		}// try-catch

	}// export()

	/**
	 * Export.
	 * 
	 * @param exportType
	 *            the export type
	 * @return the byte[]
	 * @throws ReportingException
	 */
	public byte[] export(final ExportType exportType) throws ReportingException {

		if (LOG.isInfoEnabled()) {
			LOG.info("exporting report file " + getFileName() + " as " + exportType.getType());
		}// if

		byte[] data = null;

		try {

			switch (exportType) {
			case PDF:
				data = exportPDF();
				break;

			case HTML:
				data = exportHTML();
				break;

			default:
				throw new ReportingException("Invaild export option");
			}
		} catch (ReportingException e) {
			throw e;
		} catch (Exception e) {
			throw new ReportingException("error occurred while exporting " + getFileName() + " report to " + exportType.getType(), e);
		}// try-catch

		return data;
	}// export()

	/**
	 * Export pdf.
	 * 
	 * @return the byte[]
	 * @throws JRException
	 */
	private byte[] exportPDF() throws JRException {
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	/**
	 * Export html.
	 * 
	 * @return the byte[]
	 */
	private byte[] exportHTML() {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			JRHtmlExporter htmlExporter = new JRHtmlExporter();
			htmlExporter.setParameter(JRExporterParameter.JASPER_PRINT, getJasperPrint());
			htmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(outputStream);
		}// try-catch-finally

		return outputStream.toByteArray();
	}
}// class
