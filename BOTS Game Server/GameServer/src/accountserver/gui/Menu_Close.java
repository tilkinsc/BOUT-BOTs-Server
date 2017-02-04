package accountserver.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import accountserver.Main;

public class Menu_Close extends MouseAdapter {

	@Override
	public void mouseReleased(MouseEvent arg0) {
		Main.invokeShutdown();
	}
	
}
