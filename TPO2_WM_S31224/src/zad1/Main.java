/**
 *
 *  @author Wierciński Mateusz S31224
 *
 */

package zad1;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();
//    JsonObject jsonObject = new Gson().fromJson(weatherJson, JsonObject.class);
//    System.out.println(jsonObject.getAsJsonObject("main").get ("temp"));


    // ...
    // część uruchamiająca GUI


  }
}
