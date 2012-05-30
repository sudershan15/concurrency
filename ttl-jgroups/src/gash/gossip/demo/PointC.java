package gash.gossip.demo;

import gash.gossip.util.ForwardListener;
import gash.gossip.util.Gossip;
import gash.gossip.util.Payload;

import java.io.File;

public class PointC {
	private Gossip gossip;

	public PointC() {
		gossip = new Gossip(new File("conf/gossip.xml"), "C", "test");
		System.out.println("I AM: " + gossip.whoami());
	}

	public void run() {
		try {
			gossip.info();

			// only need to sent the message once since ttl allows the message
			// to be propagated
			Payload msg = new Payload();
			msg.setMessage("hello I'm a TTL message");
			msg.setTtl(System.currentTimeMillis() + 10000);
			gossip.broadcast(msg);

			System.out.println("** Starting **");
			while (true) {
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("** Done **\n");
			gossip.info();
			gossip.close();
		}
	}

	public static void main(String[] args) throws Exception {
		PointC pt = new PointC();
		ForwardListener fl = new ForwardListener(pt.gossip);
		pt.run();
	}
}
