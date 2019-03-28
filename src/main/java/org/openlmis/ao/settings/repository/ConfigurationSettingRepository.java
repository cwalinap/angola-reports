package org.openlmis.ao.settings.repository;

import org.openlmis.ao.reports.repository.ReferenceDataRepository;
import org.openlmis.ao.settings.domain.ConfigurationSetting;

public interface ConfigurationSettingRepository
    extends ReferenceDataRepository<ConfigurationSetting, String> {
}
