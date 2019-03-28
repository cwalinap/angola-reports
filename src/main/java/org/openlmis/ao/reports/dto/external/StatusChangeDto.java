package org.openlmis.ao.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.i18n.LocaleContextHolder;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class StatusChangeDto implements Comparable<StatusChangeDto> {

  @Getter
  @Setter
  private RequisitionStatusDto status;

  @Getter
  @Setter
  private UUID authorId;

  @Getter
  @Setter
  private ZonedDateTime createdDate;

  @Setter
  private UserDto author;

  @JsonIgnore
  public UserDto getAuthor() {
    return author;
  }

  /**
   * Print createdDate for display purposes.
   * @return created date
   */
  @JsonIgnore
  public String printDate(String zoneId) {
    Locale locale = LocaleContextHolder.getLocale();
    String datePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            FormatStyle.MEDIUM, FormatStyle.MEDIUM, Chronology.ofLocale(locale), locale);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern)
        .withZone(ZoneId.of(zoneId));

    return dateTimeFormatter.format(createdDate);
  }

  @Override
  public int compareTo(StatusChangeDto statusChange) {
    return this.createdDate.compareTo(statusChange.createdDate);
  }

  @JsonIgnore
  public PrintableZonedDateTime getDate() {
    return new PrintableZonedDateTime(this.createdDate);
  }
}
