package org.openlmis.ao.reports.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class RequisitionDto {
  @Getter
  @Setter
  private UUID id;

  @Getter
  @Setter
  private ZonedDateTime createdDate;

  @Getter
  @Setter
  private ZonedDateTime modifiedDate;

  @Getter
  @Setter
  private String draftStatusMessage;

  @Getter
  @Setter
  private FacilityDto facility;

  @Getter
  @Setter
  private ProgramDto program;

  @Getter
  @Setter
  private ProcessingPeriodDto processingPeriod;

  @Getter
  @Setter
  private RequisitionStatusDto status;

  @Getter
  @Setter
  private Boolean emergency;

  @Getter
  @Setter
  private UUID supplyingFacility;

  @Getter
  @Setter
  private UUID supervisoryNode;

  @Getter
  @Setter
  private RequisitionTemplateDto template;

  @Getter
  @Setter
  private List<RequisitionLineItemDto> requisitionLineItems;

  @Getter
  @Setter
  private List<StatusChangeDto> statusHistory = new ArrayList<>();

  /**
   * Filter out requisitionLineItems that are skipped and not-full supply.
   *
   * @return non-skipped full supply requisition line items
   */
  public List<RequisitionLineItemDto> getNonSkippedFullSupplyRequisitionLineItems() {
    return this.requisitionLineItems.stream()
            .filter(line -> !line.getSkipped())
            .filter(line -> !line.isNonFullSupply(program))
            .collect(Collectors.toList());
  }

  /**
   * Filter out requisitionLineItems that are skipped and full supply.
   *
   * @return non-skipped non-full supply requisition line items
   */
  public List<RequisitionLineItemDto> getNonSkippedNonFullSupplyRequisitionLineItems() {
    return this.requisitionLineItems.stream()
            .filter(line -> !line.getSkipped())
            .filter(line -> line.isNonFullSupply(program))
            .collect(Collectors.toList());
  }

  /**
   * Calculates combined cost of all requisition line items.
   *
   * @return sum of total costs.
   */
  public BigDecimal getTotalCost() {
    return calculateTotalCostForLines(requisitionLineItems);
  }

  /**
   * Calculates combined cost of non-full supply non-skipped requisition line items.
   *
   * @return sum of total costs.
   */
  public BigDecimal getNonFullSupplyTotalCost() {
    return calculateTotalCostForLines(getNonSkippedNonFullSupplyRequisitionLineItems());
  }

  /**
   * Calculates combined cost of full supply non-skipped requisition line items.
   *
   * @return sum of total costs.
   */
  public BigDecimal getFullSupplyTotalCost() {
    return calculateTotalCostForLines(getNonSkippedFullSupplyRequisitionLineItems());
  }

  private BigDecimal calculateTotalCostForLines(List<RequisitionLineItemDto> requisitionLineItems) {
    if (requisitionLineItems.isEmpty()) {
      return BigDecimal.ZERO;
    }

    Optional<BigDecimal> money = requisitionLineItems.stream()
            .map(RequisitionLineItemDto::getTotalCost)
            .filter(Objects::nonNull)
            .reduce(BigDecimal::add);

    return money.orElse(BigDecimal.ZERO);
  }
}
