package gash.gossip.demo;

import gash.gossip.util.Gossip;
import gash.gossip.util.PrintListener;

import java.io.File;

public class Random {
	private static final String sChannelName = "test";

	private Gossip gossip;

	public Random() {
		gossip = new Gossip(new File("conf/gossip.xml"), "PtoP", sChannelName);
		System.out.println("I AM: " + gossip.whoami());
	}

	public void run() {
		try {
			gossip.info();
			System.out.println("** Starting **");
			for (int n = 1; n <= 20; n++) {
				Thread.sleep(1000);
				gossip.random("hello " + n);
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
		Random pt = new Random();
		PrintListener print = new PrintListener();
		print.listenTo(pt.gossip);
		pt.run();
	}
}
