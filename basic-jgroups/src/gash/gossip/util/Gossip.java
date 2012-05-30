package gash.gossip.util;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;

public class Gossip {
	private File setup;
	private JChannel channel;
	private String channelName;
	private String nodeName;

	// demo
	private Random rand = new Random(System.currentTimeMillis());

	public Gossip(File setup) {
		this(setup, null, null);
	}

	public Gossip(File setup, String nodeName, String channelName) {
		this.setup = setup;
		this.channelName = channelName;
		this.nodeName = nodeName;
	}

	public void finalize() {
		close();
	}

	public String whoami() {
		JChannel channel = connect();
		return channel.getAddressAsString();
	}

	public void close() {
		if (channel != null) {
			channel.disconnect();
			channel.close();
			channel = null;
		}
	}

	public void info() {
		JChannel channel = connect();
		if (channel == null)
			throw new RuntimeException("Unable to create channel "
					+ channelName);

		System.out.println("INFO: cluster: " + channel.getClusterName());
		System.out.println("INFO: name: " + channel.getName());
		System.out.println("INFO: protocol: "
				+ channel.getProtocolStack().getTransport().getName());
		System.out.println("INFO: creator: " + channel.getView().getCreator());
		System.out.println("INFO: " + channel.getViewAsString());
		System.out.println("INFO: num nodes: " + channel.getView().size());
		System.out.println("INFO: messages sent: " + channel.getSentMessages());
		System.out.println("INFO: messages received: "
				+ channel.getReceivedMessages());

		System.out.println("INFO: messages in queue: "
				+ channel.getNumMessages());
	}

	public void direct(String node, String msg) throws Exception {
		JChannel channel = connect();
		if (channel == null)
			throw new RuntimeException("Unable to create channel "
					+ channelName);

		for (Address addr : channel.getView().getMembers()) {
			if (addr.toString().equals(node)) {
				System.out.println("--> sending a message to " + addr);
				channel.send(addr, null, msg.getBytes());
				break;
			}
		}

	}

	public void random(String msg) throws Exception {
		JChannel channel = connect();
		if (channel == null)
			throw new RuntimeException("Unable to create channel "
					+ channelName);

		Address dest = null;
		List<Address> members = channel.getView().getMembers();

		// don't send it to myself
		while (true) {
			int n = rand.nextInt(members.size());
			dest = members.get(n);
			if (!dest.equals(channel.getAddress()))
				break;
		}

		System.out.println("--> sending a message to " + dest);
		channel.send(dest, null, msg.getBytes());
	}

	public void broadcast(String msg) throws Exception {
		JChannel channel = connect();
		if (channel == null)
			throw new RuntimeException("Unable to create channel "
					+ channelName);

		channel.send(null, null, msg.getBytes());
	}

	public JChannel getChannel() {
		return connect();
	}

	protected JChannel connect() {
		if (channel != null)
			return channel;
		else if (channelName == null)
			throw new RuntimeException("Not initialized - missing name");

		try {
			channel = new JChannel(setup);
			channel.setName(nodeName);
			channel.connect(channelName);
		} catch (ChannelException e) {
			channel = null;
			e.printStackTrace();
		}

		return channel;
	}

	public void showConnections() {
		if (channel == null)
			return;

		for (Address a : channel.getView().getMembers()) {
			System.out.println("members: " + a.toString());
		}

	}

	public void setChannelName(String name) {
		if (channel != null) {
			channel.close();
			channel = null;
		}
		channelName = name;
	}
}
