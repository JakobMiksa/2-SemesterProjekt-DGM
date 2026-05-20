package dgm.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class ReservedOrderTest {

    @Test
    public void addLineAddsOrderLineToReservedOrder() {
        ReservedOrder order = new ReservedOrder();
        Product product = new Product(2001, "Oksekød 1 kg", ProductCategory.MEAT);
        ReservedOrderLine line = new ReservedOrderLine(product, 1, new BigDecimal("160.00"));

        order.addLine(line);

        assertEquals("Reserved order should contain one order line.", 1, order.getOrderLines().size());
        assertSame("Reserved order should contain the line that was added.", line, order.getOrderLines().get(0));
    }

    @Test
    public void calculateTotalReturnsSumOfReservedOrderLines() {
        ReservedOrder order = new ReservedOrder();
        Product meat = new Product(2001, "Oksekød 1 kg", ProductCategory.MEAT);
        Product eggs = new Product(1001, "Æg 10 stk", ProductCategory.EGGS);

        order.addLine(new ReservedOrderLine(meat, 1, new BigDecimal("160.00")));
        order.addLine(new ReservedOrderLine(eggs, 2, new BigDecimal("25.00")));

        BigDecimal total = order.calculateTotal();

        assertBigDecimalEquals("210.00", total);
        assertBigDecimalEquals("210.00", order.getTotal());
    }

    @Test
    public void calculateTotalReturnsZeroWhenReservedOrderHasNoLines() {
        ReservedOrder order = new ReservedOrder();

        BigDecimal total = order.calculateTotal();

        assertBigDecimalEquals("0", total);
        assertBigDecimalEquals("0", order.getTotal());
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals("BigDecimal value differs.", 0, new BigDecimal(expected).compareTo(actual));
    }
}
