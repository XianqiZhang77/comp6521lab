package comp6521.lab.com.GUI;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Dimension;


public class QueryAParameters extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel jFromLabel = null;
	private JTextField jFromTextField = null;
	private JLabel jToLabel = null;
	private JTextField jToTextField = null;

	/**
	 * This is the default constructor
	 */
	public QueryAParameters() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.gridx = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 1;
		jToLabel = new JLabel();
		jToLabel.setText("To");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		jFromLabel = new JLabel();
		jFromLabel.setText("From");
		this.setSize(192, 61);
		this.setLayout(new GridBagLayout());
		this.add(jFromLabel, gridBagConstraints);
		this.add(getJFromTextField(), gridBagConstraints1);
		this.add(jToLabel, gridBagConstraints2);
		this.add(getJToTextField(), gridBagConstraints3);
	}

	/**
	 * This method initializes jFromTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJFromTextField() {
		if (jFromTextField == null) {
			jFromTextField = new JTextField();
			jFromTextField.setPreferredSize(new Dimension(120, 20));
		}
		return jFromTextField;
	}

	/**
	 * This method initializes jToTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJToTextField() {
		if (jToTextField == null) {
			jToTextField = new JTextField();
			jToTextField.setPreferredSize(new Dimension(120, 20));
			jToTextField.setHorizontalAlignment(JTextField.LEFT);
		}
		return jToTextField;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"  
