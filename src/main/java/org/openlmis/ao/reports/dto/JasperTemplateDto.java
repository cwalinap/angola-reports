/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.ao.reports.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.ao.reports.domain.JasperTemplate;
import org.openlmis.ao.reports.domain.JasperTemplateParameter;
import org.openlmis.ao.reports.domain.JasperTemplate.Exporter;
import org.openlmis.ao.reports.domain.JasperTemplate.Importer;

@AllArgsConstructor
@NoArgsConstructor
public class JasperTemplateDto implements Importer, Exporter {

  @Getter
  @Setter
  private UUID id;

  @Getter
  @Setter
  private String name;

  @Getter
  @Setter
  private String type;

  @Getter
  @Setter
  private String description;

  @Getter
  @Setter
  private Boolean isDisplayed;

  @Getter
  @Setter
  private byte[] data;

  @Getter
  @Setter
  private List<String> supportedFormats;

  @Setter
  private List<JasperTemplateParameterDto> templateParameters;

  @Override
  public List<JasperTemplateParameter.Importer> getTemplateParameters() {
    return new ArrayList<>(
        Optional.ofNullable(templateParameters).orElse(Collections.emptyList())
    );
  }

  /**
   * Create new list of JasperTemplateDto based on given list of {@link JasperTemplate}
   *
   * @param templates list of {@link JasperTemplate}
   * @return new list of JasperTemplateDto.
   */
  public static List<JasperTemplateDto> newInstance(Iterable<JasperTemplate> templates) {
    return StreamSupport.stream(templates.spliterator(), false)
        .map(JasperTemplateDto::newInstance)
        .collect(Collectors.toList());
  }

  /**
   * Create new instance of JasperTemplateDto based on given {@link JasperTemplate}
   *
   * @param jasperTemplate instance of Template
   * @return new instance of JasperTemplateDto.
   */
  public static JasperTemplateDto newInstance(JasperTemplate jasperTemplate) {
    if (jasperTemplate == null) {
      return null;
    }

    JasperTemplateDto jasperTemplateDto = new JasperTemplateDto();
    jasperTemplate.export(jasperTemplateDto);

    if (jasperTemplate.getTemplateParameters() != null) {
      jasperTemplateDto.setTemplateParameters(
          jasperTemplate.getTemplateParameters()
              .stream()
              .map(JasperTemplateParameterDto::newInstance)
              .collect(Collectors.toList())
      );
    }

    return jasperTemplateDto;
  }
}
