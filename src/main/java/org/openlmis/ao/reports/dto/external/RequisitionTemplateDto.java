package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class RequisitionTemplateDto {

  private UUID id;

  private ZonedDateTime createdDate;

  private ZonedDateTime modifiedDate;

  private UUID programId;

  private Integer numberOfPeriodsToAverage;

  private Map<String, RequisitionTemplateColumnDto> columnsMap;
}
