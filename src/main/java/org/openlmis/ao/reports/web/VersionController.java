package org.openlmis.ao.reports.web;

import org.openlmis.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used for displaying service's version.
 */
@RestController
public class VersionController {
  private static final Logger LOGGER = LoggerFactory.getLogger(VersionController.class);

  @RequestMapping("/reports")
  public Version display() {
    LOGGER.debug("Returning version");
    return new Version();
  }
}