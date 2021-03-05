package org.openlmis.ao.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.openlmis.ao.reports.exception.EncodingException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public final class RequestHelper {

  private RequestHelper() {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a {@link URI} from the given string representation and with the given parameters.
   */
  public static URI createUri(String url, RequestParameters parameters) {
    UriComponentsBuilder builder = UriComponentsBuilder.newInstance().uri(URI.create(url));

    RequestParameters
            .init()
            .setAll(parameters)
            .forEach(e -> e.getValue().forEach(one -> {
              try {
                builder.queryParam(e.getKey(),
                        UriUtils.encodeQueryParam(String.valueOf(one),
                                StandardCharsets.UTF_8.name()));
              } catch (UnsupportedEncodingException ex) {
                throw new EncodingException(ex);
              }
            }
            ));

    return builder.build(true).toUri();
  }
}
