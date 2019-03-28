package org.openlmis.ao.settings.service;

import static org.openlmis.ao.settings.i18n.ConfigurationSettingMessageKeys.ERROR_CONFIGURATION_SETTING_NOT_FOUND;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import org.openlmis.ao.reports.exception.NotFoundMessageException;
import org.openlmis.ao.settings.domain.ConfigurationSetting;
import org.openlmis.ao.settings.repository.ConfigurationSettingRepository;
import org.openlmis.ao.utils.Message;

@Service
@NoArgsConstructor
public class ConfigurationSettingService {

  @Autowired
  private ConfigurationSettingRepository configurationSettingRepository;

  /**
   * Return configuration setting with given key.
   *
   * @param key String value of key.
   * @return Configuration setting containing given key.
   * @throws NotFoundMessageException Exception saying that setting was not found.
   */
  public ConfigurationSetting getByKey(String key) {
    ConfigurationSetting setting = configurationSettingRepository.findOne(key);
    if (setting == null) {
      throw new NotFoundMessageException(
          new Message(ERROR_CONFIGURATION_SETTING_NOT_FOUND, key));
    }
    return setting;
  }

  /**
   * Return value for given key if possible.
   *
   * @param key String value indicates key.
   * @return String value of given key.
   */
  public String getStringValue(String key) {
    ConfigurationSetting configurationSetting = configurationSettingRepository.findOne(key);
    if (configurationSetting == null || configurationSetting.getValue() == null) {
      throw new NotFoundMessageException(new Message(ERROR_CONFIGURATION_SETTING_NOT_FOUND,
          key));
    }
    return configurationSetting.getValue();
  }

  /**
   * Return boolean value for given key.
   * If does not exist return false.
   *
   * @param key String value indicates key.
   * @return Boolean value of given key.
   */
  public Boolean getBoolValue(String key) {
    try {
      String value = getStringValue(key);
      return Boolean.parseBoolean(value);
    } catch (NotFoundMessageException exception) {
      return false;
    }
  }
}
