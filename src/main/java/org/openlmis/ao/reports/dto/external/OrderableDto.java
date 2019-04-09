package org.openlmis.ao.reports.dto.external;

import static java.lang.Boolean.parseBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderableDto {
  private UUID id;
  private static final String USE_VVM = "useVVM";

  private String productCode;
  private String fullProductName;
  private long netContent;
  private long packRoundingThreshold;
  private boolean roundToZero;
  private Set<ProgramOrderableDto> programs;
  private DispensableDto dispensable;
  private Map<String, String> extraData;

  public OrderableDto(String productCode, String fullProductName) {
    this(null, productCode, fullProductName, 0L, 0L, false, null, null, null);
  }

  @JsonIgnore
  public boolean useVvm() {
    return null != extraData && parseBoolean(extraData.get(USE_VVM));
  }

  /**
   * Get program orderable for given program id
   * @return program orderable.
   */
  @JsonIgnore
  public ProgramOrderableDto findProgramOrderableDto(UUID programId) {
    return programs.stream().filter(po -> po.getProgramId().equals(programId))
        .findFirst().orElse(null);
  }
}
