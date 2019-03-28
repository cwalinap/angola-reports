package org.openlmis.ao.reports.dto.external;

import java.util.UUID;

import lombok.Data;

@Data
public class FacilityTypeDto {
  private UUID id;
  private String code;
  private String name;
  private String description;
  private Integer displayOrder;
  private Boolean active;
}
