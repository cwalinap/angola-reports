package org.openlmis.ao.reports.service;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openlmis.ao.reports.dto.external.DetailedRoleAssignmentDto;
import org.openlmis.ao.reports.dto.external.FacilityDto;
import org.openlmis.ao.reports.dto.external.UserContactDetailsDto;
import org.openlmis.ao.reports.dto.external.UserDto;
import org.openlmis.ao.reports.dto.external.UserInfoDto;
import org.openlmis.ao.reports.service.referencedata.FacilityReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.ProgramReferenceDataService;
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

@Service
public class UsersReportViewService implements ConcreteReportView {

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  @Autowired
  private UserContactDetailsReferenceDataService userContactDetailsReferenceDataService;

  @Autowired
  private ProgramReferenceDataService programReferenceDataService;

  private static final String NO_DATA = "";
  private static final String ALL = "Todos";
  private static final String FACILITY_ID = "facilityId";

  @Override
  public ModelAndView getReportView(JasperReportsMultiFormatView jasperView,
                                    Map<String, Object> parameters) {
    List<UserDto> users = getFilteredUsers(parameters);

    String acceptedRole = parameters.get("roleName") == null
            ? "" : parameters.get("roleName").toString();
    String acceptedProgramId = parameters.get("programId") == null
            ? "" : parameters.get("programId").toString();
    Map<String, String> programsMap = getProgramsMap(acceptedProgramId);

    List<UserInfoDto> data = new ArrayList<>();
    users.forEach(user -> {
      //by default, no roles will be returned, so we have to fetch them manually
      Map<String, List<String>> rightAssignments =
              getRightAssignments(user, acceptedRole, acceptedProgramId);
      UserContactDetailsDto contactDetails = getContactDetails(user);
      String facility = getFacilityName(user);

      if (CollectionUtils.isNotEmpty(rightAssignments.entrySet())) {
        data.addAll(generateUserInfo(user, programsMap, rightAssignments,
                contactDetails, facility));
      } else if (acceptedRole.isEmpty()) {
        data.add(new UserInfoDto(user, facility, NO_DATA, contactDetails, NO_DATA));
      }
    });

    String program = programsMap.get(acceptedProgramId) == null
            ? ALL : programsMap.get(acceptedProgramId);
    String node = parameters.get(FACILITY_ID) == null
            ? ALL : facilityReferenceDataService.findOne(
                    UUID.fromString(parameters.get(FACILITY_ID).toString())).getName();
    String role = acceptedRole.isEmpty() ? ALL : acceptedRole;
    parameters.put(DATASOURCE, new JRBeanCollectionDataSource(data));
    parameters.put("node", node);
    parameters.put("program", program);
    parameters.put("role", role);
    return new ModelAndView(jasperView, parameters);
  }

  private List<UserInfoDto> generateUserInfo(UserDto user, Map<String, String> programsMap,
                                             Map<String, List<String>> rightAssignments,
                                             UserContactDetailsDto contactDetails,
                                             String facility) {
    List<UserInfoDto> result = new ArrayList<>();
    rightAssignments.forEach((programId, roles) -> {
      String program = "";
      if (StringUtils.isNotEmpty(programId)) {
        program = programsMap.get(programId) == null
                ? NO_DATA : programsMap.get(programId);
      }
      result.add(new UserInfoDto(user, facility, program,
              contactDetails, buildRightAccessInfo(program, roles)));
    });
    return result;
  }

  private List<UserDto> getFilteredUsers(Map<String, Object> parameters) {
    String nodeId = parameters.get(FACILITY_ID) == null
            ? "" : parameters.get(FACILITY_ID).toString();
    String username = parameters.get("username") == null
            ? "" : parameters.get("username").toString();

    List<UserDto> users = userReferenceDataService.search(nodeId, username);

    String firstName = parameters.get("firstName") == null
            ? "" : parameters.get("firstName").toString();
    String lastName = parameters.get("lastName") == null
            ? "" : parameters.get("lastName").toString();

    return users.stream().filter(user ->
            user.getFirstName().contains(firstName) && user.getLastName().contains(lastName))
            .collect(Collectors.toList());
  }

  private Map<String, String> getProgramsMap(String acceptedProgramId) {
    Map<String, String> result = new HashMap<>();

    programReferenceDataService.findAll().stream()
            .filter(p -> p.getId().toString().contains(acceptedProgramId))
            .forEach(program -> result.put(program.getId().toString(), program.getName()));

    return result;
  }

  private Map<String, List<String>> getRightAssignments(UserDto user, String acceptedRole,
                                                      String acceptedProgramId) {
    Map<String, List<String>> result = new HashMap<>();

    List<DetailedRoleAssignmentDto> roles = userReferenceDataService
            .getUserRightsAndRoles(user.getId());

    roles.forEach(assignment -> {
      String programId = assignment.getProgramId() == null
              ? "" : assignment.getProgramId().toString();
      if (assignment.getRole().getName().contains(acceptedRole)
          && programId.contains(acceptedProgramId)) {
        if (result.containsKey(programId)) {
          result.get(programId).add(assignment.getRole().getName());
        } else {
          result.put(programId, new ArrayList<>());
          result.get(programId).add(assignment.getRole().getName());
        }
      }
    });

    return result;
  }

  private UserContactDetailsDto getContactDetails(UserDto user) {
    return userContactDetailsReferenceDataService.findOne(user.getId());
  }

  private String getFacilityName(UserDto user) {
    FacilityDto facility = user.getHomeFacilityId() == null
            ? null : facilityReferenceDataService.findOne(user.getHomeFacilityId());
    return facility == null ? NO_DATA : facility.getName();
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
