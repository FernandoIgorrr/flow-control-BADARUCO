
package br.com.midnightsyslabs.flow_control.domain.entity.revenue;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Revenue{

    public BigDecimal getRevenue();
    public LocalDate getDate();
}
