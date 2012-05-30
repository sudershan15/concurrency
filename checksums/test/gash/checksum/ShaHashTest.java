package gash.checksum;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class ShaHashTest {

	@Test
	public void testSHAHash() {

		Integer i = 20394;
		String s = "hello how are you today?";
		Date d = new Date(); // today

		ShaHash hash = new ShaHash();

		String expected = "efb48622ea6404e69bebba43e2ed0e59d6ed789a9fa3349611ea3564f75da6";
		String hc = hash.createCheckSum(i);
		//Assert.assertEquals(expected, hc);
		System.out.println(i + " -> " + hc);

		expected = "2196d81b7504835a09acdf52fd99c2157d401775544c44f621f6b763bda4e";
		hc = hash.createCheckSum(s);
		//Assert.assertEquals(expected, hc);
		System.out.println(s + " -> " + hc);

		hc = hash.createCheckSum(d);
		Assert.assertTrue(hash.verifyCheckSum(d, hc));
		System.out.println(d + " -> " + hc);

	}
}
