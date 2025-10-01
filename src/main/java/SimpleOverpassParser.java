import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SimpleOverpassParser {
    
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
            
            // Ergebnisse ausgeben (nur die gewünschten Felder)
            System.out.println("Gefundene Datensätze: " + dataList.size());
            System.out.println("==========================================");
            
            for (OverpassData data : dataList) {
                System.out.println("Name: " + data.getName());
                System.out.println("Latitude: " + data.getLat());
                System.out.println("Longitude: " + data.getLon());
                System.out.println("Adresse: " + data.getAddress());
                System.out.println("---");
            }
            
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
                
                // Analysiere dieses Element
                for (int j = i; j < lines.length; j++) {
                    String elementLine = lines[j].trim();
                    
                    // Wenn wir das Ende des Elements erreichen
                    if (elementLine.equals("},") || elementLine.equals("}")) {
                        break;
                    }
                    
                    // Extrahiere lat
                    if (elementLine.contains("\"lat\":")) {
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
                    
                    // Extrahiere lon
                    if (elementLine.contains("\"lon\":")) {
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
                    if (elementLine.contains("\"name\":")) {
                        String name = extractValueFromLine(elementLine, "name");
                        if (!name.isEmpty()) {
                            data.setName(name);
                            hasValidData = true;
                        }
                    }
                    
                    // Extrahiere Adressinformationen (aus den tags)
                    if (elementLine.contains("\"addr:street\":")) {
                        String street = extractValueFromLine(elementLine, "addr:street");
                        String housenumber = "";
                        String postcode = "";
                        String city = "";
                        String country = "";
                        
                        // Suche nach weiteren Adressinformationen in den nächsten Zeilen
                        for (int k = j; k < Math.min(j + 50, lines.length); k++) {
                            String addrLine = lines[k].trim();
                            
                            // Stoppe wenn wir das Ende der tags erreichen
                            if (addrLine.equals("},") || addrLine.equals("}")) {
                                break;
                            }
                            
                            if (addrLine.contains("\"addr:housenumber\":")) {
                                housenumber = extractValueFromLine(addrLine, "addr:housenumber");
                            } else if (addrLine.contains("\"addr:postcode\":")) {
                                postcode = extractValueFromLine(addrLine, "addr:postcode");
                            } else if (addrLine.contains("\"addr:city\":")) {
                                city = extractValueFromLine(addrLine, "addr:city");
                            } else if (addrLine.contains("\"addr:country\":")) {
                                country = extractValueFromLine(addrLine, "addr:country");
                            }
                        }
                        
                        // Baue die vollständige Adresse
                        StringBuilder address = new StringBuilder();
                        if (!street.isEmpty()) {
                            address.append(street);
                            if (!housenumber.isEmpty()) {
                                address.append(" ").append(housenumber);
                            }
                        }
                        if (!postcode.isEmpty()) {
                            if (address.length() > 0) address.append(", ");
                            address.append(postcode);
                        }
                        if (!city.isEmpty()) {
                            if (address.length() > 0) address.append(" ");
                            address.append(city);
                        }
                        if (!country.isEmpty()) {
                            if (address.length() > 0) address.append(", ");
                            address.append(country);
                        }
                        
                        if (address.length() > 0) {
                            data.setAddress(address.toString());
                            hasValidData = true;
                        }
                    }
                }
                
                // Füge das Objekt hinzu, wenn es gültige Daten hat
                if (hasValidData) {
                    dataList.add(data);
                }
            }
        }
        
        System.out.println("Gefundene Elemente: " + dataList.size());
        return dataList;
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