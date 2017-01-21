package channelserver;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.text.BadLocationException;

public class ChannelGameServerGUI extends JFrame {

	private static final long serialVersionUID = -5261847908536149302L;
	
	protected Timer timer;
	
	public ChannelGameServerGUI() {
		initComponents();
	}

	public void startUpdateTimer() {
		this.timer = new Timer();
		this.timer.schedule(new UpdateClientCountTask(0), 1000, 1000);
	}
	
	public void write(String msg) throws BadLocationException {
		this.debugTextArea.getDocument().insertString(0, msg + "\n", null);
	}

	public void setClientCount(String msg) {
		this.clientCountLabel.setText(msg);
	}
	
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		clientCountLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		debugTextArea = new javax.swing.JTextArea();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent we) {
				timer.cancel();
				Main.invokeShutdown();
				dispose();
			}
		});
		
		
		clientCountLabel.setText("0 clients");

		debugTextArea.setColumns(20);
		debugTextArea.setEditable(false);
		debugTextArea.setLineWrap(true);
		debugTextArea.setRows(5);
		debugTextArea.setPreferredSize(new java.awt.Dimension(200, 100));
		jScrollPane1.setViewportView(debugTextArea);

		final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup().addContainerGap()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620,
														Short.MAX_VALUE)
												.addComponent(clientCountLabel))
										.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(clientCountLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
						.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel clientCountLabel;
	private javax.swing.JTextArea debugTextArea;
	private javax.swing.JScrollPane jScrollPane1;
	// End of variables declaration//GEN-END:variables

}
