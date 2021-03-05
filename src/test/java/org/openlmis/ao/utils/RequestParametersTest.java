package org.openlmis.ao.utils;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@SuppressWarnings("PMD.TooManyMethods")
public class RequestParametersTest {
  private static final String KEY = "key";
  private static final String VALUE = "value";

  private static final Pageable PAGE_WITHOUT_SORT = new PageRequest(0, 10);
  private static final Pageable PAGE_WITH_SORT = new PageRequest(1, 15, Direction.DESC, "test");

  @Test
  public void shouldConstructFromMap() {
    HashMap<String, Object> map = new HashMap<>();
    map.put(KEY, VALUE);

    RequestParameters requestParameters = RequestParameters.of(map);
    assertHasEntry(toMap(requestParameters), KEY, VALUE);
  }

  @Test
  public void shouldSetParameter() {
    RequestParameters params = RequestParameters.init().set(KEY, VALUE);
    assertHasEntry(toMap(params), KEY, VALUE);
  }

  @Test
  public void shouldNotSetParametersValueCollectionIsNull() {
    RequestParameters params = RequestParameters.init().set(KEY, null);
    assertThat(toMap(params), not(hasKey(KEY)));
  }

  @Test
  public void shouldNotSetParametersValueIsNull() {
    RequestParameters params = RequestParameters.init().set(KEY, (Object) null);
    assertThat(toMap(params), not(hasKey(KEY)));
  }

  @Test
  public void shouldNotSetPageIfValueIsNull() {
    RequestParameters params = RequestParameters.init().setPage(null);
    assertThat(toMap(params), not(hasKey(RequestParameters.PAGE)));
    assertThat(toMap(params), not(hasKey(RequestParameters.SIZE)));
    assertThat(toMap(params), not(hasKey(RequestParameters.SORT)));
  }

  @Test
  public void shouldSetPageWithoutSort() {
    RequestParameters params = RequestParameters.init().setPage(PAGE_WITHOUT_SORT);
    assertHasEntry(toMap(params), RequestParameters.PAGE, PAGE_WITHOUT_SORT.getPageNumber());
    assertHasEntry(toMap(params), RequestParameters.SIZE, PAGE_WITHOUT_SORT.getPageSize());
    assertThat(toMap(params), not(hasKey(RequestParameters.SORT)));
  }

  @Test
  public void shouldSetPageIfSortIsPresent() {
    RequestParameters params = RequestParameters.init().setPage(PAGE_WITH_SORT);
    assertHasEntry(toMap(params), RequestParameters.PAGE, PAGE_WITH_SORT.getPageNumber());
    assertHasEntry(toMap(params), RequestParameters.SIZE, PAGE_WITH_SORT.getPageSize());
    assertHasEntry(toMap(params), RequestParameters.SORT, "test," + Direction.DESC);
  }

  @Test
  public void shouldSetAllParametersFromOtherInstance() {
    RequestParameters parent = RequestParameters.init().set(KEY, VALUE);
    RequestParameters params = RequestParameters.init().setAll(parent);

    assertHasEntry(toMap(params), KEY, VALUE);
  }

  @Test
  public void shouldHandleNullValueWhenTryToSetAllParametersFromOtherInstance() {
    RequestParameters params = RequestParameters.init().setAll(null);

    assertThat(toMap(params).entrySet(), hasSize(0));
  }

  private void assertHasEntry(Map<String, List<String>> map, String key, Object value) {
    assertThat(map, hasEntry(key, Collections.singletonList(String.valueOf(value))));
  }

  private Map<String, List<String>> toMap(RequestParameters parameters) {
    Map<String, List<String>> map = Maps.newHashMap();
    parameters.forEach(e -> map.put(e.getKey(), e.getValue()));

    return map;
  }
}