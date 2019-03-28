package org.openlmis.ao.reports.service.referencedata;

import org.openlmis.ao.reports.service.BaseCommunicationService;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseReferenceDataService<T> extends BaseCommunicationService<T> {

  @Value("${referencedata.url}")
  private String referenceDataUrl;

  @Override
  protected String getServiceUrl() {
    return referenceDataUrl;
  }
}
