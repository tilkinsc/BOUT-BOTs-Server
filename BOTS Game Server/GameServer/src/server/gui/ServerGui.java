package server.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import server.Main;
import shared.Logger;

import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class ServerGui extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	
	private Vector<Logger> loggers;
	private Vector<JPanel> tabs;
	
	private final Timer timer = new Timer();
	
	public void startUpdateTimer() {
		this.timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				final int count = Main.accountpath.getClientCount();
				tabbedPane.setTitleAt(0, "Account Server: " + count);
			}
		}, 500, 500);
	}
	
	public static void write(JTextArea textarea, String msg) {
		textarea.setText(textarea.getText() + msg + '\n');
		textarea.setCaretPosition(textarea.getDocument().getLength());
	}
	
	public static PrintStream createGuiSessionStream(JTextArea textarea) {
		final OutputStream os = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		};
		return new PrintStream(os) {
			@Override
			public void println(String x) {
				try {
					write(textarea, x);
				} catch (Exception e) {
				}
			}
		};
	}
	
	public int addTab(String default_name) {
		JPanel panel = new JPanel();
		tabbedPane.addTab(default_name, null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JTextArea txtrTest2 = new JTextArea();
		txtrTest2.setAutoscrolls(true);
		txtrTest2.setEditable(false);
		scrollPane.setViewportView(txtrTest2);
		tabs.add(panel);
		
		Logger logger = new Logger(new PrintStream[] {System.out, createGuiSessionStream(txtrTest2)});
		loggers.add(logger);
		
		return loggers.size() - 1;
	}
	
	public ServerGui() {
		loggers = new Vector<Logger>();
		tabs = new Vector<JPanel>();
		setTitle("Bout Server Control Panel");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				timer.cancel();
				Main.invokeShutdown();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmReserved = new JMenuItem("Reserved");
		mnFile.add(mntmReserved);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Close");
		mntmExit.addMouseListener(new Menu_Close());
		mnFile.add(mntmExit);
		
		JMenu mnSql = new JMenu("Settings");
		menuBar.add(mnSql);
		
		JMenuItem mntmEditInformation = new JMenuItem("Edit MySQL Config");
		mntmEditInformation.addMouseListener(new Menu_EditMySQLConfig());
		mnSql.add(mntmEditInformation);
		
		JMenuItem mntmEditPreferences = new JMenuItem("Edit Preferences");
		mnSql.add(mntmEditPreferences);
		
		JMenu mnChannels = new JMenu("Channels");
		menuBar.add(mnChannels);
		
		JMenuItem mntmSync = new JMenuItem("Sync");
		mnChannels.add(mntmSync);
		
		JSeparator separator_2 = new JSeparator();
		mnChannels.add(separator_2);
		
		JMenuItem mntmAdd = new JMenuItem("Add");
		mnChannels.add(mntmAdd);
		
		JMenuItem mntmRemove = new JMenuItem("Remove");
		mnChannels.add(mntmRemove);
		
		JSeparator separator_1 = new JSeparator();
		mnChannels.add(separator_1);
		
		JMenuItem mntmPushUpdates = new JMenuItem("Push Updates");
		mnChannels.add(mntmPushUpdates);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
	}
	
	public Logger getLogger(int id) {
		return this.loggers.get(id);
	}
	
}
