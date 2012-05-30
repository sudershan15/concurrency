package gash.gossip.demo;

import gash.gossip.util.Gossip;
import gash.gossip.util.PrintListener;

import java.io.File;

public class Direct {
	private static final String sChannelName = "test";

	private Gossip gossip;

	public Direct() {
		gossip = new Gossip(new File("conf/gossip.xml"), "PtoP", sChannelName);
		System.out.println("I AM: " + gossip.whoami());
	}

	public void run() {
		try {
			gossip.info();
			System.out.println("** Starting (sending only to node A **");
			for (int n = 1; n <= 10; n++) {
				Thread.sleep(1000);
				gossip.direct("A", "hello " + n);
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
		Direct pt = new Direct();
		PrintListener print = new PrintListener();
		print.listenTo(pt.gossip);
		pt.run();
	}
}
