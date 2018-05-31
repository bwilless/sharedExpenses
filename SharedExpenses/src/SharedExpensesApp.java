

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

public class SharedExpensesApp {

	// Class Variables
	
	// GUI Objects
	JFrame frame;
	JPanel buttonPanel;
	JSplitPane splitPane;
	JButton addGuestButton, newTrackerButton, loadTrackerButton, addExpenseButton, saveTrackerButton;
	JFrame addExpenseFrame, addGuestFrame; 
	JTextField expenseTypeText, costText, dateText, noteText, addGuestText;
	JComboBox guestText; 
	JFormattedTextField dateInput;
	JTable dataTable, expenseTable;
	
	// Constants
	public final static int MAX_GUESTS = 10;
	public final static int MAX_EXPENSE_TYPES = 10;

	private final static int DATA_TABLE_X_DIMENSION = MAX_GUESTS + 2;
	private final static int DATA_TABLE_Y_DIMENSION = MAX_EXPENSE_TYPES + 2;

	// Define the starting cell for the expense table list
	private final static int EXPENSE_X_BASE = 0;
	private final static int EXPENSE_Y_BASE = 0;
		
	// Define the starting cell for the expense totals
	private final static int EXPENSE_TOTAL_X_BASE = 1;
	private final static int EXPENSE_TOTAL_Y_BASE = 0;
	
	// Data Objects
	File currentFile = null;
	static ArrayList<Expense> expenseArray;
	static ArrayList<String> guestArray;
	static String[][] dataArray;
	static ArrayList<String> columnNames;
	static ArrayList<String> typeNames;
	
	static enum ExpenseTableEntry {
		item,
		total,
		guestn
	}
	
	// Main entry point
	public static void main(String[] args) {
		
		// Create an instance of the application class
		SharedExpensesApp expenseApp = new SharedExpensesApp();
		
		// Instantiate the data variables

		// Create the ArrayList to hold all the expenses
		expenseArray = new ArrayList<Expense>();
		
		// The guest array contains non-duplicate guest names.  This list is also used to
		// determine which column in the table data to display guest specific data
		guestArray = new ArrayList<String>();
		
		// The data array is used for the main data table in the gui.
		dataArray = new String[DATA_TABLE_X_DIMENSION][DATA_TABLE_Y_DIMENSION];
		
		// columNames is used to hold the column names for the Expense table.
		columnNames = new ArrayList<String>();
		
		// The typeNames array contains non-duplicate strings representing the expense types, i.e., "Food", "Fuel",  . . . 
		typeNames = new ArrayList<String>();

		// Let's get the party started!
		expenseApp.setupGUI();		
		expenseApp.go();
	}
	
	public void setupGUI() {

		// Create the main Swing frame
		frame = new JFrame("Shared Expense Tracker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//                 Data Table Stuff
		// Create the data table
		dataTable = new JTable(new DataTableModel());
		dataTable.setPreferredScrollableViewportSize(new Dimension(650, 225));
		dataTable.setFillsViewportHeight(true);		
		
		// Create a scrolling JPanel for the data table
		JScrollPane dataPane = new JScrollPane(dataTable);

		//                 Expense Table Stuff		
		// Create the expense table
		expenseTable = new JTable(new ExpenseTableModel());
		expenseTable.setPreferredScrollableViewportSize(new Dimension(650, 225));
		expenseTable.setFillsViewportHeight(true);
		
		// Create a scrolling JPanel for the expense table
		JScrollPane expensePane = new JScrollPane(expenseTable);

		// Create the split pane and add data and expense panes
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				        dataPane, expensePane);
		frame.add(splitPane);
		
		// Create the button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2,3));
		
		// Create all the buttons for the Button Panel
		addGuestButton = new JButton("Add Guest");
		addExpenseButton = new JButton("Add Expense");
		loadTrackerButton = new JButton("Load Tracker");
		saveTrackerButton = new JButton("Save");
		newTrackerButton = new JButton("New Tracker");
		
		// Add all the buttons to the button panel
		buttonPanel.add(addGuestButton);
		buttonPanel.add(loadTrackerButton);
		buttonPanel.add(newTrackerButton);
		buttonPanel.add(addExpenseButton);
		buttonPanel.add(saveTrackerButton);	
		
		// Set frame characteristics
		frame.getContentPane().add(BorderLayout.SOUTH, buttonPanel);
		frame.setSize(800, 600);
		frame.setVisible(true);

	}
	
