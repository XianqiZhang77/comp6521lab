/**
 * 
 */
package comp6521.lab.com.GUI;

import javax.swing.JFrame;

/**
 * @author dimitri.tiago
 *
 */
public class ControlThread implements Runnable 
{
	@Override
	public void run() 
	{
		Control control = new Control();

		control.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);	// exit on close
		control.setSize(250, 190);	// set size of frame
		control.setVisible(true);	// display frame
	}
}
