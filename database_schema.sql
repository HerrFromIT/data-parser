-- Datenbank erstellen
CREATE DATABASE IF NOT EXISTS company_data;
USE company_data;

-- Haupttabelle: company_data
CREATE TABLE company_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    lat DECIMAL(10, 8),
    lon DECIMAL(11, 8),
    amenity VARCHAR(100),
    description TEXT,
    emergency VARCHAR(100),
    healthcare VARCHAR(100),
    operator VARCHAR(255),
    wheelchair VARCHAR(50),
    wikidata VARCHAR(255),
    wikipedia VARCHAR(255),
    image VARCHAR(500),
    opening_hours TEXT,
    payment VARCHAR(100),
    isAddress BOOLEAN DEFAULT FALSE,
    isContact BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabelle: company_name (mehrsprachige Namen)
CREATE TABLE company_name (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name_en VARCHAR(255),
    name_de VARCHAR(255),
    name_fr VARCHAR(255),
    old_name VARCHAR(255),
    short_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Zwischentabelle: company_data_name
CREATE TABLE company_data_name (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_data_id INT NOT NULL,
    company_name_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_data_id) REFERENCES company_data(id) ON DELETE CASCADE,
    FOREIGN KEY (company_name_id) REFERENCES company_name(id) ON DELETE CASCADE,
    UNIQUE KEY unique_company_name (company_data_id, company_name_id)
);

-- Tabelle: company_speciality (Spezialitäten)
CREATE TABLE company_speciality (
    id INT AUTO_INCREMENT PRIMARY KEY,
    speciality VARCHAR(255) NOT NULL,
    speciality_de VARCHAR(255),
    speciality_fr VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Zwischentabelle: company_data_speciality
CREATE TABLE company_data_speciality (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_data_id INT NOT NULL,
    company_speciality_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_data_id) REFERENCES company_data(id) ON DELETE CASCADE,
    FOREIGN KEY (company_speciality_id) REFERENCES company_speciality(id) ON DELETE CASCADE,
    UNIQUE KEY unique_company_speciality (company_data_id, company_speciality_id)
);

-- Tabelle: company_social_media
CREATE TABLE company_social_media (
    id INT AUTO_INCREMENT PRIMARY KEY,
    facebook VARCHAR(255),
    instagram VARCHAR(255),
    twitter VARCHAR(255),
    youtube VARCHAR(255),
    linkedin VARCHAR(255),
    tiktok VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Zwischentabelle: company_data_social_media
CREATE TABLE company_data_social_media (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_data_id INT NOT NULL,
    company_social_media_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_data_id) REFERENCES company_data(id) ON DELETE CASCADE,
    FOREIGN KEY (company_social_media_id) REFERENCES company_social_media(id) ON DELETE CASCADE,
    UNIQUE KEY unique_company_social_media (company_data_id, company_social_media_id)
);

-- Tabelle: company_address
CREATE TABLE company_address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    country VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(100),
    street VARCHAR(255),
    housenumber VARCHAR(50),
    postal_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Zwischentabelle: company_data_address
CREATE TABLE company_data_address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_data_id INT NOT NULL,
    company_address_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_data_id) REFERENCES company_data(id) ON DELETE CASCADE,
    FOREIGN KEY (company_address_id) REFERENCES company_address(id) ON DELETE CASCADE,
    UNIQUE KEY unique_company_address (company_data_id, company_address_id)
);

-- Tabelle: company_contact
CREATE TABLE company_contact (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(50),
    fax VARCHAR(50),
    website VARCHAR(255),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Zwischentabelle: company_data_contact
CREATE TABLE company_data_contact (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_data_id INT NOT NULL,
    company_contact_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_data_id) REFERENCES company_data(id) ON DELETE CASCADE,
    FOREIGN KEY (company_contact_id) REFERENCES company_contact(id) ON DELETE CASCADE,
    UNIQUE KEY unique_company_contact (company_data_id, company_contact_id)
);

-- Indizes für bessere Performance
CREATE INDEX idx_company_data_name ON company_data(name);
CREATE INDEX idx_company_data_amenity ON company_data(amenity);
CREATE INDEX idx_company_data_city ON company_data(lat, lon);
CREATE INDEX idx_company_address_city ON company_address(city);
CREATE INDEX idx_company_address_country ON company_address(country);
CREATE INDEX idx_company_contact_email ON company_contact(email);

-- Beispieldaten einfügen

