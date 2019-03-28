package org.openlmis.ao.reports.dto.external;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;
import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * This class is required to print date from status change using zone id,
 * which was not possible using {@link StatusChangeDto}.
 */
@AllArgsConstructor
public class PrintableZonedDateTime {

  private ZonedDateTime createdDate;

  /**
   * Print createdDate for display purposes.
   * @return created date
   */
  public String printDate(String zoneId) {
    Locale locale = LocaleContextHolder.getLocale();
    String datePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.MEDIUM, FormatStyle.MEDIUM, Chronology.ofLocale(locale), locale);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern)
        .withZone(ZoneId.of(zoneId));

    return dateTimeFormatter.format(createdDate);
  }
}
