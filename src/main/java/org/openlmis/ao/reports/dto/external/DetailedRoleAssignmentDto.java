package org.openlmis.ao.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class DetailedRoleAssignmentDto {

  @Getter
  private RoleDto role;

  @Getter
  @Setter
  private String programCode;

  @Getter
  @Setter
  private String supervisoryNodeCode;

  @Getter
  @Setter
  private String warehouseCode;

  @Getter
  @Setter
  private UUID programId;

  @Getter
  @Setter
  private UUID supervisoryNodeId;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DetailedRoleAssignmentDto)) {
      return false;
    }
    DetailedRoleAssignmentDto that = (DetailedRoleAssignmentDto) obj;
    return Objects.equals(role, that.role)
        && Objects.equals(programCode, that.programCode)
        && Objects.equals(supervisoryNodeCode, that.supervisoryNodeCode)
        && Objects.equals(warehouseCode, that.warehouseCode)
        && Objects.equals(programId, that.programId)
        && Objects.equals(supervisoryNodeId, that.supervisoryNodeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, programCode, supervisoryNodeCode, warehouseCode,
        programId, supervisoryNodeId);
  }
}
