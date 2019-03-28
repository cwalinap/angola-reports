package org.openlmis.ao.reports.dto.external;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
  private UUID id;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private boolean verified;
  private boolean active;
  private boolean loginRestricted;
  private UUID homeFacilityId;
  private Set<RoleAssignmentDto> roleAssignments;

  /**
   * Prints the name of the user for display purposes.
   * The format is "firstName lastName". If one of them is missing, it is
   * omitted and the space is trimmed. If they are both missing, the
   * user name is used.
   * @return the name of the user for display purposes
   */
  public String printName() {
    if (StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName)) {
      return username;
    } else {
      return StringUtils.trim(StringUtils.defaultString(firstName) + ' '
          + StringUtils.defaultString(lastName));
    }
  }
}
