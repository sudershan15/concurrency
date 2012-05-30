package gash.gossip.demo;

import gash.gossip.util.Gossip;
import gash.gossip.util.PrintListener;

import java.io.File;

public class Broadcast {
	private Gossip gossip;

	public Broadcast() {
		gossip = new Gossip(new File("conf/gossip.xml"), "common", "test");
		System.out.println("I AM: " + gossip.whoami());
	}

	public void run() {
		try {
			gossip.info();
			System.out.println("** Starting **");
			for (int n = 1; n <= 20; n++) {
				Thread.sleep(1000);
				gossip.broadcast("hello " + n);
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
		Broadcast pt = new Broadcast();
		PrintListener print = new PrintListener();
		print.listenTo(pt.gossip);
		pt.run();
	}
}
