<?php
require_once 'CompanyData.php';

class CompanyDataParser
{

    public function matchData($data)
    {
        $data = json_decode($data, true);
        $elements = $data['elements'];
        $companyData = [];
        $count = 0;
        for ($i = 0; $i < count($elements); $i++) {
            $element = $elements[$i];
            if (!isset($element['tags']['name'])) {
                continue;
            } else {
                $count++;

                // Table company_data
                $name = $element['tags']['name'];
                $lat = isset($element['lat']) ? $element['lat'] : null;
                $lon = isset($element['lon']) ? $element['lon'] : null;
                $createdAt = new DateTime();
                $updatedAt = new DateTime();

                // Table company_address
                $country = isset($element['tags']['addr:country']) ? $element['tags']['addr:country'] : null;
                $city = isset($element['tags']['addr:city']) ? $element['tags']['addr:city'] : null;
                $state = isset($element['tags']['addr:state']) ? $element['tags']['addr:state'] : null;
                $street = isset($element['tags']['addr:street']) ? $element['tags']['addr:street'] : null;
                $housenumber = isset($element['tags']['addr:housenumber']) ? $element['tags']['addr:housenumber'] : null;
                $postalCode = isset($element['tags']['addr:postcode']) ? $element['tags']['addr:postcode'] : null;

                // Table company_contact
                $phone = isset($element['tags']['phone']) ? $element['tags']['phone'] : null;
                $website = isset($element['tags']['website']) ? $element['tags']['website'] : null;
                $email = isset($element['tags']['email']) ? $element['tags']['email'] : null;


                $companyData[$i] = new CompanyData($name, $lat, $lon, $createdAt, $updatedAt, $country, $city, $state, $street, $housenumber, $postalCode, $phone, $website, $email);
            }
        }
        return $companyData;
    }

    public function saveData($data, $folder, $filename, $amenity)
    {
        if (!file_exists($folder)) {
            mkdir($folder, 0777, true);
        }
        if (!file_exists($folder . '/' . $amenity)) {
            mkdir($folder . '/' . $amenity, 0777, true);
        }
        file_put_contents($folder . '/' . $amenity . '/' . $filename, json_encode($data));
    }

    public function saveCache($data, $folder, $filename, $amenity)
    {
        if (!file_exists($folder)) {
            mkdir($folder, 0777, true);
        }
        if (!file_exists($folder . '/' . $amenity)) {
            mkdir($folder . '/' . $amenity, 0777, true);
        }
        file_put_contents($folder . '/' . $amenity . '/' . $filename, $data);
    }

    public function loadCache($filename, $amenity, $folder)
    {
        if (file_exists($folder . '/' . $amenity . '/' . $filename)) {
            $data = file_get_contents($folder . '/' . $amenity . '/' . $filename);
            return $data;
        } else {
            return null;
        }
    }
}