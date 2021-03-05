package org.openlmis.ao.reports.service.referencedata;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.openlmis.ao.reports.dto.external.GeographicZoneDto;
import org.openlmis.ao.utils.RequestParameters;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class GeographicZoneReferenceDataService
    extends BaseReferenceDataService<GeographicZoneDto> {

  @Override
  protected String getUrl() {
    return "/api/geographicZones/";
  }

  @Override
  protected Class<GeographicZoneDto> getResultClass() {
    return GeographicZoneDto.class;
  }

  @Override
  protected Class<GeographicZoneDto[]> getArrayResultClass() {
    return GeographicZoneDto[].class;
  }

  /**
   * This method retrieves geographic zones filtered by geographic level and parent zone.
   *
   * @param levelNumber geographic level number
   * @param parent ID of parent geographic zone
   * @return List of matched geographic zones.
   */
  public Collection<GeographicZoneDto> search(Integer levelNumber, UUID parent) {
    HashMap<String, Object> parameters = new HashMap<>();
    parameters.put("levelNumber", levelNumber);
    parameters.put("parent", parent);

    RequestParameters requestParameters = RequestParameters
            .init()
            .setPage(new PageRequest(0, Integer.MAX_VALUE, Direction.ASC, "name"));

    return getPage("search", requestParameters, parameters).getContent();
  }
}
