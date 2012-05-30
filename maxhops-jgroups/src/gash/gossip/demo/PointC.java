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
			System.out.println("** Starting **");
			for (int n = 1; n <= 1; n++) {
				Thread.sleep(1000);
				Payload msg = new Payload();
				msg.setMessage("hello " + n);
				msg.setMaxHops(2);
				gossip.broadcast(msg);
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
