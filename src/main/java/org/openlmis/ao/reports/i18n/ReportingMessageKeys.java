package org.openlmis.ao.reports.i18n;

public class ReportingMessageKeys extends MessageKeys {
  private static final String ERROR = join(SERVICE_ERROR, "reporting");
  private static final String INCORRECT_TYPE = "incorrectType";
  private static final String PARAMETER = "parameter";
  private static final String CREATION = "creation";
  private static final String TEMPLATE = "template";
  private static final String INVALID = "invalid";
  private static final String MISSING = "missing";
  private static final String EXISTS = "exists";
  private static final String EMPTY = "empty";
  private static final String FILE = "file";


  public static final String ERROR_REPORTING_IO = join(ERROR, "io");
  public static final String ERROR_REPORTING_CLASS_NOT_FOUND = join(ERROR, "class", "notFound");
  public static final String ERROR_REPORTING_CREATION = join(ERROR, CREATION);
  public static final String ERROR_REPORTING_FILE_EMPTY = join(ERROR, FILE, EMPTY);
  public static final String ERROR_REPORTING_TEMPLATE_PARAMETER_INVALID =
      join(ERROR, TEMPLATE, PARAMETER, INVALID);
  public static final String ERROR_REPORTING_FILE_INCORRECT_TYPE =
      join(ERROR, FILE, INCORRECT_TYPE);
  public static final String ERROR_REPORTING_FILE_INVALID = join(ERROR, FILE, INVALID);
  public static final String ERROR_REPORTING_FILE_MISSING = join(ERROR, FILE, MISSING);
  public static final String ERROR_REPORTING_PARAMETER_INCORRECT_TYPE =
      join(ERROR, PARAMETER, INCORRECT_TYPE);
  public static final String ERROR_REPORTING_PARAMETER_MISSING =
      join(ERROR, PARAMETER, MISSING);
  public static final String ERROR_REPORTING_TEMPLATE_EXIST =
      join(ERROR, TEMPLATE, EXISTS);
  public static final String ERROR_REPORTING_TEMPLATE_NOT_FOUND =
      join(ERROR, TEMPLATE, NOT_FOUND);
}
