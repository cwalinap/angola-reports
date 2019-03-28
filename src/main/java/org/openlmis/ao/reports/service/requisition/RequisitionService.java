package org.openlmis.ao.reports.service.requisition;

import org.openlmis.ao.reports.dto.external.BasicRequisitionDto;
import org.openlmis.ao.reports.dto.external.RequisitionDto;
import org.openlmis.ao.reports.dto.external.RequisitionStatusDto;
import org.openlmis.ao.utils.RequestParameters;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RequisitionService extends BaseRequisitionService<RequisitionDto> {

  @Override
  protected String getUrl() {
    return "/api/requisitions/";
  }

  @Override
  protected Class<RequisitionDto> getResultClass() {
    return RequisitionDto.class;
  }

  @Override
  protected Class<RequisitionDto[]> getArrayResultClass() {
    return RequisitionDto[].class;
  }

  /**
   * Finds requisitions matching all of the provided parameters.
   */
  public Page<BasicRequisitionDto> search(UUID facility, UUID program,
                                          ZonedDateTime initiatedDateFrom,
                                          ZonedDateTime initiatedDateTo, UUID processingPeriod,
                                          UUID supervisoryNode, Set<RequisitionStatusDto>
                                              requisitionStatuses, Boolean emergency) {
    String commaDelimitedStatuses = (requisitionStatuses == null) ? null :
        requisitionStatuses.stream().map(Enum::name).collect(Collectors.joining(","));
    RequestParameters parameters = RequestParameters.init()
        .set("facility", facility)
        .set("program", program)
        .set("initiatedDateFrom", initiatedDateFrom)
        .set("initiatedDateTo", initiatedDateTo)
        .set("processingPeriod", processingPeriod)
        .set("supervisoryNode", supervisoryNode)
        .set("requisitionStatus", commaDelimitedStatuses)
        .set("emergency", emergency);

    return getPage("search", parameters, null, HttpMethod.GET, BasicRequisitionDto.class);
  }
}
