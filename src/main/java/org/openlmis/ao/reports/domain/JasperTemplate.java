package org.openlmis.ao.reports.domain;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jasper_templates")
@NoArgsConstructor
@AllArgsConstructor
public class JasperTemplate extends BaseEntity {

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION, unique = true, nullable = false)
  @Getter
  @Setter
  private String name;

  @Column
  @Getter
  @Setter
  private byte[] data;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String type;

  @Column(columnDefinition = "boolean DEFAULT true")
  @Getter
  @Setter
  private Boolean isDisplayed = true;

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION)
  @Getter
  @Setter
  private String description;

  @ElementCollection
  @Getter
  @Setter
  private List<String> supportedFormats;

  @OneToMany(
      mappedBy = "template",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE},
      fetch = FetchType.EAGER,
      orphanRemoval = true)
  @Fetch(FetchMode.SELECT)
  @Getter
  @Setter
  private List<JasperTemplateParameter> templateParameters;

  /**
   * Create a new instance of Jasper template based on data from {@link Importer}
   *
   * @param importer instance of {@link Importer}
   * @return new instance od template.
   */
  public static JasperTemplate newInstance(Importer importer) {
    JasperTemplate jasperTemplate = new JasperTemplate();

    jasperTemplate.setId(importer.getId());
    jasperTemplate.setName(importer.getName());
    jasperTemplate.setData(importer.getData());
    jasperTemplate.setType(importer.getType());
    jasperTemplate.setDescription(importer.getDescription());
    jasperTemplate.setIsDisplayed(importer.getIsDisplayed());
    jasperTemplate.setSupportedFormats(importer.getSupportedFormats());
    jasperTemplate.setTemplateParameters(new ArrayList<>());

    if (importer.getTemplateParameters() != null) {
      importer.getTemplateParameters().forEach(tp -> jasperTemplate.getTemplateParameters()
          .add(JasperTemplateParameter.newInstance(tp)));
    }

    return jasperTemplate;
  }

  /**
   * Copy values of attributes into new or updated Template.
   *
   * @param jasperTemplate Template with new values.
   */
  public void updateFrom(JasperTemplate jasperTemplate) {
    this.name = jasperTemplate.getName();
    this.data = jasperTemplate.getData();
    this.templateParameters = jasperTemplate.getTemplateParameters();
    this.type = jasperTemplate.getType();
    this.description = jasperTemplate.getDescription();
    this.isDisplayed = jasperTemplate.getIsDisplayed();
  }

  @PrePersist
  @PreUpdate
  private void preSave() {
    forEachParameter(line -> line.setTemplate(this));
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setData(data);
    exporter.setDescription(description);
    exporter.setId(id);
    exporter.setName(name);
    exporter.setType(type);
    exporter.setIsDisplayed(isDisplayed);
    exporter.setSupportedFormats(supportedFormats);
  }

  private void forEachParameter(Consumer<JasperTemplateParameter> consumer) {
    Optional.ofNullable(templateParameters)
        .ifPresent(list -> list.forEach(consumer));
  }

  public interface Exporter {
    void setId(UUID id);

    void setName(String name);

    void setData(byte[] data);

    void setType(String type);

    void setDescription(String description);

    void setIsDisplayed(Boolean isDisplayed);

    void setSupportedFormats(List<String> formats);
  }

  public interface Importer {
    UUID getId();

    String getName();

    byte[] getData();

    String getType();

    String getDescription();

    List<JasperTemplateParameter.Importer> getTemplateParameters();

    Boolean getIsDisplayed();

    List<String> getSupportedFormats();
  }
}
