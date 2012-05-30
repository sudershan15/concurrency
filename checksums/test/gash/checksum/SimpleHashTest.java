package gash.checksum;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class SimpleHashTest {

	@Test
	public void testJavaHash() {

		Integer i = 20394;
		String s = "hello how are you today?";
		Date d = new Date(); // today

		SimpleHash hash = new SimpleHash();

		String hc = hash.createCheckSum(i);
		Assert.assertTrue(hash.verifyCheckSum(i, hc));
		System.out.println(i + " -> " + hc);

		hc = hash.createCheckSum(s);
		Assert.assertTrue(hash.verifyCheckSum(s, hc));
		System.out.println(s + " -> " + hc);

		hc = hash.createCheckSum(d);
		Assert.assertTrue(hash.verifyCheckSum(d, hc));
		System.out.println(d + " -> " + hc);

	}
}
