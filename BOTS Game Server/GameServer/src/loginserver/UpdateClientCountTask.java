package loginserver;

import java.util.TimerTask;

public class UpdateClientCountTask extends TimerTask {

	protected int count;
	
	@Override
	public void run() {
		count = Main.loginServer.getClientCount();
		final String msg = count + " client" + ((count > 1) ? "s" : "");
		Main.gui.setClientCount(msg);
	}
	
}
