import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompanyDataManager {
    
    // Datenbankverbindung
    private static final String DB_URL = "jdbc:mysql://localhost:3306/company_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123"; // Passwort hier eingeben
    
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
            
            // Datenbankverbindung herstellen
            Connection conn = getDatabaseConnection();
            
            // Alle Firmen in JSON-Dateien und Datenbank speichern
            int savedCount = 0;
            int skippedCount = 0;
            for (OverpassData data : dataList) {
                // Prüfe ob die Firma einen Namen hat
                if (data.getName() == null || data.getName().trim().isEmpty()) {
                    System.out.println("Firma ohne Namen übersprungen (Lat: " + data.getLat() + ", Lon: " + data.getLon() + ")");
                    skippedCount++;
                    continue;
                }
                
                // In JSON-Datei speichern
                saveCompanyToJson(data, companiesDir);
                
                // In Datenbank speichern
                saveCompanyToDatabase(data, conn);
                
                savedCount++;
            }
            
            // Datenbankverbindung schließen
            if (conn != null) {
                conn.close();
            }
            
            System.out.println("Gefundene Datensätze: " + dataList.size());
            System.out.println("Gespeicherte Firmen: " + savedCount);
            System.out.println("Übersprungene Firmen (ohne Namen): " + skippedCount);
            System.out.println("==========================================");
            
        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static Connection getDatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver nicht gefunden", e);
        }
    }
    
    private static void saveCompanyToDatabase(OverpassData data, Connection conn) throws SQLException {
        // Prüfe ob die Firma bereits existiert
        Integer existingCompanyId = findExistingCompany(data, conn);
        
        if (existingCompanyId != null) {
            // Firma existiert bereits - prüfe ob Daten vervollständigt werden können
            System.out.println("Firma '" + data.getName() + "' existiert bereits in der Datenbank.");
            updateCompanyData(existingCompanyId, data, conn);
        } else {
            // Neue Firma - erstelle neuen Eintrag
            int addressId = saveAddressToDatabase(data, conn);
            int companyId = saveCompanyToDatabaseTable(data, conn);
            saveCompanyAddressRelation(companyId, addressId, conn);
            System.out.println("Neue Firma '" + data.getName() + "' in Datenbank gespeichert.");
        }
    }
    
    private static Integer findExistingCompany(OverpassData data, Connection conn) throws SQLException {
        String sql = "SELECT id FROM companies WHERE name = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.getName());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        
        return null;
    }
    
    private static void updateCompanyData(int companyId, OverpassData newData, Connection conn) throws SQLException {
        // Hole aktuelle Firmendaten
        OverpassData existingData = getCompanyData(companyId, conn);
        
        // Prüfe welche Daten aktualisiert werden müssen
        boolean hasUpdates = false;
        
        // Aktualisiere Firmendaten
        if (isEmpty(existingData.getPhone()) && !isEmpty(newData.getPhone())) {
            updateCompanyField(companyId, "phone", newData.getPhone(), conn);
            hasUpdates = true;
        }
        
        if (isEmpty(existingData.getWebsite()) && !isEmpty(newData.getWebsite())) {
            updateCompanyField(companyId, "website", newData.getWebsite(), conn);
            hasUpdates = true;
        }
        
        if (isEmpty(existingData.getEmail()) && !isEmpty(newData.getEmail())) {
            updateCompanyField(companyId, "email", newData.getEmail(), conn);
            hasUpdates = true;
        }
        
        // Aktualisiere Adressdaten
        Integer addressId = getCompanyAddressId(companyId, conn);
        if (addressId != null) {
            if (updateAddressData(addressId, newData, conn)) {
                hasUpdates = true;
            }
        }
        
        if (hasUpdates) {
            System.out.println("Daten für Firma '" + newData.getName() + "' wurden vervollständigt.");
        } else {
            System.out.println("Alle Daten für Firma '" + newData.getName() + "' sind bereits vollständig.");
        }
    }
    
    private static OverpassData getCompanyData(int companyId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM companies WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    OverpassData data = new OverpassData();
                    data.setName(rs.getString("name"));
                    data.setCity(rs.getString("city"));
                    data.setCountry(rs.getString("country"));
                    data.setPhone(rs.getString("phone"));
                    data.setWebsite(rs.getString("website"));
                    data.setEmail(rs.getString("email"));
                    return data;
                }
            }
        }
        
        return new OverpassData();
    }
    
    private static void updateCompanyField(int companyId, String field, String value, Connection conn) throws SQLException {
        String sql = "UPDATE companies SET " + field + " = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.setInt(2, companyId);
            pstmt.executeUpdate();
        }
    }
    
    private static Integer getCompanyAddressId(int companyId, Connection conn) throws SQLException {
        String sql = "SELECT address_id FROM company_addresses WHERE company_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("address_id");
                }
            }
        }
        
        return null;
    }
    
    private static boolean updateAddressData(int addressId, OverpassData newData, Connection conn) throws SQLException {
        // Hole aktuelle Adressdaten
        String sql = "SELECT * FROM addresses WHERE id = ?";
        String currentStreet = "";
        String currentHousenumber = "";
        String currentPostalCode = "";
        String currentCity = "";
        String currentCountry = "";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addressId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentStreet = rs.getString("street");
                    currentHousenumber = rs.getString("housenumber");
                    currentPostalCode = rs.getString("postal_code");
                    currentCity = rs.getString("city");
                    currentCountry = rs.getString("country");
                }
            }
        }
        
        // Prüfe welche Adressfelder aktualisiert werden müssen
        boolean hasUpdates = false;
        
        if (isEmpty(currentStreet) && !isEmpty(newData.getAddress())) {
            // Extrahiere Straße aus der neuen Adresse
            String[] addressParts = extractStreetAndHousenumber(newData.getAddress());
            if (!isEmpty(addressParts[0])) {
                updateAddressField(addressId, "street", addressParts[0], conn);
                hasUpdates = true;
            }
        }
        
        if (isEmpty(currentHousenumber) && !isEmpty(newData.getAddress())) {
            // Extrahiere Hausnummer aus der neuen Adresse
            String[] addressParts = extractStreetAndHousenumber(newData.getAddress());
            if (!isEmpty(addressParts[1])) {
                updateAddressField(addressId, "housenumber", addressParts[1], conn);
                hasUpdates = true;
            }
        }
        
        if (isEmpty(currentPostalCode) && !isEmpty(newData.getPostalCode())) {
            updateAddressField(addressId, "postal_code", newData.getPostalCode(), conn);
            hasUpdates = true;
        }
        
        if (isEmpty(currentCity) && !isEmpty(newData.getCity())) {
            updateAddressField(addressId, "city", newData.getCity(), conn);
            hasUpdates = true;
        }
        
        if (isEmpty(currentCountry) && !isEmpty(newData.getCountry())) {
            updateAddressField(addressId, "country", newData.getCountry(), conn);
            hasUpdates = true;
        }
        
        return hasUpdates;
    }
    
    private static String[] extractStreetAndHousenumber(String address) {
        String street = "";
        String housenumber = "";
        
        if (address != null && !address.isEmpty()) {
            String trimmedAddress = address.trim();
            
            // Suche nach der letzten Zahl in der Adresse (Hausnummer)
            int lastDigitIndex = -1;
            for (int i = trimmedAddress.length() - 1; i >= 0; i--) {
                if (Character.isDigit(trimmedAddress.charAt(i))) {
                    lastDigitIndex = i;
                    break;
                }
            }
            
            if (lastDigitIndex >= 0) {
                // Finde den Anfang der Hausnummer
                int housenumberStart = lastDigitIndex;
                while (housenumberStart > 0 && (Character.isDigit(trimmedAddress.charAt(housenumberStart - 1)) || 
                       trimmedAddress.charAt(housenumberStart - 1) == ' ' || 
                       trimmedAddress.charAt(housenumberStart - 1) == '-')) {
                    housenumberStart--;
                }
                
                street = trimmedAddress.substring(0, housenumberStart).trim();
                housenumber = trimmedAddress.substring(housenumberStart).trim();
            } else {
                // Keine Hausnummer gefunden
                street = trimmedAddress;
            }
        }
        
        return new String[]{street, housenumber};
    }
    
    private static void updateAddressField(int addressId, String field, String value, Connection conn) throws SQLException {
        String sql = "UPDATE addresses SET " + field + " = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.setInt(2, addressId);
            pstmt.executeUpdate();
        }
    }
    
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    private static int saveAddressToDatabase(OverpassData data, Connection conn) throws SQLException {
        String sql = "INSERT INTO addresses (street, housenumber, postal_code, city, country, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Extrahiere Straße und Hausnummer aus der Adresse
            String street = "";
            String housenumber = "";
            
            if (data.getAddress() != null && !data.getAddress().isEmpty()) {
                // Versuche, Straße und Hausnummer zu trennen
                String address = data.getAddress().trim();
                
                // Suche nach der letzten Zahl in der Adresse (Hausnummer)
                int lastDigitIndex = -1;
                for (int i = address.length() - 1; i >= 0; i--) {
                    if (Character.isDigit(address.charAt(i))) {
                        lastDigitIndex = i;
                        break;
                    }
                }
                
                if (lastDigitIndex >= 0) {
                    // Finde den Anfang der Hausnummer
                    int housenumberStart = lastDigitIndex;
                    while (housenumberStart > 0 && (Character.isDigit(address.charAt(housenumberStart - 1)) || 
                           address.charAt(housenumberStart - 1) == ' ' || 
                           address.charAt(housenumberStart - 1) == '-')) {
                        housenumberStart--;
                    }
                    
                    street = address.substring(0, housenumberStart).trim();
                    housenumber = address.substring(housenumberStart).trim();
                } else {
                    // Keine Hausnummer gefunden
                    street = address;
                }
            }
            
            // Begrenze die Länge der Felder
            if (street.length() > 255) street = street.substring(0, 255);
            if (housenumber.length() > 100) housenumber = housenumber.substring(0, 100);
            
            pstmt.setString(1, street);
            pstmt.setString(2, housenumber);
            pstmt.setString(3, data.getPostalCode());
            pstmt.setString(4, data.getCity());
            pstmt.setString(5, data.getCountry());
            pstmt.setDouble(6, data.getLat());
            pstmt.setDouble(7, data.getLon());
            
            pstmt.executeUpdate();
            
            // ID der eingefügten Adresse zurückgeben
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return -1;
    }
    
    private static int saveCompanyToDatabaseTable(OverpassData data, Connection conn) throws SQLException {
        String sql = "INSERT INTO companies (name, city, country, phone, website, email) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, data.getName());
            pstmt.setString(2, data.getCity());
            pstmt.setString(3, data.getCountry());
            pstmt.setString(4, data.getPhone());
            pstmt.setString(5, data.getWebsite());
            pstmt.setString(6, data.getEmail());
            
            pstmt.executeUpdate();
            
            // ID der eingefügten Firma zurückgeben
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return -1;
    }
    
    private static void saveCompanyAddressRelation(int companyId, int addressId, Connection conn) throws SQLException {
        String sql = "INSERT INTO company_addresses (company_id, address_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            pstmt.setInt(2, addressId);
            pstmt.executeUpdate();
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
                    
                    if (inTags && elementLine.contains("\"website\":")) {
                        String website = extractValueFromLine(elementLine, "website");
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
        System.out.println("JSON gespeichert: " + filePath.toString());
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