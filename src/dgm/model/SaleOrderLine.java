package dgm.model;

import java.math.BigDecimal;

public class SaleOrderLine {
	private int quantity;
	private BigDecimal unitPrice;
	private Product product;

	public SaleOrderLine(Product product, int quantity, BigDecimal unitPrice) {
		this.product = product;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public BigDecimal getSubtotal() {
		BigDecimal res = unitPrice.multiply(BigDecimal.valueOf(quantity));
		return res;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
