package org.openlmis.ao.reports.service;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openlmis.ao.reports.dto.external.DetailedRoleAssignmentDto;
import org.openlmis.ao.reports.dto.external.RightAssignmentDto;
import org.openlmis.ao.reports.dto.external.SupervisoryNodeDto;
import org.openlmis.ao.reports.dto.external.UserContactDetailsDto;
import org.openlmis.ao.reports.dto.external.UserDto;
import org.openlmis.ao.reports.dto.external.UserInfoDto;
import org.openlmis.ao.reports.service.referencedata.ProgramReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.SupervisoryNodeReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.UserContactDetailsReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.UserReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.openlmis.ao.utils.ReportUtils.getStringParameter;

@Service
public class UsersReportViewService implements ConcreteReportView {

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  @Autowired
  private SupervisoryNodeReferenceDataService supervisoryNodeReferenceDataService;

  @Autowired
  private UserContactDetailsReferenceDataService userContactDetailsReferenceDataService;

  @Autowired
  private ProgramReferenceDataService programReferenceDataService;

  private static final String NO_DATA = "";
  private static final String ALL = "Todos";
  private static final String NODE_ID = "nodeId";
  private static final String ROLE_SEPARATOR = "+";

  @Override
  public ModelAndView getReportView(JasperReportsMultiFormatView jasperView,
                                    Map<String, Object> parameters) {
    List<UserDto> users = getFilteredUsers(parameters);

    String allowedRole = getStringParameter(parameters, "roleName");
    String allowedProgramId = getStringParameter(parameters, "programId");
    String allowedEmail = getStringParameter(parameters, "email");
    String allowedNodeId = getStringParameter(parameters, NODE_ID);
    Map<String, String> programsMap = getProgramsMap(allowedProgramId);

    List<UserInfoDto> data = new ArrayList<>();
    users.forEach(user -> {
      //by default, no roles will be returned, so we have to fetch them manually
      Map<String, RightAssignmentDto> rightAssignments =
              getRightAssignments(user, allowedRole, allowedProgramId, allowedNodeId);
      UserContactDetailsDto contactDetails = getContactDetails(user);

      if (isEmailAllowed(allowedEmail, contactDetails)) {
        if (CollectionUtils.isNotEmpty(rightAssignments.entrySet())) {
          data.addAll(generateUserInfo(user, programsMap, rightAssignments,
                  contactDetails));
        } else if (allowedRole.isEmpty()) {
          data.add(new UserInfoDto(user, NO_DATA, NO_DATA, contactDetails, NO_DATA));
        }
      }
    });

    String program = programsMap.get(allowedProgramId) == null
            ? ALL : programsMap.get(allowedProgramId);
    String node = parameters.get(NODE_ID) == null
            ? ALL : supervisoryNodeReferenceDataService.findOne(
                    UUID.fromString(parameters.get(NODE_ID).toString())).getName();
    String role = allowedRole.isEmpty() ? ALL : allowedRole;
    parameters.put(DATASOURCE, new JRBeanCollectionDataSource(data));
    parameters.put("users", data);
    parameters.put("node", node);
    parameters.put("program", program);
    parameters.put("role", role);

    return new ModelAndView(jasperView, parameters);
  }

  /**
   * Transform params to user-friendly form.
   *
   * @param parameters template parameters populated with values from the request
   * @return params used to generate filename with proper names instead of ids
   */
  public List<Object> getFilenameValues(Map<String, Object> parameters) {
    List<Object> result = new ArrayList<>();
    String programId = getStringParameter(parameters, "programId");
    String nodeId = getStringParameter(parameters, NODE_ID);

    if (StringUtils.isNotEmpty(nodeId)) {
      result.add(supervisoryNodeReferenceDataService
              .findOne(UUID.fromString(nodeId)).getName());
    }

    if (StringUtils.isNotEmpty(programId)) {
      result.add(programReferenceDataService
              .findOne(UUID.fromString(programId)).getName());
    }

    result.add(getStringParameter(parameters, "roleName"));
    result.add(getStringParameter(parameters, "firstName"));
    result.add(getStringParameter(parameters, "lastName"));
    result.add(getStringParameter(parameters, "username"));
    result.add(getStringParameter(parameters, "email"));
    return result.stream().filter(x -> StringUtils.isNotEmpty(x.toString()))
            .collect(Collectors.toList());
  }

