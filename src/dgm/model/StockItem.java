package dgm.model;

import java.time.LocalDate;

public class StockItem {
	
	private Product product;
	private Location location;
	private int availableQty;
    private LocalDate expirationDate;
    
    public StockItem(Product product, Location location, int availableQty, LocalDate expirationDate) {
        this.product = product;
        this.location = location;
        this.availableQty = availableQty;
        this.expirationDate = expirationDate;
    }
    
    public void decreaseAvailableQty(int quantity) {
        this.availableQty = this.availableQty - quantity;
    }
   
    public Product getProduct() {
    	return product;
    }
    
    public Location getLocation() {
    	return location;
    }

    public int getAvailableQty() {
    	return availableQty;
    }
    
    public LocalDate getExpirationDate() {
    	return expirationDate;
    }  
    
    public void setProduct(Product product) {
    	this.product = product;
    }
    
    public void setLocation(Location location) {
    	this.location = location;
    }  
    
    public void setAvailableQty(int availableQty) {
    	this.availableQty = availableQty;
    }
    
    public void setExperationDate(LocalDate expirationDate) {
    	this.expirationDate = expirationDate;
    }
}
