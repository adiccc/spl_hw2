package bgu.spl.mics;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class FileReaderUtil<T> {
    // Method to read JSON from a file
    public static JsonObject readJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            // Parse the JSON file into JsonObject using Gson
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject;
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
        // Return null if an error occurs
        return null;
    }
}