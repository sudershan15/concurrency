package gash.checksum;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author gash
 * 
 */
public class ShaHash implements CheckSum {
	public static final String sSHA1 = "SHA-1";
	public static final String sSHA256 = "SHA-256";
	public static final String sSHA512 = "SHA-512";

	private String whichOne;

	public ShaHash() {
		this(sSHA1);
	}

	public ShaHash(String whichOne) {
		if (whichOne == null)
			this.whichOne = sSHA1;
		else
			// you know what you are doing
			this.whichOne = whichOne;
	}

	@Override
	public String createCheckSum(Object obj) {
		String rtn = null;
		try {
			MessageDigest hash = MessageDigest.getInstance(whichOne);
			hash.update(obj.toString().getBytes());
			rtn = asHex(hash.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return rtn;
	}

	@Override
	public boolean verifyCheckSum(Object obj, String hash) {
		if (obj == null || hash == null)
			return false;
		else
			return createCheckSum(obj).equals(hash);
	}

	/**
	 * variation for creating hashes for large documents => not as memory
	 * intensive as createCheckSum()
	 * 
	 * @param source
	 * @return
	 */
	public String createCheckSumFromStream(InputStream source) {
		String rtn = null;
		try {
			MessageDigest hash = MessageDigest.getInstance("SHA-256");
			byte[] raw = new byte[4048];
			int n = 0;
			while ((n = source.read(raw)) > 0) {
				hash.update(raw, 0, n);
			}
			rtn = asHex(hash.digest());
		} catch (Exception ex) {
		} finally {
			// TODO we may want to close the stream
		}

		return rtn;
	}

	private String asHex(byte[] data) {
		StringBuilder rtn = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			rtn.append(Integer.toHexString(0xFF & data[i]));
		}
		return rtn.toString();
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}
