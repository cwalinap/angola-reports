package org.openlmis.ao.reports.service.referencedata;

import org.openlmis.ao.reports.dto.external.LotDto;
import org.openlmis.ao.utils.RequestParameters;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LotReferenceDataService extends BaseReferenceDataService<LotDto> {

  @Override
  protected String getUrl() {
    return "/api/lots/";
  }

  @Override
  protected Class<LotDto> getResultClass() {
    return LotDto.class;
  }

  @Override
  protected Class<LotDto[]> getArrayResultClass() {
    return LotDto[].class;
  }

  public List<LotDto> findAll() {
    return getPage("", RequestParameters.init()).getContent();
  }
}
