package br.com.midnightsyslabs.flow_control.view;

public enum PartnerCategory {
    PERSONAL('p'),
    COMPANY('c'),
    UNDEFINED('u');

    private final char code;

    PartnerCategory(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }

    public static PartnerCategory fromCode(char code) {
        for (PartnerCategory category : PartnerCategory.values()) {
            if (category.getCode() == code) {
                return category;
            }
        }
        return UNDEFINED;
    }
}
