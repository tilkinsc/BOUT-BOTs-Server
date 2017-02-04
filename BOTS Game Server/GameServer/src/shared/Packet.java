package shared;

public class Packet {

	private StringBuilder header = new StringBuilder();
	private StringBuilder body = new StringBuilder();
	
	public int getHeadLen() {
		return this.header.length();
	}
	
	public int getBodyLen() {
		return this.body.length();
	}
	
	// TODO: make sure this resolves properly, errors in channelserver expecting body len
	public int getLen() {
		return this.header.length() + this.body.length();
	}
	
	public void setHead(String head) {
		this.header = new StringBuilder(head);
	}
	
	public void setBody(String body) {
		this.body = new StringBuilder(body);
	}
	
	public void addHead(byte... b) {
		this.header.append(new String(b));
	}
	
	public void addBody(byte... b) {
		this.body.append(new String(b));
	}
	
	public static String removeHeader(int len, String str) {
		return str.substring(len);
	}
	
//	public void removeHeader() {
//		this.body = this.body.substring(4);
//	}
	
	protected String calcHeader(int len) {
		return this.header.append(Util.getbyteiso(this.body.length(), len)).toString();
	}
	
	// TODO: horrible name
	public String getHeader(int len) {
		try {
			return Util.isoString(calcHeader(len).getBytes("ISO8859-1"));
		} catch (Exception e) {
		}
		return null;
	}
	
	// TODO: horrible name
	public void addPacketHead(final byte... b) {
		try {
			this.body.append(Util.isoString(b));
		} catch (Exception e) {
		}
	}
	
	public void addString(String string) {
		this.body.append(string);
	}
	
	public String getString(int start, int end, boolean nulled) {
		final String thestring = this.body.substring(start, end);
		this.body = new StringBuilder(this.body.substring(end));
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
					this.body.append(Util.isoString(varbyte));
				} else {
					final byte[] varbyte = { (byte) b2, (byte) b1 };
					this.body.append(Util.isoString(varbyte));
				}
			} else if (num == 4) {
				final int b1 = var & 0xff;
				final int b2 = (var >> 8) & 0xff;
				final int b3 = (var >> 16) & 0xff;
				final int b4 = (var >> 24) & 0xff;
				final byte[] varbyte = { (byte) b1, (byte) b2, (byte) b3, (byte) b4 };
				this.body.append(Util.isoString(varbyte));
			}
		} catch (Exception e) {
		}
	}
	
	public int getInt(int bytec) {
		try {
			final String thestring = this.body.substring(0, bytec);
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
			this.body = new StringBuilder(this.body.substring(bytec));
			return Integer.parseInt(hex_data_s, 16);
		} catch (Exception e) {
			
		}
		return 0;
	}
	
	public void addByteArray(byte[] bytearr) {
		try {
			this.body.append(Util.isoString(bytearr));
		} catch (Exception e) {
		}
	}
	
	public void addByte(final byte... b) {
		addByteArray(b);
	}
	
	// TODO: horrible, inaccurate name
	public String getPacket() {
		try {
			return Util.isoString(this.body.toString().getBytes("ISO8859-1"));
		} catch (Exception e) {
		}
		return null;
	}
	
	public void clean() {
		this.header = new StringBuilder();
		this.body = new StringBuilder();
	}
	
}
