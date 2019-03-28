package org.openlmis.ao.reports.dto.external;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Ordering;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.openlmis.ao.testutils.OrderDtoDataBuilder;
import org.openlmis.ao.testutils.StatusChangeDtoDataBuilder;
import org.junit.Test;

public class OrderDtoTest {

  @Test
  public void shouldReturnEmptySetIfThereIsNoAuthorizeStatusChange() {
    OrderDto order = new OrderDtoDataBuilder()
        .withStatusChanges(singletonList(
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.IN_APPROVAL)
                .build()))
        .build();

    assertEquals(0, order.getInApprovalStatusChanges().size());
  }

  @Test
  public void shouldReturnSortedStatusChanges() {
    LocalTime localTime = LocalTime.now();

    OrderDto order = new OrderDtoDataBuilder()
        .withStatusChanges(asList(
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.AUTHORIZED)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 1), localTime,
                    ZoneId.systemDefault()))
                .build(),
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.IN_APPROVAL)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 10), localTime,
                    ZoneId.systemDefault()))
                .build(),
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.IN_APPROVAL)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 9), localTime,
                    ZoneId.systemDefault()))
                .build(),
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.IN_APPROVAL)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 12), localTime,
                    ZoneId.systemDefault()))
                .build(),
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.IN_APPROVAL)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 11), localTime,
                    ZoneId.systemDefault()))
                .build()))
        .build();

    assertTrue(Ordering.natural().isOrdered(order.getInApprovalStatusChanges()));

  }

  @Test
  public void shouldReturnStatusChangesAfterLastAuthorization() {
    LocalTime localTime = LocalTime.now();

    StatusChangeDto statusChange1 = new StatusChangeDtoDataBuilder()
        .withStatus(RequisitionStatusDto.IN_APPROVAL)
        .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 11), localTime,
            ZoneId.systemDefault()))
        .build();
    StatusChangeDto statusChange2 = new StatusChangeDtoDataBuilder()
        .withStatus(RequisitionStatusDto.IN_APPROVAL)
        .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 12), localTime,
            ZoneId.systemDefault()))
        .build();

    OrderDto order = new OrderDtoDataBuilder()
        .withStatusChanges(asList(
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.AUTHORIZED)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 1), localTime,
                    ZoneId.systemDefault()))
                .build(),
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.IN_APPROVAL)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 2), localTime,
                    ZoneId.systemDefault()))
                .build(),
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.AUTHORIZED)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 10), localTime,
                    ZoneId.systemDefault()))
                .build(),
            statusChange1,
            statusChange2))
        .build();

    assertThat(order.getInApprovalStatusChanges(), hasItems(statusChange1, statusChange2));
  }

  @Test
  public void shouldReturnOnlyInApprovalStatusChanges() {
    LocalTime localTime = LocalTime.now();

    StatusChangeDto statusChange = new StatusChangeDtoDataBuilder()
        .withStatus(RequisitionStatusDto.IN_APPROVAL)
        .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 11), localTime,
            ZoneId.systemDefault()))
        .build();

    OrderDto order = new OrderDtoDataBuilder()
        .withStatusChanges(asList(
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.AUTHORIZED)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 1), localTime,
                    ZoneId.systemDefault()))
                .build(),
            statusChange,
            new StatusChangeDtoDataBuilder()
                .withStatus(RequisitionStatusDto.APPROVED)
                .withCreatedDate(ZonedDateTime.of(LocalDate.of(2018, 10, 12), localTime,
                    ZoneId.systemDefault()))
                .build()))
        .build();

    assertThat(order.getInApprovalStatusChanges(), hasItems(statusChange));
  }
}
