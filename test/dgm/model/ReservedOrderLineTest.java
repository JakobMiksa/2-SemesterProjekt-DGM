package dgm.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class ReservedOrderLineTest {

    @Test
    public void getSubtotalReturnsQuantityTimesUnitPrice() {
        Product product = new Product(2001, "Oksekød 1 kg", ProductCategory.MEAT);
        ReservedOrderLine line = new ReservedOrderLine(product, 2, new BigDecimal("160.00"));

        BigDecimal subtotal = line.getSubtotal();

        assertBigDecimalEquals("320.00", subtotal);
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals("BigDecimal value differs.", 0, new BigDecimal(expected).compareTo(actual));
    }
}
