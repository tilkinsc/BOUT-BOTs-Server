package server.gameserver;

import shared.Packet;

public class Shop {

	BotClass bot;
	
	protected final int head_buy = 0xEA;
	protected final int head_sell = 0xEB;
	protected final int head_buycoins = 0xEC;
	
	protected final int nogigas = 0x41;
	protected final int nocoins = 0x41;
	protected final int noitem = 0x42;
	protected final int noslot = 0x44;
	
	public Shop(BotClass pbot) {
		this.bot = pbot;
	}
	
	public Packet buy(int id) {
		final int price = ItemClass.getBuy(id);
		if (price == -1)
			return getErrorPacket(noitem, head_buy);
		final int cgigas = bot.getGigas();
		if (cgigas < price)
			return getErrorPacket(nogigas, head_buy);
		final int slot = slotAvaible();
		if (slot == -1)
			return getErrorPacket(noslot, head_buy);
		bot.setGigas(cgigas - price);
		bot.setInvent(id, slot);
		return bot.getInventPacket(head_buy);
	}
	
	public Packet buycoin(int id) {
		final int price = ItemClass.getBuyCoins(id);
		if (price == -1)
			return getErrorPacket(noitem, head_buycoins);
		final int ccoins = bot.getCoins();
		if (ccoins < price)
			return getErrorPacket(nocoins, head_buycoins);
		final int slot = slotAvaible();
		if (slot == -1)
			return getErrorPacket(noslot, head_buycoins);
		bot.setCoins(ccoins - price);
		bot.setInvent(id, slot);
		return bot.getInventPacket(head_buycoins);
	}
	
	public Packet sell(int id, int slot) {
		final int price = ItemClass.getSell(id);
		if (price == -1 || !itemAtSlot(id, slot))
			return getErrorPacket(noitem, 0xEB);
		bot.setGigas(bot.getGigas() + price);
		bot.setInvent(0, slot);
		return bot.getInventPacket(head_sell);
	}
	
	protected Packet getErrorPacket(int error, int head) {
		final Packet packet = new Packet();
		packet.addHead((byte) head, (byte) 0x2E);
		packet.addIsoBody((byte) 0x00, (byte) error);
		for (int i = 0; i < 95; i++)
			packet.addByte((byte) 0xCC);
		return packet;
	}
	
	protected int slotAvaible() {
		final int[] inventory = bot.getInventAll();
		for (int i = 0; i < 10; i++)
			if (inventory[i] == 0)
				return i;
		return -1;
	}
	
	protected boolean itemAtSlot(int id, int slot) {
		if (bot.getInvent(slot) == id)
			return true;
		return false;
	}
	
}
