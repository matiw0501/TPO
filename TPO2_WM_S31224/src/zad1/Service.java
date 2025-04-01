/**
 *
 *  @author Wierciński Mateusz S31224
 *
 */

package zad1;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Currency;
import java.util.Locale;
import java.util.stream.Collectors;

public class Service {
    String countryName;
    String countryCode;
    String currencyCode=null;
    public Service(String countryName) {
        this.countryName = countryName;
        try {
            for (Locale availableLocale : Locale.getAvailableLocales()) {
//                System.out.println(availableLocale.getDisplayCountry() + " " + availableLocale.getCountry() + " " + Currency.getInstance(availableLocale).getCurrencyCode());
                if (availableLocale.getDisplayCountry(Locale.ENGLISH).equals(countryName)) {
                    try {
                        currencyCode = Currency.getInstance(availableLocale).getCurrencyCode();
                        countryCode = availableLocale.getCountry();
                    } catch (Exception e) {
                        countryCode = availableLocale.getCountry();
                    }
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getWeather(String city) {
        String json="";
        String convertedCity=city.trim().replace(" ","%20");
        try(
                BufferedReader br = new BufferedReader(new InputStreamReader(new URI( "https://api.openweathermap.org/data/2.5/weather?q="+ convertedCity + "," + countryCode+"&units=metric&appid=a327ae1c7ff2c4596d079738f726bce5").toURL().openStream()))
        ){
            json = br.lines().collect(Collectors.joining());
        }
        catch(Exception e){
            throw new RuntimeException("getWeather failed");
        }
        System.out.println(json);
        return json;
    }

    public double getRateFor(String curr){

        if(curr.equals(currencyCode)){
            return 1.0;
        }

        String json="";
        try(
                BufferedReader br = new BufferedReader(new InputStreamReader(new URI("https://v6.exchangerate-api.com/v6/e3f6f27c0684c3e7973ca2f2/latest/" + curr).toURL().openStream()))
        ){
            json = br.lines().collect(Collectors.joining());
        }
        catch(Exception e){
           throw new RuntimeException("getRateFor failed");
        }
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        return (jsonObject.getAsJsonObject("conversion_rates").get(currencyCode).getAsDouble());
    }

    public double getNBPRate(){
        System.out.println(currencyCode);
        if (currencyCode.equals("PLN")) {
            return 1.0;
        }

        String json = "";
        try(
                BufferedReader br = new BufferedReader(new InputStreamReader(new URI("https://api.nbp.pl/api/exchangerates/rates/A/" +currencyCode +"/").toURL().openStream())) //tutaj cos z locale zeby zamiast "USD" byla waluta dostarczanego panstwa
        ){
            json = br.lines().collect(Collectors.joining());
        }
        catch(Exception e){
            throw new RuntimeException("getNBPRate failed");
        }
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("rates");
        return jsonArray.getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsDouble();
    }

}  
