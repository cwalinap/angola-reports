package org.openlmis.ao.reports.service.referencedata;

import java.util.List;
import org.openlmis.ao.reports.dto.external.OrderableDto;
import org.openlmis.ao.utils.RequestParameters;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class OrderableReferenceDataService extends BaseReferenceDataService<OrderableDto> {

  @Override
  protected String getUrl() {
    return "/api/orderables/";
  }

  @Override
  protected Class<OrderableDto> getResultClass() {
    return OrderableDto.class;
  }

  @Override
  protected Class<OrderableDto[]> getArrayResultClass() {
    return OrderableDto[].class;
  }

  /**
   * Retrieves all orderables.
   *
   * @return A list of orderables
   */
  public List<OrderableDto> findAll() {
    RequestParameters requestParameters = RequestParameters
            .init()
            .setPage(new PageRequest(0, Integer.MAX_VALUE, Direction.ASC, "fullProductName"));

    return getPage("", requestParameters).getContent();
  }

  public List<OrderableDto> findByProgramCode(String programCode) {
    return getPage("", RequestParameters.init().set("program", programCode)).getContent();
  }

}
