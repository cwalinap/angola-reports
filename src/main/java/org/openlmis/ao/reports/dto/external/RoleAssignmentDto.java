package org.openlmis.ao.reports.dto.external;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleAssignmentDto {
  private UUID id;
  protected RoleDto role;
  protected UserDto user;
}
