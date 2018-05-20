
import java.io.Serializable;
import java.util.Date;

public class Expense implements Serializable {

	private Date date;
	private String guest;
	private String expenseType;
	private double cost;
	private String note;
	
	// Constructor
	public Expense (Date date, String guest, String expenseType, double cost, String note) {
		this.date = date;
		this.guest = guest;
		this.expenseType = expenseType;
		this.cost = cost;
		this.note = note;
	}

	// Setters
	
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public void setGuest(String guest) {
		this.guest = guest;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	// Getters

	public Object getExpenseData(int index) {
		
		switch(index) {
		case 0: return date;
		case 1: return guest;
		case 2: return expenseType;
		case 3: return cost;
		case 4: return note;
		default: return null;
		}
	}
	
	public String setExpenseType() {
		return expenseType;
	}
	
	public double setCost() {
		return cost;
	}
	
	public String setGuest() {
		return guest;
	}
	
	public Date setDate() {
		return date;
	}
	
	public String setNote() {
		return note;
	}
	
	
}
