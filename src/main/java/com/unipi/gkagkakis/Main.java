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
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String ORANGE = "\u001B[38;5;208m";
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String ITALIC = "\u001B[3m";

    public static void main(String[] args){
        Database.createTable();
        while (true) {
            displayMenu();
            String option = scanner.nextLine();
            String formattedOption = option.replaceAll("\\s+", "+");
            makePOSTRequest(formattedOption);
        }
    }

    private static void displayMenu(){
        System.out.println("\n" + BLUE + BOLD + ITALIC + UNDERLINE + "Welcome to our weather app! \nPlease enter a city to get detailed weather information!");
        System.out.print("\n" + RESET + BOLD + PURPLE + "City Name: " + RESET);

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
            String wind_speed_Kmph1 = current_condition.get("windspeedKmph").getAsString() + " km/h";
            int uv_index = current_condition.get("uvIndex").getAsInt();
            String weather_Desc = current_condition.getAsJsonArray("weatherDesc").get(0).getAsJsonObject().get("value").getAsString();
            String country = jsonObject.getAsJsonArray("nearest_area").get(0).getAsJsonObject().get("country").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
            System.out.println(ITALIC + CYAN + "\nFound a city in country: " + ORANGE + BOLD + country + RESET + ITALIC + CYAN + ".");
            System.out.println("If it's wrong, please try typing the country next to city name!\n");
            System.out.println(RED + "Temperature: " + YELLOW + temp_c);
            System.out.println(RED + "Humidity: " + YELLOW + humidity);
            System.out.println(RED + "Wind Speed: " + YELLOW + wind_speed_Kmph);
            System.out.println(RED + "UV Index: " + YELLOW + uv_index);
            System.out.println(RED + "Weather Description: " + YELLOW + weather_Desc);
            Database.insertNewWeatherSearch(city, new Timestamp(System.currentTimeMillis()), temp_c, humidity, wind_speed_Kmph, uv_index, weather_Desc);
        } catch (Exception e) {
            System.out.println(RED + BOLD + "An error occurred while processing the request. Please try again!");
        }
    }
}
