package org.openlmis.ao.reports.dto.external;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {
  private UUID id;
  private String name;
  private String description;
  private Set<RightDto> rights;
}
