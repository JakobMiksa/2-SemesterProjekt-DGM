package dgm.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import dgm.database.*;
import dgm.model.*;

public class SaleOrderController {

    private ProductDAO productDAO;
    private PriceDAO priceDAO;
    private StockItemDAO stockItemDAO;
    private SaleOrderDAO saleOrderDAO;
    private SaleOrder currentOrder;

    public SaleOrderController() throws DataAccessException {
        this.productDAO = new ProductDB();
        this.priceDAO = new PriceDB();
        this.stockItemDAO = new StockItemDB();
        this.saleOrderDAO = new SaleOrderDB();
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
            for (SaleOrderLine orderLine : currentOrder.getOrderLines()) {
                if (orderLine.getProduct().getProductNumber() == productNumber) {
                    requestedQuantity += orderLine.getQuantity();
                }
            }
        }

        if (stockItem.getAvailableQty() < requestedQuantity) {
            throw new IllegalArgumentException("Not enough in stock.");
        }

        if (currentOrder == null) {
            currentOrder = new SaleOrder();
        }

        SaleOrderLine orderLine = new SaleOrderLine(product, quantity, price.getAmount());
        currentOrder.addLine(orderLine);
    }

    public void registerPaymentConfirmation(String paymentMethod) {
        if (currentOrder == null) {
            throw new IllegalStateException("No active order exists.");
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required.");
        }

        currentOrder.setPaymentMethod(paymentMethod);
    }

    public void confirmSale() throws DataAccessException {
        if (currentOrder == null) {
            throw new IllegalStateException("No active order exists.");
        }

        if (currentOrder.getOrderLines().isEmpty()) {
            throw new IllegalStateException("Order has no order lines.");
        }

        if (currentOrder.getPaymentMethod() == null || currentOrder.getPaymentMethod().trim().isEmpty()) {
            throw new IllegalStateException("Payment has not been confirmed.");
        }

        DBConnection connection = DBConnection.getInstance();

        try {
            connection.startTransaction();

            currentOrder.setDate(LocalDate.now());
            currentOrder.calculateTotal();

            for (SaleOrderLine orderLine : currentOrder.getOrderLines()) {
                int productNumber = orderLine.getProduct().getProductNumber();
                int quantity = orderLine.getQuantity();
                stockItemDAO.decreaseAvailableQty(productNumber, quantity);
            }

            saleOrderDAO.save(currentOrder);
            connection.commitTransaction();

            currentOrder = null;
        } catch (DataAccessException e) {
            connection.rollbackTransaction();
            throw e;
        } catch (RuntimeException e) {
            connection.rollbackTransaction();
            throw e;
        }
    }

    public SaleOrder getCurrentOrder() {
        return currentOrder;
    }

    public void clearCurrentOrder() {
        currentOrder = null;
    }
}
