package dgm.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dgm.controller.ReservationOrderController;
import dgm.controller.SaleOrderController;
import dgm.database.DataAccessException;
import dgm.model.Product;
import dgm.model.ProductCategory;
import dgm.model.ReservedOrder;
import dgm.model.ReservedOrderLine;
import dgm.model.SaleOrder;
import dgm.model.SaleOrderLine;
import dgm.model.StockItem;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private ReservationOrderController reservationOrderController;
    private SaleOrderController saleOrderController;

    private JLabel statusLabel;
    private JTabbedPane tabbedPane;
    private JTextField reservationNameField;
    private JTextField reservationPhoneField;
    private JLabel reservationTotalLabel;
    private JLabel saleTotalLabel;

    private DefaultTableModel reservationProductModel;
    private DefaultTableModel reservationCartModel;
    private DefaultTableModel saleProductModel;
    private DefaultTableModel saleCartModel;
    private DefaultTableModel stockProductModel;

    public MainFrame() {
        setTitle("Den Glade Bondemand");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);

        getRootPane().setBorder(new EmptyBorder(16, 16, 16, 16));
        setLayout(new BorderLayout(0, 16));

        JLabel header = new JLabel("Den Glade Bondemand", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 24f));

        statusLabel = new JLabel("Systemstatus: Starter systemet.");

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Reservation", createReservationPanel());
        tabbedPane.addTab("Salg", createSalePanel());
        tabbedPane.addTab("Lager", createStockPanel());

        add(header, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        createControllers();
        showAvailableProducts();
    }

    public void showAvailableProducts() {
        if (!databaseIsReady()) {
            return;
        }

        try {
            fillProductTableModel(reservationProductModel, reservationOrderController.showAvailableProducts());
            fillProductTableModel(saleProductModel, saleOrderController.showAvailableProducts());
            fillStockTableModel(stockProductModel, reservationOrderController.showAvailableProducts());
            showStatus("Produkter hentet fra databasen.");
        } catch (DataAccessException e) {
            showStatus("Produkterne kunne ikke hentes fra databasen.");
            showError("Produkterne kunne ikke hentes fra databasen.");
        } catch (RuntimeException e) {
            showStatus("Der opstod en fejl ved hentning af produkter.");
            showError("Der opstod en fejl ved hentning af produkter.");
        }
    }

    public void addProduct(int productNumber, int quantity) {
        int selectedTab = getSelectedTabIndex();

        if (selectedTab == 0) {
            addReservationProduct(productNumber, quantity);
        } else if (selectedTab == 1) {
            addSaleProduct(productNumber, quantity);
        }
    }

    public void enterReservationInformation(String name, String phoneNo) {
        if (!databaseIsReady()) {
            return;
        }

        try {
            reservationOrderController.enterReservationInformation(name, phoneNo);
            showStatus("Kundeoplysninger registreret.");
        } catch (DataAccessException e) {
            showStatus("Kundeoplysninger kunne ikke hentes.");
            showError("Kundeoplysninger kunne ikke hentes.");
        } catch (RuntimeException e) {
            showStatus("Kundeoplysningerne matcher ikke.");
            showError("Kundeoplysningerne matcher ikke en kunde i systemet.");
        }
    }

    public void registerPaymentConfirmation(String paymentMethod) {
        if (!databaseIsReady()) {
            return;
        }

        int selectedTab = getSelectedTabIndex();

        try {
            if (selectedTab == 0) {
                reservationOrderController.registerPaymentConfirmation(paymentMethod);
            } else if (selectedTab == 1) {
                saleOrderController.registerPaymentConfirmation(paymentMethod);
            }

            showStatus("Betaling registreret.");
        } catch (RuntimeException e) {
            showStatus("Betaling kunne ikke registreres.");
            showError("Betaling kunne ikke registreres.");
        }
    }

    public void confirmReservation() {
        if (!databaseIsReady()) {
            return;
        }

        try {
            ReservedOrder order = reservationOrderController.getCurrentOrder();
            reservationOrderController.confirmReservation();
            renderReservationCart();
            reservationNameField.setText("");
            reservationPhoneField.setText("");
            showAvailableProducts();
            showStatus("Reservation bekræftet og lager opdateret.");
            showReservationReceipt(order);
        } catch (DataAccessException e) {
            showStatus("Reservationen kunne ikke gemmes.");
            showError("Reservationen kunne ikke gemmes.");
        } catch (RuntimeException e) {
            showStatus("Reservationen kunne ikke gennemføres.");
            showError("Reservationen kunne ikke gennemføres.");
        }
    }

    public void confirmSale() {
        if (!databaseIsReady()) {
            return;
        }

        try {
            SaleOrder order = saleOrderController.getCurrentOrder();
            saleOrderController.confirmSale();
            renderSaleCart();
            showAvailableProducts();
            showStatus("Salg registreret og lager opdateret.");
            showSaleReceipt(order);
        } catch (DataAccessException e) {
            showStatus("Salget kunne ikke gemmes.");
            showError("Salget kunne ikke gemmes.");
        } catch (RuntimeException e) {
            showStatus("Salget kunne ikke gennemføres.");
            showError("Salget kunne ikke gennemføres.");
        }
    }

    private JPanel createReservationPanel() {
        JPanel panel = createBasePanel("Reservation");
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 12, 0));

        reservationProductModel = createProductTableModel();
        reservationCartModel = createCartTableModel();

        JTable productTable = createTable(reservationProductModel);
        JTable cartTable = createTable(reservationCartModel);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

        JButton addButton = new JButton("Tilføj til kurv");
        addButton.addActionListener(e -> addReservationProductFromTable(productTable, quantitySpinner));

        JPanel productPanel = new JPanel(new BorderLayout(0, 8));
        productPanel.add(new JLabel("Vælg produkt"), BorderLayout.NORTH);
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        productPanel.add(createAddPanel(quantitySpinner, addButton), BorderLayout.SOUTH);

        reservationNameField = new JTextField();
        reservationPhoneField = new JTextField();

        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        infoPanel.add(new JLabel("Navn:"));
        infoPanel.add(reservationNameField);
        infoPanel.add(new JLabel("Telefonnummer:"));
        infoPanel.add(reservationPhoneField);
        infoPanel.add(new JLabel("Betaling:"));
        infoPanel.add(createMobilePayCheckBox());

        JButton confirmButton = new JButton("Bekræft reservation");
        confirmButton.addActionListener(e -> confirmReservationFromForm());
        JButton clearButton = new JButton("Ryd kurv");
        clearButton.addActionListener(e -> clearReservationCart());
        reservationTotalLabel = new JLabel(formatTotal(BigDecimal.ZERO));

        JPanel orderPanel = new JPanel(new BorderLayout(0, 8));
        orderPanel.add(infoPanel, BorderLayout.NORTH);
        orderPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        orderPanel.add(createOrderBottomPanel(reservationTotalLabel, clearButton, confirmButton), BorderLayout.SOUTH);

        contentPanel.add(productPanel);
        contentPanel.add(orderPanel);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSalePanel() {
        JPanel panel = createBasePanel("Fysisk salg");
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 12, 0));

        saleProductModel = createProductTableModel();
        saleCartModel = createCartTableModel();

        JTable productTable = createTable(saleProductModel);
        JTable cartTable = createTable(saleCartModel);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

        JButton addButton = new JButton("Tilføj til kurv");
        addButton.addActionListener(e -> addSaleProductFromTable(productTable, quantitySpinner));

        JPanel productPanel = new JPanel(new BorderLayout(0, 8));
        productPanel.add(new JLabel("Vælg produkt"), BorderLayout.NORTH);
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        productPanel.add(createAddPanel(quantitySpinner, addButton), BorderLayout.SOUTH);

        JPanel paymentPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        paymentPanel.add(new JLabel("Betaling:"));
        paymentPanel.add(createMobilePayCheckBox());

        JButton confirmButton = new JButton("Registrer salg");
        confirmButton.addActionListener(e -> confirmSaleFromForm());
        JButton clearButton = new JButton("Ryd kurv");
        clearButton.addActionListener(e -> clearSaleCart());
        saleTotalLabel = new JLabel(formatTotal(BigDecimal.ZERO));

        JPanel orderPanel = new JPanel(new BorderLayout(0, 8));
        orderPanel.add(paymentPanel, BorderLayout.NORTH);
        orderPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        orderPanel.add(createOrderBottomPanel(saleTotalLabel, clearButton, confirmButton), BorderLayout.SOUTH);

        contentPanel.add(productPanel);
        contentPanel.add(orderPanel);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStockPanel() {
        JPanel panel = createBasePanel("Lageroversigt");

        stockProductModel = createStockTableModel();
        JTable stockTable = createTable(stockProductModel);

        JButton refreshButton = new JButton("Opdater lager");
        refreshButton.addActionListener(e -> showAvailableProducts());

        panel.add(new JScrollPane(stockTable), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAddPanel(JSpinner quantitySpinner, JButton addButton) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(new JLabel("Antal:"));
        actionPanel.add(quantitySpinner);
        actionPanel.add(addButton);
        return actionPanel;
    }

    private JPanel createOrderBottomPanel(JLabel totalLabel, JButton clearButton, JButton confirmButton) {
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 0));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        buttonPanel.add(clearButton);
        buttonPanel.add(confirmButton);

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    private void createControllers() {
        try {
            reservationOrderController = new ReservationOrderController();
            saleOrderController = new SaleOrderController();
            showStatus("Forbundet til databasen.");
        } catch (DataAccessException e) {
            showStatus("Kunne ikke forbinde til databasen.");
            showError("Kunne ikke forbinde til databasen.");
        } catch (RuntimeException e) {
            showStatus("Kunne ikke forbinde til databasen.");
            showError("Kunne ikke forbinde til databasen.");
        }
    }

    private void addReservationProductFromTable(JTable productTable, JSpinner quantitySpinner) {
        int productNumber = getSelectedProductNumber(productTable);

        if (productNumber <= 0) {
            return;
        }

        addReservationProduct(productNumber, (int) quantitySpinner.getValue());
    }

    private void addSaleProductFromTable(JTable productTable, JSpinner quantitySpinner) {
        int productNumber = getSelectedProductNumber(productTable);

        if (productNumber <= 0) {
            return;
        }

        addSaleProduct(productNumber, (int) quantitySpinner.getValue());
    }

    private void addReservationProduct(int productNumber, int quantity) {
        if (!databaseIsReady()) {
            return;
        }

        try {
            reservationOrderController.addProduct(productNumber, quantity);
            renderReservationCart();
            showStatus("Produkt tilføjet til reservation.");
        } catch (DataAccessException e) {
            showStatus("Produktet kunne ikke tilføjes.");
            showError("Produktet kunne ikke tilføjes.");
        } catch (RuntimeException e) {
            showStatus("Produktet kunne ikke tilføjes.");
            showError("Produktet kunne ikke tilføjes. Tjek antal og lagerstatus.");
        }
    }

    private void addSaleProduct(int productNumber, int quantity) {
        if (!databaseIsReady()) {
            return;
        }

        try {
            saleOrderController.addProduct(productNumber, quantity);
            renderSaleCart();
            showStatus("Produkt tilføjet til salg.");
        } catch (DataAccessException e) {
            showStatus("Produktet kunne ikke tilføjes.");
            showError("Produktet kunne ikke tilføjes.");
        } catch (RuntimeException e) {
            showStatus("Produktet kunne ikke tilføjes.");
            showError("Produktet kunne ikke tilføjes. Tjek antal og lagerstatus.");
        }
    }

    private void clearReservationCart() {
        if (!databaseIsReady()) {
            return;
        }

        reservationOrderController.clearCurrentOrder();
        renderReservationCart();
        showStatus("Reservationskurven er ryddet.");
    }

    private void clearSaleCart() {
        if (!databaseIsReady()) {
            return;
        }

        saleOrderController.clearCurrentOrder();
        renderSaleCart();
        showStatus("Salgskurven er ryddet.");
    }

    private void confirmReservationFromForm() {
        if (!databaseIsReady()) {
            return;
        }

        try {
            reservationOrderController.enterReservationInformation(
                    reservationNameField.getText(),
                    reservationPhoneField.getText());
            reservationOrderController.registerPaymentConfirmation("MobilePay");
            confirmReservation();
        } catch (DataAccessException e) {
            showStatus("Reservationen kunne ikke gennemføres.");
            showError("Reservationen kunne ikke gennemføres.");
        } catch (RuntimeException e) {
            showStatus("Reservationen mangler oplysninger eller kunden matcher ikke.");
            showError("Reservationen mangler oplysninger, eller navn og telefonnummer matcher ikke.");
        }
    }

    private void confirmSaleFromForm() {
        if (!databaseIsReady()) {
            return;
        }

        try {
            saleOrderController.registerPaymentConfirmation("MobilePay");
            confirmSale();
        } catch (RuntimeException e) {
            showStatus("Salget kunne ikke gennemføres.");
            showError("Salget kunne ikke gennemføres.");
        }
    }

    private void renderReservationCart() {
        clearTable(reservationCartModel);
        reservationTotalLabel.setText(formatTotal(BigDecimal.ZERO));

        ReservedOrder currentOrder = reservationOrderController.getCurrentOrder();

        if (currentOrder == null) {
            return;
        }

        for (ReservedOrderLine line : currentOrder.getOrderLines()) {
            Product product = line.getProduct();
            reservationCartModel.addRow(new Object[] {
                    product.getProductNumber(),
                    product.getName(),
                    line.getQuantity(),
                    formatMoney(line.getUnitPrice()),
                    formatMoney(line.getSubtotal())
            });
        }

        reservationTotalLabel.setText(formatTotal(currentOrder.calculateTotal()));
    }

    private void renderSaleCart() {
        clearTable(saleCartModel);
        saleTotalLabel.setText(formatTotal(BigDecimal.ZERO));

        SaleOrder currentOrder = saleOrderController.getCurrentOrder();

        if (currentOrder == null) {
            return;
        }

        for (SaleOrderLine line : currentOrder.getOrderLines()) {
            Product product = line.getProduct();
            saleCartModel.addRow(new Object[] {
                    product.getProductNumber(),
                    product.getName(),
                    line.getQuantity(),
                    formatMoney(line.getUnitPrice()),
                    formatMoney(line.getSubtotal())
            });
        }

        saleTotalLabel.setText(formatTotal(currentOrder.calculateTotal()));
    }

    private void showReservationReceipt(ReservedOrder order) {
        if (order == null) {
            return;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("Reservation\n\n");
        receipt.append("Navn: ").append(order.getCustomer().getName()).append("\n");
        receipt.append("Telefonnummer: ").append(order.getCustomer().getPhoneNo()).append("\n");
        receipt.append("Betaling: ").append(order.getPaymentMethod()).append("\n");
        receipt.append("Dato: ").append(order.getDate()).append("\n");
        receipt.append("Afhentes senest: ").append(order.getExpiryDate()).append("\n\n");
        receipt.append("Varer:\n");

        for (ReservedOrderLine line : order.getOrderLines()) {
            Product product = line.getProduct();
            receipt.append(product.getName())
                    .append(" x ")
                    .append(line.getQuantity())
                    .append(" - ")
                    .append(formatMoney(line.getSubtotal()))
                    .append("\n");
        }

        receipt.append("\nTotal: ").append(formatMoney(order.getTotal()));

        JOptionPane.showMessageDialog(this, receipt.toString(), "Kvittering", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSaleReceipt(SaleOrder order) {
        if (order == null) {
            return;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("Salg\n\n");
        receipt.append("Betaling: ").append(order.getPaymentMethod()).append("\n");
        receipt.append("Dato: ").append(order.getDate()).append("\n\n");
        receipt.append("Varer:\n");

        for (SaleOrderLine line : order.getOrderLines()) {
            Product product = line.getProduct();
            receipt.append(product.getName())
                    .append(" x ")
                    .append(line.getQuantity())
                    .append(" - ")
                    .append(formatMoney(line.getSubtotal()))
                    .append("\n");
        }

        receipt.append("\nTotal: ").append(formatMoney(order.getTotal()));

        JOptionPane.showMessageDialog(this, receipt.toString(), "Kvittering", JOptionPane.INFORMATION_MESSAGE);
    }

    private int getSelectedProductNumber(JTable productTable) {
        int selectedRow = productTable.getSelectedRow();

        if (selectedRow < 0) {
            showStatus("Vælg et produkt først.");
            return -1;
        }

        return (int) productTable.getValueAt(selectedRow, 0);
    }

    private int getSelectedTabIndex() {
        return tabbedPane.getSelectedIndex();
    }

    private boolean databaseIsReady() {
        if (reservationOrderController == null || saleOrderController == null) {
            showStatus("Databaseforbindelsen er ikke klar.");
            return false;
        }

        return true;
    }

    private DefaultTableModel createProductTableModel() {
        return new DefaultTableModel(new Object[][] {},
                new String[] { "Varenr.", "Produkt", "Kategori", "På lager" }) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createStockTableModel() {
        return new DefaultTableModel(new Object[][] {},
                new String[] { "Varenr.", "Produkt", "Kategori", "Placering", "Antal", "Udløbsdato" }) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createCartTableModel() {
        return new DefaultTableModel(new Object[][] {},
                new String[] { "Varenr.", "Produkt", "Antal", "Stk. pris", "I alt" }) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    private void fillProductTableModel(DefaultTableModel model, List<StockItem> stockItems) {
        clearTable(model);

        for (StockItem stockItem : stockItems) {
            Product product = stockItem.getProduct();
            model.addRow(new Object[] {
                    product.getProductNumber(),
                    product.getName(),
                    formatCategory(product.getCategory()),
                    stockItem.getAvailableQty()
            });
        }

        if (model.getRowCount() == 0) {
            showStatus("Der er ingen produkter på lager.");
        }
    }

    private void fillStockTableModel(DefaultTableModel model, List<StockItem> stockItems) {
        clearTable(model);

        for (StockItem stockItem : stockItems) {
            Product product = stockItem.getProduct();
            model.addRow(new Object[] {
                    product.getProductNumber(),
                    product.getName(),
                    formatCategory(product.getCategory()),
                    stockItem.getLocation().getName(),
                    stockItem.getAvailableQty(),
                    stockItem.getExpirationDate()
            });
        }

        if (model.getRowCount() == 0) {
            showStatus("Der er ingen produkter på lager.");
        }
    }

    private void clearTable(DefaultTableModel model) {
        if (model == null) {
            return;
        }

        model.setRowCount(0);
    }

    private JCheckBox createMobilePayCheckBox() {
        JCheckBox checkBox = new JCheckBox("MobilePay", true);
        checkBox.setEnabled(false);
        return checkBox;
    }

    private JPanel createBasePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    private String formatCategory(ProductCategory category) {
        if (category == ProductCategory.MEAT) {
            return "Kød";
        }

        if (category == ProductCategory.VEGETABLES) {
            return "Grønt";
        }

        if (category == ProductCategory.EGGS) {
            return "Æg";
        }

        return "Andet";
    }

    private String formatMoney(BigDecimal amount) {
        return amount + " kr.";
    }

    private String formatTotal(BigDecimal amount) {
        return "Total: " + formatMoney(amount);
    }

    private void showStatus(String message) {
        statusLabel.setText("Systemstatus: " + message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
