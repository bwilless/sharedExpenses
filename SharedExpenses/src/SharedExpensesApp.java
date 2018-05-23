

import java.awt.BorderLayout;
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

	// Define the starting cell for the expense typle list
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
		
		// Create an instance of the app class
		SharedExpensesApp expenseApp = new SharedExpensesApp();
		
		// Instantiate the data variables
		expenseArray = new ArrayList<Expense>();
		guestArray = new ArrayList<String>();
		dataArray = new String[DATA_TABLE_X_DIMENSION][DATA_TABLE_Y_DIMENSION];
		columnNames = new ArrayList<String>();
		typeNames = new ArrayList<String>();

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
		frame.setSize(650, 600);
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
		
		setInitialColumnNames();
		
	}

	public void setInitialColumnNames() {
		
		JTableHeader th = dataTable.getTableHeader();
		TableColumnModel tcm = th.getColumnModel();
		
		TableColumn tc = tcm.getColumn(0);
		tc.setHeaderValue("Expense Item");
		
		tc = tcm.getColumn(1);
		tc.setHeaderValue("Expense Total");
		
		for(int i = 2; i < DATA_TABLE_X_DIMENSION; i++) {

			tc = tcm.getColumn(i);
			tc.setHeaderValue("--");
			
		}
		
		th.repaint();
		
	}
	
	public double calcExpensesTotal(String expenseType) {
		
		double total = 0.0;
		
		for(Expense currExpense: expenseArray) {
			if(currExpense.getExpenseType() == expenseType) {
				total += currExpense.getCost();
			}
		}
		
		return total;
	}
	
	public double calcExpenseTotalByGuest(String expenseType, String guest) {
		
		double total = 0.0;
		
		for(Expense currExpense: expenseArray) {
			if((currExpense.getExpenseType() == expenseType) && (currExpense.getGuest() == guest)) {
				total += currExpense.getCost();
			}
		}
				
		return total;
	}
	
	public double calcTotalOfAllExpensesByGuest(String guest) {
		
		double total = 0.0;
		
		for(Expense currExpense: expenseArray) {
			if(currExpense.getGuest() == guest) {
				total += currExpense.getCost();
			}
		}
		
		return total;
	}
	
	public double calcTotalOfAllExpenses()  {
		
		double total = 0.0;
		
		for(Expense currExpense: expenseArray) {
			total += currExpense.getCost();
		}
		
		return total;
	}
	
	
	public double calcSharedExpenseTotal() {
		
		return calcTotalOfAllExpenses()/guestArray.size();

	}
	
	public double calcBallanceByGuest(String guest) {
		
		return calcSharedExpenseTotal()- calcTotalOfAllExpensesByGuest(guest);
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
			
			while (newFile == null) {
				
				fileChooser.setDialogTitle("Please specify the file open");
				fileChooser.showOpenDialog(frame);
				newFile = fileChooser.getSelectedFile();
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
						
						// Add the new guest column
						TableColumn tc = new TableColumn();
						tc.setHeaderValue(expenseArray.get(i).getGuest());
						dataTable.addColumn(tc);
					
						// Add the Expense type to the table
						if(!typeNames.contains(expenseArray.get(i).getExpenseType())) {
							
							System.out.println("Adding new expense type: " + expenseArray.get(i).getExpenseType() + " dataArray[" + (int)EXPENSE_X_BASE + "][" + (int)(EXPENSE_Y_BASE+typeNames.size()) + "]");
							
							typeNames.add(expenseArray.get(i).getExpenseType());
							dataArray[(int)EXPENSE_X_BASE][(int)(EXPENSE_Y_BASE+typeNames.size())] = expenseArray.get(i).getExpenseType();
//							dataTable.getModel().setValueAt(expenseArray.get(i).getExpenseType(), (int)EXPENSE_X_BASE, (int)(EXPENSE_Y_BASE+typeNames.size()));
							
						}
						
						
						splitPane.updateUI();
//						rebuildDataTable();
					
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
		
//			System.out.println("getRowCount(): " + DATA_TABLE_Y_DIMENSION);
			return DATA_TABLE_Y_DIMENSION;
		
		}

		public void setValueAt(int x, int y, String newData) {
			
			dataArray[x][y] = newData; 
			
		}
		
		public Object getValueAt(int expenseType, int column) {
		
			String returnString = null;
			double returnVal = 0.0;
			
//			return("[" + expenseType + "][" + column + "]");
			
			System.out.println("[" + expenseType + "][" + column + "]");
			
			System.out.println("expenseArray.size(): " + expenseArray.size());
			
			
			if(expenseArray.size() > expenseType) {

				System.out.println("processing!" + "[" + expenseType + "][" + column + "]");
				
				switch(column) {
				case 0: 

					System.out.println("printing expense type string: " + expenseArray.get(expenseType).getExpenseType());
					return expenseArray.get(expenseType).getExpenseType();
					
				case 1: 

					returnVal = calcExpensesTotal(expenseArray.get(expenseType).getExpenseType());
					return (returnVal == 0.0) ? "-" : String.format("$%.2f", returnVal);
					
				default: 

					// Decrement the column number by two to account for the offset into the table where guest columns start
					column -= 2;
					
					if(guestArray.size() > column) {
						return String.format("$%.2f", calcExpenseTotalByGuest(typeNames.get(expenseType), guestArray.get(column)));
					} else {
						return "-";
					}
					
//					return (guestArray.size() > column-2) ? "-" : String.format("$%.2f", calcExpenseTotalByGuest(typeNames.get(expenseType), guestArray.get(column-1)));
						
				}
			}

			return null;
		}
		
//		public String getColumnName(int col) {
//		
//			return columnNames.get(col);
//		
//		}
		
		// Provide a method to add a new column header.  This will take a new guest name when added.
		public void addColumnText(String newColumnName) {
			
			columnNames.add(2, newColumnName);
			for (int i = 0; i < columnNames.size(); i++) {
				System.out.println(columnNames.get(i));
			}
			
		}

		
	}
}

/*
 * TODO List
 * 
 *   Add check for max expense types
 *   
 *   Implement table change update for expense table, change fields to editable
 *   
 *  			
 *   
 * 
 * 
 * 
 */


