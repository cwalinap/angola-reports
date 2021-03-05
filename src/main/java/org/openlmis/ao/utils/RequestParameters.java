package org.openlmis.ao.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@EqualsAndHashCode
public final class RequestParameters {
  static final String PAGE = "page";
  static final String SIZE = "size";
  static final String SORT = "sort";

  private final MultiValueMap<String, String> params;

  private RequestParameters() {
    params = new LinkedMultiValueMap<>();
  }

  public static RequestParameters init() {
    return new RequestParameters();
  }

  /**
   * Constructs new RequestParameters based on Map with request parameters.
   */
  public static RequestParameters of(Map<String, Object> params) {
    RequestParameters requestParameters = new RequestParameters();
    params.forEach(requestParameters::set);
    return requestParameters;
  }

  /**
   * Set parameter (key argument) with the value only if the value is not null.
   */
  public RequestParameters set(String key, Collection<?> valueCollection) {
    Optional
            .ofNullable(valueCollection)
            .orElse(Collections.emptyList())
            .forEach(elem -> set(key, elem));

    return this;
  }

  /**
   * Set parameter (key argument) with the value only if the value is not null.
   */
  public RequestParameters set(String key, Object value) {
    if (null != value) {
      params.add(key, String.valueOf(value));
    }

    return this;
  }

  /**
   * Set parameters like page, size, sort only if the argument is not null.
   */
  public RequestParameters setPage(Pageable pageable) {
    if (null != pageable) {
      set(PAGE, pageable.getPageNumber());
      set(SIZE, pageable.getPageSize());

      if (pageable.getSort() != null) {
        Set<String> sort = StreamSupport
                .stream(pageable.getSort().spliterator(), false)
                .map(order -> String.format("%s,%s", order.getProperty(), order.getDirection()))
                .collect(Collectors.toSet());

        set(SORT, sort);
      }
    }

    return this;
  }

  /**
   * Copy parameters from the existing {@link RequestParameters}. If null value has been passed,
   * the method will return non changed instance.
   */
  public RequestParameters setAll(RequestParameters parameters) {
    if (null != parameters) {
      parameters.forEach(entry -> set(entry.getKey(), entry.getValue()));
    }

    return this;
  }

  public void forEach(Consumer<Map.Entry<String, List<String>>> action) {
    params.entrySet().forEach(action);
  }
}
