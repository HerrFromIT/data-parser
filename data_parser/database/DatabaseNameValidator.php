<?php

class DatabaseNameValidator
{
    private $errors = [];
    private $databaseName;

    public function __construct($databaseName)
    {
        $this->databaseName = $databaseName;
        $this->errors = [];
    }

    public function validate()
    {
        $this->checkSet()
            ->checkString()
            ->checkEmpty()
            ->checkLength()
            ->checkChars()
            ->checkNumericOnly();

        return empty($this->errors) ? 200 : $this->errors[0];
    }

    private function checkSet()
    {
        if (!isset($this->databaseName)) {
            $this->errors[] = 100;
        }
        return $this;
    }

    private function checkString()
    {
        if (!is_string($this->databaseName)) {
            $this->errors[] = 102;
        }
        return $this;
    }

    private function checkEmpty()
    {
        if (empty($this->databaseName)) {
            $this->errors[] = 101;
        }
        return $this;
    }

    private function checkLength()
    {
        if (is_string($this->databaseName)) {
            if (strlen($this->databaseName) < 3) {
                $this->errors[] = 106;
            }
            if (strlen($this->databaseName) > 63) {
                $this->errors[] = 107;
            }
        }
        return $this;
    }

    private function checkChars()
    {
        if (is_string($this->databaseName) && !preg_match('/^[a-zA-Z0-9_]+$/', $this->databaseName)) {
            $this->errors[] = 103;
        }
        if ($this->databaseName === '') {
            $this->errors[] = 105;
        }
        return $this;
    }

    private function checkNumericOnly()
    {
        if (is_string($this->databaseName) && preg_match('/^[0-9]+$/', $this->databaseName)) {
            $this->errors[] = 108;
        }
        return $this;
    }

    public function getErrors()
    {
        return $this->errors;
    }

    public function hasErrors()
    {
        return !empty($this->errors);
    }
}

?>