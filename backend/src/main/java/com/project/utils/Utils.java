package com.project.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Utils {
    private Utils() {
    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>("{\"message\":\"" + responseMessage + "\"}", httpStatus);
    }

    public static String getUUID() {
        Date date = new Date();
        long time = date.getTime();
        return "BILL-" + time;
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data) {
        if (!Strings.isNullOrEmpty(data))
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {
            }.getType());
        return new HashMap<>();
    }

    public static Boolean isFileExists(String path) {
        try {
            File file = new File(path);
            return file.exists();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String generateTemporaryPassword() {
        int length = 8;
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numericChars = "0123456789";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";
        String allChars = upperCaseChars + lowerCaseChars + numericChars + specialChars;
        String password = getRandomChar(upperCaseChars)
                + getRandomChar(lowerCaseChars)
                + getRandomChar(numericChars)
                + getRandomChar(specialChars);
        for (int i = 4; i < length; i++) {
            password += getRandomChar(allChars);
        }

        return password;
    }

    private static String getRandomChar(String charSet) {
        Random random = new Random();
        return String.valueOf(charSet.charAt(random.nextInt(charSet.length())));
    }
}
