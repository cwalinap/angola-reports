package org.openlmis.ao.utils;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;
import org.junit.Test;

public class RequestHelperTest {

  private static final String URL = "http://localhost";

  @Test
  public void shouldCreateUriWithoutParameters() throws Exception {
    URI uri = RequestHelper.createUri(URL, RequestParameters.init());
    assertThat(uri.getQuery(), is(nullValue()));
  }

  @Test
  public void shouldCreateUriWithParameters() throws Exception {
    URI uri = RequestHelper.createUri(URL, RequestParameters.init().set("a", "b"));
    assertThat(uri.getQuery(), is("a=b"));
  }

  @Test
  public void shouldCreateUriWithIncorrectParameters() throws Exception {
    URI uri = RequestHelper.createUri(URL, RequestParameters.init().set("a", "b c"));
    assertThat(uri.getQuery(), is("a=b c"));
  }
}
