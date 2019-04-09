package org.openlmis.ao.reports.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequisitionTemplateColumnDto {

  private String name;

  private String label;

  private String indicator;

  private int displayOrder;

  private Boolean isDisplayed;

  private SourceType source;

  private AvailableRequisitionColumnDto columnDefinition;

  private AvailableRequisitionColumnOptionDto option;

  private String definition;

  private String tag;
}