	// Main worker function
	public void go() {
		
		// Setup all the button listeners
		addGuestButton.addActionListener(new AddGuest());
		addExpenseButton.addActionListener(new AddExpense());
		loadTrackerButton.addActionListener(new LoadTracker());
		saveTrackerButton.addActionListener(new SaveTracker());
		newTrackerButton.addActionListener(new NewTracker());
		
		// Setup the column names for the expense panel
		setInitialColumnNames();
		initColumnSizes(expenseTable);
		initColumnSizes(dataTable);
		
	}

	// This method initializes the main data table headers with static text and "--" as place holders 
	// where guest names will be placed later.
	public void setInitialColumnNames() {
		
		JTableHeader th = dataTable.getTableHeader();
		TableColumnModel tcm = th.getColumnModel();
		
		TableColumn tc = tcm.getColumn(0);
		tc.setHeaderValue("Expense Item:  ");
		
		tc = tcm.getColumn(1);
		tc.setHeaderValue("Expense Total:  ");
		
		for(int i = 2; i < DATA_TABLE_X_DIMENSION; i++) {

			tc = tcm.getColumn(i);
			tc.setHeaderValue("    --    ");
			
		}
		
		th.repaint();
		
	}
	
	// This method will sum all expenses for the given expense type
	public double calcExpensesTotal(String expenseType) {
		
		double total = 0.0;
		
		for(Expense currExpense: expenseArray) {
			if(currExpense.getExpenseType().equals(expenseType)) {
				total += currExpense.getCost();
			}
		}
		
		return total;
	}

	// This method will sum all expenses of the given expense type for the given guest
	public double calcExpenseTotalByGuest(String expenseType, String guest) {
		
		double total = 0.0;
		
		for(Expense currExpense: expenseArray) {
			if((currExpense.getExpenseType().equals((expenseType)) && (currExpense.getGuest().equals(guest)))) {
				total += currExpense.getCost();
			}
		}
				
		return total;
	}
	
	// This method will sum all expenses for the given guest regardless of the expense type.  "Jim put out $234.00"
	public double calcTotalOfAllExpensesByGuest(String guest) {
		
		double total = 0.0;
		
		for(Expense currExpense: expenseArray) {
			if(currExpense.getGuest().equals(guest)) {
				total += currExpense.getCost();
			}
		}
		
		return total;
	}
	
	// This method sums all expenses.  "This trip cost the group $524.33"
	public double calcTotalOfAllExpenses()  {
		
		double total = 0.0;
		
		for(Expense currExpense: expenseArray) {
			total += currExpense.getCost();
		}
		
		return total;
	}
	
	// This method returns the total for the entire trip divided by the number of guests.   
	public double calcSharedExpenseTotal() {

		if(guestArray.isEmpty()) {
			return 0.0;
		} else {
		return calcTotalOfAllExpenses()/guestArray.size();
		}
		
	}
	
	// This method returns how much each guest needs to receive/pay to be even for the trip.
	public double calcBallanceByGuest(String guest) {
		
		return calcSharedExpenseTotal()- calcTotalOfAllExpensesByGuest(guest);
	}
	
