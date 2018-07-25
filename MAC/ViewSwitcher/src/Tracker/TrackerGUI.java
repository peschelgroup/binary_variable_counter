package Tracker;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Button;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JLabel;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import javax.swing.JList;
import javax.swing.AbstractListModel;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

/**
 * TrackerGUI - Class which holds all the elements needed for the program.
 * @author Ryan Lanciloti
 *
 */
public class TrackerGUI{

	/**
	 * frmViewTracker - Main JFrame containing all elements
	 */
	private JFrame frmViewTracker;
	
	/**
	 * txtName - Text area responsible for the name
	 */
	private JTextField txtName;
	
	/**
	 * timer - Responsible for updating the timer
	 */
	private ViewTimer timer;
	
	/**
	 * Pause button for the timer
	 */
	private JButton btnPause;
	
	/**
	 * begin - True if the program has started running, false otherwise
	 */
	private boolean begin;
	
	/**
	 * paused - True if timer is paused, false otherwise
	 */
	private boolean paused;
	
	/**
	 * lModel - Contains the elements of the left table
	 */
	private DefaultTableModel lModel;
	
	/**
	 * rModel - Contains the elements of the right table
	 */
	private DefaultTableModel rModel;
	
	/**
	 * rTable - Holds {Number of switches, Start time, Duration, End time}
	 * for the right view
	 */
	private JTable rTable;
	
	/**
	 * lTable - Holds {Number of switches, Start time, Duration, End time}
	 * for the left view
	 */
	private JTable lTable;
	
	/**
	 * selectedView - Keeps track of which view is selected
	 */
	private int selectedView;
	
	/**
	 * f - FileHandler object which allows data to be outputted to a file.
	 */
	private FileHandler f;
	
	/**
	 * switches - Keeps track of the number of times the view has been switched
	 */
	private int switches;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TrackerGUI window = new TrackerGUI();
					window.frmViewTracker.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TrackerGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmViewTracker = new JFrame();
		frmViewTracker.setTitle("View Tracker");
		frmViewTracker.setResizable(false);
		frmViewTracker.getContentPane().setEnabled(false);
		frmViewTracker.setBounds(100, 100, 666, 647);
		frmViewTracker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmViewTracker.getContentPane().setLayout(null);
		frmViewTracker.setFocusable(true);
		frmViewTracker.setAlwaysOnTop(true);
		frmViewTracker.setFocusTraversalKeysEnabled(false);
		
		JLabel lblTimer = new JLabel("Timer: ");
		lblTimer.setBounds(287, 590, 46, 14);
		frmViewTracker.getContentPane().add(lblTimer);
		
		JLabel time = new JLabel("00:00.000");
		time.setHorizontalAlignment(SwingConstants.TRAILING);
		time.setBounds(332, 590, 89, 14);
		frmViewTracker.getContentPane().add(time);
		