  private boolean isEmailAllowed(String allowedEmail, UserContactDetailsDto contactDetails) {
    if (StringUtils.isBlank(allowedEmail)) {
      return true;
    } else {
      return contactDetails != null && contactDetails.getEmailDetails() != null
              && containsIgnoreCase(contactDetails.getEmailDetails().getEmail(), allowedEmail);
    }
  }

  private List<UserInfoDto> generateUserInfo(UserDto user, Map<String, String> programsMap,
                                             Map<String, RightAssignmentDto> rightAssignmentsMap,
                                             UserContactDetailsDto contactDetails) {
    List<UserInfoDto> result = new ArrayList<>();
    rightAssignmentsMap.forEach((key, right) -> {
      String programId = key.substring(0, key.indexOf(ROLE_SEPARATOR));
      String program = "";
      if (StringUtils.isNotEmpty(programId)) {
        program = programsMap.get(programId) == null
                ? NO_DATA : programsMap.get(programId);
      }
      String supervisoryNode = getSupervisoryNodeName(right.getSupervisoryNodeId());
      result.add(new UserInfoDto(user, supervisoryNode, program,
              contactDetails, buildRightAccessInfo(program, right.getRoles())));
    });

    result.sort(UserInfoDto::compareTo);
    return result;
  }

  private List<UserDto> getFilteredUsers(Map<String, Object> parameters) {
    String username = getStringParameter(parameters, "username");
    List<UserDto> users = userReferenceDataService.search(username);

    String firstName = getStringParameter(parameters, "firstName");
    String lastName = getStringParameter(parameters, "lastName");

    return users.stream().filter(user ->
            containsIgnoreCase(user.getFirstName(), firstName)
                    && containsIgnoreCase(user.getLastName(), lastName))
            .collect(Collectors.toList());
  }

  private Map<String, String> getProgramsMap(String allowedProgramId) {
    Map<String, String> result = new HashMap<>();

    programReferenceDataService.findAll().stream()
            .filter(p -> contains(p.getId().toString(), allowedProgramId))
            .forEach(program -> result.put(program.getId().toString(), program.getName()));

    return result;
  }

  private Map<String, RightAssignmentDto> getRightAssignments(UserDto user, String allowedRole,
                                                        String allowedProgramId,
                                                        String allowedNodeId) {
    Map<String, RightAssignmentDto> result = new HashMap<>();

    List<DetailedRoleAssignmentDto> roles = userReferenceDataService
            .getUserRightsAndRoles(user.getId());

    roles.forEach(assignment -> {
      String programId = assignment.getProgramId() == null
              ? "" : assignment.getProgramId().toString();
      String nodeId = assignment.getSupervisoryNodeId() == null
              ? "" : assignment.getSupervisoryNodeId().toString();
      String key = programId + ROLE_SEPARATOR + nodeId;
      if (containsIgnoreCase(assignment.getRole().getName(), allowedRole)
              && contains(programId, allowedProgramId)
              && contains(nodeId, allowedNodeId)) {
        if (result.containsKey(key)) {
          result.get(key).getRoles().add(assignment.getRole().getName());
        } else {
          result.put(key, new RightAssignmentDto(
                  new ArrayList<>(), assignment.getSupervisoryNodeId()));
          result.get(key).getRoles().add(assignment.getRole().getName());
        }
      }
    });
    return result;
  }

  private UserContactDetailsDto getContactDetails(UserDto user) {
    return userContactDetailsReferenceDataService.findOne(user.getId());
  }

  private String getSupervisoryNodeName(UUID supervisoryNodeId) {
    SupervisoryNodeDto supervisoryNode = supervisoryNodeId == null
            ? null : supervisoryNodeReferenceDataService.findOne(
                    supervisoryNodeId);
    return supervisoryNode == null ? NO_DATA : supervisoryNode.getName();
  }

  private String buildRightAccessInfo(String program, List<String> roles) {
    if (CollectionUtils.isEmpty(roles)) {
      return "";
    }

    StringBuilder result = new StringBuilder(program);
    if (StringUtils.isNotEmpty(program)) {
      result.append(": ");
    }

    for (String role : roles) {
      result.append(role).append(", ");
    }

    return result.toString().substring(0, result.toString().length() - 2);
  }
}
