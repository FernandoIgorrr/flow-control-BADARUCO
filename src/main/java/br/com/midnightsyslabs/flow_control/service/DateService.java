package br.com.midnightsyslabs.flow_control.service;

import java.time.LocalDate;

import br.com.midnightsyslabs.flow_control.config.TimeIntervalEnum;

public class DateService {

    public static LocalDate timeIntervalEnumToDateFrom(TimeIntervalEnum interval) {
        LocalDate today = LocalDate.now();
        switch (interval) {
            case TimeIntervalEnum.LAST_7_DAYS:
                return today.minusDays(7);
            case TimeIntervalEnum.LAST_15_DAYS:
                return today.minusDays(15);
            case TimeIntervalEnum.LAST_30_DAYS:
                return today.minusDays(30);
            case TimeIntervalEnum.PREVIOUS_MONTH:
                return today
                        .minusMonths(1)
                        .withDayOfMonth(1);
            case LAST_SEMESTER: {
                LocalDate target = today.minusMonths(6);
                int safeDay = Math.min(
                        today.getDayOfMonth(),
                        target.lengthOfMonth());
                return target.withDayOfMonth(safeDay);
            }

            case TimeIntervalEnum.PREVIOUS_YEAR:
                return today
                        .minusYears(1)
                        .withMonth(1)
                        .withDayOfMonth(1);
            case TimeIntervalEnum.CURRENT_MONTH:
               return today.withDayOfMonth(1);
            case TimeIntervalEnum.CURRENT_YEAR:
                return today
                        .withMonth(1)
                        .withDayOfMonth(1);
            default:
                return today.minusDays(7);
        }
    }

    public static LocalDate timeIntervalEnumToDateTo(TimeIntervalEnum interval) {
        LocalDate today = LocalDate.now();
        switch (interval) {
            case TimeIntervalEnum.PREVIOUS_MONTH:
                var lastMonth = today.minusMonths(1);
                return lastMonth
                        .withDayOfMonth(lastMonth.lengthOfMonth());
            case TimeIntervalEnum.PREVIOUS_YEAR:
                return today
                        .minusYears(1)
                        .withMonth(12)
                        .withDayOfMonth(31);
            default:
                return today;

        }
    }
}
