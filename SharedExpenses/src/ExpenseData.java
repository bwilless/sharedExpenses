import java.util.ArrayList;

// Singleton Class to hold our Expense Data Array

public class ExpenseData {
 
	// Execute the Eager Initialization of our Singleton object
	private static final ExpenseData instance = new ExpenseData();
	
	// Declare the ArrayList to hold our data
	ArrayList<Expense> expenseArray = new ArrayList<Expense>();
	
	// Mark the constructor as private to protect against additional instantiations.
	private ExpenseData() {}
		
	// Provide a method for other classes to get the singleton instance
	public static synchronized ExpenseData getInstance() {
		return instance;
	}
	
	// Provide a getter method for the data array
	public ArrayList<Expense> getExpenseArray(){
		return expenseArray;
	}
	
	// Provide a setter method for the data array
	public void setExpenseArray(ArrayList<Expense> newArrayList) {
		
		expenseArray = newArrayList;
	}
	
}
