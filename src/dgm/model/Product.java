package dgm.model;

public class Product {

	private int productNumber;
	private String name;
    private ProductCategory category;
    
	public Product(int productNumber, String name, ProductCategory category){
		this.productNumber = productNumber;
		this.name = name;
		this.category = category;
	}
	
	public int getProductNumber() {
		return productNumber;
	}
	
	public String getName() {
		return name;
	}
	public ProductCategory getCategory() {
		return category;
	}
	public void setProductNumber(int productNumber) {
		this.productNumber = productNumber;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setCategory(ProductCategory category) {
		this.category = category;
	}
}
