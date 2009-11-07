package comp6521.lab.com.GUI;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DateFormatter;




import java.text.*;
import java.awt.Dimension;

/**

 */
public class QueryAParameters extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    
    
    //Labels to identify the text fields
    private JLabel startDateLabel;
    private JLabel endDateLabel;
    private JLabel indexLabel;
    
    
    //Strings for the labels   
    private static String startDateString = "Start Date";
    private static String endDateString = "End Date";
    private static String indexString = "Index";  //  @jve:decl-index=0:
    
    //Text fields for data entry  
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
  
    
    //Checkbox for data entry
    private ButtonGroup 	indexButtonGroup;  //  @jve:decl-index=0:
    private JRadioButton 	withIndexRadioButton , withoutIndexRadioButton;

   
    //private MyVerifier verifier = new MyVerifier();

    public QueryAParameters() {
        init();
    }

    public void init(){
    	setLayout(new BorderLayout());
        

        //Create the labels.       
        startDateLabel = new JLabel(startDateString);
        endDateLabel = new JLabel(endDateString);
        indexLabel = new JLabel(indexString);
        
        //Create Radio Buttons
        indexButtonGroup = new ButtonGroup();
        withIndexRadioButton = new JRadioButton("With Index", false);
		withoutIndexRadioButton = new JRadioButton("Without Index", true);
		indexButtonGroup.add(withIndexRadioButton);
		indexButtonGroup.add(withoutIndexRadioButton);
        
      
        //Date Format
        DateFormat format = new SimpleDateFormat("MM/dd/yy");
        DateFormatter displayFormatter = new DateFormatter(format);
      
        //Create the text fields and set them up.
      
        startDateField = new JFormattedTextField(displayFormatter);
        startDateField.setInputVerifier(new FormattedTextFieldVerifier());
        
        endDateField = new JFormattedTextField(displayFormatter);
        endDateField.setInputVerifier(new FormattedTextFieldVerifier());
        
       
        
        // Create Buttons
        JButton executeButton = new JButton("Execute");
        executeButton.setPreferredSize(new Dimension(100,20));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100,20));

        //Register an action listener to handle Return.
        startDateField.addActionListener(actionListner);        
        endDateField.addActionListener(actionListner);        
        
        //Tell accessibility tools about label/textfield pairs.
        
        startDateLabel.setLabelFor(startDateField);
        endDateLabel.setLabelFor(endDateField);
       
        
        
        
        
        //Lay out the labels in a panel.
        JPanel labelPane = new JPanel(new GridLayout(0,1));
        labelPane.add(startDateLabel);        
        labelPane.add(endDateLabel);
        labelPane.add(indexLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel(new GridLayout(0,1));
        fieldPane.add(startDateField);        
        fieldPane.add(endDateField);
        JPanel radioButtonPane = new JPanel(new FlowLayout());
        radioButtonPane.add(withoutIndexRadioButton);
        radioButtonPane.add(withIndexRadioButton);
        fieldPane.add(radioButtonPane);
        
        
        
        //Layout the Buttons in a panel.
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(executeButton);
        buttonPane.add(cancelButton);

        //Put the panels in this panel, labels on left,
        //text fields on right.
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(labelPane, BorderLayout.CENTER);
        add(fieldPane, BorderLayout.LINE_END);
        add(buttonPane, BorderLayout.SOUTH);
    	
    }
 
    ActionListener actionListner = new ActionListener(){
    	 public void actionPerformed(ActionEvent e) {
	    	JFormattedTextField source = (JFormattedTextField)e.getSource();
	    	Object vlaue =  source.getValue();	        
	    }
    };
   
    public class FormattedTextFieldVerifier extends InputVerifier {
        public boolean verify(JComponent input) {
            if (input instanceof JFormattedTextField) {
                JFormattedTextField formattedTextField = (JFormattedTextField)input;
                AbstractFormatter formatter = formattedTextField.getFormatter();
                if (formatter != null) {
                    String text = formattedTextField.getText();
                    try {
                         formatter.stringToValue(text);
                         return true;
                     } catch (ParseException pe) {
                         return false;
                     }
                 }
             }
             return true;
         }
         public boolean shouldYieldFocus(JComponent input) {
             return verify(input);
         }
     }
    
    public class TextFieldVerifier extends InputVerifier {
        public boolean verify(JComponent input) {
            if (input instanceof JTextField) {
                JTextField textField = (JTextField)input;
                
                String text = textField.getText();
             }
             return true;
         }
         public boolean shouldYieldFocus(JComponent input) {
             return verify(input);
         }
     }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("InputVerificationDialogDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 500));
        JComponent newContentPane = new QueryAParameters();
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
	
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    
}

