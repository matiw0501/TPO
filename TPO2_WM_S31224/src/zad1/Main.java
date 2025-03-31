/**
 *
 *  @author Wierciński Mateusz S31224
 *
 */

package zad1;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.util.Locale;

public class Main{







  public static void main(String[] args) {



    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();


    // ...
    // część uruchamiająca GUI


    SwingUtilities.invokeLater(TestingGui::new);

  }
}
