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
		String fileName = "E:\\FAVZCU\\KIV-UPG\\Semestralka\\galaxy_sp_2022\\data\\pulsar.csv";
		CSVLoader csvLoader = new CSVLoader(fileName);
		Space space = csvLoader.parseDataToSpace();
		JFrame window = new JFrame();
		window.setTitle("Semestralni prace UPG 2022 - Vesmir");
		window.setMinimumSize(new Dimension(800,600));
		window.setSize(800, 600);
		DrawingPanel panel = new DrawingPanel(space);
		window.add(panel, BorderLayout.CENTER);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				panel.repaint();
			}
		},0,20);

		window.pack(); //udela resize okna dle komponent

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null); //vycentrovat na obrazovce
		window.setVisible(true);
	}
}
