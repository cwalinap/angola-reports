package org.openlmis.ao.testutils;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.openlmis.ao.reports.dto.external.RequisitionStatusDto;
import org.openlmis.ao.reports.dto.external.StatusChangeDto;
import org.openlmis.ao.reports.dto.external.UserDto;

public class StatusChangeDtoDataBuilder {

  private RequisitionStatusDto status;
  private UUID authorId;
  private ZonedDateTime createdDate;
  private UserDto author;

  /**
   * Creates instance to be used for building {@link StatusChangeDtoDataBuilder}.
   */
  public StatusChangeDtoDataBuilder() {
    status = RequisitionStatusDto.APPROVED;
    authorId = UUID.randomUUID();
    createdDate = ZonedDateTime.now();
    author = new UserDto();
  }

  /**
   * Builds {@link StatusChangeDto} object from set properties.
   */
  public StatusChangeDto build() {
    return new StatusChangeDto(status, authorId, createdDate, author);
  }

  public StatusChangeDtoDataBuilder withStatus(RequisitionStatusDto status) {
    this.status = status;
    return this;
  }

  public StatusChangeDtoDataBuilder withCreatedDate(ZonedDateTime createdDate) {
    this.createdDate = createdDate;
    return this;
  }
}
