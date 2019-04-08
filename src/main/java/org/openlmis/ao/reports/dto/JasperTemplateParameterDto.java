package org.openlmis.ao.reports.dto;


import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.ao.reports.domain.JasperTemplateParameter;
import org.openlmis.ao.reports.domain.JasperTemplateParameter.Exporter;
import org.openlmis.ao.reports.domain.JasperTemplateParameter.Importer;

@Getter
@Setter
@NoArgsConstructor
public class JasperTemplateParameterDto
    implements Importer, Exporter {

  private UUID id;
  private String name;
  private String displayName;
  private String defaultValue;
  private String dataType;
  private String selectExpression;
  private String selectMethod;
  private String selectBody;
  private String selectProperty;
  private String displayProperty;
  private String description;
  private Boolean required;
  private List<String> options;
  private List<JasperTemplateParameterDependencyDto> dependencies;

  /**
   * Create new instance of JasperTemplateParameterDto based on given {@link
   * JasperTemplateParameter}
   *
   * @param jasperTemplateParameter instance of Template
   * @return new instance of JasperTemplateDto.
   */
  public static JasperTemplateParameterDto newInstance(
      JasperTemplateParameter jasperTemplateParameter) {
    JasperTemplateParameterDto jasperTemplateParameterDto = new JasperTemplateParameterDto();
    jasperTemplateParameter.export(jasperTemplateParameterDto);
    return jasperTemplateParameterDto;
  }
}
