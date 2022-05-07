import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;


import javax.swing.*;
/**
 * Tato trida reprezentuje hlavni tridu celeho programu.
 * V teto tride se inicializuje cely program, vcetne nacteni souboru, naslychani uzivatelskych vstupu z klavesnice
 * nebo z myse a zajistuje opakovane prekleslovani platna.
 * @author Vaclav Tran
 */
public class Galaxy_SP2022 {

	/**
	 * Hlavni trida
	 * @param args Vstupni parametry
	 */
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

		//graph
		JFrame graph = new JFrame();
		graph.setTitle("A21B0299P, Vaclav Tran");
		graph.setMinimumSize(new Dimension(800,600));
		graph.setSize(800, 600);
		graph.setLocationRelativeTo(window);
		graph.setLocation(graph.getX()+window.getWidth(), graph.getY());




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
				if(panel.getSelected(new Coord2D(e.getX(),e.getY())) != null){
					if(!graph.isVisible()){
						graph.setVisible(true);
					}
				}

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

				if(!space.isSimPaused()) { //pokud neni simulace pozastavena, updatne se nas system/vesmir
					space.checkCollision();
                    space.updateSystem();
					//kod zajistujici aktualizaci grafu rychlosti vybraneho objektu
					if(panel.getSelectedObj() != null){
						space.trackPlanetVel(panel.getSelectedObj());
						if(chart != null) {
							xyDataset = getDataset(processData(space.getTrackTime()),processData(space.getTrackVel()));
							chart.setChart(ChartFactory.createXYLineChart(
									"Rychlost vesmirneho objektu " + panel.getSelectedObj().getName() + " za poslednich 30 sekund",
									"t [s]",
									"v [km/h]",
									xyDataset));
						}
					}
				}
				panel.repaint();

			}
		},0,10);

		window.pack(); //udela resize okna dle komponent
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null); //vycentrovat na obrazovce
		window.setVisible(true);

		graph.pack();
		graph.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		//graph.setLocationRelativeTo(null);
		chart = new ChartPanel(createXYGraph(processData(space.getTrackTime()),processData(space.getTrackVel())));
		graph.add(chart);
	}

	//atributy jsou tady abych nemusel scrollovat porad nahoru : D, ale meli by byt nahore

	/**
	 * Zobrazitelny graf
	 */
	private static ChartPanel chart;
	/**
	 * X,Y data
	 */
	private static XYDataset xyDataset;

	/**
	 * Prevede frontu na list, poradi prvku se uchovava.
	 * @param data Fronta
	 * @return List
	 */
	private static List<Double> processData(Queue<Double> data){
		return data.stream().toList();
	}

	/**
	 * Vraci XY data pro JFreeChart graf. Predpoklada se, ze oba listy maji stejnou velikost.
	 * @param x List s x-ovyma prvkama
	 * @param y List s y-ovyma prvkama
	 * @return XY dataset
	 */
	private static XYDataset getDataset(List<Double> x, List<Double> y){
		xyDataset = new DefaultXYDataset();
		XYSeries rychlost = new XYSeries("Rychlost vesmirneho objektu");
		for(int i = 0; i < x.size(); i++){
			//3.6 krat pro km/h
			rychlost.add((double)x.get(i),(double)(3.6*y.get(i)));
		}

		XYSeriesCollection dataset = new XYSeriesCollection( );
		dataset.addSeries(rychlost);
		return dataset;
	}

	/**
	 * Vytvori a vrati XY graf
	 * @param x List s x-ovyma prvkama
	 * @param y List s y-ovyma prvkama
	 * @return Zobrazitelny XY graf
	 */
	private static JFreeChart createXYGraph(List<Double> x, List<Double> y){
		JFreeChart chart = ChartFactory.createXYLineChart("Rychlost","Cas","Rychlost", getDataset(x,y));
		return chart;
	}


}
