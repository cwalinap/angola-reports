package org.openlmis.ao.utils;

import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Extension of {@link ParameterizedTypeReference} from Spring that allows dynamically changing
 * the type it represents at runtime. Since generic hacks are generally ugly, so is this class.
 * It eases the usage of the rest template however, allowing easily retrieving objects with the
 * provided generic type at runtime.
 */
public abstract class BaseParameterizedTypeReference<T> extends ParameterizedTypeReference<T> {
  private final Class<?> valueType;

  /**
   * Constructs an instance that will represents wrappers for the given type.
   *
   * @param valueType the value type (generic type)
   */
  public BaseParameterizedTypeReference(Class<?> valueType) {
    this.valueType = valueType;
  }

  protected abstract Type getBaseType();

  @Override
  public Type getType() {
    Type[] responseWrapperActualTypes = {valueType};

    return new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
        return responseWrapperActualTypes;
      }

      @Override
      public Type getRawType() {
        return getBaseType();
      }

      @Override
      public Type getOwnerType() {
        return null;
      }
    };
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof BaseParameterizedTypeReference)) {
      return false;
    }

    BaseParameterizedTypeReference dptr = (BaseParameterizedTypeReference) other;

    return getType().equals(dptr.getType());
  }

  @Override
  public int hashCode() {
    return getType().hashCode();
  }
}