		JButton btnClose = new JButton("Close");
		btnClose.setBounds(551, 586, 89, 23);
		frmViewTracker.getContentPane().add(btnClose);
		btnClose.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		btnClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
		        frmViewTracker.dispose();
		        System.exit(0);
		    }
		});
		
		JButton btnBegin = new JButton("Begin");
		btnBegin.setBounds(10, 586, 89, 23);
		frmViewTracker.getContentPane().add(btnBegin);
		btnBegin.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		btnBegin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(begin != true){
					if(!txtName.getText().isEmpty()){
						int lCount = lModel.getRowCount();
						int rCount = rModel.getRowCount();
						switches = 0;
						for(int x = 0; x < lCount; x++){
							lModel.removeRow(0);
						}
						
						for(int x = 0; x < rCount; x++){
							rModel.removeRow(0);
						}
						
						selectedView = 1;
						timer = new ViewTimer(time, frmViewTracker);
						timer.setCurrentModel(lModel);
						lModel.addRow(new Object[]{switches, timer.getTime(0), "", ""});
						timer.resume();
						btnBegin.setText("End");
						begin = true;
						paused = false;
						txtName.enable(false);
					}
				}else{
					begin = false;
					paused = true;
					time.setText("00:00.000");
					btnBegin.setText("Start");
					if(selectedView == 1){
						lModel.setValueAt(timer.getTime(timer.getTimeMilli()), lModel.getRowCount()-1, 3);
					}else{
						rModel.setValueAt(timer.getTime(timer.getTimeMilli()), rModel.getRowCount()-1, 3);
					}
					selectedView = 0;
					timer.pause();
					txtName.enable(true);
					long lDur = totalDuration(lModel);
					long rDur = totalDuration(rModel);
					lModel.addRow(new Object[]{lModel.getRowCount(), timer.getTime(lDur), String.format("%2.2f", percentage(lDur+rDur, lDur)) + "%"});
					rModel.addRow(new Object[]{rModel.getRowCount(), timer.getTime(rDur), String.format("%2.2f", percentage(lDur+rDur, rDur)) + "%"});
					
					f = new FileHandler(txtName.getText(), "participants");
					f.writeToFile("==============================================Left===============================================\n");
					for(int x = 0; x < lModel.getRowCount()-1; x++){
						f.writeToFile(String.format("%s,%s,%s,%s\n", "" + lModel.getValueAt(x, 0), "" + lModel.getValueAt(x, 1), "" + lModel.getValueAt(x, 2), "" + lModel.getValueAt(x, 3)));
					}
					f.writeToFile(String.format("\n%s,%s,%s", lModel.getRowCount()-1, "" + timer.getTime(lDur), String.format("%2.2f", percentage(lDur+rDur, lDur)) + "%"));
					f.writeToFile("\n==============================================Left===============================================\n\n\n\n");
					f.writeToFile("==============================================Right===============================================\n");
					for(int x = 0; x < rModel.getRowCount()-1; x++){
						f.writeToFile(String.format("%s,%s,%s,%s\n", "" + rModel.getValueAt(x, 0), "" + rModel.getValueAt(x, 1), "" + rModel.getValueAt(x, 2), "" + rModel.getValueAt(x, 3)));
					}
					f.writeToFile(String.format("\n%s,%s,%s", rModel.getRowCount()-1, "" + timer.getTime(lDur), String.format("%2.2f", percentage(lDur+rDur, rDur)) + "%"));
					f.writeToFile("\n==============================================Right===============================================\n\n\n\n");
					f.close();
				}
			}
		});
		
		JButton btnPause = new JButton("Pause");
		this.btnPause = btnPause;
		btnPause.setBounds(109, 586, 89, 23);
		frmViewTracker.getContentPane().add(btnPause);
		btnPause.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		btnPause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(begin){
					if(!paused){
						timer.pause();
						btnPause.setText("Play");
						paused = true;
					}else{
						timer.resume();
						btnPause.setText("Pause");
						paused = false;
					}
				}
		    }
		});
		
		txtName = new JTextField();
		txtName.setBounds(73, 11, 567, 20);
		frmViewTracker.getContentPane().add(txtName);
		txtName.setColumns(10);
		
		JLabel lblFileName = DefaultComponentFactory.getInstance().createLabel("File Name: ");
		lblFileName.setBounds(10, 14, 73, 14);
		frmViewTracker.getContentPane().add(lblFileName);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBounds(0, 42, 650, 537);
		frmViewTracker.getContentPane().add(splitPane);
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.5);
		splitPane.setEnabled(false);
		
		JScrollPane lPanel = new JScrollPane();
		splitPane.setLeftComponent(lPanel);
		
		lTable = new JTable();
		lTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Switch", "Start", "Duration", "End"
			}
		));
		lPanel.setViewportView(lTable);
		
		lModel = (DefaultTableModel) lTable.getModel();
		
		JScrollPane rPanel = new JScrollPane();
		splitPane.setRightComponent(rPanel);
		
		rTable = new JTable();
		rTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Switch", "Start", "Duration", "End"
			}
		));
		rPanel.setViewportView(rTable);
		
		rModel = (DefaultTableModel) rTable.getModel();
		
		KeyListener key = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(begin){
					if(e.getKeyCode() == KeyEvent.VK_SPACE){
						if(!paused){
							btnPause.setText("Play");
							timer.pause();
							paused = true;
						}else{
							btnPause.setText("Pause");
							timer.resume();
							paused = false;
						}
					}
					
					if((e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_NUMPAD1) && selectedView != 1 && !paused){
						long startTime = timer.getTimeMilli();
						if(rModel.getRowCount() > 0){
							rModel.setValueAt(timer.getTime(startTime), rModel.getRowCount() - 1, 3);
						}
						switches++;
						lModel.addRow(new Object[]{switches, timer.getTime(startTime), "", ""});
						selectedView = 1;
						timer.setCurrentModel(lModel);
						timer.setStartTime(startTime);
					}
					
					if((e.getKeyCode() == KeyEvent.VK_2 || e.getKeyCode() == KeyEvent.VK_NUMPAD2) && selectedView != 2 && !paused){
						long startTime = timer.getTimeMilli();
						if(lModel.getRowCount() > 0){
							lModel.setValueAt(timer.getTime(startTime), lModel.getRowCount() - 1, 3);
						}
						switches++;
						rModel.addRow(new Object[]{switches, timer.getTime(startTime), "", ""});
						selectedView = 2;
						timer.setCurrentModel(rModel);
						timer.setStartTime(startTime);
					}
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				return;
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
				return;
			}
		};
		
		frmViewTracker.addKeyListener(key);
	}
	
	/**
	 * totalDuration - Counts the duration column in a model and sums them together
	 * @param m - model
	 * @return length
	 */
	public long totalDuration(DefaultTableModel m){
		long length = 0;
		for(int x = 0; x < m.getRowCount(); x++){
			length += timer.toLong((String)(m.getValueAt(x, 2)));
		}
		return length;
	}
	
	/**
	 * percentage - Calculates the percentage given a duration and total time.
	 * @param total - Total time
	 * @param duration - Length of time
	 * @return Duration percentage with respect to total
	 */
	public double percentage(long total, long duration){
		return ((duration * 1.0)/total * 100.0);
	}
}

