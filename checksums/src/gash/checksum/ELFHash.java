package gash.checksum;

public class ELFHash implements CheckSum {

	private long elf(String str) {
		long hash = 0;
		long x = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash << 4) + str.charAt(i);
			if ((x = hash & 0xF0000000L) != 0) {
				hash ^= (x >> 24);
			}
			hash &= ~x;
		}

		return hash;
	}

	@Override
	public String createCheckSum(Object obj) {
		if (obj == null)
			return null;

		return String.valueOf(elf(obj.toString()));
	}

	@Override
	public boolean verifyCheckSum(Object obj, String hash) {
		if (obj == null || hash == null)
			return false;
		else
			return createCheckSum(obj).equals(hash);
	}
}
