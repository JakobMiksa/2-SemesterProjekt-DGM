package dgm.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class SaleOrderTest {

    @Test
    public void addLineAddsOrderLineToSaleOrder() {
        SaleOrder order = new SaleOrder();
        Product product = new Product(1001, "Æg 10 stk", ProductCategory.EGGS);
        SaleOrderLine line = new SaleOrderLine(product, 1, new BigDecimal("25.00"));

        order.addLine(line);

        assertEquals("Sale order should contain one order line.", 1, order.getOrderLines().size());
        assertSame("Sale order should contain the line that was added.", line, order.getOrderLines().get(0));
    }

    @Test
    public void calculateTotalReturnsSumOfSaleOrderLines() {
        SaleOrder order = new SaleOrder();
        Product eggs = new Product(1001, "Æg 10 stk", ProductCategory.EGGS);
        Product carrots = new Product(3001, "Gulerødder 0.5 kg", ProductCategory.VEGETABLES);

        order.addLine(new SaleOrderLine(eggs, 2, new BigDecimal("25.00")));
        order.addLine(new SaleOrderLine(carrots, 3, new BigDecimal("12.50")));

        BigDecimal total = order.calculateTotal();

        assertBigDecimalEquals("87.50", total);
        assertBigDecimalEquals("87.50", order.getTotal());
    }

    @Test
    public void calculateTotalReturnsZeroWhenSaleOrderHasNoLines() {
        SaleOrder order = new SaleOrder();

        BigDecimal total = order.calculateTotal();

        assertBigDecimalEquals("0", total);
        assertBigDecimalEquals("0", order.getTotal());
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals("BigDecimal value differs.", 0, new BigDecimal(expected).compareTo(actual));
    }
}
