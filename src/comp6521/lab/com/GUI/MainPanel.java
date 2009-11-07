package comp6521.lab.com.GUI;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Point;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton jQueryAButton = null;
	private JButton jQueryBButton = null;
	private JButton jQueryCButton = null;
	private JButton jQueryDButton = null;
	private JButton jQueryEButton = null;
	private JButton jQueryFButton = null;
	private JButton jQueryZButton = null;

	/**
	 * This is the default constructor
	 */
	public MainPanel() {

		super();
		initialize();

    }
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setSize(511, 377);
		this.add(getJQueryAButton(), null);
		this.add(getJQueryBButton(), null);
		this.add(getJQueryCButton(), null);
		this.add(getJQueryDButton(), null);
		this.add(getJQueryEButton(), null);
		this.add(getJQueryFButton(), null);
		this.add(getJQueryZButton(), null);
	}

	/**
	 * This method initializes jQueryAButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJQueryAButton() {
		if (jQueryAButton == null) {
			jQueryAButton = new JButton();
			jQueryAButton.setPreferredSize(new Dimension(100, 20));
			jQueryAButton.setLocation(new Point(31, 27));
			jQueryAButton.setSize(new Dimension(100, 20));
			jQueryAButton.setText("Query A");
			jQueryAButton.addActionListener(actionListner);
		}
		return jQueryAButton;
	}

	/**
	 * This method initializes jQueryBButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJQueryBButton() {
		if (jQueryBButton == null) {
			jQueryBButton = new JButton();
			jQueryBButton.setPreferredSize(new Dimension(100, 20));
			jQueryBButton.setText("Query B");
			jQueryBButton.setLocation(new Point(32, 74));
			jQueryBButton.setSize(new Dimension(100, 20));
			jQueryAButton.addActionListener(actionListner);
		}
		return jQueryBButton;
	}

	/**
	 * This method initializes jQueryCButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJQueryCButton() {
		if (jQueryCButton == null) {
			jQueryCButton = new JButton();
			jQueryCButton.setPreferredSize(new Dimension(100, 20));
			jQueryCButton.setLocation(new Point(33, 123));
			jQueryCButton.setSize(new Dimension(100, 20));
			jQueryCButton.setText("Query C");
		}
		return jQueryCButton;
	}

	/**
	 * This method initializes jQueryDButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJQueryDButton() {
		if (jQueryDButton == null) {
			jQueryDButton = new JButton();
			jQueryDButton.setPreferredSize(new Dimension(100, 20));
			jQueryDButton.setLocation(new Point(35, 170));
			jQueryDButton.setSize(new Dimension(100, 20));
			jQueryDButton.setText("Query D");
		}
		return jQueryDButton;
	}

	/**
	 * This method initializes jQueryEButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJQueryEButton() {
		if (jQueryEButton == null) {
			jQueryEButton = new JButton();
			jQueryEButton.setPreferredSize(new Dimension(100, 20));
			jQueryEButton.setLocation(new Point(36, 212));
			jQueryEButton.setSize(new Dimension(100, 20));
			jQueryEButton.setText("Query E");
		}
		return jQueryEButton;
	}

	/**
	 * This method initializes jQueryFButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJQueryFButton() {
		if (jQueryFButton == null) {
			jQueryFButton = new JButton();
			jQueryFButton.setPreferredSize(new Dimension(100, 20));
			jQueryFButton.setLocation(new Point(39, 258));
			jQueryFButton.setSize(new Dimension(100, 20));
			jQueryFButton.setText("Query F");
		}
		return jQueryFButton;
	}

	/**
	 * This method initializes jQueryZButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJQueryZButton() {
		if (jQueryZButton == null) {
			jQueryZButton = new JButton();
			jQueryZButton.setPreferredSize(new Dimension(100, 20));
			jQueryZButton.setLocation(new Point(39, 306));
			jQueryZButton.setSize(new Dimension(100, 20));
			jQueryZButton.setText("Query Z");
		}
		return jQueryZButton;
	}
	ActionListener actionListner = new ActionListener(){
   	 public void actionPerformed(ActionEvent e) {
	    	JFormattedTextField source = (JFormattedTextField)e.getSource();
	    	Object vlaue =  source.getValue();	        
	    }
   };
}  //  @jve:decl-index=0:visual-constraint="8,0"
