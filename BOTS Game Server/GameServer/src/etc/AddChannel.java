package etc;

import java.net.UnknownHostException;

import shared.SQLDatabase;

public class AddChannel {

	// Adds a user to the database...
	public static void main(String[] args) throws UnknownHostException {
		
		final int CHANNEL_ID = 2;
		final String CHANNEL_NAME = "Beta Test";
		final int CHANNEL_MIN_LEVEL = 10;
		final int CHANNEL_MAX_LEVEL = 255;
		final int PLAYERS = 0;
		final int STATUS = 1;
		
		final String query = "INSERT INTO `bout_channels` (id, name, minlevel, maxlevel, players, status) VALUES "
				+ "(" + CHANNEL_ID + ", \"" + CHANNEL_NAME + "\", " + CHANNEL_MIN_LEVEL + ", " + CHANNEL_MAX_LEVEL + ", "
				+ PLAYERS + ", " + STATUS + ")";

		SQLDatabase.start();
		SQLDatabase.doupdate(query);

	}

}
