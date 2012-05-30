package gash.checksum;

/**
 * generation of a checksum can be as simple as using the hashCode() for an
 * object.
 * 
 * @author gash
 * 
 */
public class SimpleHash implements CheckSum {
	/* (non-Javadoc)
	 * @see gash.checksum.CheckSum#createCheckSum(java.lang.Object)
	 */
	@Override
	public String createCheckSum(Object obj) {
		if (obj == null)
			return null;
		else
			return String.valueOf(obj.hashCode());
	}

	/* (non-Javadoc)
	 * @see gash.checksum.CheckSum#verifyCheckSum(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean verifyCheckSum(Object obj, String hash) {
		if (obj == null || hash == null)
			return false;

		String check = createCheckSum(obj);
		return hash.equals(check);
	}
}
