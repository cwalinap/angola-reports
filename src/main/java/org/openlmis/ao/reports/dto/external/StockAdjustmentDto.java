
package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StockAdjustmentDto {
  private UUID id;
  private UUID reasonId;
  private Integer quantity;
}
