package accountserver;

import java.util.TimerTask;

public class UpdateClientCountTask extends TimerTask {

	protected int count;
	
	public UpdateClientCountTask(int count) {
		this.count = 0;
	}
	
	@Override
	public void run() {
		count = Main.accountpath.getClientCount();
		final String msg = count + " client" + ((count > 1) ? "s" : "");
		Main.gui.setClientCount(msg);
	}
	
}
