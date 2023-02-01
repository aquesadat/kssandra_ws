# kssandra_task

## Descripción
Aplicación Java basada en SpringBoot que proporciona una API REST para consultar información relevatante para la inversión en criptodivisas como: datos actuales de cotización, predicciones a corto plazo, sugerencias y simulaciones de inversión.
La información devuelta por los servicios tiene como origen los datos generados en el módulo [kassandra_task](https://github.com/aquesadat/kssandra_task "kassandra_task")<br>
Los servicios están securizados con [Keycloak](https://www.keycloak.org/ "keycloak") por lo que será necesario obtener previamente un access token para su uso.

## Servicios expuestos

- **Obtención de access token**<br>
Necesario para invocar al resto de endpoints puesto que a través de este se realiza la autenticación y autorización usuario.<br>
**URL**: /auth/realms/KsdServiceRealm/protocol/openid-connect/token <br><br>

- **Obtención de datos actuales**<br>
Devuelve los datos actuales de cotización para una criptodivisa en las últimas 24h en orden cronológico.<br>Requiere especificar access token de manera obligatoria.<br>
**URL**: /api/v1/intraday/data (POST)
  ##### Parámetros request
	- cxCurr: Código de la cryptomoneda. Ejemplo: "BTC"
	- exCurr: Código de la divisa para la conversión. Ejemplo: "EUR"
	- interval: Intervalo de tiempo entre resultados (M15: 15min, M60: 60min)
	- extended: Muestra información extendida en la respuesta. (true/false)
  
  ##### Parámetros response
	- cxCurr: Código de la cryptomoneda.
	- exCurr: Código de la divisa para la conversión.
	- items: Listado de elementos.
		- dateTime: Fecha y hora de la cotización.
		- open: Valor apertura. Solo si extended=true
		- close: Valor cierre. Solo si extended=true
		- high: Valor máximo. Solo si extended=true
		- low: Valor mínimo. Solo si extended=true
		- avg: Valor medio. Solo si extended=false
    <br>

- **Obtención de predicciones de precios**<br>
Devuelve predicciones de precios de una criptodivisa para las próximas 24h en order cronológico.<br>Requiere especificar access token de manera obligatoria.<br>
**URL**: /api/v1/intraday/prediction (POST)
  ##### Parámetros request
	- cxCurr: Código de la cryptomoneda. Ejemplo: "BTC"
	- exCurr: Código de la divisa para la conversión. Ejemplo: "EUR"
	- interval: Intervalo de tiempo entre resultados (M15: 15min, M60: 60min)
  ##### Parámetros response
	- cxCurr: Código de la cryptomoneda.
	- exCurr: Código de la divisa para la conversión.
	- items: Listado de elementos.
		- dateTime: Fecha y hora de la predicción.
		- expectedVal: Precio esperado.
		- success: Probabilidad en % de que se cumpla la predicción
    <br>

- **Simulación de precios a corto plazo**<br>
Simula una inversión concreta en una criptodivisa<br>Requiere especificar access token de manera obligatoria.<br>
**URL**: /api/v1/intraday/simulate (POST)
  ##### Parámetros request
	- cxCurr: Código de la cryptomoneda. Ejemplo: "BTC"
	- exCurr: Código de la divisa para la conversión. Ejemplo: "EUR"
	- interval: Intervalo de tiempo entre resultados (M15: 15min, M60: 60min)
	- amount: Cantidad de dinero a invertir
	- saleFee: Comisión de compra aplicada por el broker (Opcional)
	- purchaseFee: Comisión de venta aplicada por el broker (Opcional)
  ##### Parámetros response
	- cxCurr: Código de la cryptomoneda.
	- exCurr: Código de la divisa para la conversión.
	- items: Listado de elementos.
		- dateTime: Fecha y hora de la predicción.
		- expectedVal: Precio esperado.
		- success: Probabilidad en % de que se cumpla la predicción
		- profit: Beneficio o pérdida expresado en %
    <br>
