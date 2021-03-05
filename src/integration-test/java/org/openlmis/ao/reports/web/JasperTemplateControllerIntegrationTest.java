package org.openlmis.ao.reports.web;

import guru.nidi.ramltester.junit.RamlMatchers;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.ao.reports.domain.JasperTemplate;
import org.openlmis.ao.reports.dto.JasperTemplateDto;
import org.openlmis.ao.reports.exception.JasperReportViewException;
import org.openlmis.ao.reports.repository.JasperTemplateRepository;
import org.openlmis.ao.reports.service.JasperReportsViewService;
import org.openlmis.ao.reports.service.PermissionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("PMD.TooManyMethods")
public class JasperTemplateControllerIntegrationTest extends BaseWebIntegrationTest {
  private static final String RESOURCE_URL = "/api/reports/templates/angola";
  private static final String ID_URL = RESOURCE_URL + "/{id}";
  private static final String FORMAT_PARAM = "format";
  private static final String REPORT_URL = ID_URL + "/{" + FORMAT_PARAM + "}";

  @MockBean
  private JasperTemplateRepository jasperTemplateRepository;

  @MockBean
  private JasperReportsViewService jasperReportsViewService;

  @MockBean
  private PermissionService permissionService;

  @Before
  public void setUp() {
    mockUserAuthenticated();
  }

  // GET /api/reports/templates

  @Test
  public void shouldGetAllTemplates() {
    // given
    JasperTemplate[] templates = { generateTemplate(), generateTemplate() };
    given(jasperTemplateRepository.findByIsDisplayed(true)).willReturn(Arrays.asList(templates));
    given(permissionService.canViewReports(any())).willReturn(true);

    // when
    JasperTemplateDto[] result = restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL)
        .then()
        .statusCode(200)
        .extract().as(JasperTemplateDto[].class);

    // then
    assertNotNull(result);
    assertEquals(2, result.length);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // DELETE /api/reports/templates

  @Test
  public void shouldDeleteExistentTemplate() {
    // given
    JasperTemplate template = generateTemplate();

    // when
    restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(204);

    // then
    verify(jasperTemplateRepository, atLeastOnce()).delete(eq(template));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotDeleteNonExistentTemplate() {
    // given
    given(jasperTemplateRepository.findOne(anyUuid())).willReturn(null);

    // when
    restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(404);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/templates/{id}

  @Test
  public void shouldGetExistentTemplate() {
    // given
    JasperTemplate template = generateTemplate();

    // when
    JasperTemplateDto result = restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(200)
        .extract().as(JasperTemplateDto.class);

    // then
    assertEquals(template.getId(), result.getId());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotGetNonExistentTemplate() {
    // given
    given(jasperTemplateRepository.findOne(anyUuid())).willReturn(null);

    // when
    restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(404);

    // them
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/templates/{id}/{format}

  @Test
  public void generateReportShouldReturnNotFoundWhenReportTemplateDoesNotExist() {
    // given
    given(jasperTemplateRepository.findOne(anyUuid())).willReturn(null);

    // when
    restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .pathParam(FORMAT_PARAM, "pdf")
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(404);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldGenerateReportInPdfFormat() throws JasperReportViewException {
    testGenerateReportInGivenFormat("application/pdf", "pdf");
  }

  @Test
  public void shouldGenerateReportInCsvFormat() throws JasperReportViewException {
    testGenerateReportInGivenFormat("application/csv", "csv");
  }

  @Test
  public void shouldGenerateReportInXlsFormat() throws JasperReportViewException {
    testGenerateReportInGivenFormat("application/xls", "xls");
  }

  @Test
  public void shouldGenerateReportInHtmlFormat() throws JasperReportViewException {
    testGenerateReportInGivenFormat("text/html", "html");
  }

  // Helper methods

  private void testGenerateReportInGivenFormat(String contentType, String formatParam)
      throws JasperReportViewException {
    // given
    JasperTemplate template = generateTemplate();

    JasperReportsMultiFormatView view = mock(JasperReportsMultiFormatView.class);
    given(view.getContentType()).willReturn(contentType);
    given(view.getContentDispositionMappings()).willReturn(mock(Properties.class));
    given(view.getContentDispositionMappings().getProperty("attachment.pdf")).willReturn("text");

    given(jasperTemplateRepository.findOne(template.getId())).willReturn(template);
    given(jasperReportsViewService
        .getJasperReportsView(any(JasperTemplate.class), any(HttpServletRequest.class)))
        .willReturn(view);

    // when
    restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .pathParam("id", template.getId())
        .pathParam(FORMAT_PARAM, formatParam)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(200);
  }

  private JasperTemplate generateTemplate() {
    return generateTemplate(true);
  }

  private JasperTemplate generateTemplate(Boolean isDisplayed) {
    UUID id = UUID.randomUUID();
    JasperTemplate template = new JasperTemplate();

    template.setId(id);
    template.setName("name");
    template.setIsDisplayed(isDisplayed);

    given(jasperTemplateRepository.findOne(id)).willReturn(template);

    return template;
  }
}
