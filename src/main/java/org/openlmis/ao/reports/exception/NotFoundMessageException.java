package org.openlmis.ao.reports.exception;

import org.openlmis.ao.utils.Message;

/**
 * Exception thrown when resource was not found.
 */
public class NotFoundMessageException extends BaseMessageException {
  public NotFoundMessageException(Message message) {
    super(message);
  }
}
