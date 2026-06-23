package br.com.midnightsyslabs.flow_control.config;

public enum TimeIntervalEnum {

    LAST_7_DAYS("Nos últimos 7 dias"),
    LAST_15_DAYS("Nos últimos 15 dias"),
    LAST_30_DAYS("Nos últimos 30 dias"),

    PREVIOUS_MONTH("No mês passado"),
    CURRENT_MONTH("No mês atual"),

    LAST_SEMESTER("Nos últimos 6 meses"),

    PREVIOUS_YEAR("No ano passado"),
    CURRENT_YEAR("No ano atual"),

    ALL_TIME("Todo tempo");

    private final String label;

    TimeIntervalEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
