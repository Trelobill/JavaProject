package com.unipi.gkagkakis.main;

import com.unipi.gkagkakis.GUI.GUI;
import com.unipi.gkagkakis.database.Database;

import java.sql.Timestamp;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
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
        GUIorConsole();
    }

    //συναρτηση που τρεχει συνεχεια αν δωσω terminal app
    private static void displayMenu(){
        Database.createTable();
        System.out.println("\n" + BLUE + BOLD + ITALIC + UNDERLINE + "Welcome to our terminal weather app! \nPlease enter a city to get detailed weather information!");
        while (true) {
            System.out.print("\n" + RESET + BOLD + PURPLE + "City Name: " + RESET);
            String option = scanner.nextLine();
            String formattedOption = option.replaceAll("\\s+", "+");
            makePOSTRequest(formattedOption, true);
        }
    }

    //στην αρχη ο χρηστης διαλεγει αν θελει GUI ή console app
    private static void GUIorConsole(){
        System.out.println("\n" + BLUE + BOLD + "Press 1 for GUI or 2 for terminal app!");
        String option = scanner.nextLine();
        switch (option) {
            case "1":
                Database.createTable();
                new GUI();
                System.out.println("\n" + YELLOW + BOLD + "Application starting...");
                break;
            case "2":
                displayMenu();
                break;
            default:
                System.out.println(BOLD + RED + "Invalid option. Please try again.");
                GUIorConsole();
                break;
        }
    }

    //γινεται το request για τα data μεσω json
    //χειρισμος και επιστροφη τιμων
    public static Map<String, String> makePOSTRequest(String city, boolean terminal){
        Map<String, String> weatherData = new HashMap<>();
        try {
            String endpoint = "https://wttr.in/" + city + "?format=j2";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            JsonObject current_condition = jsonObject.getAsJsonArray("current_condition").get(0).getAsJsonObject();
            String temp_c = current_condition.get("temp_C").getAsString() + " °C";
            String humidity = current_condition.get("humidity").getAsString() + " %";
            String wind_speed_Kmph = current_condition.get("windspeedKmph").getAsString() + " km/h";
            int uv_index = current_condition.get("uvIndex").getAsInt();
            String weather_Desc = current_condition.getAsJsonArray("weatherDesc").get(0).getAsJsonObject().get("value").getAsString();
            String country = jsonObject.getAsJsonArray("nearest_area").get(0).getAsJsonObject().get("country").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
            weatherData.put("Temperature", temp_c);
            weatherData.put("Humidity", humidity);
            weatherData.put("Wind Speed", wind_speed_Kmph);
            weatherData.put("UV Index", String.valueOf(uv_index));
            weatherData.put("Weather Description", weather_Desc);
            weatherData.put("Country", country);
            if (terminal) {
                System.out.println(ITALIC + CYAN + "\nFound a city in country: " + ORANGE + BOLD + country + RESET + ITALIC + CYAN + ".");
                System.out.println("If it's wrong, please try typing the country next to city name!\n");
                System.out.println(RED + "Temperature: " + YELLOW + temp_c);
                System.out.println(RED + "Humidity: " + YELLOW + humidity);
                System.out.println(RED + "Wind Speed: " + YELLOW + wind_speed_Kmph);
                System.out.println(RED + "UV Index: " + YELLOW + uv_index);
                System.out.println(RED + "Weather Description: " + YELLOW + weather_Desc);
            }
            Database.insertNewWeatherSearch(city, new Timestamp(System.currentTimeMillis()), temp_c, humidity, wind_speed_Kmph, uv_index, weather_Desc);
        } catch (Exception e) {
            System.out.println(RED + BOLD + "An error occurred while processing the request. Please try again!");
        }
        return weatherData;
    }
}
