package org.openlmis.ao.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;

public enum RequisitionStatusDto {
  INITIATED(1),
  REJECTED(1),
  SUBMITTED(2),
  AUTHORIZED(3),
  IN_APPROVAL(3),
  APPROVED(4),
  RELEASED(5),
  SKIPPED(-1);

  private int value;

  RequisitionStatusDto(int value) {
    this.value = value;
  }

  @JsonIgnore
  public boolean isSubmittable() {
    return value == 1;
  }

  @JsonIgnore
  public boolean isUpdatable() {
    return value < 4 && value != -1;
  }

  @JsonIgnore
  public boolean isPreAuthorize() {
    return value == 1 || value == 2;
  }

  @JsonIgnore
  public boolean isPostSubmitted() {
    return value >= 2;
  }

  @JsonIgnore
  public boolean isApproved() {
    return value >= 4;
  }

  public boolean duringApproval() {
    return value == 3;
  }

  public boolean isAuthorized() {
    return value >= 3;
  }
}
