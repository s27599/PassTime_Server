/**
 *
 *  @author Kaczor Wiktor S27599
 *
 */

package zad1;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.TimeZone;

public class Time {
    public static String passed(String from, String to) {
        try {
            if (from.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}")) {
                LocalDateTime fromTime = LocalDateTime.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                LocalDateTime toTime = LocalDateTime.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));


                Duration between = Duration.between(fromTime, toTime);
                Duration betweenForHours = Duration.between(fromTime.atZone(TimeZone.getTimeZone("ECT").toZoneId()), toTime.atZone(TimeZone.getTimeZone("ECT").toZoneId()));

                StringBuilder info = new StringBuilder();
                info.append("Od ").append(fromTime.getDayOfMonth())
                        .append(" ").append(fromTime.getMonth().getDisplayName(TextStyle.FULL, new Locale("pl", "PL"))).append(" ")
                        .append(fromTime.getYear()).append(" (").append(fromTime.getDayOfWeek().
                                getDisplayName(TextStyle.FULL, new Locale("pl", "PL"))).append(") godz. ")
                        .append(fromTime.getHour()).append(":");
                if (fromTime.getMinute() < 10) {
                    info.append("0").append(fromTime.getMinute());
                } else {
                    info.append(fromTime.getMinute());
                }


                info.append(" do ").append(toTime.getDayOfMonth()).append(" ").append(toTime.getMonth().getDisplayName(TextStyle.FULL, new Locale("pl", "PL")))
                        .append(" ").append(toTime.getYear()).append(" (").append(toTime.getDayOfWeek().
                                getDisplayName(TextStyle.FULL, new Locale("pl", "PL"))).append(") godz. ")
                        .append(toTime.getHour()).append(":");
                if (toTime.getMinute() < 10) {
                    info.append("0").append(toTime.getMinute());
                } else {
                    info.append(toTime.getMinute());
                }
                info.append("\n");


                StringBuilder nonCalendar = getNonCalendarStringBuilder(between);

                nonCalendar.append("\n - godzin: ");
                nonCalendar.append(betweenForHours.toHours());
                nonCalendar.append(", minut: ");
                nonCalendar.append(betweenForHours.toMinutes());

                LocalDate tmpLocalDateFrom = fromTime.toLocalDate();
                LocalDate tmpLocalDateTo = toTime.toLocalDate();

                StringBuilder sb = getCalendarStringBuilder(tmpLocalDateFrom, tmpLocalDateTo);


                return info.toString() + nonCalendar.toString() + sb.toString();
            }
//bez godziny

            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);

            StringBuilder info = new StringBuilder();
            info.append("Od ").append(fromDate.getDayOfMonth())
                    .append(" ").append(fromDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("pl", "PL"))).append(" ")
                    .append(fromDate.getYear()).append(" (").append(fromDate.getDayOfWeek().
                            getDisplayName(TextStyle.FULL, new Locale("pl", "PL"))).append(")");


            info.append(" do ").append(toDate.getDayOfMonth()).append(" ").append(toDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("pl", "PL")))
                    .append(" ").append(toDate.getYear()).append(" (").append(toDate.getDayOfWeek().
                            getDisplayName(TextStyle.FULL, new Locale("pl", "PL"))).append(")\n");


            Duration between = Duration.between(fromDate.atStartOfDay(), toDate.atStartOfDay());
            StringBuilder nonCalendar = getNonCalendarStringBuilder(between);

            StringBuilder calendar = getCalendarStringBuilder(fromDate, toDate);

            return info.toString() + nonCalendar.toString() + calendar.toString();

        } catch (DateTimeParseException e) {
            return ("*** " + e);
        }


    }
    private static StringBuilder getNonCalendarStringBuilder(Duration between) {
        StringBuilder nonCalendar = new StringBuilder();
        nonCalendar.append(" - mija: ");
        int offset = 0;
        if (between.toHours()%24 != 0 || between.toMinutes()%1440 != 0)
            offset = 1;
        if (between.toDays() == 1) {
            nonCalendar.append(1 + offset).append(" dzień,");
        } else {
            nonCalendar.append(between.toDays()+offset).append(" dni,");
        }

        double weeks = (double) (between.toDays()+offset) / 7.0;
        nonCalendar.append(" tygodni ");
        nonCalendar.append((double) Math.round(weeks * 100) / 100.0);
        return nonCalendar;
    }

    private static StringBuilder getCalendarStringBuilder(LocalDate tmpLocalDateFrom, LocalDate tmpLocalDateTo) {
        Period period = Period.between(tmpLocalDateFrom, tmpLocalDateTo);

        StringBuilder sb = new StringBuilder();
        sb.append("\n - kalendarzowo: ");

        String afterYear = "";
        String afterMonth = "";
        if(period.getMonths()!=0||period.getDays()!=0)
            afterYear = ", ";
        if(period.getDays()!=0)
            afterMonth =", ";


        if (period.getYears() == 1)
            sb.append(1 + " rok").append(afterYear);
        else if (period.getYears() > 1 && period.getYears() < 5) {
            sb.append(period.getYears()).append(" lata").append(afterYear);
        } else if (period.getYears() != 0) {
            sb.append(period.getYears()).append(" lat").append(afterYear);
        }
        if (period.getMonths() == 1)
            sb.append(1 + " miesiąc").append(afterMonth);
        else if (period.getMonths() > 1 && period.getMonths() < 5) {
            sb.append(period.getMonths()).append(" miesiące").append(afterMonth);
        } else if (period.getMonths() != 0) {
            sb.append(period.getMonths()).append(" miesięcy").append(afterMonth);
        }
        if (period.getDays() == 1)
            sb.append(1 + " dzień");
        else if (period.getDays() != 0) {
            sb.append(period.getDays()).append(" dni");
        }
//        sb.append("\n");D
        return sb;
    }
}
