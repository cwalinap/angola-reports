package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MoneyDto {
  private BigDecimal value;
}
