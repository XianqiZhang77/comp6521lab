package comp6521.lab.com.GUI;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.DateFormatter;


import java.text.*;
import java.awt.Dimension;

/**

 */

public class QueryBParameters extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    
    
    //Labels to identify the text fields
    private JLabel cntryCodeLabel;
    
    
    //Strings for the labels
    private static String cntryCodeString = "Country Code";  //  @jve:decl-index=0:
   
    
    //Text fields for data entry
    private JTextField cntryCodeField;
    
    
   
    //private MyVerifier verifier = new MyVerifier();

    public QueryBParameters() {
        super(new BorderLayout());
        

        //Create the labels.
        cntryCodeLabel = new JLabel(cntryCodeString);
        
       
      
        
      
        //Create the text fields and set them up.
        cntryCodeField = new JTextField();
        cntryCodeField.setSize(20, 100);
        cntryCodeField.setInputVerifier(getInputVerifier());
        
        
        
        
       

        //Register an action listener to handle Return.
        cntryCodeField.addActionListener(actionListner);
        

        //Tell accessibility tools about label/textfield pairs.
        cntryCodeLabel.setLabelFor(cntryCodeField);
       

        //Lay out the labels in a panel.
        JPanel labelPane = new JPanel(new GridLayout(0,1));
        labelPane.add(cntryCodeLabel);
        

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel(new GridLayout(0,1));
        fieldPane.add(cntryCodeField);
       
       

        //Put the panels in this panel, labels on left,
        //text fields on right.
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(labelPane, BorderLayout.CENTER);
        add(fieldPane, BorderLayout.LINE_END);
    }

    
 
    ActionListener actionListner = new ActionListener(){
    	 public void actionPerformed(ActionEvent e) {
	    	JFormattedTextField source = (JFormattedTextField)e.getSource();  //  @jve:decl-index=0:
	    	Object vlaue =  source.getValue();	        
	    }
    };
   
   

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Quer B");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(350, 200));
        JComponent newContentPane = new QueryBParameters();
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

