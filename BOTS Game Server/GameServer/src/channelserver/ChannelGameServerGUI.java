package channelserver;

import java.util.Timer;

public class ChannelGameServerGUI extends javax.swing.JFrame {

	private static final long serialVersionUID = -5331344483523207837L;

	protected ChannelServer server;
	protected Timer timer;

	public ChannelGameServerGUI(ChannelServer server) {
		initComponents();
		this.server = server;
		this.timer = new Timer();
		this.timer.schedule(new UpdateClientCountTask(), 1000, 1000);
	}

	public void write(String msg) {
		try {
			this.debugTextArea.getDocument().insertString(0, msg + "\n", null);
		} catch (Exception e) {
		}
	}

	public void setClientCount(String msg) {
		try {
			this.clientCountLabel.setText(msg);
		} catch (Exception e) {
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		clientCountLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		debugTextArea = new javax.swing.JTextArea();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
