/**
 * @author Wierciński Mateusz S31224
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
            return calculateLocalDateTime(from, to);
        } catch (DateTimeParseException e) {
            try {
                return calculateLocalDate(from, to);
            } catch (DateTimeException ex) {
                String[] arr = to.split("-").clone();
                if (!(Integer.parseInt(arr[0]) % 4 == 0) && Integer.parseInt(arr[1]) == 2 && !arr[2].contains("T") && Integer.parseInt(arr[2]) == 29) {
                    return "***" + e;
                }
                return ("***" + ex);
            }
        }


    }


    public static String calculateLocalDateTime(String from, String to) {

        LocalDateTime fromLocalDateTime = LocalDateTime.parse(from);
        LocalDateTime toLocalDateTime = LocalDateTime.parse(to);


        Period period = Period.between()
    }

    public static String calculateLocalDate(String from, String to) {

        LocalDate toLocalDate = LocalDate.parse(to);
        LocalDate fromLocalDate = LocalDate.parse(from);

        Period period = Period.between(fromLocalDate, toLocalDate);
        return period.toString();

    }
}
