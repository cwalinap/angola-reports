package org.openlmis.ao.reports.service.stockmanagement;

import org.openlmis.ao.reports.dto.external.StockCardDto;
import org.openlmis.ao.reports.service.referencedata.BaseReferenceDataService;
import org.springframework.stereotype.Service;

@Service
public class StockCardStockmanagementService
    extends BaseReferenceDataService<StockCardDto> {

  @Override
  protected String getUrl() {
    return "/api/stockCards/";
  }

  @Override
  protected Class<StockCardDto> getResultClass() {
    return StockCardDto.class;
  }

  @Override
  protected Class<StockCardDto[]> getArrayResultClass() {
    return StockCardDto[].class;
  }
}
