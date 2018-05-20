
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

public class SharedExpensesApp {

	// Class Variables
	JFrame frame;
	JPanel buttonPanel;
	JButton newTrackerButton, loadTrackerButton, addGuestButton, addExpenseButton, 
			showAllExpensesButton, saveTrackerButton;
	JFrame addExpenseFrame; 
	JTextField guestText, expenseTypeText, costText, dateText, noteText;
	JFormattedTextField dateInput;
		
	JTable expenseTable;
	
	File currentFile = null;
	
	// Main entry point
	public static void main(String[] args) {
		SharedExpensesApp expenseApp = new SharedExpensesApp();
		expenseApp.go();
	}
	
	// Main worker function
	public void go() {
		
		// Create all the buttons for the Button Panel
		newTrackerButton = new JButton("New Tracker");
		loadTrackerButton = new JButton("Load Tracker");
		addGuestButton = new JButton("Add Guest");
		addExpenseButton = new JButton("Add Expense");
		showAllExpensesButton = new JButton("Show All Expenses");
		saveTrackerButton = new JButton("Save");

		// Create the main Swing frame
		frame = new JFrame("Shared Expense Tracker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create the expense table
		expenseTable = new JTable(new SharedExpenseTableModel());
		expenseTable.setPreferredScrollableViewportSize(new Dimension(650, 550));
		expenseTable.setFillsViewportHeight(true);
		
		// Create a scrolling JPanel for the expense table
		JScrollPane expensePane = new JScrollPane(expenseTable);
		frame.add(expensePane);
		
		// Create the button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2,3));
		
		// Add all the buttons to the button panel
		buttonPanel.add(newTrackerButton);
		buttonPanel.add(loadTrackerButton);
		buttonPanel.add(addGuestButton);
		buttonPanel.add(addExpenseButton);
		buttonPanel.add(showAllExpensesButton);
		buttonPanel.add(saveTrackerButton);
		
		// Set frame characteristics
		frame.getContentPane().add(BorderLayout.SOUTH, buttonPanel);
		frame.setSize(650, 600);
		frame.setVisible(true);
		
		// Setup all the button listeners
		addExpenseButton.addActionListener(new AddExpense());
		saveTrackerButton.addActionListener(new SaveTracker());
		loadTrackerButton.addActionListener(new LoadTracker());
		
	}
	
	class NewTracker implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
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
				ExpenseData.getInstance().setExpenseArray((ArrayList<Expense>) (inputStream.readObject()));
				inputStream.close();
				
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			
		}
	}
	
	class AddGuest implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
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
				outputStream.writeObject(ExpenseData.getInstance().getExpenseArray());
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
	

	class ShowAllExpenses implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	class AddExpenseOK implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			Date dateField = (Date) (dateInput.getValue());
			
			System.out.println(expenseTypeText.getText() + " " +  Double.parseDouble(costText.getText()) + " " + 
			           guestText.getText() + " " + dateField + " " + noteText.getText());
				
			Expense newExpense = new Expense(dateField, guestText.getText(), expenseTypeText.getText(), 
											 Double.parseDouble(costText.getText()), noteText.getText());
			
			ExpenseData.getInstance().getExpenseArray().add(newExpense);
			addExpenseFrame.setVisible(false);
		}
	}

	
}