/**
 * ViewTimer - Custom class which allows for pause and resume of a timer more easily
 * 
 * @author Ryan Lanciloti
 *
 */
class ViewTimer extends Timer{
	/**
	 * startTime - Time in milliseconds when the object was created
	 */
	private long startTime;
	
	/**
	 * currTime - The current time in milliseconds
	 */
	private long currTime;
	
	/**
	 * offSet - Offset time for the new timer generated in resume
	 */
	private long offSet;
	
	/**
	 * milliSeconds - Number of milliseconds
	 */
	private int milliSeconds;
	
	/**
	 * seconds - Number of seconds
	 */
	private int seconds;
	
	/**
	 * minutes - Number of minutes
	 */
	private int minutes;
	
	/**
	 * timer - Timer object that does schedule updates
	 */
	private Timer timer;
	
	/**
	 * time - Time label used in the main JFrame
	 */
	private JLabel time;
	
	/**
	 * main - Main JFrame container
	 */
	private JFrame main;
	
	/**
	 * model - Holds the currently active model
	 */
	private DefaultTableModel model;
	
	/**
	 * modelStart - Holds the start time for the model
	 */
	private long modelStart;
	
	/**
	 * ViewTimer - Constructor that creates a new timer object
	 * @param t - Time label to update
	 * @param m - Allows for the focus to be set on the main frame for key listening
	 */
	public ViewTimer(JLabel t, JFrame m){
		time = t;
		main = m;
	}
	
	/**
	 * resume - Creates a new timer to continue where the last left off
	 */
	public void resume(){
		timer = new Timer();
		startTime = System.currentTimeMillis();
		timer.schedule(new TimerTask(){
			public void run(){
				currTime = (System.currentTimeMillis() - startTime + offSet);
				milliSeconds = (int) (currTime%1000);
				seconds = (int) ((currTime/1000)%60);
				minutes = ((int) ((currTime/1000)/60));
				
				main.requestFocus();
				
				time.setText(String.format("%02d:%02d.%03d", minutes, seconds, milliSeconds));
				try{
					model.setValueAt(getTime(getTimeMilli() - modelStart), model.getRowCount() - 1, 2);
				}catch(ArrayIndexOutOfBoundsException e){
					System.out.println(e);
				}
			}
		}, 1, 1);
	}
	
	/**
	 * pause - Pauses the current timer
	 */
	public void pause(){
		offSet = currTime;
		timer.cancel();
	}
	
	/**
	 * getTimeMilli - Gets the current time in milliseconds
	 * @return time
	 */
	public long getTimeMilli(){
		return ((minutes*60*1000) + (seconds * 1000) + milliSeconds);
	}
	
	/**
	 * getTime - Convert from milliseconds to human readable time
	 * @param milli - Milliseconds to be converted
	 * @return time
	 */
	public String getTime(long milli){
		int mi = (int) (milli%1000);
		int s = (int) ((milli/1000)%60);
		int m = ((int) ((milli/1000)/60));
		return String.format("%02d:%02d.%03d", m, s, mi);
	}
	/**
	 * setCurrentModel - Sets the current data model for duration update
	 * @param m - model
	 */
	public void setCurrentModel(DefaultTableModel m){
		model = m;
	}
	
	/**
	 * toLong - Converts a given time string back to milliseconds
	 * @param time - Time string
	 * @return Time in milliseconds
	 */
	public long toLong(String time){
		Integer[] t = new Integer[3];
		t[0] = Integer.parseInt(time.split(":")[0]);
		t[1] = Integer.parseInt(time.split(":")[1].split(Pattern.quote("."))[0]);
		t[2] = Integer.parseInt(time.split(":")[1].split(Pattern.quote("."))[1]);
		return ((t[0] * 60 * 1000) + (t[1] * 1000) + t[2]);
	}
	
	/**
	 * setStartTime - Sets the start time for the model. Used in calculating duration.
	 * @param s - Start time
	 */
	public void setStartTime(long s){
		modelStart = s;
	}
}

/**
 * File Handler - Custom class which allows for outputting to a file
 * @author Ryan Lanciloti
 *
 */
class FileHandler{
	
	/**
	 * f - Output file
	 */
	private File f;
	
	/**
	 * dir - Parent directory
	 */
	private File dir;
	
	/**
	 * writer - File Writer that will output to a file
	 */
	private FileWriter writer;
	
	/**
	 * FileHandler - Constructer which creates a new file handler object
	 * @param n
	 * @param d
	 */
	public FileHandler(String n, String d){
		dir = new File(d);
		f = new File(d + "/" + n + ".txt");
		if(!dir.isDirectory()){
			dir.mkdir();
		}
		
		if(!f.isFile()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		
		try {
			writer = new FileWriter(f);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * writeToFile - Writes a string to a file
	 * @param s - String to write
	 */
	public void writeToFile(String s){
		try {
			writer.write(s);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * close - Closes the FileWriter object
	 */
	public void close(){
		try {
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}






















