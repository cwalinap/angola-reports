package org.openlmis.ao.reports.service.referencedata;

import org.openlmis.ao.reports.dto.external.UserContactDetailsDto;
import org.springframework.stereotype.Service;

@Service
public class UserContactDetailsReferenceDataService
        extends BaseReferenceDataService<UserContactDetailsDto> {

  @Override
  protected String getUrl() {
    return "/api/userContactDetails/";
  }

  @Override
  protected Class<UserContactDetailsDto> getResultClass() {
    return UserContactDetailsDto.class;
  }

  @Override
  protected Class<UserContactDetailsDto[]> getArrayResultClass() {
    return UserContactDetailsDto[].class;
  }
}
