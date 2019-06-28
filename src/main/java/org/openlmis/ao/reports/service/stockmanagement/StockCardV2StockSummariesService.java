package org.openlmis.ao.reports.service.stockmanagement;

import org.openlmis.ao.reports.dto.external.WrappedStockCardV2Dto;
import org.openlmis.ao.reports.service.referencedata.BaseReferenceDataService;
import org.springframework.stereotype.Service;

@Service
public class StockCardV2StockSummariesService
    extends BaseReferenceDataService<WrappedStockCardV2Dto> {

  @Override
  protected String getUrl() {
    return "/api/v2/stockCardSummaries/";
  }

  @Override
  protected Class<WrappedStockCardV2Dto> getResultClass() {
    return WrappedStockCardV2Dto.class;
  }

  @Override
  protected Class<WrappedStockCardV2Dto[]> getArrayResultClass() {
    return WrappedStockCardV2Dto[].class;
  }
}
