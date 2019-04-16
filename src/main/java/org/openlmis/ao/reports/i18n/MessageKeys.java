package org.openlmis.ao.reports.i18n;

import java.util.Arrays;

public class MessageKeys {
  private static final String DELIMITER = ".";

  // General
  protected static final String SERVICE = "angola.reports";
  protected static final String SERVICE_ERROR = join(SERVICE, "error");
  protected static final String REQUISITION_ERROR = "requisition.error";

  public static final String ERROR_IO = REQUISITION_ERROR + ".io";
  public static final String ERROR_JASPER_FILE_FORMAT = REQUISITION_ERROR + ".jasper.file.format";
  public static final String STATUS_CHANGE_USER_SYSTEM =
          REQUISITION_ERROR + ".statusChange.user.system";
  public static final String ERROR_REQUISITION_NOT_FOUND = REQUISITION_ERROR
          + ".requisitionNotFound";
  public static final String ERROR_GENERATE_REPORT_FAILED =
          SERVICE_ERROR + ".generateReport.failed";

  protected static final String NOT_FOUND = "notFound";

  protected static String join(String... params) {
    return String.join(DELIMITER, Arrays.asList(params));
  }

  protected MessageKeys() {
    throw new UnsupportedOperationException();
  }
}
