package gash.gossip.demo;

import java.io.File;

import gash.gossip.util.ForwardListener;
import gash.gossip.util.Gossip;
import gash.gossip.util.PrintListener;

public class PointB {
	private Gossip gossip;

	public PointB() {
		gossip = new Gossip(new File("conf/gossip.xml"), "B", "test");
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
		PointB pt = new PointB();
		ForwardListener fl = new ForwardListener(pt.gossip);
		pt.run();
	}
}
