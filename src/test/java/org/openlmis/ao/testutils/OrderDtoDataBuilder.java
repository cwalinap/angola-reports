package org.openlmis.ao.testutils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.openlmis.ao.reports.dto.external.FacilityDto;
import org.openlmis.ao.reports.dto.external.OrderDto;
import org.openlmis.ao.reports.dto.external.OrderLineItemDto;
import org.openlmis.ao.reports.dto.external.OrderStatusDto;
import org.openlmis.ao.reports.dto.external.ProcessingPeriodDto;
import org.openlmis.ao.reports.dto.external.ProgramDto;
import org.openlmis.ao.reports.dto.external.StatusChangeDto;
import org.openlmis.ao.reports.dto.external.StatusMessageDto;
import org.openlmis.ao.reports.dto.external.UserDto;

public class OrderDtoDataBuilder {

  private static int instanceNumber = 0;

  private UUID id;
  private UUID externalId;
  private Boolean emergency;
  private FacilityDto facility;
  private ProcessingPeriodDto processingPeriod;
  private ZonedDateTime createdDate;
  private UserDto createdBy;
  private ProgramDto program;
  private FacilityDto requestingFacility;
  private FacilityDto receivingFacility;
  private FacilityDto supplyingFacility;
  private String orderCode;
  private OrderStatusDto status;
  private BigDecimal quotedCost;
  private List<OrderLineItemDto> orderLineItems;
  private List<StatusMessageDto> statusMessages;
  private List<StatusChangeDto> statusChanges;

  /**
   * Creates instance to be used for building {@link OrderDtoDataBuilder}.
   */
  public OrderDtoDataBuilder() {
    instanceNumber++;

    id = UUID.randomUUID();
    externalId = UUID.randomUUID();
    emergency = false;
    facility = new FacilityDto();
    processingPeriod = new ProcessingPeriodDto();
    createdDate = ZonedDateTime.now();
    createdBy = new UserDto();
    program = new ProgramDto();
    requestingFacility = new FacilityDto();
    receivingFacility = new FacilityDto();
    supplyingFacility = new FacilityDto();
    orderCode = "O-" + instanceNumber;
    status = OrderStatusDto.ORDERED;
    quotedCost = new BigDecimal(100);
    orderLineItems = new ArrayList<>();
    statusMessages = new ArrayList<>();
    statusChanges = new ArrayList<>();
  }

  /**
   * Builds {@link OrderDto} object from set properties.
   */
  public OrderDto build() {
    return new OrderDto(id, externalId, emergency, facility, processingPeriod, createdDate,
        createdBy, program, requestingFacility, receivingFacility, supplyingFacility, orderCode,
        status, quotedCost, orderLineItems, statusMessages, statusChanges);
  }

  public OrderDtoDataBuilder withStatusChanges(List<StatusChangeDto> statusChanges) {
    this.statusChanges = statusChanges;
    return this;
  }
}
