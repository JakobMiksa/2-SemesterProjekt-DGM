package dgm.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class SaleOrderLineTest {

    @Test
    public void getSubtotalReturnsQuantityTimesUnitPrice() {
        Product product = new Product(1001, "Æg 10 stk", ProductCategory.EGGS);
        SaleOrderLine line = new SaleOrderLine(product, 3, new BigDecimal("25.00"));

        BigDecimal subtotal = line.getSubtotal();

        assertBigDecimalEquals("75.00", subtotal);
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals("BigDecimal value differs.", 0, new BigDecimal(expected).compareTo(actual));
    }
}
