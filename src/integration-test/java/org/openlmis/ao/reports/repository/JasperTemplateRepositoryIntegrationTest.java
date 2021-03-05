package org.openlmis.ao.reports.repository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.openlmis.ao.reports.domain.JasperTemplate;

public class JasperTemplateRepositoryIntegrationTest extends
    BaseCrudRepositoryIntegrationTest<JasperTemplate> {

  private static final String NAME = "TemplateRepositoryIntegrationTest";

  @Autowired
  private JasperTemplateRepository jasperTemplateRepository;

  @Override
  JasperTemplateRepository getRepository() {
    return this.jasperTemplateRepository;
  }

  @Override
  protected JasperTemplate generateInstance() {
    JasperTemplate jasperTemplate = new JasperTemplate();
    jasperTemplate.setName(NAME);
    return jasperTemplate;
  }

  @Test
  public void shouldFindTemplateByName() {
    jasperTemplateRepository.save(generateInstance());

    JasperTemplate found = jasperTemplateRepository.findByName(NAME);

    assertThat(found.getName(), is(NAME));
  }

  @Test
  public void shouldFindTemplateByDisplayed() {
    jasperTemplateRepository.save(generateInstance());

    JasperTemplate template = generateInstance();
    template.setName("Test");
    template.setIsDisplayed(false);
    jasperTemplateRepository.save(template);

    jasperTemplateRepository.findByIsDisplayed(true)
            .forEach(jt -> assertTrue(jt.getIsDisplayed()));
  }
}
