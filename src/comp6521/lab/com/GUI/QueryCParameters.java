package comp6521.lab.com.GUI;

import java.awt.GridBagLayout;
import javax.swing.JPanel;

public class QueryCParameters extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	public QueryCParameters() {
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
