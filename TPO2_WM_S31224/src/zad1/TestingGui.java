package zad1;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ServiceConfigurationError;

public class TestingGui extends JFrame {
    private JPanel mainPanel;
    private JTextField country;
    private JButton button1;
    private JPanel webPanel;
    private JTextField town;
    private JPanel signCountry;
    private JPanel signFeelsLike;
    private JPanel signTempMin;
    private JPanel signTempMax;
    private JPanel signPressure;
    private JPanel signHumidity;
    private JPanel brake;
    private JPanel signToPLN;
    private JPanel signToGivenCurr;
    private JPanel signCity;
    private JPanel signTemp;
    private JPanel signCurr;
    private JPanel outputCity;
    private JPanel outputCountry;
    private JPanel outputCurrency;
    private JTextField currency;
    private JPanel feelsLike;
    private JPanel tempMin;
    private JPanel tempMax;
    private JPanel pressure;
    private JPanel humidity;
    private JPanel toPLN;
    private JPanel toGivenCurr;
    private JPanel temp;
    private JFXPanel fxPanel;


    public TestingGui() {

        fxPanel = new JFXPanel();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(mainPanel);
        Platform.runLater(this::createJFXContent);
        webPanel.add(fxPanel);
        this.pack();

        signCountry.add(new JLabel("Country : "));
        signCity.add(new JLabel("City : "));
        signCurr.add(new JLabel("Currency  : "));
        signTemp.add(new JLabel("Temperature : "));
        signFeelsLike.add(new JLabel("Feels Like : "));
        signTempMin.add(new JLabel("Min. temperature : "));
        signTempMax.add(new JLabel("Max. temperature : "));
        signPressure.add(new JLabel("Pressure : "));
        signHumidity.add(new JLabel("Humidity : "));
        signToPLN.add(new JLabel("Currency of country to PLN : "));
        signToGivenCurr.add(new JLabel("Given currency to currency of country : "));

        button1.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {


                outputCurrency.removeAll();
                outputCity.removeAll();
                outputCountry.removeAll();

                temp.removeAll();
                feelsLike.removeAll();
                tempMin.removeAll();
                tempMax.removeAll();
                pressure.removeAll();
                humidity.removeAll();

                toPLN.removeAll();
                toGivenCurr.removeAll();

                if(!country.getText().trim().isEmpty() && !town.getText().trim().isEmpty() && !currency.getText().trim().isEmpty()) {
                    try {
                        outputCountry.add(new JLabel(country.getText().trim()));
                        outputCity.add(new JLabel(town.getText().trim()));
                        outputCurrency.add(new JLabel(currency.getText().trim()));

                        updateWeatherData(town.getText().trim(), country.getText().trim());
                        updateCurrencyData(town.getText().trim(), country.getText().trim(), currency.getText().trim());

                        Platform.runLater(TestingGui.this::createJFXContent);
                        webPanel.add(fxPanel);
                        TestingGui.this.pack();



                    }
                    catch (Exception e1) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null,
                                    "Some data are wrong! Please enter correct informations.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }
                else if (!country.getText().trim().isEmpty() && !town.getText().trim().isEmpty() && currency.getText().trim().isEmpty()) {
                    try {
                        outputCountry.add(new JLabel(country.getText().trim()));
                        outputCity.add(new JLabel(town.getText().trim()));
                        updateWeatherData(town.getText().trim(), country.getText().trim());
                        Platform.runLater(TestingGui.this::createJFXContent);
                        webPanel.add(fxPanel);
                        TestingGui.this.pack();
                    }
                    catch (Exception e1) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null,
                                    "Some data are wrong! Please enter correct informations.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }
                else{
                    SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(null,
                                        "Some data are wrong! Please enter correct informations.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                    });
                    Platform.runLater(TestingGui.this::createJFXContent);
                    webPanel.add(fxPanel);
                    TestingGui.this.pack();
                }
                country.setText("");
                town.setText("");
                currency.setText("");

            }
        });
    }

    public void createJFXContent() {
        WebView webView = new WebView();
        if (country.getText().trim().isEmpty())
            webView.getEngine().load("https://en.wikipedia.org/wiki/" + town.getText());
        else
        webView.getEngine().load("https://en.wikipedia.org/wiki/" + town.getText() + ",_" + country.getText()) ;
        Scene scene = new Scene(webView);
        fxPanel.setScene(scene);
        this.pack();
    }

    private void updateWeatherData(String city, String country) {
        Service s = new Service(country);
        String jsonWeather = s.getWeather(city);

        Weather weather = new Gson().fromJson(jsonWeather, Weather.class);

        temp.add(new JLabel((weather.main.temp)+ "°C"));
        feelsLike.add(new JLabel((weather.main.feels_like) + "°C"));
        tempMin.add(new JLabel((weather.main.temp_min)+ "°C"));
        tempMax.add(new JLabel((weather.main.temp_max)+ "°C"));
        pressure.add(new JLabel((weather.main.pressure)+ "hPa"));
        humidity.add(new JLabel((weather.main.humidity)+ "%"));


    }

    private void updateCurrencyData(String city, String country, String currency) {
        Service s = new Service(country);
        toPLN.add(new JLabel(""+s.getNBPRate()));
        toGivenCurr.add(new JLabel(""+s.getRateFor(currency)));

    }






    public static void main(String[] args) {
        // ...
        // część uruchamiająca GUI

        SwingUtilities.invokeLater(TestingGui::new);

    }

}

class Weather{
    Main main;
}

class Main{
    double temp;
    double feels_like;
    double temp_min;
    double temp_max;
    double pressure;
    double humidity;
}


