<?php

class FautDecoder
{
    public static function decode($errorCode)
    {
        $codeError = json_decode(file_get_contents(__DIR__ . '/errorCode.json'), true);
        $errorMessage = "";

        foreach ($codeError as $code => $message) {
            if ($code == $errorCode) {
                $errorMessage = $message;
                break;
            }
        }

        return $errorMessage;
    }
}