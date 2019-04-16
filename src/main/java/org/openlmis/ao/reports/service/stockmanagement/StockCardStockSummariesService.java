package org.openlmis.ao.reports.service.stockmanagement;

import org.openlmis.ao.reports.dto.external.WrappedStockCardDto;
import org.openlmis.ao.reports.service.referencedata.BaseReferenceDataService;
import org.springframework.stereotype.Service;

@Service
public class StockCardStockSummariesService
    extends BaseReferenceDataService<WrappedStockCardDto> {

  @Override
  protected String getUrl() {
    return "/api/stockCardSummaries/";
  }

  @Override
  protected Class<WrappedStockCardDto> getResultClass() {
    return WrappedStockCardDto.class;
  }

  @Override
  protected Class<WrappedStockCardDto[]> getArrayResultClass() {
    return WrappedStockCardDto[].class;
  }
}
