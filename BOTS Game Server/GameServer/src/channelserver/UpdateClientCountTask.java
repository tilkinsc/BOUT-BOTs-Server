package channelserver;

import java.util.TimerTask;

public class UpdateClientCountTask extends TimerTask {

	protected int count;

	@Override
	public void run() {
		count = Main.channelServer.getClientCount();
		final String msg = count + " client" + ((count != 1) ? "s" : "");
		Main.gui.setClientCount(msg);
	}

}
