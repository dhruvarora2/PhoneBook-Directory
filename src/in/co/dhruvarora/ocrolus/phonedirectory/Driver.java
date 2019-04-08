package in.co.dhruvarora.ocrolus.phonedirectory;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

//Main Driver Class of the Application

public class Driver {
	private final PhoneDirectory phoneDirectory;
	private final DirectoryOperations directoryOperations;
	private final static Logger logger = Logger.getLogger(DirectoryOperations.class);

	public Driver() {
		phoneDirectory = new PhoneDirectory();
		directoryOperations = new DirectoryOperations();
		logger.setLevel(Level.INFO);
	}

	

	public String importContacts() {
		String defaultPath = "phone_dataset.csv";
		return importContactsFromFilePath(defaultPath);

	}

	public String importContactsFromFilePath(String filePath) {
		String response = directoryOperations.importContactsFromFile(filePath, phoneDirectory);
		return response;

	}

	public String searchContactsFromFile() {
		String querySetPath = "queries.txt";
		String outputFilePath = "";
		return directoryOperations.searchContactFromFile(outputFilePath, querySetPath, phoneDirectory);

	}

	public String searchContactByLastName(String lname) {
		return directoryOperations.searchContactFromLastName(lname, phoneDirectory);
	}

	
	
	//The runner method for Driver class.
	public void runner() {
		String welcomeText = "Please choose from the following options:\n"
				+ "1. Import Contacts from sample csv file path. \n" + "2. Import Contacts from given csv file path. \n"
				+ "3. Search Contacts from query text file.\n" + "4. Search Contact by Last Name\n" + "5. Exit";
		System.out.println(welcomeText);
		try {
			Scanner sc = new Scanner(System.in);
			int input = sc.nextInt();
			String response = "";
			boolean isEnd = false;
			while (!isEnd) {
				switch (input) {
				case 1:
					response = importContacts();
					System.out.println(response);
					System.out.println(welcomeText);
					input = sc.nextInt();
					break;
				case 2:
					System.out.println("Please enter contacts csv file path");
					sc.nextLine();// for blank space
					String filePath = sc.nextLine();
					response = importContactsFromFilePath(filePath);
					System.out.println(response);
					System.out.println(welcomeText);
					input = sc.nextInt();
					break;
				case 3:
					response = searchContactsFromFile();
					System.out.println(response);
					System.out.println(welcomeText);
					input = sc.nextInt();
					break;
				case 4:
					System.out.println("Please enter the last name");
					sc.nextLine();// for blank space
					String lname = sc.nextLine();

					response = searchContactByLastName(lname);
					System.out.println(response);
					System.out.println(welcomeText);
					input = sc.nextInt();
					break;
				case 5:
					System.out.println("Thank You!");
					isEnd = true; //Will cause end of while
					sc.close();
					break;
				default:
					System.out.println("Please choose a valid input from 1,2,3 & 4.");
					System.out.println(welcomeText);
					input = sc.nextInt();
					break;

				}
			}
		} catch (InputMismatchException e) {
			System.out.println("Please Try again with Valid input"); 
			runner();
		}

	}
	
	public static void main(String[] args) {

		new Driver().runner();
	}

}
