package org.openlmis.ao.errorhandling;

import org.openlmis.util.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.openlmis.ao.reports.exception.AuthenticationMessageException;
import org.openlmis.ao.reports.exception.BaseMessageException;
import org.openlmis.ao.reports.exception.DataRetrievalException;
import org.openlmis.ao.reports.exception.NotFoundMessageException;
import org.openlmis.ao.reports.exception.PermissionMessageException;
import org.openlmis.ao.reports.exception.ValidationMessageException;
import org.openlmis.ao.reports.i18n.MessageService;
import org.openlmis.ao.utils.Message;

/**
 * Global error handling for all controllers in the service.
 * Contains common error handling mappings.
 */
@ControllerAdvice
public class BaseErrorHandling {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private MessageService messageService;
  
  @ExceptionHandler(DataRetrievalException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorResponse handleRefDataException(DataRetrievalException ex) {
    return logErrorAndRespond("Error fetching from reference data", ex);
  }

  /**
   * Handles Message exceptions and returns status 400 Bad Request.
   *
   * @param ex the ValidationMessageException to handle
   * @return the error response for the user
   */
  @ExceptionHandler(ValidationMessageException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Message.LocalizedMessage handleMessageException(ValidationMessageException ex) {
    return getLocalizedMessage(ex);
  }

  @ExceptionHandler(AuthenticationMessageException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public Message.LocalizedMessage handleAuthenticationException(AuthenticationMessageException ex) {
    return getLocalizedMessage(ex);
  }

  @ExceptionHandler(PermissionMessageException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  public Message.LocalizedMessage handlePermissionException(PermissionMessageException ex) {
    return getLocalizedMessage(ex);
  }

  @ExceptionHandler(NotFoundMessageException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public Message.LocalizedMessage handleNotFoundMessageException(NotFoundMessageException ex) {
    return getLocalizedMessage(ex);
  }

  /**
   * Logs an error message and returns an error response.
   *
   * @param message the error message
   * @param ex      the exception to log. Message from the exception is used as the error
   *                description.
   * @return the error response that should be sent to the client
   */
  protected ErrorResponse logErrorAndRespond(String message, Exception ex) {
    logger.error(message, ex);
    return new ErrorResponse(message, ex.getMessage());
  }

  /**
   * Translate the Message in a ValidationMessageException into a LocalizedMessage.
   *
   * @param exception is any ValidationMessageException containing a Message
   * @return a LocalizedMessage translated by the MessageService bean
   */
  protected final Message.LocalizedMessage getLocalizedMessage(
      BaseMessageException exception) {
    Message.LocalizedMessage message = messageService.localize(exception.asMessage());
    logger.error(message.toString());
    return message;
  }
}
