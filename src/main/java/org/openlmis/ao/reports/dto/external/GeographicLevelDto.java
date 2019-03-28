package org.openlmis.ao.reports.dto.external;

import java.util.UUID;

import lombok.Data;

@Data
public class GeographicLevelDto {
  private UUID id;
  private String code;
  private String name;
  private Integer levelNumber;
}

