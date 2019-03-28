package org.openlmis.ao.reports.service.fulfillment;

import org.openlmis.ao.reports.service.BaseCommunicationService;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseFulfillmentService<T> extends BaseCommunicationService<T> {

  @Value("${fulfillment.url}")
  private String fulfillmentUrl;

  @Override
  protected String getServiceUrl() {
    return fulfillmentUrl;
  }
}
