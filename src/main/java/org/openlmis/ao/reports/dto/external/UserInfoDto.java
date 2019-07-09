package org.openlmis.ao.reports.dto.external;

import lombok.Data;

import java.util.Comparator;

@Data
public class UserInfoDto {

  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String isEmailVerified;
  private String phone;
  private String program;
  private String supervisoryNode;
  private String rightAccess;

  /**
   * Constructor with all required data.
   *
   * @param base user object, with the base data
   * @param facility name of the supervisoryNode assigned to the user
   * @param program name of the program
   * @param contactDetails contactDetails attached to the user
   * @param rightAccess the summary of rights for a specified user and the program
   */
  public UserInfoDto(UserDto base, String facility, String program,
                      UserContactDetailsDto contactDetails, String rightAccess) {
    this.username = base.getUsername();
    this.firstName = base.getFirstName();
    this.lastName = base.getLastName();
    this.email = getEmail(contactDetails);
    this.supervisoryNode = facility;
    this.program = program;
    this.isEmailVerified = isEmailVerified(contactDetails);
    this.phone = getPhoneNumber(contactDetails);
    this.rightAccess = rightAccess;
  }

  /**
   * Compares objects by supervisory nodes and programs.
   *
   * @param other - the object to be compared with
   * @return result of the comparision
   */
  public int compareTo(UserInfoDto other) {
    Comparator<String> nullSafeStringComparator = Comparator
            .nullsFirst(String::compareToIgnoreCase);
    Comparator<UserInfoDto> comparator = Comparator
            .comparing(UserInfoDto::getSupervisoryNode, nullSafeStringComparator)
            .thenComparing(UserInfoDto::getProgram, nullSafeStringComparator);
    return comparator.compare(this, other);
  }

  private String getEmail(UserContactDetailsDto contactDetails) {
    return contactDetails == null || contactDetails.getEmailDetails() == null
            || contactDetails.getEmailDetails().getEmail() == null
            ? "" : String.valueOf(contactDetails.getEmailDetails().getEmail());
  }

  private String getPhoneNumber(UserContactDetailsDto contactDetails) {
    return contactDetails == null || contactDetails.getPhoneNumber() == null
            ? "" : contactDetails.getPhoneNumber();
  }

  private String isEmailVerified(UserContactDetailsDto contactDetails) {
    return contactDetails == null || contactDetails.getEmailDetails() == null
            || contactDetails.getEmailDetails().getEmailVerified() == null
            ? "" : String.valueOf(contactDetails.getEmailDetails().getEmailVerified());
  }
}
