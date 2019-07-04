package org.openlmis.ao.reports.service;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import java.util.Map;

public interface ConcreteReportView {

  String DATASOURCE = "datasource";

  ModelAndView getReportView(JasperReportsMultiFormatView jasperView,
                             Map<String, Object> parameters);
}
