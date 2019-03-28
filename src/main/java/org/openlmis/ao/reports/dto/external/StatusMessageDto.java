package org.openlmis.ao.reports.dto.external;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusMessageDto {
  private UUID id;
  private UUID authorId;
  private RequisitionStatusDto status;
  private String body;
}