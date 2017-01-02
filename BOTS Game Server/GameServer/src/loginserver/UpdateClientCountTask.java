/*
 * UpdateClientCountTask.java
 * This file checks how many clients are waiting for login information.
 */

package loginserver;

import java.util.TimerTask;

/**
 * UpdateClientCountTask updates the amount of connected clients.
 */
public class UpdateClientCountTask extends TimerTask {

	protected int count;

	/**
	 * Updates the label with the number of connected clients.
	 */
	@Override
	public void run() {
		count = Main.loginServer.getClientCount();
		final String msg = count + " client" + ((count > 1) ? "s" : "");
		Main.gui.setClientCount(msg);
	}
	
}
