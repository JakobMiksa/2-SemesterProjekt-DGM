package dgm.controller;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dgm.database.CustomerDAO;
import dgm.database.DataAccessException;
import dgm.database.PriceDAO;
import dgm.database.ProductDAO;
import dgm.database.ReservedOrderDAO;
import dgm.database.SaleOrderDAO;
import dgm.database.StockItemDAO;
import dgm.model.Customer;
import dgm.model.Location;
import dgm.model.Price;
import dgm.model.Product;
import dgm.model.ProductCategory;
import dgm.model.ReservedOrder;
import dgm.model.SaleOrder;
import dgm.model.StockItem;

public class ControllerAlternativeFlowTest {

    @Test
    public void reservationAddProductThrowsWhenQuantityIsGreaterThanStock() throws Exception {
        TestReservationOrderController controller = newReservationController(1, false);

        try {
            controller.addProduct(1001, 2);
            fail("Expected reservation to fail when there is not enough stock.");
        } catch (IllegalArgumentException e) {
            assertEquals("There is not enough in stock.", e.getMessage());
        }

        assertNull("No reservation should be created when stock is too low.", controller.getCurrentOrder());
    }

    @Test
    public void reservationConfirmThrowsWhenPaymentHasNotBeenConfirmed() throws Exception {
        TestReservationOrderController controller = newReservationController(5, false);
        controller.addProduct(1001, 1);
        controller.enterReservationInformation("Marvin", "22222222");

        try {
            controller.confirmReservation();
            fail("Expected reservation to fail when payment has not been confirmed.");
        } catch (IllegalStateException e) {
            assertEquals("Payment has not been confirmed.", e.getMessage());
        }

        assertFalse("Transaction should not start before payment is confirmed.", controller.transactionStarted);
        assertFalse("Reservation should not be committed without payment.", controller.committed);
        assertFalse("Reservation should not be rolled back when no transaction was started.", controller.rolledBack);
    }

    @Test
    public void reservationConfirmRollsBackWhenStockUpdateFails() throws Exception {
        TestReservationOrderController controller = newReservationController(5, true);
        FakeReservedOrderDAO reservedOrderDAO = (FakeReservedOrderDAO) controller.reservedOrderDAO;
        controller.addProduct(1001, 1);
        controller.enterReservationInformation("Marvin", "22222222");
        controller.registerPaymentConfirmation("MobilePay");

        try {
            controller.confirmReservation();
            fail("Expected reservation to fail when stock update fails.");
        } catch (DataAccessException e) {
            assertEquals("Stock update failed.", e.getMessage());
        }

        assertTrue("Transaction should start before stock is updated.", controller.transactionStarted);
        assertTrue("Transaction should be rolled back when stock update fails.", controller.rolledBack);
        assertFalse("Reservation should not be committed when stock update fails.", controller.committed);
        assertFalse("Reservation should not be saved when stock update fails.", reservedOrderDAO.saved);
        assertNotNull("Current reservation should still exist after a failed confirmation.", controller.getCurrentOrder());
    }

    @Test
    public void saleAddProductThrowsWhenQuantityIsGreaterThanStock() throws Exception {
        TestSaleOrderController controller = newSaleController(1, false);

        try {
            controller.addProduct(1001, 2);
            fail("Expected sale to fail when there is not enough stock.");
        } catch (IllegalArgumentException e) {
            assertEquals("Not enough in stock.", e.getMessage());
        }

        assertNull("No sale should be created when stock is too low.", controller.getCurrentOrder());
    }

    @Test
    public void saleConfirmThrowsWhenPaymentHasNotBeenConfirmed() throws Exception {
        TestSaleOrderController controller = newSaleController(5, false);
        controller.addProduct(1001, 1);

        try {
            controller.confirmSale();
            fail("Expected sale to fail when payment has not been confirmed.");
        } catch (IllegalStateException e) {
            assertEquals("Payment has not been confirmed.", e.getMessage());
        }

        assertFalse("Transaction should not start before payment is confirmed.", controller.transactionStarted);
        assertFalse("Sale should not be committed without payment.", controller.committed);
        assertFalse("Sale should not be rolled back when no transaction was started.", controller.rolledBack);
    }

    @Test
    public void saleConfirmRollsBackWhenStockUpdateFails() throws Exception {
        TestSaleOrderController controller = newSaleController(5, true);
        FakeSaleOrderDAO saleOrderDAO = (FakeSaleOrderDAO) controller.saleOrderDAO;
        controller.addProduct(1001, 1);
        controller.registerPaymentConfirmation("MobilePay");

        try {
            controller.confirmSale();
            fail("Expected sale to fail when stock update fails.");
        } catch (DataAccessException e) {
            assertEquals("Stock update failed.", e.getMessage());
        }

        assertTrue("Transaction should start before stock is updated.", controller.transactionStarted);
        assertTrue("Transaction should be rolled back when stock update fails.", controller.rolledBack);
        assertFalse("Sale should not be committed when stock update fails.", controller.committed);
        assertFalse("Sale should not be saved when stock update fails.", saleOrderDAO.saved);
        assertNotNull("Current sale should still exist after a failed confirmation.", controller.getCurrentOrder());
    }

