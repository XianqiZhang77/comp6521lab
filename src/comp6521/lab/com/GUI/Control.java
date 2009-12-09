/**
 * Dec. 8, 2009
 * Control.java: class that allows us to stop/resume the execution of the db application
 */
package comp6521.lab.com.GUI;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JButton;

import comp6521.lab.com.Log;

/**
 * @author dimitri.tiago
 */
public class Control extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton[] buttons;	// button array
	private final String names[] = 	{"Pause", "Resume", "Step", "Step Out"};
	private GridLayout gridLayout;	// layout manager
	
	public Control()
	{
		super("COMP 6521 Project - Control");
		gridLayout = new GridLayout( 4, 1, 5, 5 );	// 2 by 7; gaps of 5 
		setLayout(gridLayout);
		buttons = new JButton[ names.length ];	// create array of JButtons
		
		// add action listeners to buttons
		for ( int count = 0; count < names.length; count++)
		{
			buttons[ count ] = new JButton( names[count] );
			buttons[ count ].addActionListener( this );	// register listener 
			add(buttons [ count ] );	// add button to JFrame
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// get option selected by user
		String selection = e.getActionCommand();	// get source
		
		// pause
		if ( selection.compareToIgnoreCase("Pause") == 0 )  
		{
			System.out.println("PAUSED");
			
			Log.Pause();
		}
		
		// resume
		if ( selection.compareToIgnoreCase("Resume") == 0 )  
		{
			System.out.println("RESUMED");
			
			Log.Continue();
		}		
		
		// step
		if ( selection.compareToIgnoreCase("Step") == 0 )  
		{
			System.out.println("STEP");
			
			Log.Step();
		}		
		
		// step out
		if ( selection.compareToIgnoreCase("Step Out") == 0 )  
		{
			System.out.println("STEP OUT");
			
			Log.StepOut();
		}		
	}	
}
