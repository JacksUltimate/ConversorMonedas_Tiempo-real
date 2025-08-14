\# README - Conversor de Monedas (Java)



Este proyecto es una aplicación de consola en Java que permite consultar tasas de cambio usando la API de ExchangeRate-API, realizar conversiones entre monedas y llevar un historial simple de las conversiones realizadas. En este documento explico, en primera persona y con lenguaje académico, la estructura del código y el propósito de cada función, así como los aspectos importantes que consideré al implementarlo.



---



\## Requisitos y dependencias



\- \*\*Java 11 o superior\*\* (se utiliza `java.net.http.HttpClient` introducido en Java 11).

\- \*\*Biblioteca Gson\*\* de Google para parseo de JSON.  

&nbsp; - Si uso Maven, agrego la dependencia.

&nbsp; - Si uso NetBeans con Ant, incluyo el `.jar` de Gson en las librerías del proyecto.

\- \*\*Conexión a internet\*\* para consultar la API: \[https://v6.exchangerate-api.com](https://v6.exchangerate-api.com)

\- \*\*Cuenta y API key\*\* de ExchangeRate-API (en el código la API key está en la constante `API\_KEY`).



---



\## Resumen de la aplicación



Implemento un menú de 4 opciones:



1\. \*\*Mostrar monedas filtradas\*\* (lista limitada de monedas de interés).

2\. \*\*Realizar conversión entre monedas\*\* (pido origen, destino y cantidad).

3\. \*\*Ver historial de conversiones\*\* (se registra cada resultado con fecha y hora).

4\. \*\*Salir del programa\*\*.



La lista de monedas filtradas que uso por defecto es:  

`ARS`, `BOB`, `BRL`, `CLP`, `COP`, `USD`.



El historial se mantiene en memoria en la lista `historial` durante la ejecución.



---



\## Constantes y campos importantes



\- \*\*`API\_KEY`\*\*: Mi clave para la API. Actualmente está en el código como constante; recomiendo moverla a un archivo de configuración o variable de entorno para mayor seguridad.

\- \*\*`MONEDAS\_DESEADAS`\*\*: Lista con las abreviaturas de las monedas que deseo mostrar/validar.

\- \*\*`historial`\*\*: Lista que almacena cadenas con el formato `\[YYYY-MM-DD HH:MM:SS] <resultado>`.  

&nbsp; Esta lista existe en memoria y no se persiste en disco en la versión actual.



---



\## Explicación detallada de cada función



\### `private static List<List<String>> obtenerMonedas()`

\*\*Propósito:\*\*  

Consultar el endpoint `/codes` de ExchangeRate-API y devolver la estructura `supported\_codes` que contiene pares `\[código, nombre]` para cada moneda.



\*\*Parámetros:\*\*  

Ninguno (usa `API\_KEY` desde la clase).



\*\*Retorno:\*\*  

`List<List<String>>` donde cada sublista contiene al menos dos elementos:  

el código (ej. `"USD"`) y el nombre (ej. `"United States Dollar"`). Retorna `null` si ocurre un error.



\*\*Detalles importantes:\*\*

\- Uso `HttpClient` y `HttpRequest` para hacer la petición HTTP.

\- Uso `Gson` y `TypeToken` para parsear el JSON genérico.

\- Capturo `IOException` e `InterruptedException` y retorno `null` si falla.



---



\### `private static void mostrarMonedasFiltradas(List<List<String>> monedas)`

\*\*Propósito:\*\*  

Imprimir por consola las monedas definidas en `MONEDAS\_DESEADAS`, mostrando el nombre completo y su código.



\*\*Parámetros:\*\*  

\- `monedas`: la lista obtenida por `obtenerMonedas()`.



\*\*Retorno:\*\*  

`void`.



\*\*Detalles importantes:\*\*

\- Uso `stream()` para filtrar y ordenar por código.

\- Imprimo en formato:  

&nbsp; `Nombre - CODIGO`.



---



\### `private static String pedirMonedaValida(String mensaje, List<List<String>> monedas, Scanner sc)`

\*\*Propósito:\*\*  

Pedir al usuario un código de moneda y validar que corresponda a una de las monedas filtradas (`MONEDAS\_DESEADAS`).  

Repite hasta recibir un código válido.



\*\*Parámetros:\*\*

\- `mensaje`: texto mostrado al usuario.

\- `monedas`: lista completa de monedas para validación.

\- `sc`: Scanner para entrada.



\*\*Retorno:\*\*  

Código válido en mayúsculas (`String`).



\*\*Detalles importantes:\*\*

\- Bucle `while(true)` hasta recibir un código válido.

\- Validación restringida a `MONEDAS\_DESEADAS`.

\- Muestra lista filtrada cuando hay error.



---



\### `private static void realizarConversion(List<List<String>> monedas, Scanner sc)`

\*\*Propósito:\*\*  

Gestionar el flujo para realizar una conversión:  

pedir origen, destino, cantidad → consultar API → mostrar resultado → guardar en historial.



\*\*Parámetros:\*\*

\- `monedas`: lista para validación.

\- `sc`: Scanner para entrada.



\*\*Retorno:\*\*  

`void`.



\*\*Flujo:\*\*

1\. Obtengo monedas válidas con `pedirMonedaValida`.

2\. Pido cantidad validando formato numérico.

3\. Consulto API `/pair/{origen}/{destino}/{cantidad}`.

4\. Parseo y muestro resultado.

5\. Guardo en `historial` con timestamp.



---



\### `private static void mostrarHistorial()`

\*\*Propósito:\*\*  

Mostrar el historial de conversiones registradas.



\*\*Parámetros:\*\*  

Ninguno.



\*\*Retorno:\*\*  

`void`.



\*\*Detalles:\*\*

\- Si está vacío, aviso al usuario.

\- Muestra entradas en orden de registro.



---



\### `public static void main(String\[] args)`

\*\*Propósito:\*\*  

Punto de entrada; carga monedas, muestra menú y ejecuta funciones.



\*\*Flujo:\*\*

1\. Inicializo `Scanner`.

2\. Llamo a `obtenerMonedas()`.

3\. Muestro menú repetitivo.

4\. Ejecuto opción seleccionada.



---



