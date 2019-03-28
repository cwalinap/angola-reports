package org.openlmis.ao.reports.web;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openlmis.ao.reports.domain.BaseEntity;
import org.openlmis.ao.reports.dto.external.UserDto;
import org.openlmis.ao.reports.exception.PermissionMessageException;
import org.openlmis.ao.reports.i18n.PermissionMessageKeys;
import org.openlmis.ao.reports.service.PermissionService;
import org.openlmis.ao.reports.web.utils.WireMockResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import javax.annotation.PostConstruct;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.restassured.RestAssuredClient;
import org.openlmis.ao.utils.AuthenticationHelper;
import org.openlmis.ao.utils.Message;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
public abstract class BaseWebIntegrationTest {
  protected static final String BASE_URL = System.getenv("BASE_URL");
  protected static final String CONTENT_TYPE = "Content-Type";
  protected static final String ACCESS_TOKEN = "access_token";
  protected static final String RAML_ASSERT_MESSAGE =
      "HTTP request/response should match RAML definition.";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(80);

  @MockBean
  protected AuthenticationHelper authenticationHelper;

  @MockBean
  protected PermissionService permissionService;

  protected RestAssuredClient restAssured;

  @Autowired
  private ObjectMapper objectMapper;

  @LocalServerPort
  private int serverPort;

  /**
   * Method called to initialize basic resources after the object is created.
   */
  @PostConstruct
  public void init() {
    mockExternalAuthorization();

    RestAssured.baseURI = BASE_URL;
    RestAssured.port = serverPort;
    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
        new ObjectMapperConfig().jackson2ObjectMapperFactory((clazz, charset) -> objectMapper)
    );

    RamlDefinition ramlDefinition = RamlLoaders.fromClasspath()
        .load("api-definition-raml.yaml").ignoringXheaders();
    restAssured = ramlDefinition.createRestAssured();
  }

  protected void mockUserAuthenticated() {
    UserDto user = new UserDto();
    user.setId(UUID.randomUUID());
    user.setFirstName("admin");
    user.setLastName("strator");
    user.setEmail("admin@openlmis.org");

    given(authenticationHelper.getCurrentUser()).willReturn(user);
  }

  protected PermissionMessageException mockPermissionException(String... deniedPermissions) {
    PermissionMessageException exception = mock(PermissionMessageException.class);

    Message errorMessage = new Message(
        PermissionMessageKeys.ERROR_NO_PERMISSION, (Object[])deniedPermissions);
    given(exception.asMessage()).willReturn(errorMessage);

    return exception;
  }

  protected UUID anyUuid() {
    return any(UUID.class);
  }

  protected String getToken() {
    return UUID.randomUUID().toString();
  }

  protected void mockExternalAuthorization() {
    // This mocks the auth check to always return valid admin credentials.
    wireMockRule.stubFor(post(urlEqualTo("/api/oauth/check_token"))
        .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(WireMockResponses.MOCK_CHECK_RESULT)));

    // This mocks the auth token request response
    wireMockRule.stubFor(post(urlPathEqualTo("/api/oauth/token?grant_type=client_credentials"))
        .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(WireMockResponses.MOCK_TOKEN_REQUEST_RESPONSE)));
  }

  protected static class SaveAnswer<T extends BaseEntity> implements Answer<T> {

    @Override
    public T answer(InvocationOnMock invocation) throws Throwable {
      T obj = (T) invocation.getArguments()[0];

      if (null == obj) {
        return null;
      }

      if (null == obj.getId()) {
        obj.setId(UUID.randomUUID());
      }

      extraSteps(obj);

      return obj;
    }

    void extraSteps(T obj) {
      // should be overridden if any extra steps are required.
    }

  }
}
