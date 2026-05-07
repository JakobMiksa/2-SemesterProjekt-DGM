package dgm.model;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class SaleOrder {

	private LocalDate date;
	private String paymentMethod;
	private BigDecimal total;
	private List<SaleOrderLine> orderLines;

	public SaleOrder() {
		date = LocalDate.now();
		paymentMethod = null;
		total = BigDecimal.ZERO;
		orderLines = new ArrayList<>();
	}

	public void addLine(SaleOrderLine line) {
		orderLines.add(line);
	}

	public BigDecimal calculateTotal() {
		BigDecimal calculatedTotal = BigDecimal.ZERO;

		for (SaleOrderLine line : orderLines) {
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

	public List<SaleOrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<SaleOrderLine> orderLines) {
		this.orderLines = orderLines;
	}
}
