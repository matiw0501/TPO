/**
 *
 *  @author Wierciński Mateusz S31224
 *
 */

package zad1;




import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;



public class Time {
    public static String passed(String from, String to) {
        LocalDateTime localDateTime = null; // LocalDateTime.parse("2000-02-02T10:15:30");
        LocalDate localDate = null;
        try {
           //  a = LocalDateTime.parse(to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            localDate = LocalDate.parse(to, DateTimeFormatter.ISO_LOCAL_DATE);
            return localDate.toString();

        }
        catch (DateTimeParseException e) {
           try{
               localDateTime = LocalDateTime.parse(to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
               return localDateTime.toString();
           }
           catch (DateTimeException ex) {
               String [] arr = to.split("-").clone();
               if (!(Integer.parseInt(arr[0]) % 4 == 0) && Integer.parseInt(arr[1]) == 2 && Integer.parseInt(arr[2]) == 29 ) {
                  return "***" + e;
               }
               return("***" + ex);
           }
        }



    }
}
