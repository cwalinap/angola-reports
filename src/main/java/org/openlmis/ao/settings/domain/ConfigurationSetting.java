package org.openlmis.ao.settings.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuration_settings")
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationSetting {

  @Id
  @Getter
  @Setter
  private String key;

  @Column(nullable = false, columnDefinition = "text")
  @Getter
  @Setter
  private String value;
}
