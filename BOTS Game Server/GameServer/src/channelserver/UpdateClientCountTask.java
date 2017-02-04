package channelserver;

import java.util.TimerTask;

import accountserver.Main;

public class UpdateClientCountTask extends TimerTask {

	protected int count;
	
	public UpdateClientCountTask(int count) {
		this.count = 0;
	}
	
	@Override
	public void run() {
//		count = Main.channelserver.getClientCount();
//		final String msg = count + " client" + ((count > 1) ? "s" : "");
//		Main.gui.setClientCount(msg);
	}
	
}
