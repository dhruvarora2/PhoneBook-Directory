package in.co.dhruvarora.ocrolus.phonedirectory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

public class DirectoryOperations {
	
	private static final  Logger LOGGER = Logger.getLogger(DirectoryOperations.class);
	
	//Regex to validate US number
	private static final String NUMBER_REGEX = "^\\D?(\\d{3})\\D?\\D?(\\d{3})\\D?(\\d{4})$";

	
	
	public String importContactsFromFile(String filePath, PhoneDirectory phoneDirectory) {
		String delimiter = ",";
		String line = "";
		BufferedReader br = null;
		int numberOfRecords = 0;

		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {
				if (line.equals("")) {
					continue; //when line is equal to ""
				}

				ContactEntity contact = makeContact(line, delimiter);
				if (contact == null) { //The object will be null if line is invalid
					continue; // line is invalid
				}
				// Add the contact object to phone Directory
				phoneDirectory.add(contact);
				numberOfRecords++;

			}

		} catch (FileNotFoundException e) {
			LOGGER.error("File not found");
			return ("File not found!");
		} catch (IOException e) {
			LOGGER.error("Exception found" + e);
			return ("Exception Found");
		} finally {
			try {
				if (br != null) {
					br.close();
				}

			} catch (IOException e) {
				LOGGER.error("Error in closing Buffer Reader" + e);
			}
		}

		return "Successfully Imported - "+numberOfRecords+" contacts";
	}

	//Searching from a single Last Name (Added functionality) & Making output
	public String searchContactFromLastName(String lname, PhoneDirectory phoneDirectory) {
		StringBuilder result = new StringBuilder("");
		result.append("Matches for: " + lname + "\n");
		int count = 1;
		Set<ContactEntity> tempResultSet = phoneDirectory.getByLastName(lname.toLowerCase()); //The key is stored in Lower Case
		if (tempResultSet == null) {
			result.append("No results found\n");
			return result.toString();
		}
		//Iterating over each Contact Pojo object in ResultSet
		for (ContactEntity contact : tempResultSet) {
			String fname = contact.getFirstName();
			String lastName = contact.getLastName();
			String phone = contact.getPhoneNumber();
			String state = contact.getState();
			result.append("Result " + count + ": " + lastName + ", " + fname + ", " + state + ", " + phone + "\n");
			count++;
		}
		return result.toString();
	}

	//Searching Contact from a given Query text file
	public String searchContactFromFile(String outputFilePath, String inputFilePath, PhoneDirectory phoneDirectory) {
		BufferedReader br = null;
		String line;
		ArrayList<String> queryText = new ArrayList<>();
		ArrayList<Set<ContactEntity>> resultSet = new ArrayList<>();

		try {
			br = new BufferedReader(new FileReader(inputFilePath));
			while ((line = br.readLine()) != null && !line.equals("")) {
				String[] queryKeys = line.split(" ");
				//If a line contains more words than 1, skipping it
				if (queryKeys.length > 1) { 
					LOGGER.warn("Invalid Query Line, Hence Skipping");
					continue;
				}
				String lname = queryKeys[0].toLowerCase(); // as the key is in lower case
				queryText.add(queryKeys[0]);
				resultSet.add(phoneDirectory.getByLastName(lname)); // as the key is in lower case

			}

		} catch (FileNotFoundException e) {
			LOGGER.error("File not found" + e);
			return ("File not found!");
		} catch (IOException e) {
			LOGGER.error("Exception Found "+e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				LOGGER.error("Error in closing Buffer Reader" + e);
			}
		}

		// Send details to output file
		boolean res = makeOutputFile(outputFilePath, queryText, resultSet, phoneDirectory);

		if(res) {
			return "OutPut file is generated!";
		}else {
			return "Error in generating output file";
		}
		
	}

	private boolean makeOutputFile(String outputFilePath, ArrayList<String> queryText,
			ArrayList<Set<ContactEntity>> resultSet, PhoneDirectory phoneDirectory) {

		if (outputFilePath.equals("")) {
			outputFilePath = "output.txt"; //default name for output file
		}

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(outputFilePath);
			StringBuilder fileContents = new StringBuilder("");

			for (int i = 0; i < queryText.size(); i++) {
				fileContents.append("Matches for: " + queryText.get(i) + "\n");
				int count = 1;
				//Getting tempResultSet from ArrayList
				Set<ContactEntity> tempResultSet = resultSet.get(i);
				if (tempResultSet == null) {
					fileContents.append("No results found\n");
					continue;
				}
				//Iterating over each Contact Pojo object in tempResultSet
				for (ContactEntity contact : tempResultSet) {
					String fname = contact.getFirstName();
					String lname = contact.getLastName();
					String phone = contact.getPhoneNumber();
					String state = contact.getState();
					fileContents.append(
							"Result " + count + ": " + lname + ", " + fname + ", " + state + ", " + phone + "\n");
					count++;
				}
			}
			fileWriter.write(fileContents.toString());
			return true;

		} catch (IOException e) {
			LOGGER.error("IOException" + e);
			return false;
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					LOGGER.error("Error in closing Buffer Reader" + e);
				}
			}
		}

	

	}

	private ContactEntity makeContact(String line, String delimiter) {
		String[] fields = line.trim().split("\\s*,\\s*");
		if (fields.length == 4) {
			//checking if 3rd & 4th field matches the US number format
			boolean isThirdNumber = fields[2].matches(NUMBER_REGEX);
			boolean isFourthNumber = fields[3].matches(NUMBER_REGEX);
			if ((isThirdNumber && isFourthNumber)) {
				LOGGER.info("Invalid Contact Details found for "+line); //None of the 2 fields matches the regex
				return null;
			} else if (isThirdNumber) {
				// When Format is : Firstname, Lastname, (917) 358-1291, California
				String fname = fields[0];
				String lname = fields[1];
				String number = fields[2];
				String state = fields[3];
				return new ContactEntity(fname, lname, state, number);
			} else if (isFourthNumber) {
				// When Format is : Lastname, Firstname, New York, (917) 958-1191
				String fname = fields[1];
				String lname = fields[0];
				String number = fields[3];
				String state = fields[2];
				return new ContactEntity(fname, lname, state, number);
			} else {
				LOGGER.error("Invalid number found in "+line);
				return null;
			}

		} else if (fields.length == 3) {
			// When Format is : Firstname Lastname, 9179581191, New York
			String[] names = fields[0].split(" "); //Splitting according to space to get fname & lname separate
			boolean validNumber = fields[1].matches(NUMBER_REGEX);
			if (!validNumber) {
				LOGGER.info("Invalid Phone Number found for "+line);
				return null;
			}
			String fname = names[0];
			String lname = names[1];
			String number = fields[1];
			String state = fields[2];
			return new ContactEntity(fname, lname, state, number);

		} else {
			
			LOGGER.info("Invalid Contact Details for "+line);
			return null;
		}

	}
}
