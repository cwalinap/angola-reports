package org.openlmis.ao.reports.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * StatusLogEntry is used to encapsulate data (author and dateTime) associated with a
 * requisition's status change.
 */
@AllArgsConstructor
@NoArgsConstructor
public class StatusLogEntryDto {

  @Getter
  @Setter
  private UUID authorId;

  @Getter
  @Setter
  private ZonedDateTime changeDate;
}
