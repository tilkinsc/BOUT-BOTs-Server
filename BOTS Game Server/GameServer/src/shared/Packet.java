package shared;

public class Packet {

	private StringBuilder head = new StringBuilder();
	private StringBuilder body = new StringBuilder();
	
	private boolean calced = false;
	
	public int getBodyLen() {
		return this.body.length();
	}
	
	public int getHeadLen() {
		return this.head.length();
	}
	
	public void setHead(String head) {
		this.head = new StringBuilder(head);
	}
	
	public void setBody(String body) {
		this.body = new StringBuilder(body);
	}
	
	public void addHeader(final byte... b) {
		calced = false;
		this.head.append(b);
	}
	
	public void removeHeader() {
		this.body = new StringBuilder(this.body.substring(4));
	}
	
	protected void calcHeader() {
		calced = true;
		this.head.append(Util.getbyteiso(this.body.length(), 2));
	}
	
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
				if (!reverse)
					this.body.append(Util.isoString(new byte[] {(byte) b1, (byte) b2}));
				else this.body.append(Util.isoString(new byte[] {(byte) b2, (byte) b1}));
			} else if (num == 4)
				this.body.append(Util.isoString(new byte[]{
						(byte) (var & 0xff),
						(byte) ((var >> 8) & 0xff),
						(byte) ((var >> 16) & 0xff),
						(byte) ((var >> 24) & 0xff)
					}));
		} catch (Exception e) {
		}
	}
	
	public int getInt(int bytec) {
		try {
			final byte[] bytes = this.body.substring(0, bytec).getBytes("ISO8859-1");
			String hex_data_s = "";
			for (int i = bytec - 1; i >= 0; i--) {
				int data = bytes[i];
				if (data < 0)
					data += 256;
				final String hex_data = Integer.toHexString(data);
				hex_data_s += hex_data.length() == 1 ? "0" + hex_data : hex_data;
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
	
	public String getHeader() {
		if (!calced) this.calcHeader();
		try {
			return Util.isoString(this.head.toString().getBytes("ISO8859-1"));
		} catch (Exception e) {
		}
		return null;
	}
	
	public String getHead() {
		try {
			return Util.isoString(this.head.toString().getBytes("ISO8859-1"));
		} catch (Exception e) {
		}
		return null;
	}
	
	public String getBody() {
		try {
			return Util.isoString(this.body.toString().getBytes("ISO8859-1"));
		} catch (Exception e) {
		}
		return null;
	}
	
	public void clean() {
		this.head = new StringBuilder();
		this.body = new StringBuilder();
	}
	
}
