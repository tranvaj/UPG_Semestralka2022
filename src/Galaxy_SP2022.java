import java.io.FileNotFoundException;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Galaxy_SP2022 {

	public static void main(String[] args) {
		String fileName = "E:\\FAVZCU\\KIV-UPG\\Semestralka\\galaxy_sp_2022\\data\\solar.csv";
		CSVLoader csvLoader = new CSVLoader(fileName);
		//spaces
		Space space = csvLoader.parseDataToSpace();
		JFrame window = new JFrame();
		window.setTitle("Semestralni prace UPG 2022 - Vesmir");
		window.setMinimumSize(new Dimension(800,600));
		window.setSize(800, 600);
		long startTime = System.currentTimeMillis();

		space.setSimStartTime(startTime);
		DrawingPanel panel = new DrawingPanel(space);
		window.add(panel, BorderLayout.CENTER);

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(new KeyEventDispatcher() {
					@Override
					public boolean dispatchKeyEvent(KeyEvent e) {
						if(e.getID() == KeyEvent.KEY_PRESSED
								&& e.getKeyCode() == KeyEvent.VK_SPACE){
							if(!space.isSimPaused()) {
								space.startPause();
							} else{
								space.stopPause();
							}

						}
						return false;
					}
				});

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			long timeElapsedSinceUpdate = 0;

			@Override
			public void run() {
				//long currentTime = System.currentTimeMillis() - startTime;
				//timeElapsedSinceUpdate = currentTime - timeElapsedSinceUpdate;
				if(!space.isSimPaused()) space.updateSystem();
				panel.repaint();
			}
		},0,20);

		window.pack(); //udela resize okna dle komponent

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null); //vycentrovat na obrazovce
		window.setVisible(true);
	}


}
