-- Tabelle für Firmen erstellen
CREATE TABLE company (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    lat DECIMAL(10, 8),
    lon DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabelle für die Kontaktdaten erstellen
CREATE TABLE contact (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(50),
    email VARCHAR(255),
    website VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabelle für die Adressen erstellen
CREATE TABLE address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    housenumber VARCHAR(100),
    postal_code VARCHAR(20),
    city VARCHAR(100),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Zwischentabelle für die Verbindung zwischen Firmen und Adressen
CREATE TABLE company_address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    address_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE,
    FOREIGN KEY (address_id) REFERENCES address(id) ON DELETE CASCADE,
    UNIQUE KEY unique_company_address (company_id, address_id)
);

-- Tabelle für die Verbindung zwischen Firmen und Kontaktdaten erstellen
CREATE TABLE company_contacts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    contact_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE,
    FOREIGN KEY (contact_id) REFERENCES contact(id) ON DELETE CASCADE,
    UNIQUE KEY unique_company_contact (company_id, contact_id)
);

-- Index für bessere Performance
CREATE INDEX idx_companies_name ON company(company_name);
CREATE INDEX idx_addresses_city ON address(city);
CREATE INDEX idx_addresses_country ON address(country); 