package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProgramOrderableDto {
  private UUID programId;
  private UUID orderableDisplayCategoryId;
  private String orderableCategoryDisplayName;
  private Integer orderableCategoryDisplayOrder;
  private Boolean active;
  private Boolean fullSupply;
  private Integer displayOrder;
  private Integer dosesPerPatient;
  private BigDecimal pricePerPack;
}
