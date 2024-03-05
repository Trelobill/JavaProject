package com.unipi.gkagkakis.GUI;

import com.unipi.gkagkakis.main.Main;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

public class GUI extends JFrame{
    private JPanel MainPanel;
    private JTextField textField;
    private JLabel countryArea;
    private JLabel temperatureArea;
    private JLabel humidityArea;
    private JLabel windSpeedArea;
    private JLabel UVIndexArea;
    private JLabel weatherDescriptionArea;
    private JLabel Hint;
    private JButton searchButton;

    public GUI(){
        setTitle("WeatherApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        textField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!textField.getText().isEmpty()) {
                        searchButton.doClick();
                    } else {
                        setLabelsToDefault();
                        JOptionPane.showMessageDialog(null, "Text field cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                        textField.requestFocusInWindow();
                    }
                }
            }
        });
        searchButton.addActionListener(e -> {
            String text = textField.getText();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Text field cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                textField.requestFocusInWindow();
            } else {
                String formattedtext = text.replaceAll("\\s+", "+");
                Map<String, String> weatherData = Main.makePOSTRequest(formattedtext, false);
                handleData(weatherData);
            }
        });
    }

    private void handleData(Map<String, String> weatherData){
        if (weatherData.containsKey("Error")) {
            System.out.println(weatherData.get("Error"));
        } else {
            temperatureArea.setText(weatherData.get("Temperature"));
            countryArea.setText(weatherData.get("Country"));
            humidityArea.setText(weatherData.get("Humidity"));
            windSpeedArea.setText(weatherData.get("Wind Speed"));
            UVIndexArea.setText(weatherData.get("UV Index"));
            weatherDescriptionArea.setText(weatherData.get("Weather Description"));
        }
    }

    private void setLabelsToDefault(){
        countryArea.setText("----");
        temperatureArea.setText("----");
        humidityArea.setText("----");
        windSpeedArea.setText("----");
        UVIndexArea.setText("----");
        weatherDescriptionArea.setText("----");
    }
}
