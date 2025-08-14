/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cmonedas;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Conversor {
    
    private static final String API_KEY = "null";/* por cuestiones de seguridad quite la llave y puse null, solo remplace el null por la llave(su llave) y todo queda full*/
    private static final List<String> MONEDAS_DESEADAS = Arrays.asList("ARS", "BOB", "BRL", "CLP", "COP", "USD");
    private static final List<String> historial = new ArrayList<>();
    
    private static List<List<String>> obtenerMonedas() {
        String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/codes";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> jsonMap = gson.fromJson(response.body(), type);
            return (List<List<String>>) jsonMap.get("supported_codes");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void mostrarMonedasFiltradas(List<List<String>> monedas) {
        monedas.stream()
                .filter(c -> MONEDAS_DESEADAS.contains(c.get(0)))
                .sorted(Comparator.comparing(c -> c.get(0)))
                .forEach(c -> System.out.println(c.get(1) + " - " + c.get(0)));
    }

private static String pedirMonedaValida(String mensaje, List<List<String>> monedas, Scanner sc) {
        
        while (true) {
            String moneda;
            System.out.print(mensaje);
            moneda = sc.nextLine().toUpperCase();

            boolean existe = monedas.stream()
                    .filter(c -> MONEDAS_DESEADAS.contains(c.get(0)))
                    .anyMatch(c -> c.get(0).equals(moneda));

            if (existe) {
                return moneda;
            } else {
                System.out.println("Moneda no encontrada. Monedas disponibles:");
                mostrarMonedasFiltradas(monedas);
            }
        }
    }


    private static void realizarConversion(List<List<String>> monedas, Scanner sc) {
        String origen = pedirMonedaValida("Moneda origen (ej: USD): ", monedas, sc);
        String destino = pedirMonedaValida("Moneda destino (ej: COP): ", monedas, sc);

        double cantidad;
        while (true) {
            try {
                System.out.print("Cantidad a convertir: ");
                cantidad = Double.parseDouble(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Cantidad no valida. Intente de nuevo.");
            }
        }

        String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/" + origen + "/" + destino + "/" + cantidad;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> jsonMap = gson.fromJson(response.body(), type);

            if ("success".equals(jsonMap.get("result"))) {
                double conversion = ((Number) jsonMap.get("conversion_result")).doubleValue();
                String resultado = cantidad + " " + origen + " = " + conversion + " " + destino;

                System.out.println(resultado);

                // Guardar en historial con fecha y hora
                String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                historial.add("[" + fecha + "] " + resultado);
            } else {
                System.out.println("Error en la conversion.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarHistorial() {
        if (historial.isEmpty()) {
            System.out.println("No hay conversiones registradas.");
        } else {
            System.out.println("\n===== HISTORIAL DE CONVERSIONES =====");
            historial.forEach(System.out::println);
        }
    }
 
   

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<List<String>> monedas = obtenerMonedas();

        if (monedas == null) {
            System.out.println("No se pudieron cargar las monedas.");
            return;
        }

        while (true) {
            System.out.println("\n===== CONVERSOR DE MONEDAS =====");
            System.out.println("1. Mostrar monedas filtradas");
            System.out.println("2. Realizar conversion");
            System.out.println("3. Salir");
            System.out.println("4. Ver historial");
            System.out.print("Seleccione una opcion: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    mostrarMonedasFiltradas(monedas);
                    break;
                case "2":
                    realizarConversion(monedas, sc);
                    break;
                case "3":
                    System.out.println("Saliendo del programa...");
                    return;
                case "4":
                    mostrarHistorial();
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

}