    private TestReservationOrderController newReservationController(int availableQty, boolean failOnDecrease) {
        Product product = new Product(1001, "Æg 10 stk", ProductCategory.EGGS);
        Price price = new Price(new BigDecimal("25.00"), LocalDate.now(), null);
        StockItem stockItem = new StockItem(product, new Location("Vejbod"), availableQty, LocalDate.now().plusDays(7));

        return new TestReservationOrderController(
                new FakeProductDAO(product),
                new FakePriceDAO(price),
                new FakeStockItemDAO(stockItem, failOnDecrease),
                new FakeCustomerDAO(new Customer("Marvin", "22222222")),
                new FakeReservedOrderDAO());
    }

    private TestSaleOrderController newSaleController(int availableQty, boolean failOnDecrease) {
        Product product = new Product(1001, "Æg 10 stk", ProductCategory.EGGS);
        Price price = new Price(new BigDecimal("25.00"), LocalDate.now(), null);
        StockItem stockItem = new StockItem(product, new Location("Vejbod"), availableQty, LocalDate.now().plusDays(7));

        return new TestSaleOrderController(
                new FakeProductDAO(product),
                new FakePriceDAO(price),
                new FakeStockItemDAO(stockItem, failOnDecrease),
                new FakeSaleOrderDAO());
    }

    private static class TestReservationOrderController extends ReservationOrderController {
        private ReservedOrderDAO reservedOrderDAO;
        private boolean transactionStarted;
        private boolean committed;
        private boolean rolledBack;

        TestReservationOrderController(ProductDAO productDAO, PriceDAO priceDAO, StockItemDAO stockItemDAO,
                CustomerDAO customerDAO, ReservedOrderDAO reservedOrderDAO) {
            super(productDAO, priceDAO, stockItemDAO, customerDAO, reservedOrderDAO);
            this.reservedOrderDAO = reservedOrderDAO;
        }

        @Override
        protected void startTransaction() {
            transactionStarted = true;
        }

        @Override
        protected void commitTransaction() {
            committed = true;
        }

        @Override
        protected void rollbackTransaction() {
            rolledBack = true;
        }
    }

    private static class TestSaleOrderController extends SaleOrderController {
        private SaleOrderDAO saleOrderDAO;
        private boolean transactionStarted;
        private boolean committed;
        private boolean rolledBack;

        TestSaleOrderController(ProductDAO productDAO, PriceDAO priceDAO, StockItemDAO stockItemDAO,
                SaleOrderDAO saleOrderDAO) {
            super(productDAO, priceDAO, stockItemDAO, saleOrderDAO);
            this.saleOrderDAO = saleOrderDAO;
        }

        @Override
        protected void startTransaction() {
            transactionStarted = true;
        }

        @Override
        protected void commitTransaction() {
            committed = true;
        }

        @Override
        protected void rollbackTransaction() {
            rolledBack = true;
        }
    }

    private static class FakeProductDAO implements ProductDAO {
        private Product product;

        FakeProductDAO(Product product) {
            this.product = product;
        }

        @Override
        public Product findByProductNumber(int productNumber) {
            return product.getProductNumber() == productNumber ? product : null;
        }
    }

    private static class FakePriceDAO implements PriceDAO {
        private Price price;

        FakePriceDAO(Price price) {
            this.price = price;
        }

        @Override
        public Price findCurrentPrice(int productNumber) {
            return price;
        }
    }

    private static class FakeStockItemDAO implements StockItemDAO {
        private StockItem stockItem;
        private boolean failOnDecrease;

        FakeStockItemDAO(StockItem stockItem, boolean failOnDecrease) {
            this.stockItem = stockItem;
            this.failOnDecrease = failOnDecrease;
        }

        @Override
        public StockItem findAvailableStock(int productNumber) {
            return stockItem.getProduct().getProductNumber() == productNumber ? stockItem : null;
        }

        @Override
        public List<StockItem> findAllAvailableStock() {
            return Arrays.asList(stockItem);
        }

        @Override
        public void decreaseAvailableQty(int productNumber, int quantity) throws DataAccessException {
            if (failOnDecrease) {
                throw new DataAccessException("Stock update failed.");
            }

            stockItem.decreaseAvailableQty(quantity);
        }
    }

    private static class FakeCustomerDAO implements CustomerDAO {
        private Customer customer;

        FakeCustomerDAO(Customer customer) {
            this.customer = customer;
        }

        @Override
        public Customer findByPhone(String phoneNo) {
            return customer.getPhoneNo().equals(phoneNo) ? customer : null;
        }
    }

    private static class FakeReservedOrderDAO implements ReservedOrderDAO {
        private boolean saved;

        @Override
        public void save(ReservedOrder order) {
            saved = true;
        }
    }

    private static class FakeSaleOrderDAO implements SaleOrderDAO {
        private boolean saved;

        @Override
        public void save(SaleOrder order) {
            saved = true;
        }
    }
}
