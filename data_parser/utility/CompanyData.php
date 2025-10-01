<?php

class CompanyData
{
    // Table company_data
    private string $name;
    private ?float $lat;
    private ?float $lon;
    private ?string $amenity;
    private ?string $description;
    private DateTime $createdAt;
    private DateTime $updatedAt;
    private ?string $emergency;
    private ?string $healthcare;
    private ?string $operator;
    private ?string $wheelchair;
    private ?string $wikidata;
    private ?string $wikipedia;
    private ?string $image;
    private ?string $opening_hours;
    private ?string $payment;
    private ?bool $isAddress;
    private ?bool $isContact;
    
    // Table company_name
    private ?string $name_en;
    private ?string $name_de;
    private ?string $name_fr;
    private ?string $old_name;
    private ?string $short_name;

    // Table company_speciality
    private ?array $speciality;
    private ?array $speciality_de;
    private ?array $speciality_fr;

    // Table company_social_media
    private ?string $facebook;
    private ?string $instagram;
    private ?string $twitter;
    private ?string $youtube;
    private ?string $linkedin;
    private ?string $tiktok;

    // Table company_address
    private ?string $country;   
    private ?string $city;
    private ?string $state;
    private ?string $street;
    private ?string $housenumber;
    private ?string $postalCode;

    // Table company_contact
    private ?string $phone;
    private ?string $fax;
    private ?string $website;
    private ?string $email;

    public function __construct(
        $name, 
        $lat, 
        $lon, 
        $amenity,
        $description,
        $createdAt, 
        $updatedAt, 
        $isAddress,
        $isContact,
        $country, 
        $city, 
        $state, 
        $street, 
        $housenumber, 
        $postalCode, 
        $phone,
        $fax,
        $website, 
        $email
        )
    {
        // Table company_data
        $this->name = $name;
        $this->lat = $lat;
        $this->lon = $lon;
        $this->amenity = $amenity;
        $this->description = $description;
        $this->createdAt = $createdAt;
        $this->updatedAt = $updatedAt;
        $this->isAddress = $isAddress;
        $this->isContact = $isContact;

        // Table company_address
        $this->country = $country;
        $this->city = $city;
        $this->state = $state;
        $this->street = $street;
        $this->housenumber = $housenumber;
        $this->postalCode = $postalCode;

        // Table company_contact
        $this->phone = $phone;
        $this->fax = $fax;
        $this->website = $website;
        $this->email = $email;
    }
}