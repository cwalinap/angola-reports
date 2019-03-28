package org.openlmis.ao.security;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.beans.factory.annotation.Autowired;

import org.openlmis.ao.reports.dto.external.UserDto;
import org.openlmis.ao.utils.AuthenticationHelper;

/**
 * This class is used by JaVers to retrieve the name of the user currently logged in.
 * JaVers then associates audited changes being made with this particular user.
 */
public class UserNameProvider implements AuthorProvider {

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Override
  public String provide() {
    try {
      UserDto currentUser = authenticationHelper.getCurrentUser();
      if (currentUser != null && currentUser.getId() != null) {
        return currentUser.getId().toString();
      } else {
        return "unauthenticated user";
      }
    } catch (Exception ex) {
      return "unknown user";
    }
  }
}
