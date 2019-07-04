package org.openlmis.ao.reports.web;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRVirtualizationHelper;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import org.apache.log4j.Logger;
import org.openlmis.ao.reports.domain.JasperTemplate;
import org.openlmis.ao.reports.dto.JasperTemplateDto;
import org.openlmis.ao.reports.dto.external.UserDto;
import org.openlmis.ao.reports.exception.JasperReportViewException;
import org.openlmis.ao.reports.exception.NotFoundMessageException;
import org.openlmis.ao.reports.exception.ReportingException;
import org.openlmis.ao.reports.repository.JasperTemplateRepository;
import org.openlmis.ao.reports.service.JasperReportsViewService;
import org.openlmis.ao.reports.service.JasperTemplateService;
import org.openlmis.ao.reports.service.PermissionService;
import org.openlmis.ao.utils.AuthenticationHelper;
import org.openlmis.ao.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.openlmis.ao.reports.i18n.JasperMessageKeys.ERROR_JASPER_TEMPLATE_NOT_FOUND;
import static org.openlmis.ao.reports.web.ReportTypes.CONSISTENCY_REPORT;
import static org.openlmis.ao.reports.web.ReportTypes.INVENTORY_REPORT;
import static org.openlmis.ao.reports.web.ReportTypes.ORDER_REPORT;
import static org.openlmis.ao.reports.web.ReportTypes.PROOF_OF_DELIVERY_PRINT;
import static org.openlmis.ao.reports.web.ReportTypes.STOCK_CARD_REPORT;
import static org.openlmis.ao.reports.web.ReportTypes.STOCK_CARD_SUMMARY_REPORT;
import static org.openlmis.ao.reports.web.ReportTypes.USERS_REPORT;

@Controller
@Transactional
@RequestMapping("/api/reports/templates/angola")
public class JasperTemplateController extends BaseController {
  private static final Logger LOGGER = Logger.getLogger(JasperTemplateController.class);

  @Autowired
  private JasperTemplateService jasperTemplateService;

  @Autowired
  private JasperTemplateRepository jasperTemplateRepository;

  @Autowired
  private JasperReportsViewService jasperReportsViewService;

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private Clock clock;

  @Autowired
  private AuthenticationHelper authenticationHelper;

  /**
   * Adding report templates with ".jrxml" format to database.
   *
   * @param file        File in ".jrxml" format to upload
   * @param name        Name of file in database
   * @param description Description of the file
   */
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void createJasperReportTemplate(
      @RequestPart("file") MultipartFile file, String name, String description)
      throws ReportingException {
    permissionService.canEditReportTemplates();

    JasperTemplate jasperTemplateToUpdate = jasperTemplateRepository.findByName(name);
    if (jasperTemplateToUpdate == null) {
      LOGGER.debug("Creating new template");
      jasperTemplateToUpdate = new JasperTemplate(
          name, null, CONSISTENCY_REPORT, true, description, null, null);
      jasperTemplateService.validateFileAndInsertTemplate(jasperTemplateToUpdate, file);
    } else {
      LOGGER.debug("Template found, updating template");
      jasperTemplateToUpdate.setDescription(description);
      jasperTemplateService.validateFileAndSaveTemplate(jasperTemplateToUpdate, file);
    }

    LOGGER.debug("Saved template with id: " + jasperTemplateToUpdate.getId());
  }

  /**
   * Get all templates.
   *
   * @return Templates.
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<JasperTemplateDto> getAllTemplates() {
    permissionService.canViewReports(null);
    return JasperTemplateDto.newInstance(jasperTemplateRepository.findAll())
        .stream()
        // filter out templates that shouldn't be displayed
        .filter(JasperTemplateDto::getIsDisplayed)
        .collect(Collectors.toList());
  }

  /**
   * Get chosen template.
   *
   * @param templateId UUID of template which we want to get
   * @return Template.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public JasperTemplateDto getTemplate(@PathVariable("id") UUID templateId) {
    permissionService.canViewReports(templateId);
    JasperTemplate jasperTemplate =
        jasperTemplateRepository.findOne(templateId);
    if (jasperTemplate == null) {
      throw new NotFoundMessageException(new Message(
          ERROR_JASPER_TEMPLATE_NOT_FOUND, templateId));
    }

    return JasperTemplateDto.newInstance(jasperTemplate);
  }

  /**
   * Allows deleting template.
   *
   * @param templateId UUID of template which we want to delete
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTemplate(@PathVariable("id") UUID templateId) {
    permissionService.canEditReportTemplates();
    JasperTemplate jasperTemplate = jasperTemplateRepository.findOne(templateId);
    if (jasperTemplate == null) {
      throw new NotFoundMessageException(new Message(
          ERROR_JASPER_TEMPLATE_NOT_FOUND, templateId));
    } else {
      jasperTemplateRepository.delete(jasperTemplate);
    }
  }

  /**
   * Generate a report based on the template, the format and the request parameters.
   *
   * @param request    request (to get the request parameters)
   * @param templateId report template ID
   * @param format     report format to generate, default is PDF
   * @return the generated report
   */
  @RequestMapping(value = "/{id}/{format}", method = RequestMethod.GET)
  @ResponseBody
  public ModelAndView generateReport(HttpServletRequest request,
                                     @PathVariable("id") UUID templateId,
                                     @PathVariable("format") String format)
      throws JasperReportViewException {

    permissionService.canViewReports(templateId);

    JasperTemplate template = jasperTemplateRepository.findOne(templateId);

    if (template == null) {
      throw new NotFoundMessageException(new Message(
          ERROR_JASPER_TEMPLATE_NOT_FOUND, templateId));
    }

    String tmpdir = System.getProperty("java.io.tmpdir");
    JRVirtualizer virtualizer = new JRSwapFileVirtualizer(
        1000, new JRSwapFile(tmpdir, 4096, 200), true);
    JRVirtualizationHelper.setThreadVirtualizer(virtualizer);

    Map<String, Object> map = jasperTemplateService.mapRequestParametersToTemplate(
        request, template
    );
    String fileName = jasperReportsViewService.getFilename(template, map);
    map.put("format", format);
    map.put("imagesDirectory", "images/");
    map.put("timeZone", clock.getZone().getId());
    UserDto currentUser = authenticationHelper.getCurrentUser();
    map.put("user", currentUser.printName());

    map.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

    JasperReportsMultiFormatView jasperView =
        jasperReportsViewService.getJasperReportsView(template, request);

    String contentDisposition = "inline; filename=" + fileName + "." + format;

    jasperView
        .getContentDispositionMappings()
        .setProperty(format, contentDisposition);

    String templateType = template.getType();
    if (ORDER_REPORT.equals(templateType)) {
      return jasperReportsViewService.getOrderJasperReportView(jasperView, map);
    } else if (STOCK_CARD_REPORT.equals(templateType)) {
      return jasperReportsViewService.getStockCardReportView(jasperView, map);
    } else if (STOCK_CARD_SUMMARY_REPORT.equals(templateType)
            || INVENTORY_REPORT.equals(templateType)) {
      return jasperReportsViewService.getInventoryReportView(jasperView, map);
    } else if (PROOF_OF_DELIVERY_PRINT.equals(templateType)) {
      return jasperReportsViewService.getPodJasperReportView(jasperView, map);
    } else if (USERS_REPORT.equals(templateType)) {
      return jasperReportsViewService.getUsersReportView(jasperView, map);
    } else {
      return new ModelAndView(jasperView, map);
    }
  }

}
