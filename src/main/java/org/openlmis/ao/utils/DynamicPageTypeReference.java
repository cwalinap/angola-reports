package org.openlmis.ao.utils;

import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Type;

/**
 * Extension of {@link ParameterizedTypeReference} from Spring that allows dynamically changing
 * the type it represents at runtime. Since generic hacks are generally ugly, so is this class.
 * It eases the usage of the rest template however, allowing easily retrieving
 * {@link PageImplRepresentation} objects with the provided generic type at runtime.
 */
public class DynamicPageTypeReference<T>
    extends BaseParameterizedTypeReference<PageImplRepresentation<T>> {

  /**
   * Constructs an instance that will represents {@link PageImplRepresentation} wrappers for the
   * given type.
   *
   * @param valueType the value type (generic type) of the {@link PageImplRepresentation} type that
   *                  this will represent
   */
  public DynamicPageTypeReference(Class<?> valueType) {
    super(valueType);
  }

  @Override
  protected Type getBaseType() {
    return PageImplRepresentation.class;
  }

}
