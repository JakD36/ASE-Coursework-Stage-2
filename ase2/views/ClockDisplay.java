package ase2.views;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import ase2.simulation.Clock;
import ase2.simulation.Simulation;

public class ClockDisplay extends JPanel {
	private static final long serialVersionUID = 1L;
	JLabel face;

	//copy of the clock instance
	Clock clock = Clock.getInstance();
	
	public ClockDisplay(Simulation sim) {
		this.setLayout(new GridLayout(1,1));
		
		
		face = new JLabel("<html><font color='green'>00:00:000</font></html>");
		face.setHorizontalAlignment(JLabel.RIGHT);
		
		this.add(face);
		
		//stop clock resizing as length of characters changes
		face.setMinimumSize(new Dimension(50,10));
		
		//update time on GUI thread
		new SwingWorker<Void, String>() {

			@Override
			protected Void doInBackground() throws Exception {
				while(true) {
					publish(clock.getTimeString());
				}
			}
			
			
			protected void process(List <String> updates) {
				//only update if sim is active
				if(sim.getState() != Thread.State.TERMINATED)
					face.setText("<html><font color='green'>" + updates.get(updates.size() - 1) + "</font></html>");
			}
		}.execute();
	}
}
