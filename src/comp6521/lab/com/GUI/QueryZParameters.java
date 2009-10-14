package comp6521.lab.com.GUI;

import java.awt.GridBagLayout;
import javax.swing.JPanel;

public class QueryZParameters extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	public QueryZParameters() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
	}

}
