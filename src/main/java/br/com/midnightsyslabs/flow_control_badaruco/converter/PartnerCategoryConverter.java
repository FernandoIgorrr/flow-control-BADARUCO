package br.com.midnightsyslabs.flow_control_badaruco.converter;

import org.springframework.boot.micrometer.metrics.autoconfigure.MetricsProperties.Web.Client;

import br.com.midnightsyslabs.flow_control_badaruco.view.PartnerCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PartnerCategoryConverter implements AttributeConverter<PartnerCategory, String> {

    @Override
    public String convertToDatabaseColumn(PartnerCategory attribute) {
        return attribute == null ? "u" : String.valueOf(attribute.getCode());
    }

    @Override
    public PartnerCategory convertToEntityAttribute(String dbData) {
        return dbData == null ? PartnerCategory.UNDEFINED : PartnerCategory.fromCode(dbData.charAt(0));
    }
}