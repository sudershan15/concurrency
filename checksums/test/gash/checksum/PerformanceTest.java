package gash.checksum;

import org.junit.Test;

public class PerformanceTest {

	private static final String sData = "THIS SOFTWARE IS PROVIDED BY THE AUTHORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, "
			+ "INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE "
			+ "DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, "
			+ "EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS "
			+ "OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN "
			+ "CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS "
			+ "SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE";

	@Test
	public void testPerformanceSHA() throws Exception {
		ShaHash hash = new ShaHash(ShaHash.sSHA512);

		long stime = System.currentTimeMillis();
		String hc = null;
		for (int n = 0, N = 1000; n < N; n++) {
			hc = hash.createCheckSum(sData);
		}
		long dt = System.currentTimeMillis() - stime;

		// Assert.assertEquals(expected, hc);
		System.out.println("SHA-512 of 1000 operations took: " + dt + " msec");
	}

	@Test
	public void testPerformanceWhirlpool() throws Exception {
		WhirlpoolHash hash = new WhirlpoolHash();

		long stime = System.currentTimeMillis();
		String hc = null;
		for (int n = 0, N = 1000; n < N; n++) {
			hc = hash.createCheckSum(sData);
		}
		long dt = System.currentTimeMillis() - stime;

		// Assert.assertEquals(expected, hc);
		System.out
				.println("Whirlpool of 1000 operations took: " + dt + " msec");
	}

	@Test
	public void testPerformanceJava() throws Exception {
		SimpleHash hash = new SimpleHash();

		long stime = System.currentTimeMillis();
		String hc = null;
		for (int n = 0, N = 1000; n < N; n++) {
			hc = hash.createCheckSum(sData);
		}
		long dt = System.currentTimeMillis() - stime;

		// Assert.assertEquals(expected, hc);
		System.out.println("Javah hashcode() of 1000 operations took: " + dt
				+ " msec");
	}

	@Test
	public void testPerformanceELF() throws Exception {
		ELFHash hash = new ELFHash();

		long stime = System.currentTimeMillis();
		String hc = null;
		for (int n = 0, N = 1000; n < N; n++) {
			hc = hash.createCheckSum(sData);
		}
		long dt = System.currentTimeMillis() - stime;

		// Assert.assertEquals(expected, hc);
		System.out.println("ELF of 1000 operations took: " + dt + " msec");
	}
}
