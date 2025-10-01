import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class OverpassParser {
    
    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";
    
    /**
     * Sendet eine Anfrage an die Overpass API und gibt die JSON-Antwort als JsonNode zurück
     */
    public JsonNode fetchOverpassData(String query) throws IOException {
        URL url = new URL(OVERPASS_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // POST-Anfrage konfigurieren
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        
        // Query als POST-Daten senden
        String postData = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // Antwort lesen
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        
        connection.disconnect();
        
        // String zu JsonNode parsen
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.toString());
    }
    
    /**
     * Speichert JSON-Daten in eine Datei
     */
    public void saveToJsonFile(JsonNode jsonData, String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), jsonData);
    }
    
    /**
     * Interaktive Methode zum Eingeben eigener Queries
     */
    public void interactiveQuery() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Overpass API Query Parser");
        System.out.println("=========================");
        System.out.println("Geben Sie Ihre Overpass Query ein (beenden Sie mit 'END' in einer neuen Zeile):");
        
        StringBuilder queryBuilder = new StringBuilder();
        String line;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.trim().equals("END")) {
                break;
            }
            queryBuilder.append(line).append("\n");
        }
        
        String query = queryBuilder.toString().trim();
        if (!query.isEmpty()) {
            try {
                System.out.println("Sende Anfrage an Overpass API...");
                JsonNode jsonData = fetchOverpassData(query);
                
                System.out.print("Dateiname für JSON-Ausgabe (Standard: overpass_output.json): ");
                String filename = scanner.nextLine().trim();
                if (filename.isEmpty()) {
                    filename = "overpass_output.json";
                }
                
                saveToJsonFile(jsonData, filename);
                System.out.println("Daten erfolgreich in '" + filename + "' gespeichert!");
                
            } catch (Exception e) {
                System.err.println("Fehler: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
}

