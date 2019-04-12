/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.ao.reports.dto.external;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public final class StockCardDto implements IdentifiableByOrderableLot {

  @Getter
  @Setter
  private UUID id;

  @Getter
  @Setter
  private Integer stockOnHand;

  @Getter
  @Setter
  private FacilityDto facility;

  @Getter
  @Setter
  private ProgramDto program;

  @Getter
  @Setter
  private OrderableDto orderable;

  @Getter
  @Setter
  private LotDto lot;

  @Getter
  @Setter
  private Map<String, String> extraData;

  @Getter
  @Setter
  @JsonFormat(shape = STRING)
  private LocalDate lastUpdate;

  @Getter
  @Setter
  private List<StockCardLineItemDto> lineItems;

  @JsonIgnore
  public UUID getOrderableId() {
    return orderable.getId();
  }

  @JsonIgnore
  public UUID getLotId() {
    return lot == null ? null : lot.getId();
  }

  public boolean hasLot() {
    return getLotId() != null;
  }

}
