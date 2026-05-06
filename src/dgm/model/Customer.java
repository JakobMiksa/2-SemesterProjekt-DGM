package dgm.model;

	public class Customer {

	private String name;
	private String phoneNo;
	
public Customer(String name, String phoneNo) {
    this.name = name;
    this.phoneNo = phoneNo;
}

	public String getName() {
		return name;
}
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setName(String name) {
	this.name = name;
}
	public void setPhoneNo(String phoneNo) {
	this.phoneNo = phoneNo;
}
}

