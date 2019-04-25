package org.openlmis.ao.reports.dto.external;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
}
