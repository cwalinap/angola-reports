package org.openlmis.ao.reports.i18n;

public class AuthorizationMessageKeys extends MessageKeys {
  public static String ERROR = join(SERVICE_ERROR, "authorization");

  public static String ERROR_USER_NOT_FOUND = join(ERROR, "user", "notFound");
  public static String ERROR_RIGHT_NOT_FOUND = join(ERROR, "right", "notFound");
}
