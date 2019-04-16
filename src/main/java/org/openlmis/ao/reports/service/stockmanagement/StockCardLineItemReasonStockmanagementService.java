package org.openlmis.ao.reports.service.stockmanagement;

import org.openlmis.ao.reports.dto.external.StockCardLineItemReasonDto;
import org.openlmis.ao.reports.service.referencedata.BaseReferenceDataService;
import org.springframework.stereotype.Service;

@Service
public class StockCardLineItemReasonStockmanagementService
    extends BaseReferenceDataService<StockCardLineItemReasonDto> {

  @Override
  protected String getUrl() {
    return "/api/stockCardLineItemReasons";
  }

  @Override
  protected Class<StockCardLineItemReasonDto> getResultClass() {
    return StockCardLineItemReasonDto.class;
  }

  @Override
  protected Class<StockCardLineItemReasonDto[]> getArrayResultClass() {
    return StockCardLineItemReasonDto[].class;
  }
}
