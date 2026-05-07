package dgm.model;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class ReservedOrder {

	private LocalDate date;
	private LocalDate expiryDate;
	private String paymentMethod;
	private BigDecimal total;
	private Customer customer;
	private List<ReservedOrderLine> orderLines;

	public ReservedOrder() {
		date = LocalDate.now();
		expiryDate = LocalDate.now().plusDays(3);
		paymentMethod = null;
		total = BigDecimal.ZERO;
		customer = null;
		orderLines = new ArrayList<>();
	}

	public void addLine(ReservedOrderLine line) {
		orderLines.add(line);
	}

	public BigDecimal calculateTotal() {
		BigDecimal calculatedTotal = BigDecimal.ZERO;

		for (ReservedOrderLine line : orderLines) {
			calculatedTotal = calculatedTotal.add(line.getSubtotal());
		}

		total = calculatedTotal;
		return total;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<ReservedOrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<ReservedOrderLine> orderLines) {
		this.orderLines = orderLines;
	}
}
