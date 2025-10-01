import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BuiltinJsonParser {
    public static void main(String[] args) {
        try {
            // JSON-Datei einlesen
            String jsonContent = new String(Files.readAllBytes(Paths.get("overpass_data.json")));
            if (jsonContent.isEmpty()) {
                System.out.println("JSON-Datei ist leer!");
                return;
            }
            
            // Einfache JSON-Parsing-Logik
            parseJsonData(jsonContent);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void parseJsonData(String jsonContent) {
        // Einfache String-basierte Parsing-Logik
        String[] elements = extractElements(jsonContent);
        System.out.println("Elements: " + elements.length);
        
        for (String element : elements) {
            if (element.contains("\"type\":\"node\"")) {
                parseNodeElement(element);
            }
        }
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
        System.out.println("Start: " + startIndex + ", End: " + endIndex);
        
        String elementsContent = jsonContent.substring(startIndex + 1, endIndex);
        System.out.println("Elements Content Länge: " + elementsContent.length());
        
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
        // Einfache Aufteilung der Elemente
        return elementsContent.split("\\},\\s*\\{");
    }
    
    private static void parseNodeElement(String element) {
        // Extrahiere grundlegende Informationen
        String name = extractValue(element, "name");
        String lat = extractValue(element, "lat");
        String lon = extractValue(element, "lon");
        String addr_city = extractValue(element, "addr:city");
        String addr_street = extractValue(element, "addr:street");
        String addr_postcode = extractValue(element, "addr:postcode");
        String addr_country = extractValue(element, "addr:country");
        
        if (!name.isEmpty() || !lat.isEmpty()) {
            System.out.println("Name: " + name);
            System.out.println("Latitude: " + lat);
            System.out.println("Longitude: " + lon);
            System.out.println("Adress: " + addr_street + " " + addr_postcode + " " + addr_city + " " + addr_country);
            System.out.println("---");
        }
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