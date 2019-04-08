package in.co.dhruvarora.ocrolus.phonedirectory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class PhoneDirectory {

	//Storing phone directory in a map with lname as key and a set of ContactEntity objects having lname equal to key.
	//Here used TreeSet to sort according to FirstName
	private Map<String, TreeSet<ContactEntity>> lastNameMap;
	private Comparator<ContactEntity> comparator;
	
	final static Logger logger = Logger.getLogger(PhoneDirectory.class);

	public PhoneDirectory() {
		lastNameMap = new HashMap<>();
		
		//Comparator object for TreeSet sorting
		comparator = new Comparator<ContactEntity>() {
			public int compare(ContactEntity contact1, ContactEntity contact2) {
				String fname1 = contact1.getFirstName();
				String fname2 = contact2.getFirstName();
				int sortedByName =  fname1.compareTo(fname2);
				if(sortedByName!=0) {
					return sortedByName;
				}else {
					//If both First name is equal it will sort with respect to State
					int sortByState = contact1.getState().compareTo(contact2.getState());
					if(sortByState!=0) {
						return sortByState;
						
					}else {
						//if state is also same, it will sort according to the order 
						return -1;
						
					}
				}
				

			}
		};

	}

	public boolean add(ContactEntity entry) {

		String lname = entry.lastName.toLowerCase(); //The key is in lowerCase to accept case insensitive requests
		if (lastNameMap.containsKey(lname)) {
			lastNameMap.get(lname).add(entry); // adding new contact in set
		} else { 
			TreeSet<ContactEntity> contacts = new TreeSet<>(comparator);
			contacts.add(entry);
			lastNameMap.put(lname, contacts);
		}

		return true;
	}

	public Set<ContactEntity> getByLastName(String lname) {
		return lastNameMap.get(lname);
	}

}
