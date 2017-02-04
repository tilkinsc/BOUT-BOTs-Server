package accountserver;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class ServerGui2 extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGui2 frame = new ServerGui2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ServerGui2() {
		setTitle("Bout Server Control Panel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmReserved = new JMenuItem("Reserved");
		mnFile.add(mntmReserved);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Close");
		mnFile.add(mntmExit);
		
		JMenu mnSql = new JMenu("SQL");
		menuBar.add(mnSql);
		
		JMenuItem mntmEditInformation = new JMenuItem("Edit Information");
		mnSql.add(mntmEditInformation);
		
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
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Account Server: 0", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JTextArea txtrTest = new JTextArea();
		scrollPane.setViewportView(txtrTest);
	}
	
}
