package dgm.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Price {

    private BigDecimal amount;
    private LocalDate validFrom;
    private LocalDate validTo;

    public Price(BigDecimal amount, LocalDate validFrom, LocalDate validTo) {
        this.amount = amount;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }
}
