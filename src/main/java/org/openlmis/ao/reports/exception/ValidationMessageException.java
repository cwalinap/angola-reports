package org.openlmis.ao.reports.exception;

import org.openlmis.ao.utils.Message;

public class ValidationMessageException extends BaseMessageException {

  public ValidationMessageException(Message message) {
    super(message);
  }

  public ValidationMessageException(Message message, Throwable cause) {
    super(message, cause);
  }

  public ValidationMessageException(String messageKey) {
    super(messageKey);
  }
}