-- 1. Hauptdaten einfügen
INSERT INTO company_data (name, lat, lon, amenity, description, emergency, healthcare, operator, wheelchair, wikidata, wikipedia, image, opening_hours, payment, isAddress, isContact) VALUES
('Apotheke am Markt', 52.5200, 13.4050, 'pharmacy', 'Moderne Apotheke im Stadtzentrum', 'yes', 'pharmacy', 'Markus Müller', 'yes', 'Q123456', 'https://de.wikipedia.org/wiki/Apotheke_am_Markt', 'https://example.com/image1.jpg', 'Mo-Fr 08:00-18:00; Sa 08:00-12:00', 'cash;card', TRUE, TRUE),
('Café Central', 52.5210, 13.4060, 'cafe', 'Gemütliches Café mit Kaffee und Kuchen', NULL, NULL, 'Anna Schmidt', 'limited', 'Q123457', 'https://de.wikipedia.org/wiki/Café_Central', 'https://example.com/image2.jpg', 'Mo-Su 07:00-22:00', 'cash;card', TRUE, TRUE),
('Zahnarzt Dr. Weber', 52.5220, 13.4070, 'dentist', 'Zahnarztpraxis mit modernster Ausstattung', 'yes', 'dentist', 'Dr. Hans Weber', 'yes', 'Q123458', 'https://de.wikipedia.org/wiki/Zahnarzt_Weber', 'https://example.com/image3.jpg', 'Mo-Fr 09:00-17:00', 'cash;card;insurance', TRUE, TRUE);

-- 2. Mehrsprachige Namen
INSERT INTO company_name (name_en, name_de, name_fr, old_name, short_name) VALUES
('Pharmacy at Market', 'Apotheke am Markt', 'Pharmacie au Marché', 'Stadtapotheke', 'Apotheke Markt'),
('Central Café', 'Café Central', 'Café Central', 'Kaffeehaus Central', 'Café Central'),
('Dr. Weber Dentist', 'Zahnarzt Dr. Weber', 'Dentiste Dr. Weber', 'Zahnarztpraxis Weber', 'Dr. Weber');

-- 3. Spezialitäten
INSERT INTO company_speciality (speciality, speciality_de, speciality_fr) VALUES
('Pharmacy Services', 'Apothekenleistungen', 'Services de pharmacie'),
('Coffee & Pastries', 'Kaffee & Gebäck', 'Café & Pâtisseries'),
('Dental Care', 'Zahnpflege', 'Soins dentaires'),
('Emergency Medicine', 'Notfallmedizin', 'Médecine d''urgence'),
('Cosmetics', 'Kosmetik', 'Cosmétiques');

-- 4. Social Media
INSERT INTO company_social_media (facebook, instagram, twitter, youtube, linkedin, tiktok) VALUES
('https://facebook.com/apothekeammarkt', 'https://instagram.com/apothekeammarkt', 'https://twitter.com/apothekeammarkt', NULL, NULL, NULL),
('https://facebook.com/cafecentral', 'https://instagram.com/cafecentral', NULL, NULL, NULL, 'https://tiktok.com/@cafecentral'),
(NULL, 'https://instagram.com/drweberzahnarzt', NULL, NULL, 'https://linkedin.com/in/drweber', NULL);

-- 5. Adressen
INSERT INTO company_address (country, city, state, street, housenumber, postal_code) VALUES
('Deutschland', 'Berlin', 'Berlin', 'Marktstraße', '15', '10115'),
('Deutschland', 'Berlin', 'Berlin', 'Hauptstraße', '42', '10117'),
('Deutschland', 'Berlin', 'Berlin', 'Friedrichstraße', '123', '10117');

-- 6. Kontaktdaten
INSERT INTO company_contact (phone, fax, website, email) VALUES
('+49 30 12345678', '+49 30 12345679', 'https://www.apotheke-ammarkt.de', 'info@apotheke-ammarkt.de'),
('+49 30 87654321', NULL, 'https://www.cafe-central.de', 'info@cafe-central.de'),
('+49 30 11223344', '+49 30 11223345', 'https://www.dr-weber-zahnarzt.de', 'info@dr-weber-zahnarzt.de');

-- 7. Verbindungen herstellen (Zwischentabellen)

-- Company Data - Name Verbindungen
INSERT INTO company_data_name (company_data_id, company_name_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- Company Data - Speciality Verbindungen
INSERT INTO company_data_speciality (company_data_id, company_speciality_id) VALUES
(1, 1), -- Apotheke - Pharmacy Services
(1, 4), -- Apotheke - Emergency Medicine
(1, 5), -- Apotheke - Cosmetics
(2, 2), -- Café - Coffee & Pastries
(3, 3); -- Zahnarzt - Dental Care

-- Company Data - Social Media Verbindungen
INSERT INTO company_data_social_media (company_data_id, company_social_media_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- Company Data - Address Verbindungen
INSERT INTO company_data_address (company_data_id, company_address_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- Company Data - Contact Verbindungen
INSERT INTO company_data_contact (company_data_id, company_contact_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- Beispiel-Abfrage: Alle Firmen mit vollständigen Informationen
SELECT 
    cd.id,
    cd.name,
    cd.amenity,
    cd.description,
    cn.name_de,
    ca.street,
    ca.housenumber,
    ca.postal_code,
    ca.city,
    cc.phone,
    cc.email,
    cc.website
FROM company_data cd
LEFT JOIN company_data_name cdn ON cd.id = cdn.company_data_id
LEFT JOIN company_name cn ON cdn.company_name_id = cn.id
LEFT JOIN company_data_address cda ON cd.id = cda.company_data_id
LEFT JOIN company_address ca ON cda.company_address_id = ca.id
LEFT JOIN company_data_contact cdc ON cd.id = cdc.company_data_id
LEFT JOIN company_contact cc ON cdc.company_contact_id = cc.id
ORDER BY cd.id;
