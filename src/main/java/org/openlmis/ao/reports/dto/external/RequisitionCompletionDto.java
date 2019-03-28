package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequisitionCompletionDto {
  /**
   * Name for the grouping, telling what is this completion for (ex. period, zone).
   */
  private String grouping;

  /**
   * Number of completed requisitions (approved in requisition due).
   */
  private int completed;

  /**
   * Number of missed requisitions (not reported).
   */
  private int missed;

  /**
   * Number of requisitions reported on time.
   */
  private int onTime;

  /**
   * Number of requisitions reported late.
   */
  private int late;

  /**
   * Number of total reported requisitions.
   */
  private int total;
}
