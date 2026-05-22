package dgm.controller;

import java.math.BigDecimal;
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

    public ReservationOrderController() throws DataAccessException {
        this.productDAO = new ProductDB();
        this.priceDAO = new PriceDB();
        this.stockItemDAO = new StockItemDB();
        this.customerDAO = new CustomerDB();
        this.reservedOrderDAO = new ReservedOrderDB();
    }

    ReservationOrderController(ProductDAO productDAO, PriceDAO priceDAO, StockItemDAO stockItemDAO,
            CustomerDAO customerDAO, ReservedOrderDAO reservedOrderDAO) {
        this.productDAO = productDAO;
        this.priceDAO = priceDAO;
        this.stockItemDAO = stockItemDAO;
        this.customerDAO = customerDAO;
        this.reservedOrderDAO = reservedOrderDAO;
    }

    public List<StockItem> showAvailableProducts() throws DataAccessException {
        return stockItemDAO.findAllAvailableStock();
    }

    public void addProduct(int productNumber, int quantity) throws DataAccessException {
        if (productNumber <= 0) {
            throw new IllegalArgumentException("Product number must be greater than zero.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Product product = productDAO.findByProductNumber(productNumber);
        Price price = priceDAO.findCurrentPrice(productNumber);
        StockItem stockItem = stockItemDAO.findAvailableStock(productNumber);

        if (product == null || price == null || stockItem == null) {
            throw new IllegalArgumentException("Product not found.");
        }

        if (price.getAmount() == null || price.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price is invalid.");
        }

        int requestedQuantity = quantity;

        if (currentOrder != null) {
            for (ReservedOrderLine orderLine : currentOrder.getOrderLines()) {
                if (orderLine.getProduct().getProductNumber() == productNumber) {
                    requestedQuantity += orderLine.getQuantity();
                }
            }
        }

        if (stockItem.getAvailableQty() < requestedQuantity) {
            throw new IllegalArgumentException("There is not enough in stock.");
        }

        if (currentOrder == null) {
            currentOrder = new ReservedOrder();
        }

        ReservedOrderLine orderLine = new ReservedOrderLine(product, quantity, price.getAmount());
        currentOrder.addLine(orderLine);
    }

    public void enterReservationInformation(String name, String phoneNo) throws DataAccessException {
        if (currentOrder == null) {
            throw new IllegalStateException("No active reservation exists.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        if (phoneNo == null || phoneNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required.");
        }

        Customer customer = customerDAO.findByPhone(phoneNo.trim());

        if (customer == null) {
            throw new IllegalArgumentException("Could not find phone in the system.");
        }

        if (!customer.getName().equalsIgnoreCase(name.trim())) {
            throw new IllegalArgumentException("Name and phone number do not match.");
        }

        currentOrder.setCustomer(customer);
    }

    public void registerPaymentConfirmation(String paymentMethod) {
        if (currentOrder == null) {
            throw new IllegalStateException("There is no active reservation.");
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required.");
        }

        currentOrder.setPaymentMethod(paymentMethod);
    }

    public void confirmReservation() throws DataAccessException {
        if (currentOrder == null) {
            throw new IllegalStateException("Active reservation does not exist.");
        }

        if (currentOrder.getOrderLines().isEmpty()) {
            throw new IllegalStateException("Reservation has no order lines.");
        }

        if (currentOrder.getCustomer() == null) {
            throw new IllegalStateException("Reservation is missing customer.");
        }

        if (currentOrder.getPaymentMethod() == null || currentOrder.getPaymentMethod().trim().isEmpty()) {
            throw new IllegalStateException("Payment has not been confirmed.");
        }

        boolean transactionStarted = false;

        try {
            startTransaction();
            transactionStarted = true;

            currentOrder.setDate(LocalDate.now());
            currentOrder.setExpiryDate(LocalDate.now().plusDays(3));
            currentOrder.calculateTotal();

            for (ReservedOrderLine orderLine : currentOrder.getOrderLines()) {
                int productNumber = orderLine.getProduct().getProductNumber();
                int quantity = orderLine.getQuantity();
                stockItemDAO.decreaseAvailableQty(productNumber, quantity);
            }

            reservedOrderDAO.save(currentOrder);
            commitTransaction();

            currentOrder = null;
        } catch (DataAccessException e) {
            if (transactionStarted) {
                rollbackTransaction();
            }
            throw e;
        } catch (RuntimeException e) {
            if (transactionStarted) {
                rollbackTransaction();
            }
            throw e;
        }
    }

    protected void startTransaction() {
        DBConnection.getInstance().startTransaction();
    }

    protected void commitTransaction() {
        DBConnection.getInstance().commitTransaction();
    }

    protected void rollbackTransaction() {
        DBConnection.getInstance().rollbackTransaction();
    }

    public ReservedOrder getCurrentOrder() {
        return currentOrder;
    }

    public void clearCurrentOrder() {
        currentOrder = null;
    }
}
