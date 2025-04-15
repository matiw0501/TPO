/**
 * @author Wierciński Mateusz S31224
 */

package zad1;


import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;


public class Time {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", new Locale("pl"));
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] daysForm = {"dzień", "dni", "dni"};
    private static final String[] monthsForm = {"miesiąc", "miesiące", "miesięcy"};
    private static final String[] yearsForm = {"rok", "lata", "lat"};

    public static String passed(String from, String to) {
        LocalDateTime localDateTime = null;
        LocalDate localDate = null;
        try {
            return calculateLocalDateTime(from, to);
        } catch (DateTimeParseException e) {
            try {
                return calculateLocalDate(from, to);
            } catch (DateTimeException ex) {
                String[] arr = to.split("-").clone();
                if (!(Integer.parseInt(arr[0]) % 4 == 0) && Integer.parseInt(arr[1]) == 2 && !arr[2].contains("T") && Integer.parseInt(arr[2]) == 29) {
                    return "***" + ex;
                }
                return ("***" + e);
            }
        }


    }


    public static String calculateLocalDateTime(String from, String to) {

        StringBuilder stringBuilder = new StringBuilder();
        ZonedDateTime fromLocalDateTime = ZonedDateTime.of(LocalDateTime.parse(from), ZoneId.of("Europe/Warsaw"));
        ZonedDateTime toLocalDateTime = ZonedDateTime.of(LocalDateTime.parse(to), ZoneId.of("Europe/Warsaw"));
        long day = ChronoUnit.DAYS.between(fromLocalDateTime, toLocalDateTime);
        long hour = Duration.between(fromLocalDateTime, toLocalDateTime).toHours();
        long min = Duration.between(fromLocalDateTime, toLocalDateTime).toMinutes();
        Period period = Period.between(fromLocalDateTime.toLocalDate(),toLocalDateTime.toLocalDate());

        stringBuilder.append("Od ").append(fromLocalDateTime.format(dateFormatter)).append(" godz. ").append(fromLocalDateTime.format(timeFormatter))
                        .append(" do ").append(toLocalDateTime.format(dateFormatter)).append(" godz. ").append(toLocalDateTime.format(timeFormatter)).append("\n")
                        .append(" - mija: ").append(day).append(" ").append((wordsForm(day, daysForm))).append(", tygodni ").append(decimalFormat.format((day/7.0)).replace(",", ".")).append("\n")
                        .append(" - godzin: ").append(hour).append(", minut: ").append(min).append("\n")
                        .append(" - kalendarzowo: ").append(formatPeriod(period));

        return stringBuilder.toString();
    }

    public static String calculateLocalDate(String from, String to) {

        StringBuilder stringBuilder = new StringBuilder();
        LocalDate fromLocalDate = LocalDate.parse(from);
        LocalDate toLocalDate = LocalDate.parse(to);
        long day = ChronoUnit.DAYS.between(fromLocalDate, toLocalDate);
        Period period = Period.between(fromLocalDate, toLocalDate);

        stringBuilder.append("Od ").append(fromLocalDate.format(dateFormatter)).append(" do ").append(toLocalDate.format(dateFormatter)).append("\n");

        if (day > 0) {
            stringBuilder.append(" - mija ").append(day).append(" ").append(wordsForm(day, daysForm)).append(", tygodni ")
                    .append(decimalFormat.format( (day/7.0)).replace(",", ".")).append("\n").append(" - kalendarzowo: ").append(formatPeriod(period));
        }
        else {
            stringBuilder.append(" - mija: 0 dni, tygodni: 0\n - godzin: 0, minut: 0");
        }
        return stringBuilder.toString();

    }

    public static String wordsForm(long val, String[] forms) {
        int value = (int) Math.abs(val);
        if (value == 1)
            return forms[0];
        if ((value % 10 >= 2 && value % 10 <= 4 && (value % 100<10 || value % 100 >= 20)))
            return forms[1];
        else
            return forms[2];
    }

    public static String formatPeriod(Period period) {
        int days = period.getDays();
        int months = period.getMonths();
        int years = period.getYears();

        StringBuilder stringBuilder = new StringBuilder();
        if (years > 0)
            stringBuilder.append(years).append(" ").append(wordsForm(years, yearsForm)).append(", ");
        if (months > 0)
            stringBuilder.append(months).append(" ").append(wordsForm(months, monthsForm)).append(", ");
        if (days > 0)
            stringBuilder.append(days).append(" ").append(wordsForm(days, daysForm)).append(", ");



        if(stringBuilder.length() > 0)
            stringBuilder.setLength(stringBuilder.length() - 2);

        return stringBuilder.toString();
    }


}
