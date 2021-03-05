package org.openlmis.ao.reports.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

import org.openlmis.ao.reports.domain.JasperTemplate;

public interface JasperTemplateRepository
    extends PagingAndSortingRepository<JasperTemplate, UUID> {
  JasperTemplate findByName(@Param("name") String name);

  List<JasperTemplate> findByIsDisplayed(@Param("isDisplayed") boolean isDisplayed);
}