	/*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {

    	table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    	 
    	for (int column = 0; column < table.getColumnCount(); column++)
    	{

    		System.out.println("Processing column: " + column );
    		
    		TableColumn tableColumn = table.getColumnModel().getColumn(column);
    	    int preferredWidth = tableColumn.getMinWidth();
    	    int maxWidth = tableColumn.getMaxWidth();
    	    int headerWidth = tableColumn.getHeaderValue().toString().length();
    	 
    	    
    	    System.out.println("headerWidth: " + headerWidth + " maxWidth: " + maxWidth);
    	    
    	    if(headerWidth > maxWidth) {
    	    	maxWidth = headerWidth;
    	    }
    	    
    	    for (int row = 0; row < table.getRowCount(); row++)
    	    {
    	    
    	    	TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
    	        Component c = table.prepareRenderer(cellRenderer, row, column);
    	        int width = c.getPreferredSize().width + 10; //table.getIntercellSpacing().width;
    	        preferredWidth = Math.max(preferredWidth, width);
    	 
    	        System.out.println("row: " + row + " column: " + column + " Width: " + width + " preferredWidth: " + preferredWidth);
    	        
    	        
    	        //  We've exceeded the maximum width, no need to check other rows
    	 
    	        if (preferredWidth >= maxWidth)
    	        {
    	            preferredWidth = maxWidth;
    	            break;
    	        }
    	    }
    	 
    	    tableColumn.setPreferredWidth( preferredWidth );
    	}
    }	
	
	
	// ActionListener inner-classes for button panel 		
	class AddGuest implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			addGuestFrame = new JFrame("Add Guest");
			JPanel addGuestPanel = new JPanel();
			JLabel guestLabel = new JLabel("New Guest, Enter Name");
			
			addGuestText = new JTextField(20);
			
			addGuestPanel.add(guestLabel);
			addGuestPanel.add(addGuestText);
			
			JButton okGuestButton = new JButton("OK");
			
			addGuestFrame.getContentPane().add(BorderLayout.CENTER,addGuestPanel);
			addGuestFrame.getContentPane().add(BorderLayout.SOUTH,okGuestButton);
			
			addGuestFrame.setSize(250, 130);
			addGuestFrame.setVisible(true);
				
			 okGuestButton.addActionListener(new AddGuestOK());			
		}
	}

	class AddExpense implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			// Make sure we have registered guests, if not tell the user and exit!
			if(guestArray.isEmpty()) {
				JOptionPane optionPane = new JOptionPane("You must add a guest(s) before adding an Expense!", JOptionPane.ERROR_MESSAGE);
				JDialog dialog = optionPane.createDialog("Can not add Expense!");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
				return;
			}
			
			addExpenseFrame = new JFrame("Add Expense");
			
			JPanel addExpensePanel = new JPanel();

			JLabel guestLabel = new JLabel("Guest:");
			JLabel expenseTypeLabel = new JLabel("Expense Type:");
			JLabel costLabel = new JLabel("Cost:");
			JLabel dateLabel = new JLabel("Date");
			JLabel noteLabel = new JLabel("Note:");
			
			guestText = new JComboBox(guestArray.toArray());
			expenseTypeText = new JTextField(20);
			costText = new JTextField(10);
			
			Format shortDate = DateFormat.getDateInstance(DateFormat.SHORT);
			dateInput = new JFormattedTextField(shortDate);
			dateInput.setValue(new Date());
			dateInput.setColumns(20);
			
			noteText = new JTextField(100);
			
			addExpensePanel.add(guestLabel);
			addExpensePanel.add(guestText);
			addExpensePanel.add(expenseTypeLabel);
			addExpensePanel.add(expenseTypeText);
			addExpensePanel.add(costLabel);
			addExpensePanel.add(costText);
			addExpensePanel.add(dateLabel);
			addExpensePanel.add(dateInput);
			addExpensePanel.add(noteLabel);
			addExpensePanel.add(noteText);
			
			JButton okButton = new JButton("OK");
			
			addExpensePanel.setLayout(new GridLayout(5, 2, 10, 10));

			addExpenseFrame.getContentPane().add(BorderLayout.CENTER,addExpensePanel);
			addExpenseFrame.getContentPane().add(BorderLayout.SOUTH,okButton);
			
			addExpenseFrame.setSize(250, 200);
			addExpenseFrame.setVisible(true);
				
			 okButton.addActionListener(new AddExpenseOK());
						
		}
		
	}
	
	class LoadTracker implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			JFileChooser fileChooser = new JFileChooser();
			File newFile = null;
			int returnVal = 0;
			
			while (newFile == null) {
				
				fileChooser.setDialogTitle("Please specify the file open");
				returnVal = fileChooser.showOpenDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					newFile = fileChooser.getSelectedFile();
				} else {
//					fileChooser.setVisible(false);
				}
					
			}			
			
			currentFile = newFile;
			
			try {
				
				FileInputStream fileInputStream = new FileInputStream(currentFile);
				ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
				expenseArray = ((ArrayList<Expense>) (inputStream.readObject()));
				inputStream.close();
				
				// Run through the updated expenseArray and populate the guest and type name arrays 
				for(int i = 0; i < expenseArray.size(); i++) {
				
					if(!guestArray.contains(expenseArray.get(i).getGuest())) {
						
						// Add the guest name to the guestArray and columnName array
						guestArray.add(expenseArray.get(i).getGuest());
						columnNames.add(expenseArray.get(i).getGuest());
						
						// Change the next header name to include our new guest
						JTableHeader th = dataTable.getTableHeader();
						TableColumnModel tcm = th.getColumnModel();
						TableColumn tc = tcm.getColumn(guestArray.indexOf(expenseArray.get(i).getGuest())+2);
						tc.setHeaderValue(expenseArray.get(i).getGuest());
						th.repaint();
					
						// Add the Expense type to the table
						if(!typeNames.contains(expenseArray.get(i).getExpenseType())) {
							
							typeNames.add(expenseArray.get(i).getExpenseType());
							dataArray[(int)EXPENSE_X_BASE][(int)(EXPENSE_Y_BASE+typeNames.size())] = expenseArray.get(i).getExpenseType();
							
						}
						
						initColumnSizes(expenseTable);
						initColumnSizes(dataTable);

						splitPane.updateUI();
					
					}
	
					if(!typeNames.contains(expenseArray.get(i).getExpenseType())) {
						typeNames.add(expenseArray.get(i).getExpenseType());
					}
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
	}

	class SaveTracker implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {

			JFileChooser fileChooser = new JFileChooser();
			Boolean newFile = false;
			
			while (currentFile == null) {

				fileChooser.setDialogTitle("Please specify the file to save the current expenses");
				fileChooser.showOpenDialog(frame);
				currentFile = fileChooser.getSelectedFile();
				newFile = true;
			}			
			
			
			try {
				
				FileOutputStream fileOutputStream = new FileOutputStream(currentFile);
				ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
				outputStream.writeObject(expenseArray);
				outputStream.close();
				
				if(!newFile) {
					
					JOptionPane optionPane = new JOptionPane("Data has been saved to " + currentFile + "!", JOptionPane.INFORMATION_MESSAGE);
					JDialog dialog = optionPane.createDialog("File Saved");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
										
				}
				
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
	}

	class NewTracker implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	// ActionListener inner-class for AddExpenseDialog
	
	class AddExpenseOK implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			
			// Validate the cost field
			if((costText.getText().isEmpty()) || (Double.parseDouble(costText.getText()) <= 0)) {
				
				JOptionPane optionPane = new JOptionPane("Expense \"Cost\" must be greater than 0.");
				JDialog dialog = optionPane.createDialog("Expense cost ERROR");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
				return;
			}
			
			// Validate the expense type field
			if(expenseTypeText.getText().isEmpty() || expenseTypeText.getText().matches("\\s*")) {
				
				JOptionPane optionPane = new JOptionPane("\"Expense Type\" can NOT be blank.");
				JDialog dialog = optionPane.createDialog("Expense type ERROR");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
				return;
			}
			
			Date dateField = (Date) (dateInput.getValue());
			
			// Convert expense type to first letter CAPatilized and remaining text lower case
			String expense = expenseTypeText.getText();
			expense = expense.substring(0, 1).toUpperCase() + expense.substring(1).toLowerCase();
			
			Expense newExpense = new Expense(dateField, (String)guestText.getSelectedItem(), expense, 
											 Double.parseDouble(costText.getText()), noteText.getText());
			
			splitPane.updateUI();
			
			if(!typeNames.contains(expense)) {
				typeNames.add(expense);
			}
			
			expenseArray.add(newExpense);
			addExpenseFrame.setVisible(false);
		}
	}

	class AddGuestOK implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if(guestArray.size() >= MAX_GUESTS) {

				JOptionPane optionPane = new JOptionPane("Maximum number of guests reached.  Max = " + MAX_GUESTS, JOptionPane.ERROR_MESSAGE);
				JDialog dialog = optionPane.createDialog("Guest not added");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
		
			}
			
			String guest =  addGuestText.getText();

			// Always store guest names in all lower case with first letter CAPatalized!
			guest = guest.substring(0, 1).toUpperCase() + guest.substring(1).toLowerCase();
			
			if(!guestArray.contains(guest)) {
				
				// Update our data structures to include the new guest
				guestArray.add(guest);
				columnNames.add(guest);

				// Disappear the input dialog box
				addGuestFrame.setVisible(false);
				
				// Change the next header name to include our new guest
				JTableHeader th = dataTable.getTableHeader();
				TableColumnModel tcm = th.getColumnModel();
				TableColumn tc = tcm.getColumn(guestArray.indexOf(guest)+2);
				tc.setHeaderValue( guest );
				th.repaint();
			
			} else {
				
				JOptionPane optionPane = new JOptionPane("Guest " + guest + " already exists", JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog = optionPane.createDialog("Guest not added");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
									
			}
		}
	}
	
	// Table Model inner-classes
	
	public class ExpenseTableModel extends AbstractTableModel {

		public Object[] longValues;

		private static final long serialVersionUID = -8404802938088513836L;
		
		private String[] columnNames = {"Date", 
				    					"Guest", 
				    					"Expense Type", 
				    					"Cost", 
			    						"Note"};

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return expenseArray.size();
		}

		public Object getValueAt(int arrayIndex, int ojbectIndex) {
			return expenseArray.get(arrayIndex).getExpenseData(ojbectIndex);
		}
		
		// All our fields are editable.  Implement this method and always return true.
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
		
	    public String getColumnName(int col) {
	        return columnNames[col];
	    }
	}

	public class DataTableModel extends AbstractTableModel {
		
		public DataTableModel() {
	
		}
		
		public int getColumnCount() {

			return DATA_TABLE_X_DIMENSION;
		}
		
		public int getRowCount() {
		
			return DATA_TABLE_Y_DIMENSION;
		
		}

		public void setValueAt(int x, int y, String newData) {
			
			dataArray[x][y] = newData; 
			
		}
		
		public Object getValueAt(int expenseType, int column) {
		
			String returnString = null;
			double returnVal = 0.0;
			
			if(typeNames.size() > expenseType) {

				switch(column) {
				case 0: 

						return typeNames.get(expenseType);

				case 1: 

						returnVal = calcExpensesTotal(typeNames.get(expenseType));
						return (returnVal == 0.0) ? "-" : String.format("$%.2f", returnVal);
				
				default: 

					// Decrement the column number by two to account for the offset into the table where guest columns start
					column -= 2;

					if(guestArray.size() > column) {
						return String.format("$%.2f", calcExpenseTotalByGuest(typeNames.get(expenseType), guestArray.get(column)));
					} else {
						return "      --";
					}
				}

			} else {
			
				switch(column) {
				case 0: 
	
					if(expenseType == 10){
						return "Total Expenses by Guest";
					} else if (expenseType == 11) {
						return "Ballance of Shared Expense";
					} else {
						return "";
					}
				case 1: 
	
					if(expenseType == 10){
						returnVal = calcTotalOfAllExpenses();
						return String.format("$%.2f", returnVal);
					} else if (expenseType == 11) {
						returnVal = calcSharedExpenseTotal();
						return String.format("$%.2f", returnVal);
					} else {
						return ""; 
					}
				
				default: 
	
					// Decrement the column number by two to account for the offset into the table where guest columns start
					column -= 2;					
					
					if(guestArray.size() > column) {
						if(expenseType == 10){
							returnVal = calcTotalOfAllExpensesByGuest(guestArray.get(column));
							return String.format("$%.2f", returnVal);
						} else if (expenseType == 11) {
							returnVal = calcBallanceByGuest(guestArray.get(column));
							return String.format("$%.2f", returnVal);
						} else {
							return "";
						}
					} else {
						return "      --";
					}
				}
			}
		}
		
		// Provide a method to add a new column header.  This will take a new guest name when added.
//		public void addColumnText(String newColumnName) {
//			
//			columnNames.add(2, newColumnName);
//			for (int i = 0; i < columnNames.size(); i++) {
//				System.out.println(columnNames.get(i));
//			}
//			
//		}

		
	}
}

/*
 * TODO List
 * 
 *   Implement table change update for expense table, change fields to editable
 *   
 *   Save file filechooser can not be canceled   
 *  			
 *   
 * 
 * 
 * 
 */


