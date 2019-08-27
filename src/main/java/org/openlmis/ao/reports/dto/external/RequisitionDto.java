package org.openlmis.ao.reports.dto.external;

import static org.openlmis.ao.utils.CurrencyConfig.currencyCode;

import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

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
            .sorted(compareByOrderableCategoryDisplayOrder())
            
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
            .sorted(compareByOrderableCategoryDisplayOrder())
            .collect(Collectors.toList());
  }

  /**
   * Calculates combined cost of all requisition line items.
   *
   * @return sum of total costs.
   */
  public Money getTotalCost() {
    return calculateTotalCostForLines(requisitionLineItems);
  }

  /**
   * Calculates combined cost of non-full supply non-skipped requisition line items.
   *
   * @return sum of total costs.
   */
  public Money getNonFullSupplyTotalCost() {
    return calculateTotalCostForLines(getNonSkippedNonFullSupplyRequisitionLineItems());
  }

  /**
   * Calculates combined cost of full supply non-skipped requisition line items.
   *
   * @return sum of total costs.
   */
  public Money getFullSupplyTotalCost() {
    return calculateTotalCostForLines(getNonSkippedFullSupplyRequisitionLineItems());
  }

  private Money calculateTotalCostForLines(List<RequisitionLineItemDto> requisitionLineItems) {
    Money defaultValue = Money.of(CurrencyUnit.of(currencyCode), 0);

    if (requisitionLineItems.isEmpty()) {
      return defaultValue;
    }

    Optional<Money> money = requisitionLineItems.stream()
        .map(RequisitionLineItemDto::getTotalCost).filter(Objects::nonNull).reduce(Money::plus);

    return money.orElse(defaultValue);
  }

  private Comparator<RequisitionLineItemDto> compareByOrderableCategoryDisplayOrder() {
    //We are aware of potentially NPE here but if the exception will be thrown
    //this means that configuration of OLMIS needs to be checked and updated
    return Comparator.comparingInt((RequisitionLineItemDto requisitionLineItemDto) ->
                requisitionLineItemDto.getOrderable().getPrograms().stream().findFirst().get()
                  .getOrderableCategoryDisplayOrder()).thenComparing((reqLineItemDto, 
                    reqLineItemDto2) -> reqLineItemDto.getOrderable().getFullProductName()
                      .compareToIgnoreCase(reqLineItemDto2.getOrderable().getFullProductName()));
  }
  
}
