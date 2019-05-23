package org.openlmis.ao.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum RequisitionStatusDto {
  INITIATED(1),
  REJECTED(1),
  SUBMITTED(2),
  AUTHORIZED(3),
  IN_APPROVAL(3),
  APPROVED(4),
  RELEASED(5),
  RELEASED_WITHOUT_ORDER(6),
  SKIPPED(-1);

  private static final Map<RequisitionStatusDto, String> TRANSLATIONS =
      Collections.unmodifiableMap(new HashMap<RequisitionStatusDto, String>() {{
          put(INITIATED, "RASCUNHO");
          put(REJECTED, "REJEITADO");
          put(SUBMITTED, "SUBMETIDO");
          put(AUTHORIZED, "AUTORIZADO");
          put(IN_APPROVAL, "EM APROVAÇÃO");
          put(APPROVED, "APROVADO");
          put(RELEASED, "TERMINADO");
          put(RELEASED_WITHOUT_ORDER, "TERMINADO SEM ENCOMENDA");
          put(SKIPPED, "IGNORADO");
        }
      });

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

  public String getTranslation() {
    return TRANSLATIONS.get(this);
  }
}
