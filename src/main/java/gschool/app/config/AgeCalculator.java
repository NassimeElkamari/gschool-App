package gschool.app.config;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AgeCalculator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static int calculateAge(String dateNaissance) {
        if (dateNaissance == null || dateNaissance.isEmpty()) {
            throw new IllegalArgumentException("Date de naissance cannot be null or empty");
        }

        LocalDate birthDate = LocalDate.parse(dateNaissance, DATE_FORMATTER);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }
}