package org.openlmis.ao.reports.exception;

public class JasperReportViewException extends BaseLocalizedException {
  public JasperReportViewException(Throwable cause, String messageKey, String... params) {
    super(cause, messageKey, params);
  }
}
