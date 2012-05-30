package gash.gossip.demo;

import gash.gossip.util.ForwardListener;
import gash.gossip.util.Gossip;
import gash.gossip.util.PrintListener;

import java.io.File;

public class PointA {
	private Gossip gossip;

	public PointA() {
		gossip = new Gossip(new File("conf/gossip.xml"), "A", "test");
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
		ForwardListener fl = new ForwardListener(pt.gossip);
		pt.run();
	}
}
