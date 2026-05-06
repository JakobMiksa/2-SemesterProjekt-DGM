package dgm.controller;

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

    public SaleOrderController() {
        this.productDAO = new ProductDB();
        this.priceDAO = new PriceDB();
        this.stockItemDAO = new StockItemDB();
        this.saleOrderDAO = new SaleOrderDB();
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

        currentOrder.setPaymentMethod(paymentMethod);
    }

    public void confirmSale() {
        if (currentOrder == null) {
            throw new IllegalStateException("No active order exists.");
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
        } catch (RuntimeException e) {
            connection.rollbackTransaction();
            throw e;
        }
    }

    public SaleOrder getCurrentOrder() {
        return currentOrder;
    }
}
