package gash.gossip.demo;

import gash.gossip.util.Gossip;
import gash.gossip.util.PrintListener;

import java.io.File;

public class PointA {
	private static final String sChannelName = "test";

	private Gossip gossip;

	public PointA() {
		gossip = new Gossip(new File("conf/gossip.xml"), "A", sChannelName);
		System.out.println("I AM: " + gossip.whoami());
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PointA pt = new PointA();
		PrintListener print = new PrintListener("A");
		print.listenTo(pt.gossip);

		pt.run();
	}
}
