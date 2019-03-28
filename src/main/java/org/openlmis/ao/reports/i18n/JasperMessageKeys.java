package org.openlmis.ao.reports.i18n;

public class JasperMessageKeys extends ReportingMessageKeys {
  private static final String ERROR = join(SERVICE_ERROR, "jasper");

  public static final String ERROR_JASPER_FILE_CREATION = join(ERROR, "file", "creation");
  public static final String ERROR_JASPER_TEMPLATE_NOT_FOUND = join(ERROR, "template", NOT_FOUND);
}
