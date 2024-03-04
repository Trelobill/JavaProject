package com.unipi.gkagkakis;

import com.unipi.gkagkakis.database.Database;

import java.sql.Timestamp;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Scanner;

public class Main{
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args){
        Database.createTable();
        while (true) {
            displayMenu();
            String option = scanner.nextLine();
            makePOSTRequest(option);
        }
    }

    private static void displayMenu(){
        System.out.println("\n\033[1;92mWelcome to our weather app!\nPlease give a city for more info!");
    }

    public static void makePOSTRequest(String city){
        try {
            // API endpoint
            String endpoint = "https://wttr.in/" + city + "?format=j2";
            // Create HttpClient
            HttpClient client = HttpClient.newHttpClient();
            // Create HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .GET()
                    .build();
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parse JSON response using Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            // Print the JSON object
//            System.out.println("JSON Object: " + jsonObject.toString());
            JsonObject current_condition = jsonObject.getAsJsonArray("current_condition").get(0).getAsJsonObject();
            String temp_c = current_condition.get("temp_C").getAsString() + " °C";
            String humidity = current_condition.get("humidity").getAsString() + " %";
            String wind_speed_Kmph = current_condition.get("windspeedKmph").getAsString() + " km/h";
            int uv_index = current_condition.get("uvIndex").getAsInt();
            String weather_Desc = current_condition.getAsJsonArray("weatherDesc").get(0).getAsJsonObject().get("value").getAsString();
            System.out.println(current_condition);
            System.out.println("Temperature: " + temp_c);
            System.out.println("Humidity: " + humidity);
            System.out.println("Wind Speed: " + wind_speed_Kmph);
            System.out.println("UV Index: " + uv_index);
            System.out.println("Weather Description: " + weather_Desc);
            Database.insertNewWeatherSearch(city, new Timestamp(System.currentTimeMillis()), temp_c, humidity, wind_speed_Kmph, uv_index, weather_Desc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
