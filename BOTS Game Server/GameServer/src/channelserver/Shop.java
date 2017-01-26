package channelserver;

import shared.Packet;

public class Shop {

	protected static final int head_buy = 0xEA;
	protected static final int head_sell = 0xEB;
	protected static final int head_buycoins = 0xEC;
	
	protected static final int nogigas = 0x41;
	protected static final int nocoins = 0x41;
	protected static final int noitem = 0x42;
	protected static final int noslot = 0x44;
	
	public static Packet buy(BotClass bot, int id) {
		final int price = ItemClass.getBuy(id);
		if (price == -1)
			return getErrorPacket(bot, noitem, head_buy);
		final int cgigas = bot.getGigas();
		if (cgigas < price)
			return getErrorPacket(bot, nogigas, head_buy);
		final int slot = slotAvaible(bot);
		if (slot == -1)
			return getErrorPacket(bot, noslot, head_buy);
		bot.setGigas(cgigas - price);
		bot.setInvent(id, slot);
		return bot.getInventPacket(head_buy);
	}
	
	public static Packet buycoin(BotClass bot, int id) {
		final int price = ItemClass.getBuyCoins(id);
		if (price == -1)
			return getErrorPacket(bot, noitem, head_buycoins);
		final int ccoins = bot.getCoins();
		if (ccoins < price)
			return getErrorPacket(bot, nocoins, head_buycoins);
		final int slot = slotAvaible(bot);
		if (slot == -1)
			return getErrorPacket(bot, noslot, head_buycoins);
		bot.setCoins(ccoins - price);
		bot.setInvent(id, slot);
		return bot.getInventPacket(head_buycoins);
	}
	
	public static Packet sell(BotClass bot, int id, int slot) {
		final int price = ItemClass.getSell(id);
		if (price == -1 || !itemAtSlot(bot, id, slot))
			return getErrorPacket(bot, noitem, 0xEB);
		bot.setGigas(bot.getGigas() + price);
		bot.setInvent(0, slot);
		return bot.getInventPacket(head_sell);
	}
	
	protected static Packet getErrorPacket(BotClass bot, int error, int head) {
		final Packet packet = new Packet();
		packet.addHeader((byte) head, (byte) 0x2E);
		packet.addPacketHead((byte) 0x00, (byte) error);
		for (int i = 0; i < 95; i++)
			packet.addByte((byte) 0xCC);
		return packet;
	}
	
	protected static int slotAvaible(BotClass bot) {
		final int[] inventory = bot.getInventAll();
		for (int i = 0; i < 10; i++)
			if (inventory[i] == 0)
				return i;
		return -1;
	}
	
	protected static boolean itemAtSlot(BotClass bot, int id, int slot) {
		if (bot.getInvent(slot) == id)
			return true;
		return false;
	}
	
}
