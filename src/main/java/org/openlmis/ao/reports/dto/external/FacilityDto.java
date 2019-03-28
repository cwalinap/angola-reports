package org.openlmis.ao.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityDto {

  public static final Integer DISTRICT_LEVEL = 3;

  private UUID id;
  private String code;
  private String name;
  private String description;
  private Boolean active;
  private LocalDate goLiveDate;
  private LocalDate goDownDate;
  private String comment;
  private Boolean enabled;
  private Boolean openLmisAccessible;
  private List<SupportedProgramDto> supportedPrograms;
  private GeographicZoneDto geographicZone;
  private FacilityOperatorDto operator;
  private FacilityTypeDto type;

  /**
   * Get zone with given level number by traversing up geographicZone hierachy if needed.
   * @return zone of the facility with given level number.
   */
  @JsonIgnore
  public GeographicZoneDto getZoneByLevelNumber(Integer levelNumber) {
    GeographicZoneDto district = geographicZone;
    while (null != district && null != district.getParent()
        && district.getLevel().getLevelNumber() > levelNumber) {
      district = district.getParent();
    }
    return district;
  }

  public interface Exporter {
    void setId(UUID id);

    void setCode(String code);

    void setName(String name);

    void setDescription(String description);

    void setActive(Boolean active);

    void setGoLiveDate(LocalDate goLiveDate);

    void setGoDownDate(LocalDate goDownDate);

    void setComment(String comment);

    void setEnabled(Boolean enabled);

    void setOpenLmisAccessible(Boolean openLmisAccessible);

    void setSupportedPrograms(List<SupportedProgramDto> supportedPrograms);

    void setGeographicZone(GeographicZoneDto geographicZone);

    void setOperator(FacilityOperatorDto operator);

    void setType(FacilityTypeDto type);
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setCode(code);
    exporter.setName(name);
    exporter.setDescription(description);
    exporter.setActive(active);
    exporter.setGoLiveDate(goLiveDate);
    exporter.setGoDownDate(goDownDate);
    exporter.setComment(comment);
    exporter.setEnabled(enabled);
    exporter.setOpenLmisAccessible(openLmisAccessible);
    exporter.setSupportedPrograms(supportedPrograms);
    exporter.setGeographicZone(geographicZone);
    exporter.setOperator(operator);
    exporter.setType(type);
  }
}
