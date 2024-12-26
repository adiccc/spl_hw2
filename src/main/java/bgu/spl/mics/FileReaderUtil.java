package bgu.spl.mics;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public  class FileReaderUtil<T>{
//    private String filePath;
////    private static Gson gson;
//
//    public FileReaderUtil(String filePath) {
//        this.filePath = filePath;
////        this.gson = new Gson();
//    }

    public static JsonObject readJson(String filePath) {
        try(FileReader reader=new FileReader(filePath)){
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject;
        }
        catch (Exception e) {
            System.out.println("Error reading file: " + filePath);
        }
        return null;
    }

//    public List<T> readJsonToList(String filePath) {
//        try (FileReader reader = new FileReader(filePath)) {
//            Type dataListType = new TypeToken<ArrayList<T>>(){}.getType();
//            List<T> dataList = gson.fromJson(new FileReader(filePath), dataListType);
//            return dataList;
//        } catch (FileNotFoundException e) {
//            System.out.println("File not found: " + filePath);
//        } catch (IOException e) {
//            System.out.println("Error reading file: " + filePath);
//        }
//        // Return null if an error occurs
//        return null;
//    }

//    public static List<T>  readJsonToList(JsonObject jsonObject) {
//        Gson gson = new Gson();
//            Type dataListType = new TypeToken<ArrayList<T>>(){}.getType();
//            List<T> dataList = gson.fromJson(jsonObject, dataListType);
//            return dataList;
//    }

}