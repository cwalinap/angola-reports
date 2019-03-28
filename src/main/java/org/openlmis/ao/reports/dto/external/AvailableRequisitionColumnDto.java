package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class AvailableRequisitionColumnDto  {

  private UUID id;

  private String name;

  private Set<SourceType> sources;

  private Set<AvailableRequisitionColumnOptionDto> options;

  private String label;

  private String indicator;

  private Boolean mandatory;

  private Boolean isDisplayRequired;

  private Boolean canChangeOrder;

  private Boolean canBeChangedByUser;

  private String definition;

  private ColumnType columnType;
}
