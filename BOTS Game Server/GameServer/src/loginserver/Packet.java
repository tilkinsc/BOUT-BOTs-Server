package loginserver;

import shared.Util;

public class Packet {

	private String header = "";
	private String packet = "";
	
	private boolean calced = false;
	
	public int getLen() {
		return this.packet.length();
	}
	
	public void setPacket(String pack) {
		this.packet = pack;
	}
	
	public void addHeader(byte b1, byte b2) {
		calced = false;
		final byte[] headbyte = { b1, b2 };
		final String head = new String(headbyte);
		this.header = head;
	}
	
	public void removeHeader() {
		this.packet = this.packet.substring(4);
	}
	
	protected void calcHeader() {
		this.header += Util.getbyteiso(this.packet.length(), 2);
		calced = true;
	}
	
	public String getHeader() {
		if (!calced)
			this.calcHeader();
		try {
			return Util.isoString(this.header.getBytes("ISO8859-1"));
		} catch (Exception e) {
		}
		return null;
	}
	
	public void addPacketHead(final byte... b) {
		try {
			this.packet += Util.isoString(b);
		} catch (Exception e) {
		}
	}
	
	public void addString(String string) {
		this.packet += string;
	}
	
	public String getString(int start, int end, boolean nulled) {
		final String thestring = this.packet.substring(start, end);
		this.packet = this.packet.substring(end);
		if (nulled) return thestring;
		else return Util.removenullbyte(thestring);
	}
	
	public void addInt(int var, int num, boolean reverse) {
		try {
			if (num == 2) {
				final int b1 = var & 0xff;
				final int b2 = (var >> 8) & 0xff;
				
				if (!reverse) {
					final byte[] varbyte = { (byte) b1, (byte) b2 };
					this.packet += Util.isoString(varbyte);
				} else {
					final byte[] varbyte = { (byte) b2, (byte) b1 };
					this.packet += Util.isoString(varbyte);
				}
			} else if (num == 4) {
				final int b1 = var & 0xff;
				final int b2 = (var >> 8) & 0xff;
				final int b3 = (var >> 16) & 0xff;
				final int b4 = (var >> 24) & 0xff;
				final byte[] varbyte = { (byte) b1, (byte) b2, (byte) b3, (byte) b4 };
				this.packet += Util.isoString(varbyte);
			}
		} catch (Exception e) {
		}
	}
	
	public int getInt(int bytec) {
		try {
			final String thestring = this.packet.substring(0, bytec);
			String hex_data_s = "";
			for (int i = bytec - 1; i >= 0; i--) {
				int data = thestring.getBytes("ISO8859-1")[i];
				if (data < 0)
					data += 256;
				final String hex_data = Integer.toHexString(data);
				if (hex_data.length() == 1)
					hex_data_s += "0" + hex_data;
				else
					hex_data_s += hex_data;
			}
			this.packet = this.packet.substring(bytec);
			return Integer.parseInt(hex_data_s, 16);
		} catch (Exception e) {
			
		}
		return 0;
	}
	
	public void addByteArray(byte[] bytearr) {
		try {
			this.packet += Util.isoString(bytearr);
		} catch (Exception e) {
		}
	}
	
	public void addByte(final byte... b) {
		addByteArray(b);
	}
	
	public String getPacket() {
		try {
			final byte[] packb = this.packet.getBytes("ISO8859-1");
			return Util.isoString(packb);
		} catch (Exception e) {
		}
		return null;
	}
	
	public void clean() {
		this.header = "";
		this.packet = "";
	}
	
}
