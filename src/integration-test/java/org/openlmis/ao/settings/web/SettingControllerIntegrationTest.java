package org.openlmis.ao.settings.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.ao.settings.domain.ConfigurationSetting;
import org.openlmis.ao.settings.repository.ConfigurationSettingRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import guru.nidi.ramltester.junit.RamlMatchers;
import org.openlmis.ao.reports.web.BaseWebIntegrationTest;

public class SettingControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "api/settings";
  private static final String KEY_URL = RESOURCE_URL + "/{key}";
  private static final String KEY_PARAM = "key";

  @MockBean
  private ConfigurationSettingRepository configurationSettingRepository;

  // GET /api/settings/{key}

  @Before
  public void setUp() {
    mockUserAuthenticated();
  }

  @Test
  public void shouldReturnSettingWithExistingKey() {
    // given
    String key = "randomKey";
    String value = "randomValue";
    ConfigurationSetting setting = generateSetting(key, value);

    // when
    ConfigurationSetting result = restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam(KEY_PARAM, setting.getKey())
        .when()
        .get(KEY_URL)
        .then()
        .statusCode(200)
        .extract().as(ConfigurationSetting.class);

    // then
    assertEquals(key, result.getKey());
    assertEquals(value, result.getValue());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotReturnSettingWithNonExistentKey() {
    // given
    given(configurationSettingRepository.findOne(any(String.class))).willReturn(null);

    // when
    restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam(KEY_PARAM, "emptyKey")
        .when()
        .get(KEY_URL)
        .then()
        .statusCode(404);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // Helper methods

  private ConfigurationSetting generateSetting(String key, String value) {
    ConfigurationSetting setting = new ConfigurationSetting(key, value);

    given(configurationSettingRepository.findOne(key)).willReturn(setting);

    return setting;
  }
}
