package server.gameserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.ResultSet;

import server.Main;
import shared.Packet;
import shared.SQLDatabase;
import shared.Util;

public class ChannelServerConnection extends Thread {

	protected Socket socket;
	protected BufferedReader socketIn;
	protected PrintWriter socketOut;
	protected ChannelServer server;
	protected Lobby lobby;
	protected String account;
	protected int bottype;
	protected String ip;
	protected String charname = "";
	protected boolean firstlog = true;
	protected BotClass bot;
	protected Shop shop;
	
	public ChannelServerConnection(Socket socket, ChannelServer server, Lobby _lobby) {
		this.socket = socket;
		this.server = server;
		this.ip = socket.getInetAddress().getHostAddress();
		this.lobby = _lobby;
		Main.logger.log("ChannelServerConnection", "" + socket.getLocalSocketAddress());
	}
	
	public void checkAccount() {
		try {
			final String ip = socket.getInetAddress().getHostAddress();
			final ResultSet rs = SQLDatabase
					.doquery("SELECT username FROM bout_users WHERE current_ip='" + ip + "' LIMIT 1");
			if (rs.next())
				this.account = rs.getString("username");
			if (this.account != null && isbanned(this.account) == 0) {
				// Main.sql.doupdate("UPDATE bout_users SET current_ip='' WHERE
				// username='"+this.account+"'");
			} else
				account = "a";
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}
	
	public SocketAddress getRemoteAddress() {
		return this.socket.getRemoteSocketAddress();
	}
	
	protected int isbanned(String account) {
		try {
			final ResultSet rs = SQLDatabase
					.doquery("SELECT banned FROM bout_users WHERE username='" + account + "' LIMIT 1");
			if (rs.next())
				return rs.getInt("banned");
		} catch (Exception e) {
		}
		return 0;
	}
	
	protected String removeheader(String packet) {
		return packet.substring(4);
	}
	
	protected String removenullbyte(String thestring) {
		final byte[] stringbyte = thestring.getBytes();
		int a = 0;
		while (stringbyte[a] != 0x00)
			a++;
		return thestring.substring(0, a);
	}
	
	protected void prasecmd(int cmd, String packet) {
		try {
			final Packet pack = new Packet();
			final String[] packanwser = new String[2];
			switch (cmd) {
			case 0xF82A:
				// debug("parse f8");
				final byte[] spacketb = { 0x01, 0x00, 0x01, 0x00 };
				socketOut.write(new String(ChannelServer.CLIENT_NUMBER_HEADER));
				socketOut.flush();
				// debug("send cnumberhead");
				socketOut.write(new String(spacketb));
				socketOut.flush();
				// debug("send cnumberpacket");
				break;
			case 0xF92A:
				Main.logger.log("ChannelServerConnection", "parsing bots");
				pack.addHead((byte) 0x28, (byte) 0x27);
				pack.addBodyInt(1, 2, false);
				send(pack);
				pack.clean();
				if (bot.checkbot()) {
					bot.loadchar();
					charname = bot.getName();
					bottype = bot.getBot();
					socketOut.write(new String(ChannelServer.CHARACTER_INFORMATION_HEADER));
					socketOut.flush();
					socketOut.write(bot.getpacketcinfo());
					socketOut.flush();
					final byte[] bytearr = { (byte) 0x4E, (byte) 0x95, (byte) 0xDD, (byte) 0x29, (byte) 0xCE,
							(byte) 0x3A, (byte) 0x55, (byte) 0xDB, (byte) 0x20, (byte) 0xB6, (byte) 0xAD, (byte) 0x97,
							(byte) 0xA6, (byte) 0x5C, (byte) 0xC0, (byte) 0x1C };
					pack.addHead((byte) 0x53, (byte) 0x2F);
					pack.addBodyArray(bytearr);
					send(pack);
					pack.clean();
					pack.addHead((byte) 0x27, (byte) 0x27);
					pack.addBodyInt(1, 2, false);
					pack.addBodyString(charname);
					pack.addByte((byte) 0x00);
					pack.addByte((byte) 0xCC, (byte) 0xCC, (byte) 0x01, (byte) 0x01);
					send(pack);
					pack.clean();
					pack.addHead((byte) 0x4F, (byte) 0x2F);
					pack.addBodyInt(1, 2, false);
					pack.addByte((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
					send(pack);
					pack.clean();
				} else {
					socketOut.write(new String(ChannelServer.CHARACTER_INFORMATION_HEADER));
					socketOut.flush();
					// debug("send cinfohead");
					final byte[] cinfopackbyte = { (byte) 0x00, (byte) 0x35 };
					String cinfopack = new String(cinfopackbyte);
					cinfopack += ChannelServer.longnullbyte;
					socketOut.write(cinfopack);
					socketOut.flush();
				}
				// debug("send cinfopack");
				break;
			case 0xFA2A: {
				// debug("parse fa");
				// Packet pack = new Packet();
				pack.setBody(Packet.removeHeader(4, packet));
				final int client_num = pack.getBodyInt(2);
				final int bottype = pack.getBodyInt(2);
				final int unknown = pack.getBodyInt(2);
				final String accountname = pack.getBodyString(1, 23, false);
				final String charname = pack.getBodyString(0, 15, false);
				pack.clean();
				
				if (this.account.equals("a")) {
					final byte[] loginhack = { (byte) 0x00, (byte) 0x32 };
					this.socket.getOutputStream().write(ChannelServer.BOT_CREATION_HEADER);
					this.socket.getOutputStream().flush();
					this.socket.getOutputStream().write(loginhack);
					this.socket.getOutputStream().flush();
					this.finalize();
				} else if (!checkexist(charname, accountname)) {
					if (charname.matches("[a-zA-Z0-9.~_!\\x2d]+")) {
						bot.createbot(accountname, charname, bottype);
						socketOut.write(new String(ChannelServer.BOT_CREATION_HEADER));
						socketOut.flush();
						socketOut.write(new String(ChannelServer.CREATE_BOT_CREATED));
						socketOut.flush();
					} else {
						socketOut.write(new String(ChannelServer.BOT_CREATION_HEADER));
						socketOut.flush();
						socketOut.write(new String(ChannelServer.CREATE_BOT_USERNAME_ERROR));
						socketOut.flush();
						this.finalize();
					}
				} else {
					socketOut.write(new String(ChannelServer.BOT_CREATION_HEADER));
					socketOut.flush();
					socketOut.write(new String(ChannelServer.CREATE_BOT_USERNAME_TAKEN));
					socketOut.flush();
					this.finalize();
				}
				break;
			}
			case 0x742B: {
				// Packet packs = new Packet();
				if (firstlog) {
					lobby.adduser(this.charname, this.bottype, socketOut, socket);
					firstlog = false;
				}
				// packs = lobby.getlobbypacket();
				send(lobby.getlobbypacket());
				// lobby.g
				send(lobby.getroompacket(1, 0));
				sendChatMsg("Welcome Secured's Test Server!", 4);
				sendChatMsg("Have fun :)", 4);
				
				this.socketOut.write(new String(ChannelServer.OK_HEADER));
				this.socketOut.flush();
				this.socketOut.write(new String(ChannelServer.OK_PACKET));
				this.socketOut.flush();
				break;
			}
			case 0x1A27:
				pack.setBody(Packet.removeHeader(4, packet));
				String chatpack = pack.getIsoBody();
				final int a = Util.compareChat(chatpack, this.charname, false, isGM());
				if (a == -1)
					lobby.kickPlayer(this.charname,
							"Player " + this.charname + " has been kick for wrong chatname(hacking)");
				else {
					chatpack = chatpack.substring((a + this.charname.length() + 3));
					chatpack = chatpack.substring(0, (chatpack.length() - 1));
					Main.logger.log("ChannelServerConnection", chatpack);
					if (chatpack.startsWith("@")) {
						final String command = chatpack.substring(1, chatpack.length());
						parsechatcmd(command);
					} else
						lobby.writeMessage(pack.getIsoBody(), this.charname, isGM());
				}
				pack.clean();
				break;
			case 0x442B:
				lobby.whisper(packet, this.charname);
				break;
			case 0x222B:
				sendBye(1);
				this.finalize();
				break;
			case 0xFB2A:
				final int anwser = bot.deleteBot(this.charname, this.account);
				sendBye(anwser);
				break;
			case 0x512B:
				pack.addHead((byte) 0x37, (byte) 0x2F);
				pack.addIsoBody((byte) 0x01, (byte) 0x00);
				pack.addBodyInt(bot.getCoins(), 4, false);
				send(pack);
				send(bot.getInventPacket(0xEB));
				break;
			case 0x022B: {
				pack.setBody(packet);
				pack.getBodyString(0, 42, true);
				final int itemid = pack.getBodyInt(4);
				send(shop.buy(itemid));
				// Main.sql.doupdate("UPDATE `bout_items` SET `buyable` = 1
				// WHERE id="+itemid);
				break;
			}
			case 0x032B: {
				pack.setBody(packet);
				pack.getBodyString(0, 42, true);
				final int slotnum = pack.getBodyInt(2);
				pack.getBodyInt(2);
				final int itemid = pack.getBodyInt(4);
				send(shop.sell(itemid, slotnum));
				break;
			}
			case 0x042B: {
				pack.setBody(packet);
				pack.getBodyString(0, 42, true);
				pack.getBodyInt(2);
				final int itemid = pack.getBodyInt(4);
				
				send(shop.buycoin(itemid));
				// Main.sql.doupdate("UPDATE `bout_items` SET `buyable` = 1
				// WHERE id="+itemid);
				break;
			}
			case 0xFC2A: {
				pack.setBody(packet);
				pack.getBodyString(0, 29, false);
				final int slot = pack.getBodyInt(2);
				pack.getBodyInt(2);
				send(bot.equip(slot, 1));
				break;
			}
			case 0xFD2A: {
				pack.setBody(packet);
				pack.getBodyString(0, 29, false);
				final int slot = pack.getBodyInt(2);
				pack.getBodyInt(2);
				send(bot.deequip(slot, 1));
				break;
			}
			case 0x322B: {
				pack.setBody(packet);
				pack.getBodyString(0, 6, false);
				final int slot = pack.getBodyInt(2);
				pack.clean();
				send(bot.equip(slot, 2));
				break;
			}
			case 0x332B: {
				pack.setBody(packet);
				pack.getBodyString(0, 6, false);
				final int slot = pack.getBodyInt(2);
				pack.clean();
				send(bot.deequip(slot, 2));
				break;
			}
			case 0x342B: {
				pack.setBody(packet);
				pack.getBodyString(0, 6, false);
				final int slot = pack.getBodyInt(2);
				pack.clean();
				send(bot.equip(slot, 3));
				break;
			}
			case 0x352B: {
				pack.setBody(packet);
				pack.getBodyString(0, 6, false);
				final int slot = pack.getBodyInt(2);
				pack.clean();
				send(bot.deequip(slot, 3));
				break;
			}
			case 0x412B: {
				pack.setBody(Packet.removeHeader(4, packet));
				send(bot.getEquipByName(pack.getIsoBody()));
				break;
			}
			case 0x0A2B: {
				pack.setBody(Packet.removeHeader(4, packet));
				pack.getBodyInt(2);
				final int page = pack.getBodyInt(2);
				final int mode = pack.getBodyInt(2);
				send(lobby.getroompacket(mode, page));
				pack.clean();
				break;
			}
			case 0x092B: {
				pack.setBody(Packet.removeHeader(4, packet));
				final String cname = pack.getBodyString(0, 27, false);
				final String cpass = pack.getBodyString(0, 10, false);
				pack.getBodyInt(2);
				int roommode = pack.getBodyInt(1);
				switch (roommode) {
				case 2:
					roommode = 0;
					break;
				case 0:
					roommode = 1;
					break;
				case 3:
					roommode = 2;
					break;
				}
				final int num = lobby.addroom(roommode, cname, cpass, bot.getName(), bot.getLevel(),
						socket.getInetAddress().getHostAddress(), this.socketOut, bot);
				// lobby.setStatus(bot.getName(),0);
				final int[] room = { roommode, num };
				bot.setRoom(room);
				break;
			}
			case 0x652B: {
				final int[] room = lobby.haveRoom(bot.getName());
				if (room[0] == -1)
					lobby.kickPlayer(this.charname, "Player " + this.charname
							+ " has been kick for try to change map of not owning room(hacking)");
				else {
					pack.setBody(Packet.removeHeader(4, packet));
					pack.getBodyInt(2);
					lobby.setRoomMap(room, pack.getBodyInt(2));
					pack.clean();
				}
				break;
			}
			case 0x062B: {
				pack.setBody(Packet.removeHeader(4, packet));
				final int roomnum = pack.getBodyInt(1) - 89;
				int roommode = pack.getBodyInt(1);
				switch (roommode) {
				case 2:
					roommode = 0;
					break;
				case 0:
					roommode = 1;
					break;
				case 3:
					roommode = 2;
					break;
				}
				final String rname = pack.getBodyString(0, 27, false);
				final String rpass = pack.getBodyString(0, 10, false);
				final int[] room = { roommode, roomnum };
				final Packet npacket = lobby.addRoomPlayer(room, rpass, ip, socketOut, bot);
				if (npacket.getBodyLen() == 1715) {
					bot.setRoom(room);
					send(npacket);
					// lobby.setStatus(bot.getName(),0);
					ResultSet rs;
					int port = 0;
					while (port == 0) {
						rs = SQLDatabase.doquery("SELECT `port` FROM `rooms` WHERE `ip`='" + ip + "'");
						rs.next();
						port = rs.getInt("port");
					}
					SQLDatabase.doupdate("DELETE FROM `rooms` WHERE `ip` = '" + ip + "' and `port` = '" + port + "'");
					lobby.isConnected(room, bot.getName(), port);
				} else
					send(npacket);
				break;
			}
			case 0x422B: {
				final int[] room = bot.getRoom();
				if (room[0] == -1) {
					lobby.kickPlayer(this.charname, "Player " + this.charname
							+ " has been kick for try to exit a room while he isn't in a room(hacking)");
					break;
				}
				lobby.removeRoomPlayer(room, bot.getName());
				break;
			}
			case 0x0B2B: {
				final int[] room = lobby.haveRoom(bot.getName());
				if (room[0] == -1)
					lobby.kickPlayer(this.charname,
							"Player " + this.charname + " has been kick for try to start not owning room(hacking)");
				pack.setBody(Packet.removeHeader(4, packet));
				pack.getBodyInt(2);
				final int map = pack.getBodyInt(2);
				final int unknown = pack.getBodyInt(2);
				lobby.startRoom(room, map);
				pack.clean();
				break;
			}
			case 0x3E2B: {
				if (bot.getRoom()[0] != -1)
					lobby.readyToPlay(bot.getRoom(), bot.getName());
				break;
			}
			case 0x392B: // set ready in room
			{
				final int slot = lobby.getSlot(bot.getRoom(), bot.getName());
				if (slot == -1)
					lobby.kickPlayer(this.charname, "Player " + this.charname
							+ " has been kick for try to be ready and not being in a room(hacking)");
				lobby.setRoomStatus(bot.getRoom(), slot);
				break;
			}
			case 0x6F2B: // respawn thing
			{
				pack.addHead((byte) 0x54, (byte) 0x2F);
				pack.addByte((byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00);
				send(pack);
				break;
			}
			case 0x3A2B: // handle death of character
			{
				System.out.println("Entered 0x3A2B");
				final int[] room = lobby.haveRoom(bot.getName());
				if (room[0] == -1) {
					System.out.println("Room is -1");
					break;
				}
				pack.setBody(Packet.removeHeader(4, packet));
				System.out.println("0: " + pack.getBodyInt(0) + ", 1: " + pack.getBodyInt(1) + ", 2: " + pack.getBodyInt(2)
						+ ", 3: " + pack.getBodyInt(3) + ", 4: " + pack.getBodyInt(4));
				final int who = pack.getBodyInt(2); // I wanna try with TWO computers
				final int monsternum = pack.getBodyInt(2);
				final int monstertyp = 2;
				lobby.roomMonsterDead(room, monstertyp, monsternum, who);
				break;
			}
			case 0x3C2B: // Use revive?
			{
				if (bot.getRoom()[0] == -1)
					break;
				pack.setBody(Packet.removeHeader(4, packet));
				pack.getBodyInt(2);
				final int typ = pack.getBodyInt(1);
				final int num = pack.getBodyInt(1);
				lobby.useItem(bot.getRoom(), bot.getName(), typ, num);
				break;
			}
			case 0x3f2b: // Heartbeat?
			{
				System.out.println("Entered special packet");
				pack.setBody(packet);
				System.out.println("0: " + pack.getBodyInt(0) + ", 1: " + pack.getBodyInt(1) + ", 2: " + pack.getBodyInt(2)
						+ ", 3: " + pack.getBodyInt(3) + ", 4: " + pack.getBodyInt(4));
				break;
			}
			default:
				Main.logger.log("ChannelServerConnection", "parse unknown packet (" + cmd + ", " + Integer.toHexString(cmd) + ")");
			}
		} catch (Exception e) {
			Main.logger.log("ChannelServerConnection", e.getMessage());
		}
	}
	
	protected void parsechatcmd(String cmd) {
		Main.logger.log("ChannelServerConnection", cmd);
		String rcmd;
		final Packet packet = new Packet();
		final byte[] bytecmd = cmd.getBytes();
		int i = 0;
		while (bytecmd[i] != 0x20 && i < cmd.length() - 1)
			i++;
		
		if (i == cmd.length() - 1)
			rcmd = cmd.substring(0, cmd.length());
		else
			rcmd = cmd.substring(0, i);
		Main.logger.log("ChannelServerConnection", "rcmd : -" + rcmd + "-");
		
		if (rcmd.equals("kick") && isGM()) {
			final String chaname = cmd.substring(i + 1);
			if (lobby.kickPlayer(chaname, "Player " + chaname + " has been kicked by " + this.charname) == 0)
				sendChatMsg("Player not found", 2);
		} else if (rcmd.equals("add") && isGM()) {
			final int anz = Integer.parseInt(cmd.substring(i + 1));
			lobby.createdummy(anz);
		} else if (rcmd.equals("addroom") && isGM()) {
			final int anz = Integer.parseInt(cmd.substring(i + 1));
			lobby.createdummy(anz);
		} else if (rcmd.equals("coins")) {
			final int anz = Integer.parseInt(cmd.substring(i + 1));
			bot.setCoins((bot.getCoins() + anz));
			sendChatMsg("Current coins : " + bot.getCoins(), 2);
		} else if (rcmd.equals("gigas")) {
			final int anz = Integer.parseInt(cmd.substring(i + 1));
			bot.setGigas((bot.getGigas() + anz));
			sendChatMsg("Current gigas : " + bot.getGigas(), 2);
		} else if (rcmd.equals("delinvent")) {
			final int part = Integer.parseInt(cmd.substring(i + 1));
			if (part == 0) {
				final int[] items = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				bot.setInventAll(items);
				sendChatMsg("Invetory deleted", 2);
			} else {
				final String old = ItemClass.getItemName(bot.getInvent(part - 1));
				bot.setInvent(0, part - 1);
				sendChatMsg("Inventory-item " + old + " deleted.", 2);
			}
		} else if (rcmd.equals("item") && isGM()) {
			int id = 0;
			if (cmd.substring(i + 1).matches("\\d*"))
				id = Integer.parseInt(cmd.substring(i + 1));
			else
				id = ItemClass.getItemId(cmd.substring(i + 1));
			if (id != 0) {
				final int slot = shop.slotAvaible();
				if (slot != -1) {
					bot.setInvent(id, slot);
					sendChatMsg("Item " + ItemClass.getItemName(id) + " added at slot " + slot, 2);
				} else
					sendChatMsg("Your inventory is full!", 2);
			} else
				sendChatMsg("Item not found!", 2);
		} else if (rcmd.equals("itemname")) {
			final int id = Integer.parseInt(cmd.substring(i + 1));
			final String name = ItemClass.getItemName(id);
			if (name != null) {
				sendChatMsg("Found :", 2);
				sendChatMsg("- " + name, 2);
			} else
				sendChatMsg("Item not found!", 2);
		} else if (rcmd.equals("itemid")) {
			final String name = cmd.substring(i + 1);
			final String id[] = ItemClass.getItemIdLike(name);
			if (id != null) {
				final int found = Integer.parseInt(id[5]);
				int display;
				if (found > 5)
					display = 5;
				else
					display = found;
				if (display == 1)
					sendChatMsg("Found " + found + " item, displaying " + display + " item.", 2);
				else
					sendChatMsg("Found " + found + " items, displaying " + display + " items.", 2);
				for (int i2 = 0; i2 < display; i2++)
					sendChatMsg("- " + id[i2], 2);
			} else
				sendChatMsg("No item found!", 2);
		} else if (rcmd.equals("help")) {
			sendChatMsg("@dummy <amount>          - need gm rights", 2);
			sendChatMsg("@kick <charactername     - need gm rights>", 2);
			sendChatMsg("@coins <amount>", 2);
			sendChatMsg("@gigas <amount>", 2);
			sendChatMsg("@delinvent <part/0 for all>", 2);
			sendChatMsg("@itemname <itemid>", 2);
			sendChatMsg("@itemid <itemname>", 2);
			sendChatMsg("@item <itemid/itemname>  - need gm rights", 2);
		} else if (rcmd.equals("refresh")) {
			send(lobby.getlobbypacket());
			sendChatMsg("Userlist refreshed", 2);
		}
	}
	
	protected void send(Packet pack) {
		final String[] packet = new String[2];
		packet[0] = pack.combineIsoPacket(2);
		packet[1] = pack.getIsoBody();
		this.socketOut.write(packet[0]);
		this.socketOut.flush();
		this.socketOut.write(packet[1]);
		this.socketOut.flush();
	}
	
	protected void sendBye(int whatpack) {
		final Packet pack = new Packet();
		final String[] packandhead = new String[2];
		switch (whatpack) {
		case 1:
			pack.addHead((byte) 0x0A, (byte) 0x2F);
			pack.addBodyInt(1, 2, false);
			packandhead[0] = pack.combineIsoPacket(2);
			packandhead[1] = pack.getIsoBody();
			this.socketOut.write(packandhead[0]);
			this.socketOut.flush();
			this.socketOut.write(packandhead[1]);
			this.socketOut.flush();
			break;
		case 2:
			pack.addHead((byte) 0xE3, (byte) 0x2E);
			pack.addBodyInt(1, 2, false);
			packandhead[0] = pack.combineIsoPacket(2);
			packandhead[1] = pack.getIsoBody();
			this.socketOut.write(packandhead[0]);
			this.socketOut.flush();
			this.socketOut.write(packandhead[1]);
			this.socketOut.flush();
			break;
		}
	}
	
	protected boolean isGM() {
		return (this.account.equals("auron") || this.account.equals("auron3") || this.account.equals("kevinwagner") || this.account.equals("ydroque"));
	}
	
	protected void sendChatMsg(String msg, int color) {
		final Packet pack = new Packet();
		
		pack.addHead((byte) 0x1A, (byte) 0x27);
		
		pack.addByte((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
		pack.addBodyInt(color, 2, false);
		pack.addBodyString(msg);
		pack.addByte((byte) 0x00);
		
		final String[] packandhead = new String[2];
		
		packandhead[0] = pack.combineIsoPacket(2);
		packandhead[1] = pack.getIsoBody();
		
		this.socketOut.write(packandhead[0]);
		this.socketOut.flush();
		this.socketOut.write(packandhead[1]);
		this.socketOut.flush();
	}
	
	protected boolean checkexist(String charname, String account) {
		try {
			ResultSet rs = SQLDatabase
					.doquery("SELECT username FROM bout_characters WHERE name='" + charname + "' LIMIT 1");
			if (rs.next()) {
				final String username = rs.getString("username");
				Main.logger.log("ChannelServerConnection", username);
				return true;
			}
			rs = SQLDatabase.doquery("SELECT name FROM bout_characters WHERE username='" + account + "' LIMIT 1");
			if (rs.next()) {
				final String name = rs.getString("name");
				Main.logger.log("ChannelServerConnection", name);
				return true;
			}
			rs = SQLDatabase.doquery("SELECT * FROM bout_users WHERE username='" + account + "' LIMIT 1");
			if (rs.next())
				return false;
			else
				return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	protected String read() {
		final StringBuffer buffer = new StringBuffer();
		int codePoint;
		try {
			// debug("start read");
			for (int i = 0; i < 4; i++) {
				codePoint = this.socketIn.read();
				if (codePoint == 0) {
					final String nulls = new String(ChannelServer.NULLBYTE);
					buffer.append(nulls);
				} else if (Character.isValidCodePoint(codePoint))
					buffer.appendCodePoint(codePoint);
			}
			
			final int plen = Util.bytetoint(buffer.toString().substring(2), 2);
			if (plen > 1)
				for (int i = 0; i < plen; i++) {
					codePoint = this.socketIn.read();
					if (codePoint == 0) {
						final String nulls = new String(ChannelServer.NULLBYTE);
						buffer.append(nulls);
					} else if (Character.isValidCodePoint(codePoint))
						buffer.appendCodePoint(codePoint);
				}
			// debug("end read");
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
			this.server.removeClient(this.getRemoteAddress());
			return null;
		}
		return buffer.toString();
	}
	
	@Override
	public void run() {
		try {
			this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "ISO8859-1"));
			this.socketOut = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), "ISO8859-1"));
			checkAccount();
			bot = new BotClass(this.account);
			shop = new Shop(bot);
			String packet;
			while ((packet = read()) != null)
				// debug("main");
				prasecmd(Util.getcmd(packet), packet);
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
		// debug("bye");
		this.finalize();
	}
	
	@Override
	protected void finalize() {
		try {
			if (!this.charname.equals("")) {
				final int[] room = bot.getRoom();
				if (room[0] != -1)
					lobby.removeRoomPlayer(room, bot.getName());
				lobby.removeuser(charname);
				Main.logger.log("ChannelServerConnection", "remove charname " + charname);
				this.charname = "";
			}
			this.server.removeClient(this.getRemoteAddress());
			this.socketIn.close();
			this.socketOut.close();
			this.socket.close();
		} catch (Exception e) {
			Main.logger.log("Exception", e.getMessage());
		}
	}
	
}
