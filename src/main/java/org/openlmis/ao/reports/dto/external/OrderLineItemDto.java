package org.openlmis.ao.reports.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItemDto {
  private UUID id;
  private OrderableDto orderable;
  private Long orderedQuantity;
  private Long totalDispensingUnits;
}
