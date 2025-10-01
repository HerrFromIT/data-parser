import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class JsonParserExample {
    public static void main(String[] args) {
        try {
            // JSON-Datei laden
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File("overpass_data.json"));
            
            // Elemente direkt auswählen (ähnlich wie in PHP)
            JsonNode elements = rootNode.get("elements");
            
            // Durch alle Elemente iterieren
            for (JsonNode element : elements) {
                // Lat/Lon direkt auswählen
                double lat = element.get("lat").asDouble();
                double lon = element.get("lon").asDouble();
                
                System.out.println("Latitude: " + lat);
                System.out.println("Longitude: " + lon);
                
                // Tags auswählen
                JsonNode tags = element.get("tags");
                if (tags != null && tags.has("name")) {
                    String name = tags.get("name").asText();
                    System.out.println("Name: " + name);
                }
                
                System.out.println("---");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 