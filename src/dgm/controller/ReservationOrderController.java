package dgm.controller;

import java.time.LocalDate;
import java.util.List;

import dgm.database.*;
import dgm.model.*;

public class ReservationOrderController {

    private ProductDAO productDAO;
    private PriceDAO priceDAO;
    private StockItemDAO stockItemDAO;
    private CustomerDAO customerDAO;
    private ReservedOrderDAO reservedOrderDAO;
    private ReservedOrder currentOrder;

    public ReservationOrderController() {
        this.productDAO = new ProductDB();
        this.priceDAO = new PriceDB();
        this.stockItemDAO = new StockItemDB();
        this.customerDAO = new CustomerDB();
        this.reservedOrderDAO = new ReservedOrderDB();
    }

    public List<Product> showAvailableProducts() {
        return productDAO.findAvailableProducts();
    }

    public void addProduct(int productNumber, int quantity) {
        Product product = productDAO.findByProductNumber(productNumber);
        Price price = priceDAO.findCurrentPrice(productNumber);
        StockItem stockItem = stockItemDAO.findAvailableStock(productNumber);

        if (product == null || price == null || stockItem == null) {
            throw new IllegalArgumentException("Product not found.");
        }

        if (stockItem.getAvailableQty() < quantity) {
            throw new IllegalArgumentException("There is not enough in stock.");
        }

        if (currentOrder == null) {
            currentOrder = new ReservedOrder();
        }

        ReservedOrderLine orderLine = new ReservedOrderLine(product, quantity, price.getAmount());
        currentOrder.addLine(orderLine);
    }

    public void enterReservationInformation(String name, String phoneNo) {
        if (currentOrder == null) {
            throw new IllegalStateException("No active reservation exists.");
        }

        Customer customer = customerDAO.findByPhone(phoneNo);

        if (customer == null) {
            throw new IllegalArgumentException("Could not find phone in the system.");
        }

        currentOrder.setCustomer(customer);
    }

    public void registerPaymentConfirmation(String paymentMethod) {
        if (currentOrder == null) {
            throw new IllegalStateException("There is no active reservation.");
        }

        currentOrder.setPaymentMethod(paymentMethod);
    }

    public void confirmReservation() {
        if (currentOrder == null) {
            throw new IllegalStateException("Active reservation does not exist.");
        }

        DBConnection connection = DBConnection.getInstance();

        try {
            connection.startTransaction();

            currentOrder.setDate(LocalDate.now());
            currentOrder.setExpiryDate(LocalDate.now().plusDays(3));
            currentOrder.calculateTotal();

            for (ReservedOrderLine orderLine : currentOrder.getOrderLines()) {
                int productNumber = orderLine.getProduct().getProductNumber();
                int quantity = orderLine.getQuantity();
                stockItemDAO.decreaseAvailableQty(productNumber, quantity);
            }

            reservedOrderDAO.save(currentOrder);
            connection.commitTransaction();

            currentOrder = null;
        } catch (RuntimeException e) {
            connection.rollbackTransaction();
            throw e;
        }
    }

    public ReservedOrder getCurrentOrder() {
        return currentOrder;
    }
}
