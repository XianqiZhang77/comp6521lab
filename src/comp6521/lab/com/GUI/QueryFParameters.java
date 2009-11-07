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
public class QueryFParameters extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    
    
    //Labels to identify the text fields
    private JLabel orderKeyLabel;
    private JLabel partKeyLabel;
    private JLabel supplierKeyLabel;
    private JLabel lineNumberLabel;
    private JLabel quantityLabel;
    private JLabel extendedPriceLabel;
    private JLabel discountLabel;
    private JLabel taxLabel;
    private JLabel returnFlagLabel;
    private JLabel lineStatusLabel;
    private JLabel shipDateLabel;
    private JLabel commitDateLabel;
    private JLabel receiptDateLabel;
    private JLabel shipInstructionLabel;
    private JLabel commentLabel;
    private JLabel indexLabel;
    
    
    //Strings for the labels   
    private static String orderKeyString = "Order Key";
    private static String partKeyString = "Part Key";
    private static String supplierKeyString = "Supplier Key";
    private static String lineNumberString = "Line Number Key";
    private static String quantityString = "Quantity";
    private static String extendedPriceString = "Extended Price";
    private static String discountString = "Discount";
    private static String taxString = "Tax";
    private static String returnFlagString = "Return Flag";  //  @jve:decl-index=0:
    private static String lineStatusString = "Line Status";
    private static String shipDateString = "Ship Date";
    private static String commitDateString = "Commit Date";
    private static String receiptDateString = "Receipt Date";
    private static String shipInstructionString = "Ship Instruction";
    private static String commentString = "Comment";  //  @jve:decl-index=0:
    private static String indexString = "Index";  //  @jve:decl-index=0:
    
    //Text fields for data entry  
    private JTextField orderKeyField;
    private JTextField partKeyField;
    private JTextField supplierKeyField;
    private JTextField lineNumberField;
    private JTextField quantityField;
    private JTextField extendedPriceField;
    private JTextField discountField;
    private JTextField taxField;
    private JTextField returnFlagField;
    private JTextField lineStatusField;
    private JFormattedTextField shipDateField;
    private JFormattedTextField commitDateField;
    private JFormattedTextField receiptDateField;
    private JTextField shipInstructionField;
    private JTextField commentField;
    
    //Checkbox for data entry
    private ButtonGroup 	indexButtonGroup;  //  @jve:decl-index=0:
    private JRadioButton 	withIndexRadioButton , withoutIndexRadioButton;
    
    //private MyVerifier verifier = new MyVerifier();

    public QueryFParameters() {
        setLayout(new BorderLayout());
        

        //Create the labels.       
        orderKeyLabel = new JLabel(orderKeyString);
        partKeyLabel = new JLabel(partKeyString);
        supplierKeyLabel = new JLabel(supplierKeyString);
        lineNumberLabel = new JLabel(lineNumberString);
        quantityLabel = new JLabel(quantityString);
        extendedPriceLabel = new JLabel(extendedPriceString);
        discountLabel = new JLabel(discountString);
        taxLabel = new JLabel(taxString);
        returnFlagLabel = new JLabel(returnFlagString);
        lineStatusLabel = new JLabel(lineStatusString);
        shipDateLabel = new JLabel(shipDateString);
        commitDateLabel = new JLabel(commitDateString);
        receiptDateLabel = new JLabel(receiptDateString);
        shipInstructionLabel = new JLabel(shipInstructionString);
        commentLabel = new JLabel(commentString);
        
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
      
        orderKeyField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        partKeyField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        supplierKeyField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        lineNumberField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        quantityField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        extendedPriceField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        discountField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        taxField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        returnFlagField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        lineStatusField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        
        shipDateField = new JFormattedTextField(displayFormatter);
        orderKeyField.setInputVerifier(new FormattedTextFieldVerifier());
        
        commitDateField = new JFormattedTextField(displayFormatter);
        orderKeyField.setInputVerifier(new FormattedTextFieldVerifier());
        
        receiptDateField = new JFormattedTextField(displayFormatter);
        orderKeyField.setInputVerifier(new FormattedTextFieldVerifier());
        
        shipInstructionField = new JTextField(20);
        orderKeyField.setInputVerifier(new TextFieldVerifier());
        commentField = new JTextField(20);
        commentField.setInputVerifier(new TextFieldVerifier());
        
        // Create Buttons
        JButton insertButton = new JButton("Add");
        insertButton.setPreferredSize(new Dimension(100,20));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100,20));

        //Register an action listener to handle Return.
        orderKeyField.addActionListener(actionListner);        
        partKeyField.addActionListener(actionListner);        
        supplierKeyField.addActionListener(actionListner);        
        lineNumberField.addActionListener(actionListner);         
        quantityField.addActionListener(actionListner);        
        extendedPriceField.addActionListener(actionListner);        
        discountField.addActionListener(actionListner);       
        taxField.addActionListener(actionListner);       
        returnFlagField.addActionListener(actionListner);       
        lineStatusField.addActionListener(actionListner);        
        shipDateField.addActionListener(actionListner);        
        commitDateField.addActionListener(actionListner);        
        receiptDateField.addActionListener(actionListner);
        shipInstructionField.addActionListener(actionListner);
        commentField.addActionListener(actionListner);
        
        //Tell accessibility tools about label/textfield pairs.
        
        orderKeyLabel.setLabelFor(orderKeyField);
        partKeyLabel.setLabelFor(partKeyField);
        supplierKeyLabel.setLabelFor(supplierKeyField);
        lineNumberLabel.setLabelFor(lineNumberField);
        quantityLabel.setLabelFor(quantityField);
        extendedPriceLabel.setLabelFor(extendedPriceField);
        discountLabel.setLabelFor(discountField);
        taxLabel.setLabelFor(taxField);
        returnFlagLabel.setLabelFor(returnFlagField);
        lineStatusLabel.setLabelFor(lineStatusField);
        shipDateLabel.setLabelFor(shipDateField);
        commitDateLabel.setLabelFor(commitDateField);
        receiptDateLabel.setLabelFor(receiptDateField);
        shipInstructionLabel.setLabelFor(shipInstructionField);
        commentLabel.setLabelFor(commentField);
        
        
        // Create Buttons
        
        
        //Lay out the labels in a panel.
        JPanel labelPane = new JPanel(new GridLayout(0,1));
        labelPane.add(orderKeyLabel);        
        labelPane.add(partKeyLabel);
        labelPane.add(lineNumberLabel);
        labelPane.add(quantityLabel);       
        labelPane.add(extendedPriceLabel);
        labelPane.add(discountLabel);
        labelPane.add(taxLabel);
        labelPane.add(returnFlagLabel);
        labelPane.add(lineStatusLabel);
        labelPane.add(shipDateLabel);
        labelPane.add(commitDateLabel);
        labelPane.add(receiptDateLabel);
        labelPane.add(shipInstructionLabel);
        labelPane.add(commentLabel);
        labelPane.add(indexLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel(new GridLayout(0,1));
        fieldPane.add(orderKeyField);        
        fieldPane.add(partKeyField);
        fieldPane.add(lineNumberField);
        fieldPane.add(quantityField);       
        fieldPane.add(extendedPriceField);
        fieldPane.add(discountField);
        fieldPane.add(taxField);
        fieldPane.add(returnFlagField);
        fieldPane.add(lineStatusField);
        fieldPane.add(shipDateField);
        fieldPane.add(commitDateField);
        fieldPane.add(receiptDateField);
        fieldPane.add(shipInstructionField);
        fieldPane.add(commentField);
       
        
        JPanel radioButtonPane = new JPanel(new FlowLayout());
        radioButtonPane.add(withoutIndexRadioButton);
        radioButtonPane.add(withIndexRadioButton);
        fieldPane.add(radioButtonPane);
        
        //Layout the Buttons in a panel.
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(insertButton);
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
	    	JFormattedTextField source = (JFormattedTextField)e.getSource();  //  @jve:decl-index=0:
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
        JComponent newContentPane = new QueryFParameters();
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

