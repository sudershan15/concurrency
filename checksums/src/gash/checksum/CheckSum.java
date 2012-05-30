package gash.checksum;

public interface CheckSum {
	String createCheckSum(Object obj);

	boolean verifyCheckSum(Object obj, String hash);
}