package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class AvailableRequisitionColumnOptionDto {

  private UUID id;

  private String optionName;

  private String optionLabel;
}
