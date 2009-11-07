package comp6521.lab.com.GUI;

import java.awt.*;
import java.awt.event.ActionListener;


import javax.swing.*;



public class Project {
	
	JPanel mainPanel;
	//JFrame frame;
	
	public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
    }
	private static void createAndShowGUI() {
        JFrame frame = new JFrame("Advanced Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.add(new MainPanel());
        frame.setSize(250,250);
        frame.setVisible(true);
        
       
        
    } 
	/**
	 * This method initializes jQueryBButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQueryAButton() {
		JButton QueryAButton = new JButton();
		QueryAButton.setSize(new Dimension(100, 20));
		
		QueryAButton.addActionListener((ActionListener) this);
		
		QueryAButton.setText("Query E");
		
		return QueryAButton;
	}

	/**
	 * This method initializes jQueryBButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQueryBButton() {
		JButton QueryBButton = new JButton();
		QueryBButton.setSize(new Dimension(100, 20));
		
		QueryBButton.addActionListener((ActionListener) this);
		
		QueryBButton.setText("Query E");
		
		return QueryBButton;
	}

	/**
	 * This method initializes jQueryCButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQueryCButton() {
		JButton QueryCButton = new JButton();
		QueryCButton.setSize(new Dimension(100, 20));
		
		QueryCButton.addActionListener((ActionListener) this);
		
		QueryCButton.setText("Query E");
		
		return QueryCButton;
	}

	/**
	 * This method initializes jQueryDButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQueryDButton() {
		JButton QueryEButton = new JButton();
		QueryEButton.setSize(new Dimension(100, 20));
		
		QueryEButton.addActionListener((ActionListener) this);
		
		QueryEButton.setText("Query E");
		
		return QueryEButton;
	}

	/**
	 * This method initializes QueryEButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQueryEButton() {
		JButton QueryEButton = new JButton();
		QueryEButton.setSize(new Dimension(100, 20));
		
		QueryEButton.addActionListener((ActionListener) this);
		
		QueryEButton.setText("Query E");
		
		return QueryEButton;
	}

	/**
	 * This method initializes QueryFButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQueryFButton() {
		JButton QueryFButton = new JButton();
		QueryFButton.setSize(new Dimension(100, 20));
		
		QueryFButton.addActionListener((ActionListener) this);
		
		QueryFButton.setText("Query F");
		
		return QueryFButton;
	}

	/**
	 * This method initializes QueryZButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQueryZButton() {
		
		JButton QueryZButton = new JButton();
		QueryZButton.setSize(new Dimension(100, 20));
		
		QueryZButton.addActionListener((ActionListener) this);
		
		QueryZButton.setText("Query Z");
		
		return QueryZButton;
	}
	
}
