

import java.sql.Date;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class SharedExpenseTableModel extends AbstractTableModel {

//	private Object[][] expenseList = {
//			{new Date(0), "Brian", "Food", new Double(22.34), "This is a short note"},
//			{new Date(100), "Duane", "Fuel", new Double(99.99), "This is a slightly longer note"}
//	};
	
	private String[] columnNames = {"Date", 
			    					"Guest", 
			    					"Expense Type", 
			    					"Cost", 
		    						"Note"};

	
	public int getColumnCount() {

		return columnNames.length;
	
	}

	public int getRowCount() {
		
		return ExpenseData.getInstance().getExpenseArray().size();
	}

	public Object getValueAt(int arrayIndex, int ojbectIndex) {
		return ExpenseData.getInstance().getExpenseArray().get(arrayIndex).getExpenseData(ojbectIndex);
	}
	
	// All our fields are editable.  Implement this method and always return true.
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
	
    public String getColumnName(int col) {
        return columnNames[col];
    }
	
	
}
