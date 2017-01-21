package shared;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

	
	public static String isoString(byte[] bytes) {
		try {
			return new String(bytes, "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public static String md5hash(String text) {
		try {
			MessageDigest md = null;
			byte[] encryptMsg = null;
			try {
				md = MessageDigest.getInstance("MD5");
				encryptMsg = md.digest(text.getBytes("ISO8859-1"));
			} catch (NoSuchAlgorithmException e) {
			}
			String swap = "";
			String byteStr = "";
			final StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i <= encryptMsg.length - 1; i++) {
				byteStr = Integer.toHexString(encryptMsg[i]);
				switch (byteStr.length()) {
				case 1:
					swap = "0" + Integer.toHexString(encryptMsg[i]);
					break;
				case 2:
					swap = Integer.toHexString(encryptMsg[i]);
					break;
				case 8:
					swap = (Integer.toHexString(encryptMsg[i])).substring(6, 8);
					break;
				}
				strBuf.append(swap);
			}
			final String hash = strBuf.toString();
			return hash;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
