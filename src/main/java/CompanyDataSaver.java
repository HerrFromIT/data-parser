import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompanyDataSaver {
    
    public static void main(String[] args) {
        try {
            // JSON-Datei einlesen
            String jsonContent = new String(Files.readAllBytes(Paths.get("overpass_data.json")));
            if (jsonContent.isEmpty()) {
                System.out.println("JSON-Datei ist leer!");
                return;
            }
            
            // JSON parsen und OverpassData Objekte erstellen
            List<OverpassData> dataList = parseOverpassData(jsonContent);
            
            // Companies Verzeichnis erstellen
            Path companiesDir = Paths.get("companies");
            if (!Files.exists(companiesDir)) {
                Files.createDirectories(companiesDir);
                System.out.println("Verzeichnis 'companies' erstellt.");
            }
            
            // Alle Firmen in separate JSON-Dateien speichern
            int savedCount = 0;
            for (OverpassData data : dataList) {
                if (data.getName() != null && !data.getName().isEmpty()) {
                    saveCompanyToJson(data, companiesDir);
                    savedCount++;
                }
            }
            
            System.out.println("Gefundene Datensätze: " + dataList.size());
            System.out.println("Gespeicherte Firmen: " + savedCount);
            System.out.println("==========================================");
            
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der JSON-Datei: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static List<OverpassData> parseOverpassData(String jsonContent) {
        List<OverpassData> dataList = new ArrayList<>();
        
        // Teile den Inhalt in Zeilen auf
        String[] lines = jsonContent.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // Suche nach einem neuen Element (beginnt mit {)
            if (line.startsWith("{")) {
                OverpassData data = new OverpassData();
                boolean hasValidData = false;
                boolean inTags = false;
                
                // Analysiere dieses Element
                for (int j = i; j < lines.length; j++) {
                    String elementLine = lines[j].trim();
                    
                    // Wenn wir das Ende des Elements erreichen
                    if (elementLine.equals("},") || elementLine.equals("}")) {
                        break;
                    }
                    
                    // Prüfe ob wir in einem tags Objekt sind
                    if (elementLine.contains("\"tags\":")) {
                        inTags = true;
                        continue;
                    }
                    
                    // Wenn wir das Ende der tags erreichen
                    if (inTags && (elementLine.equals("},") || elementLine.equals("}"))) {
                        inTags = false;
                        continue;
                    }
                    
                    // Extrahiere lat (außerhalb von tags)
                    if (!inTags && elementLine.contains("\"lat\":")) {
                        String latStr = extractValueFromLine(elementLine, "lat");
                        if (!latStr.isEmpty()) {
                            try {
                                data.setLat(Double.parseDouble(latStr));
                                hasValidData = true;
                            } catch (NumberFormatException e) {
                                System.err.println("Fehler beim Parsen der Latitude: " + latStr);
                            }
                        }
                    }
                    
                    // Extrahiere lon (außerhalb von tags)
                    if (!inTags && elementLine.contains("\"lon\":")) {
                        String lonStr = extractValueFromLine(elementLine, "lon");
                        if (!lonStr.isEmpty()) {
                            try {
                                data.setLon(Double.parseDouble(lonStr));
                                hasValidData = true;
                            } catch (NumberFormatException e) {
                                System.err.println("Fehler beim Parsen der Longitude: " + lonStr);
                            }
                        }
                    }
                    
                    // Extrahiere Name (aus den tags)
                    if (inTags && elementLine.contains("\"name\":")) {
                        String name = extractValueFromLine(elementLine, "name");
                        if (!name.isEmpty()) {
                            data.setName(name);
                            hasValidData = true;
                        }
                    }
                    
                    // Extrahiere Adressinformationen (aus den tags)
                    if (inTags && elementLine.contains("\"addr:street\":")) {
                        String street = extractValueFromLine(elementLine, "addr:street");
                        data.setAddress(street);
                        hasValidData = true;
                    }
                    
                    if (inTags && elementLine.contains("\"addr:housenumber\":")) {
                        String housenumber = extractValueFromLine(elementLine, "addr:housenumber");
                        // Füge Hausnummer zur Adresse hinzu
                        if (!housenumber.isEmpty() && data.getAddress() != null) {
                            data.setAddress(data.getAddress() + " " + housenumber);
                        }
                    }
                    
                    if (inTags && elementLine.contains("\"addr:postcode\":")) {
                        String postcode = extractValueFromLine(elementLine, "addr:postcode");
                        data.setPostalCode(postcode);
                    }
                    
                    if (inTags && elementLine.contains("\"addr:city\":")) {
                        String city = extractValueFromLine(elementLine, "addr:city");
                        data.setCity(city);
                    }
                    
                    if (inTags && elementLine.contains("\"addr:country\":")) {
                        String country = extractValueFromLine(elementLine, "addr:country");
                        data.setCountry(country);
                    }
                    
                    if (inTags && elementLine.contains("\"phone\":")) {
                        String phone = extractValueFromLine(elementLine, "phone");
                        data.setPhone(phone);
                    }
                    
                    if (inTags && elementLine.contains("\"contact:website\":")) {
                        String website = extractValueFromLine(elementLine, "contact:website");
                        data.setWebsite(website);
                    }
                    
                    if (inTags && elementLine.contains("\"email\":")) {
                        String email = extractValueFromLine(elementLine, "email");
                        data.setEmail(email);
                    }
                }
                
                // Baue die vollständige Adresse
                if (data.getAddress() != null && !data.getAddress().isEmpty()) {
                    StringBuilder fullAddress = new StringBuilder(data.getAddress());
                    
                    if (data.getPostalCode() != null && !data.getPostalCode().isEmpty()) {
                        fullAddress.append(", ").append(data.getPostalCode());
                    }
                    if (data.getCity() != null && !data.getCity().isEmpty()) {
                        fullAddress.append(" ").append(data.getCity());
                    }
                    if (data.getCountry() != null && !data.getCountry().isEmpty()) {
                        fullAddress.append(", ").append(data.getCountry());
                    }
                    
                    data.setAddress(fullAddress.toString());
                }
                
                // Füge das Objekt hinzu, wenn es gültige Daten hat
                if (hasValidData) {
                    dataList.add(data);
                }
            }
        }
        
        return dataList;
    }
    
    private static void saveCompanyToJson(OverpassData data, Path companiesDir) throws IOException {
        // Erstelle einen sicheren Dateinamen basierend auf dem Firmennamen
        String fileName = createSafeFileName(data.getName());
        Path filePath = companiesDir.resolve(fileName + ".json");
        
        // Erstelle JSON-String
        String jsonContent = createJsonFromOverpassData(data);
        
        // Schreibe in Datei
        Files.write(filePath, jsonContent.getBytes());
        System.out.println("Gespeichert: " + filePath.toString());
    }
    
    private static String createSafeFileName(String companyName) {
        if (companyName == null || companyName.isEmpty()) {
            return "unknown_" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        // Entferne ungültige Zeichen für Dateinamen
        String safeName = companyName
            .replaceAll("[^a-zA-Z0-9äöüßÄÖÜ\\s-]", "_")
            .replaceAll("\\s+", "_")
            .trim();
        
        // Falls der Name leer ist, verwende UUID
        if (safeName.isEmpty()) {
            return "company_" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        return safeName;
    }
    
    private static String createJsonFromOverpassData(OverpassData data) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"name\": \"").append(escapeJsonString(data.getName())).append("\",\n");
        json.append("  \"latitude\": ").append(data.getLat()).append(",\n");
        json.append("  \"longitude\": ").append(data.getLon()).append(",\n");
        json.append("  \"address\": \"").append(escapeJsonString(data.getAddress())).append("\",\n");
        json.append("  \"city\": \"").append(escapeJsonString(data.getCity())).append("\",\n");
        json.append("  \"state\": \"").append(escapeJsonString(data.getState())).append("\",\n");
        json.append("  \"country\": \"").append(escapeJsonString(data.getCountry())).append("\",\n");
        json.append("  \"postalCode\": \"").append(escapeJsonString(data.getPostalCode())).append("\",\n");
        json.append("  \"phone\": \"").append(escapeJsonString(data.getPhone())).append("\",\n");
        json.append("  \"website\": \"").append(escapeJsonString(data.getWebsite())).append("\",\n");
        json.append("  \"email\": \"").append(escapeJsonString(data.getEmail())).append("\"\n");
        json.append("}");
        
        return json.toString();
    }
    
    private static String escapeJsonString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    private static String extractValueFromLine(String line, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = line.indexOf(searchKey);
        if (startIndex == -1) return "";
        
        startIndex += searchKey.length();
        while (startIndex < line.length() && Character.isWhitespace(line.charAt(startIndex))) {
            startIndex++;
        }
        
        if (startIndex >= line.length()) return "";
        
        char startChar = line.charAt(startIndex);
        if (startChar == '"') {
            // String-Wert
            startIndex++;
            int endIndex = line.indexOf('"', startIndex);
            if (endIndex == -1) return "";
            return line.substring(startIndex, endIndex);
        } else {
            // Numerischer Wert
            int endIndex = startIndex;
            while (endIndex < line.length() && 
                   (Character.isDigit(line.charAt(endIndex)) || 
                    line.charAt(endIndex) == '.' || 
                    line.charAt(endIndex) == '-')) {
                endIndex++;
            }
            return line.substring(startIndex, endIndex);
        }
    }
} 