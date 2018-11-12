package dk.anders.beertabGui;

//import javax.swing.event.ListSelectionEvent;
//import javax.swing.event.ListSelectionListener;
//import javax.swing.ListSelectionModel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class Beertab_gui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField username_input;
	private JTextField beverage_input;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Beertab_gui frame = new Beertab_gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static boolean isNumeric(String strNum){
		try {
			Integer.parseInt(strNum);
		}catch(NumberFormatException | NullPointerException nfe){
			return false;
		}
		return true;
	}
	/**
	 * Create the frame.
	 */
	public Beertab_gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel input_panel = new JPanel();
		panel.add(input_panel);

		JLabel lblUsername = new JLabel("Username");
		input_panel.add(lblUsername);

		username_input = new JTextField();
		input_panel.add(username_input);
		username_input.setColumns(7);

		JLabel lblBeverage = new JLabel("Beverage");
		input_panel.add(lblBeverage);

		beverage_input = new JTextField();
		input_panel.add(beverage_input);
		beverage_input.setColumns(7);

		JButton btnNewButton = new JButton("Apply");
		input_panel.add(btnNewButton);

		JPanel table_panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) table_panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(table_panel);

		//use a table model to work with the tabel dynamically.
		DefaultTableModel model = new DefaultTableModel();

		//use the model on the table
		table = new JTable(model);
		table_panel.add(table);

		//adding the first column.
		model.addColumn("name\\beverage");

		//enabling the selection of a single cell
		table.setCellSelectionEnabled(true);  

		//This mouseListener is used with the table to handle mouse clicks
		MouseListener mouseListener = new MouseAdapter() {
			//identifying mouse click
			public void mousePressed(MouseEvent mouseEvent) {
				int modifiers = mouseEvent.getModifiers();
				if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
					//System.out.println("Left button pressed.");
				}
				if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
					//System.out.println("Middle button pressed.");
				}
				if ((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
					//System.out.println("Right button pressed.");
				}
			}


			public void mouseReleased(MouseEvent mouseEvent) {
				if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
					//System.out.println("Left button released.");
					int row = table.rowAtPoint(mouseEvent.getPoint());
					int col = table.columnAtPoint(mouseEvent.getPoint());

					//Get the value at the clicked cell.
					String s=(String)table.getValueAt(row, col);

					//When no data has been entered. The first click. 
					if(s == null) { 
						table.setValueAt("1", row, col);
					}

					//We don't want to change the name or beverage.
					if(isNumeric(s)) {
						//change string to int
						int value=Integer.parseInt(s);
						value++;
						table.setValueAt(Integer.toString(value), row, col);
					}
				}
				if (SwingUtilities.isMiddleMouseButton(mouseEvent)) {
					//System.out.println("Middle button released.");
				}
				if (SwingUtilities.isRightMouseButton(mouseEvent)) {
					//System.out.println("Right button released.");
					int row = table.rowAtPoint(mouseEvent.getPoint());
					int col = table.columnAtPoint(mouseEvent.getPoint());
					String s=(String)table.getValueAt(row, col);

					if(isNumeric(s)) {
						int value=Integer.parseInt(s);
						//no minus numbers
						if(value!=0) {
							value--;
							table.setValueAt(Integer.toString(value), row, col);
						}
					}

				}

			}   
		};

		//add the mouselistener to the table. 
		table.addMouseListener(mouseListener);

		//Add the table to a scrollpane.
		JScrollPane sp=new JScrollPane(table);    
		table_panel.add(sp);  

		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {


				if(!username_input.getText().equals("")) {
					String username=username_input.getText();
					model.addRow(new Object[] {username});
					username_input.setText("");
				}
				if(!beverage_input.getText().equals("")) {
					String beverage=beverage_input.getText();
					model.addColumn(beverage);
					beverage_input.setText("");
				}

			}
		});
	}
}
