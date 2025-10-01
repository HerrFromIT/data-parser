import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleJsonParser {
    public static void main(String[] args) {
        try {
            // JSON-Datei einlesen
            String jsonContent = new String(Files.readAllBytes(Paths.get("overpass_data.json")));
            JSONObject jsonObject = new JSONObject(jsonContent);
            
            // Elemente direkt auswählen (ähnlich wie in PHP: overpass_data[elements])
            JSONArray elements = jsonObject.getJSONArray("elements");
            
            // Durch alle Elemente iterieren
            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);
                
                // Lat/Lon direkt auswählen
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");
                
                System.out.println("Latitude: " + lat);
                System.out.println("Longitude: " + lon);
                
                // Tags auswählen
                if (element.has("tags")) {
                    JSONObject tags = element.getJSONObject("tags");
                    if (tags.has("name")) {
                        String name = tags.getString("name");
                        System.out.println("Name: " + name);
                    }
                }
                
                System.out.println("---");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 