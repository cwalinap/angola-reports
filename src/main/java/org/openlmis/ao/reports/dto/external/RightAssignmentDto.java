package org.openlmis.ao.reports.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RightAssignmentDto {

  private List<String> roles;
  private UUID supervisoryNodeId;
}
