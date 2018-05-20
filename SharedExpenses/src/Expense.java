
import java.io.Serializable;
import java.util.Date;

public class Expense implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2006051151469014671L;
	private String expenseType;
	private double cost;
	private String guest;
	private Date date;
	private String note;
	
	
	// Constructor
	public Expense (String expenseType, double cost, String guest, Date date, String note) {
		this.expenseType = expenseType;
		this.cost = cost;
		this.guest = guest;
		this.date = date;
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
