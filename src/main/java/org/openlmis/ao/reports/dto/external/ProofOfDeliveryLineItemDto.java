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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static org.openlmis.ao.reports.dto.external.ResourceNames.LOTS;
import static org.openlmis.ao.reports.dto.external.ResourceNames.ORDERABLES;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"serviceUrl"})
public final class ProofOfDeliveryLineItemDto extends BaseDto {

  @Setter
  private String serviceUrl;

  @Getter
  @Setter
  private ObjectReferenceDto orderable;

  @Getter
  @Setter
  private ObjectReferenceDto lot;

  @Getter
  @Setter
  private Integer quantityAccepted;

  @Getter
  @Setter
  private Boolean useVvm;

  @Getter
  @Setter
  private VvmStatus vvmStatus;

  @Getter
  @Setter
  private Integer quantityRejected;

  @Getter
  @Setter
  private UUID rejectionReasonId;

  @Getter
  @Setter
  private String notes;

  /**
   * Find a correct {@link UUID} instance based on the passed string. The method ignores
   * the case.
   * TODO out
   */
  @JsonIgnore
  public UUID getOrderableId() {
    return null == orderable ? null : orderable.getId();
  }

  /**
   * Find a correct {@link OrderStatus} instance based on the passed string. The method ignores
   * the case.
   * TODO out
   */
  @JsonIgnore
  public void setOrderableId(UUID orderableId) {
    if (null != orderableId) {
      this.orderable = ObjectReferenceDto.create(orderableId, serviceUrl, ORDERABLES);
    }
  }

  /**
   * Find a correct {@link OrderStatus} instance based on the passed string. The method ignores
   * the case.
   * TODO out
   */
  @JsonIgnore
  public UUID getLotId() {
    return null == lot ? null : lot.getId();
  }

  /**
   * Find a correct {@link OrderStatus} instance based on the passed string. The method ignores
   * the case.
   * TODO out
   */
  @JsonIgnore
  public void setLotId(UUID lotId) {
    if (null != lotId) {
      this.lot = ObjectReferenceDto.create(lotId, serviceUrl, LOTS);
    }
  }
}
