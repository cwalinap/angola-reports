package org.openlmis.ao.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.joda.money.Money;
import org.openlmis.ao.utils.MoneyDeserializer;
import org.openlmis.ao.utils.MoneySerializer;

@Getter
@Setter
public class RequisitionLineItemDto {

  public static final String BEGINNING_BALANCE = "beginningBalance";
  public static final String ADJUSTED_CONSUMPTION = "adjustedConsumption";
  public static final String AVERAGE_CONSUMPTION = "averageConsumption";
  public static final String SKIPPED_COLUMN = "skipped";
  public static final String APPROVED_QUANTITY = "approvedQuantity";
  public static final String REMARKS_COLUMN = "remarks";

  private UUID id;
  private OrderableDto orderable;
  private Integer beginningBalance;
  private Integer totalReceivedQuantity;
  private Integer totalLossesAndAdjustments;
  private Integer stockOnHand;
  private Integer requestedQuantity;
  private Integer totalConsumedQuantity;
  private String requestedQuantityExplanation;
  private String remarks;
  private Integer approvedQuantity;
  private Integer totalStockoutDays;
  private Integer total;
  private Long packsToShip;
  @JsonSerialize(using = MoneySerializer.class)
  @JsonDeserialize(using = MoneyDeserializer.class)
  private Money pricePerPack;
  private Integer numberOfNewPatientsAdded;
  @JsonSerialize(using = MoneySerializer.class)
  @JsonDeserialize(using = MoneyDeserializer.class)
  private Money totalCost;
  private Boolean skipped;
  private Integer adjustedConsumption;
  private List<Integer> previousAdjustedConsumptions;
  private Integer averageConsumption;
  private BigDecimal maxPeriodsOfStock;
  private Integer maximumStockQuantity;
  private Integer calculatedOrderQuantity;
  private Integer idealStockAmount;
  private Integer calculatedOrderQuantityIsa;
  private Integer additionalQuantityRequired;

  @JsonProperty
  private List<StockAdjustmentDto> stockAdjustments;

  @JsonIgnore
  public Boolean isNonFullSupply(ProgramDto programDto) {
    return !orderable.findProgramOrderableDto(programDto.getId()).getFullSupply();
  }

}
