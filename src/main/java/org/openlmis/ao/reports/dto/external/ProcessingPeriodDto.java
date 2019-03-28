package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ProcessingPeriodDto {
  private UUID id;
  private ProcessingScheduleDto processingSchedule;
  private String name;
  private String description;
  private LocalDate startDate;
  private LocalDate endDate;
  private Integer durationInMonths;
}
