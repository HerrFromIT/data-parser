import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GsonJsonParser {
    public static void main(String[] args) {
        try {
            // JSON-Datei einlesen
            String jsonContent = new String(Files.readAllBytes(Paths.get("overpass_data.json")));
            
            // JSON parsen
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            
            // Elemente direkt auswählen (ähnlich wie in PHP: overpass_data[elements])
            JsonArray elements = jsonObject.getAsJsonArray("elements");
            
            // Durch alle Elemente iterieren
            for (JsonElement element : elements) {
                JsonObject elementObj = element.getAsJsonObject();
                
                // Lat/Lon direkt auswählen
                double lat = elementObj.get("lat").getAsDouble();
                double lon = elementObj.get("lon").getAsDouble();
                
                System.out.println("Latitude: " + lat);
                System.out.println("Longitude: " + lon);
                
                // Tags auswählen
                if (elementObj.has("tags")) {
                    JsonObject tags = elementObj.getAsJsonObject("tags");
                    if (tags.has("name")) {
                        String name = tags.get("name").getAsString();
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