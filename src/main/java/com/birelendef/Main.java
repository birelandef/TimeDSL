package com.birelendef;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.birelendef.TimeSequence.timeSequence;

public class Main {
    public static void main(String[] args) {
        LocalDateTime today = LocalDateTime.now();
        Duration duration = Duration.of(1, ChronoUnit.DAYS);
        TimeSequence<Integer> ts = timeSequence();
        System.out.println(ts);
        ts.append(1, today, duration);
        ts.append(2, duration);
        System.out.println(ts);
    }
//
//    public static void main(String[] args) {
//        LocalDateTime currentime = LocalDateTime.now();
//        System.out.println(currentime);
//        Duration duration = Duration.of(365, ChronoUnit.DAYS);
//
//
//        LocalDateTime today = LocalDateTime.now();
//        LocalDate birthday = LocalDate.of(1994, Month.OCTOBER, 20);
//        System.out.println(today.plusSeconds(duration.getSeconds()));
//        Period p = Period.between(birthday, today.toLocalDate());
//        long p2 = ChronoUnit.DAYS.between(birthday, today);
//        System.out.println("You are " + p.getYears() + " years, " + p.getMonths() +
//                " months, and " + p.getDays() +
//                " days old. (" + p2 + " days total)");
//
//    }
}
