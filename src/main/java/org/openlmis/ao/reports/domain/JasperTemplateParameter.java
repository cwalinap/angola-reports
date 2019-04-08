package org.openlmis.ao.reports.domain;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.ao.reports.dto.JasperTemplateParameterDependencyDto;

/**
 * Defines a parameter of Jasper Template, meant to be passed on printing.
 * <p>
 * selectExpression is used to indicate how the parameter values should be retrieved (API path).
 * selectProperty indicates which property of objects should be passed as parameter (ex. ID).
 * displayProperty is used to indicate which property of object should be displayed for choice.
 * </p>
 */
@Entity
@Table(name = "template_parameters")
@NoArgsConstructor
@AllArgsConstructor
public class JasperTemplateParameter extends BaseEntity {

  @ManyToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "templateId", nullable = false)
  @Getter
  @Setter
  private JasperTemplate template;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String name;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String displayName;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String defaultValue;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String dataType;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String selectExpression;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION, nullable = false)
  @Getter
  @Setter
  private String selectMethod;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String selectBody;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String selectProperty;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String displayProperty;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String description;

  @ElementCollection
  @Getter
  @Setter
  private List<String> options;

  @OneToMany(
      mappedBy = "parameter",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE},
      fetch = FetchType.EAGER,
      orphanRemoval = true)
  @Getter
  @Setter
  private List<JasperTemplateParameterDependency> dependencies;

  @Column(nullable = false)
  @Getter
  @Setter
  private Boolean required;

  @PrePersist
  @PreUpdate
  private void preSave() {
    if (dependencies != null) {
      dependencies.forEach(dependency -> dependency.setParameter(this));
    }
  }

  /**
   * Create new instance of JasperTemplateParameter based on given
   * {@link Importer}
   *
   * @param importer instance of {@link Importer}
   * @return instance of JasperTemplateParameter.
   */
  public static JasperTemplateParameter newInstance(Importer importer) {
    JasperTemplateParameter jasperTemplateParameter = new JasperTemplateParameter();
    jasperTemplateParameter.setId(importer.getId());
    jasperTemplateParameter.setName(importer.getName());
    jasperTemplateParameter.setDisplayName(importer.getDisplayName());
    jasperTemplateParameter.setDefaultValue(importer.getDefaultValue());
    jasperTemplateParameter.setSelectExpression(importer.getSelectExpression());
    jasperTemplateParameter.setSelectMethod(importer.getSelectMethod());
    jasperTemplateParameter.setSelectBody(importer.getSelectBody());
    jasperTemplateParameter.setDescription(importer.getDescription());
    jasperTemplateParameter.setDataType(importer.getDataType());
    jasperTemplateParameter.setSelectProperty(importer.getSelectProperty());
    jasperTemplateParameter.setDisplayProperty(importer.getDisplayProperty());
    jasperTemplateParameter.setRequired(importer.getRequired());
    jasperTemplateParameter.setOptions(importer.getOptions());
    jasperTemplateParameter.setDependencies(importer.getDependencies()
        .stream()
        .map(JasperTemplateParameterDependency::newInstance)
        .collect(Collectors.toList())
    );
    return jasperTemplateParameter;
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setName(name);
    exporter.setDescription(description);
    exporter.setDataType(dataType);
    exporter.setDefaultValue(defaultValue);
    exporter.setDisplayName(displayName);
    exporter.setSelectExpression(selectExpression);
    exporter.setSelectMethod(selectMethod);
    exporter.setSelectBody(selectBody);
    exporter.setSelectProperty(selectProperty);
    exporter.setDisplayProperty(displayProperty);
    exporter.setRequired(required);
    exporter.setOptions(options);
    exporter.setDependencies(dependencies
        .stream()
        .map(JasperTemplateParameterDependencyDto::newInstance)
        .collect(Collectors.toList())
    );
  }

  public interface Exporter {
    void setId(UUID id);

    void setName(String name);

    void setDisplayName(String displayName);

    void setDefaultValue(String defaultValue);

    void setDataType(String dataType);

    void setSelectExpression(String selectExpression);

    void setSelectMethod(String selectMethod);

    void setSelectBody(String selectBody);

    void setDescription(String description);

    void setSelectProperty(String selectProperty);

    void setDisplayProperty(String displayProperty);

    void setRequired(Boolean required);

    void setOptions(List<String> options);

    void setDependencies(List<JasperTemplateParameterDependencyDto> dependencies);
  }

  public interface Importer {
    UUID getId();

    String getName();

    String getDisplayName();

    String getDefaultValue();

    String getDataType();

    String getSelectExpression();

    String getSelectMethod();

    String getSelectBody();

    String getDescription();

    String getSelectProperty();

    String getDisplayProperty();

    Boolean getRequired();

    List<String> getOptions();

    List<JasperTemplateParameterDependencyDto> getDependencies();
  }
}
