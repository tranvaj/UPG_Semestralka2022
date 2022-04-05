import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class Galaxy_SP2022 {

	public static void main(String[] args) {
		String fileName = "C:\\Users\\Uzivatel\\Desktop\\Skola\\KIV-UPG\\UPG_Semestralka2022\\data\\solar.csv";
		CSVLoader csvLoader = new CSVLoader(fileName);
		//spaces
		Space space = csvLoader.parseDataToSpace();
		JFrame window = new JFrame();
		window.setTitle("Semestralni prace UPG 2022 - Vesmir");
		window.setMinimumSize(new Dimension(800,600));
		window.setSize(800, 600);
		long startTime = System.currentTimeMillis();
		JPanel jPanel = new JPanel();
		jPanel.add(new TextArea());
		space.setSimStartTime(startTime);
		DrawingPanel panel = new DrawingPanel(space);

		window.add(panel, BorderLayout.CENTER);
		//window.add(panel, BorderLayout.TOP);


		panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				panel.getSelected(new Coord2D(e.getX(),e.getY()));
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

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
