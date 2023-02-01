# kssandra_task

## Descripción
Aplicación Java basada en SpringBoot que proporciona una API REST para consultar distinta información de cotización de criptomonedas como: datos actuales, predicciones a corto plazo o simulaciones.
La información devuelta por los servicios tiene como origen los datos generados en el módulo [kassandra_task](https://github.com/aquesadat/kssandra_task "kassandra_task")<br>
Los servicios están securizados con [Keycloak](https://www.keycloak.org/ "keycloak") por lo que será necesario obtener previamente un access token para su uso.

## Servicios expuestos

- **Obtención de access token**<br>
Necesario para invocar al resto de endpoints puesto que a través de este se realiza la autenticación y autorización usuario.<br>
**URL**: /auth/realms/KsdServiceRealm/protocol/openid-connect/token

- **Obtención de datos actuales**<br>
Devuelve los datos actuales de cotización para una criptomoneda en las últimas 24h en orden cronológico.<br>Requiere especificar access token de manera obligatoria.<br>
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


