import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
/**
 * Tato trida reprezentuje hlavni tridu celeho programu.
 * V teto tride se inicializuje cely program, vcetne nacteni souboru, naslychani uzivatelskych vstupu z klavesnice
 * nebo z myse a zajistuje opakovane prekleslovani platna.
 */
public class Galaxy_SP2022 {

	public static void main(String[] args) {
		//String fileName = "E:\\FAVZCU\\KIV-UPG\\Semestralka\\galaxy_sp_2022\\data\\solar.csv"; #DEBUG
		CSVLoader csvLoader;
		if(args.length < 1){
			System.out.println("No parameters detected. Ending program.");
			return;
		}
		csvLoader = new CSVLoader(args[0]);
		//csvLoader = new CSVLoader(fileName); #DEBUG

		//Konvertujem predane data ze souboru do instance tridy Space, pokud se nenacte soubor spravne, do promenne
		//space se ulozi null
		Space space = csvLoader.parseDataToSpace();
		if(space == null) {
			System.out.println("Could not load data from file, exiting program.");
			return;
		}
		JFrame window = new JFrame();
		window.setTitle("Semestralni prace UPG 2022 - Vesmir");
		window.setMinimumSize(new Dimension(800,600));
		window.setSize(800, 600);


		long startTime = System.currentTimeMillis();
		space.setSimStartTime(startTime);
		DrawingPanel panel = new DrawingPanel(space);

		window.add(panel, BorderLayout.CENTER);


		panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//panel.getSelected(new Coord2D(e.getX(),e.getY()));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				//posilame relativni souradnice (vuci platnu) mysi pri stisknuti jako parametry metody getSelected
				panel.getSelected(new Coord2D(e.getX(),e.getY()));
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

		//Tato trida detekuje zda uzivatel stisknul mezernik, pokud ano pozastavi se simulace, popripade se obnovi
		//pokud simulace je uz pozastavena
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
			@Override
			public void run() {
				if(!space.isSimPaused()) space.updateSystem(); //pokud neni simulace pozastavena, updatne se nas system/vesmir
				panel.repaint();
			}
		},0,15);

		window.pack(); //udela resize okna dle komponent

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null); //vycentrovat na obrazovce
		window.setVisible(true);
	}


}
