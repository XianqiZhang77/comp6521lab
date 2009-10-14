package comp6521.lab.com.GUI;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;



public class Project {
	
	public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
    }
	private static void createAndShowGUI() {
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.add(new MainPanel());
        f.setSize(250,250);
        f.setVisible(true);
    } 

}
