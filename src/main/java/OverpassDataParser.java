import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OverpassDataParser {
    
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
            
            // Ergebnisse ausgeben
            System.out.println("Gefundene Datensätze: " + dataList.size());
            System.out.println("==========================================");
            
            for (OverpassData data : dataList) {
                System.out.println("Name: " + data.getName());
                System.out.println("Latitude: " + data.getLat());
                System.out.println("Longitude: " + data.getLon());
                System.out.println("Adresse: " + data.getAddress());
                System.out.println("Stadt: " + data.getCity());
                System.out.println("Land: " + data.getCountry());
                System.out.println("PLZ: " + data.getPostalCode());
                System.out.println("Telefon: " + data.getPhone());
                System.out.println("Website: " + data.getWebsite());
                System.out.println("Email: " + data.getEmail());
                System.out.println("---");
            }
            
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der JSON-Datei: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static List<OverpassData> parseOverpassData(String jsonContent) {
        List<OverpassData> dataList = new ArrayList<>();
        
        // Extrahiere alle Elemente aus dem JSON
        String[] elements = extractElements(jsonContent);
        System.out.println("Gefundene Elemente: " + elements.length);
        
        for (String element : elements) {
            if (element.contains("\"type\":\"node\"")) {
                OverpassData data = parseNodeElement(element);
                if (data != null && (data.getName() != null || data.getLat() != 0)) {
                    dataList.add(data);
                }
            }
        }
        
        return dataList;
    }
    
    private static String[] extractElements(String jsonContent) {
        // Extrahiere alle Elemente aus dem JSON
        int startIndex = jsonContent.indexOf("\"elements\":[");
        if (startIndex == -1) {
            System.out.println("Elements-Array nicht gefunden!");
            return new String[0];
        }
        
        startIndex = jsonContent.indexOf("[", startIndex);
        if (startIndex == -1) {
            System.out.println("Öffnende Klammer nicht gefunden!");
            return new String[0];
        }
        
        int endIndex = findMatchingBracket(jsonContent, startIndex);
        String elementsContent = jsonContent.substring(startIndex + 1, endIndex);
        
        return splitElements(elementsContent);
    }
    
    private static int findMatchingBracket(String content, int startIndex) {
        int count = 0;
        for (int i = startIndex; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '[' || c == '{') count++;
            else if (c == ']' || c == '}') {
                count--;
                if (count == 0) return i;
            }
        }
        return content.length();
    }
    
    private static String[] splitElements(String elementsContent) {
        // Aufteilung der Elemente an den Objektgrenzen
        return elementsContent.split("\\},\\s*\\{");
    }
    
    private static OverpassData parseNodeElement(String element) {
        OverpassData data = new OverpassData();
        
        // Extrahiere grundlegende Informationen
        String name = extractValue(element, "name");
        String latStr = extractValue(element, "lat");
        String lonStr = extractValue(element, "lon");
        
        // Adressinformationen aus den Tags extrahieren
        String addrCity = extractValue(element, "addr:city");
        String addrStreet = extractValue(element, "addr:street");
        String addrHousenumber = extractValue(element, "addr:housenumber");
        String addrPostcode = extractValue(element, "addr:postcode");
        String addrCountry = extractValue(element, "addr:country");
        String phone = extractValue(element, "phone");
        String website = extractValue(element, "website");
        String email = extractValue(element, "email");
        
        // Daten in das OverpassData Objekt setzen
        data.setName(name);
        
        try {
            if (!latStr.isEmpty()) {
                data.setLat(Double.parseDouble(latStr));
            }
            if (!lonStr.isEmpty()) {
                data.setLon(Double.parseDouble(lonStr));
            }
        } catch (NumberFormatException e) {
            System.err.println("Fehler beim Parsen der Koordinaten: " + e.getMessage());
        }
        
        // Adresse zusammenbauen
        StringBuilder address = new StringBuilder();
        if (!addrStreet.isEmpty()) {
            address.append(addrStreet);
            if (!addrHousenumber.isEmpty()) {
                address.append(" ").append(addrHousenumber);
            }
        }
        data.setAddress(address.toString());
        
        data.setCity(addrCity);
        data.setState(""); // Nicht in der JSON verfügbar
        data.setCountry(addrCountry);
        data.setPostalCode(addrPostcode);
        data.setPhone(phone);
        data.setWebsite(website);
        data.setEmail(email);
        
        return data;
    }
    
    private static String extractValue(String element, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = element.indexOf(searchKey);
        if (startIndex == -1) return "";
        
        startIndex += searchKey.length();
        while (startIndex < element.length() && Character.isWhitespace(element.charAt(startIndex))) {
            startIndex++;
        }
        
        if (startIndex >= element.length()) return "";
        
        char startChar = element.charAt(startIndex);
        if (startChar == '"') {
            // String-Wert
            startIndex++;
            int endIndex = element.indexOf('"', startIndex);
            if (endIndex == -1) return "";
            return element.substring(startIndex, endIndex);
        } else {
            // Numerischer Wert
            int endIndex = startIndex;
            while (endIndex < element.length() && 
                   (Character.isDigit(element.charAt(endIndex)) || 
                    element.charAt(endIndex) == '.' || 
                    element.charAt(endIndex) == '-')) {
                endIndex++;
            }
            return element.substring(startIndex, endIndex);
        }
    }
} 