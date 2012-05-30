package gash.checksum;

import whirlpool.Whirlpool;

public class WhirlpoolHash implements CheckSum {
	public WhirlpoolHash() {
	}

	@Override
	public String createCheckSum(Object obj) {
		Whirlpool hash = new Whirlpool();
		byte[] digest = new byte[Whirlpool.DIGESTBYTES];

		hash.NESSIEinit();
		hash.NESSIEadd(obj.toString());
		hash.NESSIEfinalize(digest);

		return Whirlpool.display(digest);
	}

	@Override
	public boolean verifyCheckSum(Object obj, String hash) {
		if (obj == null || hash == null)
			return false;
		else
			return createCheckSum(obj).equals(hash);
	}

}