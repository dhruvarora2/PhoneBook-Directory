package in.co.dhruvarora.ocrolus.phonedirectory;

public class ContactEntity {
	
	//Pojo for a contact 

	public final String firstName;
	public final String lastName;
	public final String state;
	public final String phoneNumber;
	
	public ContactEntity(String firstName, String lastName, String state, String phoneNumber){
		this.firstName = firstName;
		this.lastName = lastName;
		this.state = state;
		this.phoneNumber = phoneNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getState() {
		return state;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
}
