package org.openlmis.ao.reports.service;

import org.openlmis.ao.reports.dto.external.DetailedRoleAssignmentDto;
import org.openlmis.ao.reports.dto.external.RequisitionDto;
import org.openlmis.ao.reports.dto.external.ResultDto;
import org.openlmis.ao.reports.dto.external.RightDto;
import org.openlmis.ao.reports.dto.external.UserDto;
import org.openlmis.ao.reports.exception.PermissionMessageException;
import org.openlmis.ao.reports.service.referencedata.UserReferenceDataService;
import org.openlmis.ao.utils.AuthenticationHelper;
import org.openlmis.ao.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.openlmis.ao.reports.i18n.PermissionMessageKeys.ERROR_NO_PERMISSION;

@Service
@SuppressWarnings("PMD.TooManyMethods")
public class PermissionService {
  public static final String REPORT_TEMPLATES_EDIT = "REPORT_TEMPLATES_EDIT";
  public static final String REPORTS_VIEW = "REPORTS_VIEW";
  public static final String ORDERS_VIEW = "ORDERS_VIEW";
  public static final String USERS_MANAGE = "USERS_MANAGE";
  public static final UUID ORDER_ID =
          UUID.fromString("9b8726b9-0de6-46eb-b5d0-d035d400a61e");
  public static final UUID USER_REPORT_TEMPLATE_ID =
          UUID.fromString("e1a2f89c-fa5e-40a6-bd1a-b43fdd570eb1");
  public static final String REQUISITION_VIEW = "REQUISITION_VIEW";

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  public void canEditReportTemplates() {
    checkPermission(REPORT_TEMPLATES_EDIT);
  }

  /**
   * Check whether the user has REPORTS_VIEW permission.
   * @param templateId (optional) id of the report; verify only REPORTS_VIEW permission if null.
   * @return true if can be viewed, false otherwise.
   */
  public boolean canViewReports(UUID templateId) {
    try {
      validatePermissionsToViewReports(templateId);
      return true;
    } catch (PermissionMessageException ex) {
      return false;
    }
  }

  /**
   * Check whether the user has REPORTS_VIEW permission, throws an exception otherwise.
   * @param templateId (optional) id of the report; verify only REPORTS_VIEW permission if null.
   */
  public void validatePermissionsToViewReports(UUID templateId) {
    checkPermission(REPORTS_VIEW);
    if (templateId == null) {
      return;
    }
    if (templateId.equals(ORDER_ID)) {
      canViewOrders();
    } else if (templateId.equals(USER_REPORT_TEMPLATE_ID)) {
      canMaganeUsers();
    }
  }

  /**
   * Checks if current user has permission to view a requisition.
   */
  public void canViewRequisition(RequisitionDto requisition) {
    checkPermission(REQUISITION_VIEW, requisition.getProgram().getId(),
            requisition.getFacility().getId(), null);
  }

  public void canViewOrders() {
    checkPermission(ORDERS_VIEW);
  }

  public void canMaganeUsers() {
    checkPermission(USERS_MANAGE);
  }

  private void checkPermission(String rightName) {
    if (!hasPermission(rightName)) {
      throw new PermissionMessageException(new Message(ERROR_NO_PERMISSION, rightName));
    }
  }

  private void checkPermission(String rightName, UUID program, UUID facility, UUID warehouse) {
    if (!hasPermission(rightName, program, facility, warehouse)) {
      throw new PermissionMessageException(new Message(ERROR_NO_PERMISSION, rightName));
    }
  }

  private void checkAnyPermission(List<String> rightNames) {
    if (rightNames.stream().noneMatch(this::hasPermission)) {
      throw new PermissionMessageException(new Message(ERROR_NO_PERMISSION, rightNames));
    }
  }

  private Boolean hasPermission(String rightName) {
    if (ORDERS_VIEW.equals(rightName)) {
      return hasFulfillmentPermission(rightName);
    }
    UserDto user = authenticationHelper.getCurrentUser();
    RightDto right = authenticationHelper.getRight(rightName);
    ResultDto<Boolean> result = userReferenceDataService.hasRight(user.getId(), right.getId());
    return null != result && result.getResult();
  }

  private Boolean hasPermission(String rightName, UUID program, UUID facility, UUID warehouse) {
    OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext()
            .getAuthentication();
    if (authentication.isClientOnly()) {
      return true;
    }
    UserDto user = authenticationHelper.getCurrentUser();
    RightDto right = authenticationHelper.getRight(rightName);
    ResultDto<Boolean> result = userReferenceDataService.hasRight(
            user.getId(), right.getId(), program, facility, warehouse
    );
    return null != result && result.getResult();
  }

  // Check if a user has fulfillment permission without specifying the warehouse
  private Boolean hasFulfillmentPermission(String rightName) {
    UserDto user = authenticationHelper.getCurrentUser();
    List<DetailedRoleAssignmentDto> roleAssignments =
            userReferenceDataService.getUserRightsAndRoles(user.getId());

    return roleAssignments.stream().anyMatch(
        assignment -> assignment.getRole().getRights().stream().anyMatch(
            right -> right.getName().equals(rightName)
    ));
  }
}
