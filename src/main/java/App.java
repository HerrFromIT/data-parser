import java.io.*;
import com.fasterxml.jackson.databind.JsonNode;

public class App {
    public static void main(String[] args) {
        OverpassParser parser = new OverpassParser();
        
        // Beispiel-Query: Alle Restaurants in Berlin
        String query = """
            [out:json][timeout:25];
            area["name"="Berlin"]["admin_level"="4"]->.berlin;
            (
              node["amenity"="restaurant"](area.berlin);
              way["amenity"="restaurant"](area.berlin);
              relation["amenity"="restaurant"](area.berlin);
            );
            out body;
            >;
            out skel qt;
            """;
        
        try {
            // Jetzt bekommen wir ein JsonNode-Objekt zurück!
            JsonNode jsonData = parser.fetchOverpassData(query);

            // Überprüfen, ob die Antwort gültige JSON-Daten enthält
            if (jsonData.has("elements")) {
                System.out.println("Daten erfolgreich abgerufen!");
                
                // Jetzt können wir direkt mit JSON arbeiten!
                JsonNode elements = jsonData.get("elements");
                System.out.println("Anzahl gefundener Elemente: " + elements.size());
                
                // Durch alle Elemente iterieren
                for (JsonNode element : elements) {
                    // Prüfen ob das Element "lat" hat
                    if (element.has("lat")) {
                        double lat = element.get("lat").asDouble();
                        System.out.println("Lat: " + lat);
                        
                        // Auch andere Felder können wir abrufen
                        if (element.has("lon")) {
                            double lon = element.get("lon").asDouble();
                            System.out.println("  Lon: " + lon);
                        }
                        
                        if (element.has("id")) {
                            long id = element.get("id").asLong();
                            System.out.println("  ID: " + id);
                        }
                        
                        System.out.println("---");
                    }
                }
                
                // Optional: Daten in Datei speichern
                // parser.saveToJsonFile(jsonData, "overpass_data.json");
                System.out.println("Daten erfolgreich in 'overpass_data.json' gespeichert!");
            } else {
                System.out.println("Keine gültigen Daten gefunden");
                // System.out.println("Antwort: " + jsonData.toString());
            }
        } catch (Exception e) {
            // System.err.println("Fehler beim Abrufen der Daten: " + e.getMessage());
            e.printStackTrace();
        }
    }
}