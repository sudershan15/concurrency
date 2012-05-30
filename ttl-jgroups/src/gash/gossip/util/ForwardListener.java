package gash.gossip.util;

import java.io.InputStream;
import java.io.OutputStream;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelListener;
import org.jgroups.ExtendedReceiver;
import org.jgroups.Message;
import org.jgroups.View;

public class ForwardListener implements Listener {
	Gossip group;
	MessageListener listener;
	boolean viable;
	int count;

	public ForwardListener() {
		super();
		System.out.println("---> ForwardListener()");
	}

	public boolean isViable() {
		return viable;
	}

	public ForwardListener(Gossip group) {
		this.group = group;
		listener = new MessageListener();
		listenTo(group);
	}

	@Override
	public void listenTo(Gossip channel) {
		channel.getChannel().setReceiver(listener);
		channel.getChannel().addChannelListener(new JoinListener());
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub

	}

	private final class JoinListener implements ChannelListener {

		@Override
		public void channelClosed(Channel arg0) {
			System.out.println("--> Channel closed: " + arg0.getClusterName());
		}

		@Override
		public void channelConnected(Channel arg0) {
			System.out.println("--> Channel connected: "
					+ arg0.getClusterName());
		}

		@Override
		public void channelDisconnected(Channel arg0) {
			System.out.println("--> Channel disconnected: "
					+ arg0.getClusterName());
		}

		@Override
		public void channelReconnected(Address arg0) {
			System.out.println("--> Channel reconnected: " + arg0);
		}

		@Override
		public void channelShunned() {
			// TODO Auto-generated method stub

		}
	}

	private final class MessageListener implements ExtendedReceiver {

		@Override
		public byte[] getState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void receive(Message msg) {
			Payload pl = (Payload) msg.getObject();

			viable = pl.isViable();
			if (viable) {
				count++;
				System.out.println("MSG: " + msg.getSrc().toString() + " - "
						+ pl);
				try {
					Thread.sleep(2000);
					if (pl.isViable())
						group.broadcast(pl);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("*** DEAD (" + count + " msgs, "
						+ Thread.activeCount() + " threads) ***");
			}
		}

		@Override
		public void setState(byte[] arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void block() {
			System.out.println("BLOCK:");
		}

		@Override
		public void suspect(Address arg0) {
			System.out.println("SUSPECT: " + arg0.toString());
		}

		@Override
		public void viewAccepted(View view) {
			System.out.println("VIEW: someone joined or left");
		}

		@Override
		public byte[] getState(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void getState(OutputStream arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void getState(String arg0, OutputStream arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setState(InputStream arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setState(String arg0, byte[] arg1) {
			System.out.println("STATE: " + arg0);

		}

		@Override
		public void setState(String arg0, InputStream arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unblock() {
			// TODO Auto-generated method stub

		}
	}

}
