package dgm.model;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SaleOrder {
	private LocalDate date;
	private String paymentMethod;
	private BigDecimal total;
	private List<SaleOrderLine> orderLines;
	
	public SaleOrder(LocalDate date, String paymentMethod, BigDecimal total, List<SaleOrderLine> orderLines) {
		this.date = date;
		this.paymentMethod = paymentMethod;
		this.total = total;
		this.orderLines = orderLines;
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
	

