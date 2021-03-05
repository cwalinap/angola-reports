package org.openlmis.ao.utils;

import java.util.UUID;
import org.openlmis.ao.reports.dto.external.RightDto;
import org.openlmis.ao.reports.dto.external.UserDto;
import org.openlmis.ao.reports.exception.AuthenticationMessageException;
import org.openlmis.ao.reports.i18n.AuthorizationMessageKeys;
import org.openlmis.ao.reports.service.referencedata.RightReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.UserReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  @Autowired
  private RightReferenceDataService rightReferenceDataService;

  /**
   * Method returns current user based on Spring context
   * and fetches his data from reference-data service.
   *
   * @return UserDto entity of current user.
   * @throws AuthenticationMessageException if user cannot be found.
   */
  public UserDto getCurrentUser() {
    UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UserDto user = userReferenceDataService.findOne(userId);

    if (user == null) {
      throw new AuthenticationMessageException(new Message(
          AuthorizationMessageKeys.ERROR_USER_NOT_FOUND, userId));
    }

    return user;
  }

  /**
   * Method returns a correct right and fetches his data from reference-data service.
   *
   * @param name right name
   * @return RightDto entity of right.
   * @throws AuthenticationMessageException if right cannot be found.
   */
  public RightDto getRight(String name) {
    RightDto right = rightReferenceDataService.findRight(name);

    if (null == right) {
      throw new AuthenticationMessageException(new Message(
          AuthorizationMessageKeys.ERROR_RIGHT_NOT_FOUND, name));
    }

    return right;
  }
}
