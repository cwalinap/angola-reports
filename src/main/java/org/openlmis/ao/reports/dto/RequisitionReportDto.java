package org.openlmis.ao.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.Money;
import org.openlmis.ao.reports.dto.external.RequisitionDto;
import org.openlmis.ao.reports.dto.external.RequisitionLineItemDto;
import org.openlmis.ao.reports.dto.external.UserDto;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionReportDto {
  private RequisitionDto requisition;
  private List<RequisitionLineItemDto> fullSupply;
  private List<RequisitionLineItemDto> nonFullSupply;
  private Money fullSupplyTotalCost;
  private Money nonFullSupplyTotalCost;
  private Money totalCost;
  private UserDto initiatedBy;
  private ZonedDateTime initiatedDate;
  private UserDto submittedBy;
  private ZonedDateTime submittedDate;
  private UserDto authorizedBy;
  private ZonedDateTime authorizedDate;
}
