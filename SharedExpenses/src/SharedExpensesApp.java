

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
import javax.swing.table.*;

public class SharedExpensesApp {

	// Class Variables
	
	// GUI Objects
	JFrame frame;
	JPanel dataPanel, buttonPanel;
	JSplitPane splitPane;
	JButton addGuestButton, newTrackerButton, loadTrackerButton, addExpenseButton, saveTrackerButton;
	JFrame addExpenseFrame, addGuestFrame; 
	JTextField guestText, expenseTypeText, costText, dateText, noteText, addGuestText;
	JFormattedTextField dateInput;
	JTable dataTable, expenseTable;
	
	// Constants
	
	private final static int DATA_TABLE_X_DIMENSION = 11;
	private final static int DATA_TABLE_Y_DIMENSION = 10;
	public final static int MAX_GUESTS = 10;
	
	// Data Objects
	File currentFile = null;
	static ArrayList<Expense> expenseArray;
	static ArrayList<String> guestArray;
	static String[][] dataArray;
		
	// Main entry point
	public static void main(String[] args) {
		
		SharedExpensesApp expenseApp = new SharedExpensesApp();
		expenseArray = new ArrayList<Expense>();
		guestArray = new ArrayList<String>();
		dataArray = new String[DATA_TABLE_X_DIMENSION][DATA_TABLE_Y_DIMENSION];
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

			addExpenseFrame = new JFrame("Add Expense");
			
			JPanel addExpensePanel = new JPanel();

			JLabel guestLabel = new JLabel("Guest:");
			JLabel expenseTypeLabel = new JLabel("Expense Type:");
			JLabel costLabel = new JLabel("Cost:");
			JLabel dateLabel = new JLabel("Date");
			JLabel noteLabel = new JLabel("Note:");
			
			guestText = new JTextField(20);
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
			
			Date dateField = (Date) (dateInput.getValue());
			
			Expense newExpense = new Expense(dateField, guestText.getText(), expenseTypeText.getText(), 
											 Double.parseDouble(costText.getText()), noteText.getText());
			
			expenseArray.add(newExpense);
			addExpenseFrame.setVisible(false);
		}
	}

	class AddGuestOK implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if(guestArray.size() > MAX_GUESTS) {

				JOptionPane optionPane = new JOptionPane("Maximum number of guests reached.  Max = " + MAX_GUESTS, JOptionPane.ERROR_MESSAGE);
				JDialog dialog = optionPane.createDialog("Guest not added");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
		
			}
			
			String newGuest =  addGuestText.getText();

			// Always store guest names in all lower case!
			newGuest = newGuest.toLowerCase();
			
			if(!guestArray.contains(newGuest)) {
				
				guestArray.add(newGuest);
				
			
			} else {
				
				JOptionPane optionPane = new JOptionPane("Guest " + newGuest + " already exists", JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog = optionPane.createDialog("Guest not added");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
									
			}
			
			addGuestFrame.setVisible(false);
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
			return true;
		}
		
		
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
		
	    public String getColumnName(int col) {
	        return columnNames[col];
	    }
		
	}


	public class DataTableModel extends AbstractTableModel {
		
		private ArrayList<String> columnNames;
		
		// Provide a method to add a new column header.  This will take a new guest name when added.
		public void addColumnText(String newColumnName) {
			
			columnNames.add(newColumnName);
		}
		
		
		public DataTableModel() {
			
			columnNames = new ArrayList<String>();
			columnNames.add("Expense Item");
			columnNames.add("Expense Total");
			
			dataArray[0][7] = "Total Expenses by Guests";
			dataArray[0][8] = "Ballance of shared expense";
			
		}
		
		public int getColumnCount() {
		
			return columnNames.size();
		
		}
		
		public int getRowCount() {
		
			return DATA_TABLE_Y_DIMENSION;
		
		}
		
		public Object getValueAt(int x, int y) {
		
			return dataArray[x][y];
		}
		
		public String getColumnName(int col) {
		
			return columnNames.get(col);
		
		}
		
	}
}

/*
 * TODO List
 * 
 * 
 * 
 * 
 * 
 */


