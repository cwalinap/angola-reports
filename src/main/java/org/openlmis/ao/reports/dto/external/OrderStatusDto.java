package org.openlmis.ao.reports.dto.external;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum OrderStatusDto {
  ORDERED,
  FULFILLING,
  SHIPPED,
  RECEIVED,
  TRANSFER_FAILED,
  IN_ROUTE,
  READY_TO_PACK;

  private static final Map<OrderStatusDto, String> TRANSLATIONS =
      Collections.unmodifiableMap(new HashMap<OrderStatusDto, String>() {{
          put(ORDERED, "Pediu");
          put(FULFILLING, "A executar pedido");
          put(SHIPPED, "Enviado");
          put(RECEIVED, "Recebido");
          put(TRANSFER_FAILED, "Transferência sem êxito");
          put(IN_ROUTE, "A Caminho");
          put(READY_TO_PACK, "Pronto para embalar");
        }
      });

  /**
   * Find a correct {@link OrderStatusDto} instance based on the passed string. The method ignores
   * the case.
   *
   * @param arg string representation of one of order status.
   * @return instance of {@link OrderStatusDto} if the given string matches status; otherwise null.
   */
  public static OrderStatusDto fromString(String arg) {
    for (OrderStatusDto status : values()) {
      if (equalsIgnoreCase(arg, status.name())) {
        return status;
      }
    }

    return null;
  }

  public String getTranslation() {
    return TRANSLATIONS.get(this);
  }

  public static String getTranslation(String status) {
    return Objects.requireNonNull(fromString(status)).getTranslation();
  }
}
