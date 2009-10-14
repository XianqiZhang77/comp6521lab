package comp6521.lab.com.GUI;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Dimension;

public class QueryBParameters extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jCntryCodeLabel = null;
	private JComboBox jCntryCodeComboBox = null;

	/**
	 * This is the default constructor
	 */
	public QueryBParameters() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		jCntryCodeLabel = new JLabel();
		jCntryCodeLabel.setText("Select Country Code");
		this.setSize(297, 38);
		this.setLayout(new GridBagLayout());
		this.add(jCntryCodeLabel, gridBagConstraints);
		this.add(getJCntryCodeComboBox(), gridBagConstraints1);
	}

	/**
	 * This method initializes jCntryCodeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJCntryCodeComboBox() {
		if (jCntryCodeComboBox == null) {
			jCntryCodeComboBox = new JComboBox();
		}
		return jCntryCodeComboBox;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
