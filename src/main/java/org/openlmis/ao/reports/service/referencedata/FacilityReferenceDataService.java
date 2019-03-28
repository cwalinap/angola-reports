package org.openlmis.ao.reports.service.referencedata;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openlmis.ao.reports.dto.external.FacilityDto;
import org.openlmis.ao.utils.RequestParameters;

@Service
public class FacilityReferenceDataService extends BaseReferenceDataService<FacilityDto> {

  @Override
  protected String getUrl() {
    return "/api/facilities/";
  }

  @Override
  protected Class<FacilityDto> getResultClass() {
    return FacilityDto.class;
  }

  @Override
  protected Class<FacilityDto[]> getArrayResultClass() {
    return FacilityDto[].class;
  }

  /**
   * This method retrieves Facilities with facilityName similar with name parameter or
   * facilityCode similar with code parameter.
   *
   * @param code Field with string to find similar code.
   * @param name Filed with string to find similar name.
   * @return List of FacilityDtos with similar code or name.
   */
  public Page<FacilityDto> search(String code, String name, UUID zoneId, boolean recurse) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("code", code);
    requestBody.put("name", name);
    requestBody.put("zoneId", zoneId);
    requestBody.put("recurse", recurse);

    return getPage("search", RequestParameters.init(), requestBody);
  }

  /**
   * Retrieves supply lines from reference data service by program and supervisory node.
   *
   * @param programId         UUID of the program
   * @param supervisoryNodeId UUID of the supervisory node
   * @return A list of supply lines matching search criteria
   */
  public List<FacilityDto> searchSupplyingDepots(UUID programId, UUID supervisoryNodeId) {
    RequestParameters parameters = RequestParameters
        .init()
        .set("programId", programId)
        .set("supervisoryNodeId", supervisoryNodeId);

    return findAll("supplying", parameters);
  }
}
