package org.openlmis.ao.reports.web;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.openlmis.ao.reports.dto.external.RequisitionDto;
import org.openlmis.ao.reports.exception.JasperReportViewException;
import org.openlmis.ao.reports.exception.NotFoundMessageException;
import org.openlmis.ao.reports.i18n.MessageKeys;
import org.openlmis.ao.reports.service.JasperReportsViewService;
import org.openlmis.ao.reports.service.PermissionService;
import org.openlmis.ao.reports.service.requisition.RequisitionService;
import org.openlmis.ao.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Transactional
@RequestMapping("/api/reports")
public class ReportsController extends BaseController {

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private JasperReportsViewService jasperReportsViewService;

  @Autowired
  private RequisitionService requisitionService;

  /**
   * Print out requisition as a PDF file.
   *
   * @param id The UUID of the requisition to print
   * @return ResponseEntity with the "#200 OK" HTTP response status and PDF file on success, or
   *     ResponseEntity containing the error description status.
   */
  @RequestMapping(value = "/requisitions/{id}/print", method = RequestMethod.GET)
  @ResponseBody
  public ModelAndView print(HttpServletRequest request, @PathVariable("id") UUID id)
          throws JasperReportViewException {
    RequisitionDto requisition = requisitionService.findOne(id);

    if (requisition == null) {
      throw new NotFoundMessageException(
              new Message(MessageKeys.ERROR_REQUISITION_NOT_FOUND, id));
    }
    permissionService.canViewRequisition(requisition);

    return jasperReportsViewService.getRequisitionJasperReportView(requisition, request);
  }
}
