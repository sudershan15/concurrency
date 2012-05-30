package gash.leaderelection;

import gash.messaging.Message;
import gash.messaging.Node;
import gash.messaging.transports.Bus;
import gash.messaging.transports.MessageTransport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bully demonstrates the bully algorithm for election of a leader.
 * 
 * The algorithm uses an approach where the largest ID of a process wins.
 * 
 * Steps:
 * 
 * 1) Nodes monitor the health of the leader node (heart beat messages)
 * 
 * 2) On a heart beat (time delta) failure or no leader was specified, as in a
 * startup situation, a monitoring node will broadcast a challenge for
 * leadership (sending its ID) to all nodes.
 * 
 * 3) Message traffic is reduced (stops challenges) when a node receives a
 * challenge from a node with a larger ID.
 * 
 * 4) If a node receives a challenge from a node with a lower ID, it must
 * immediately send out a challenge.
 * 
 * 5) If no challenges are forthcoming (time delta) then the highest node
 * declares itself as the leader.
 * 
 * Notes:
 * 
 * 1) Alternate approaches could only send messages to nodes having a higher ID;
 * assumes an ordered network. This example assumes random ordering (e.g., a bus
 * or adhoc network) and broadcasts to all nodes.
 * 
 * 2) The initial broadcast has the benefit in alerting all nodes to enter into
 * challenge mode.
 * 
 * 3) This implementation does a broadcast to all nodes; whereas the algo should
 * only send to nodes with an ID greater than the sender's ID
 * 
 * @author gash1
 * 
 */
public class Bully {
	static AtomicInteger msgID = new AtomicInteger(0);

	private MessageTransport transport;

	public Bully() {
		transport = new Bus(0);
		// transport = new Line(10, true);
	}

	public void addNode(Node node) {
		if (node == null)
			return;

		if (node instanceof BullyNode) {
			((BullyNode) node).setTransport(transport);
			transport.addNode(node);
		} else {
			BullyWrapperNode b = new BullyWrapperNode(node);
			b.setTransport(transport);
			transport.addNode(b);
		}

		if (!node.isAlive())
			node.start();
	}

	public static class BullyWrapperNode extends BullyNode {
		private Node node;

		public BullyWrapperNode(Node node) {
			super(node.getNodeId());
			this.node = node;
		}

		@Override
		public void run() {
			node.run();
		}

		@Override
		public void process(Message msg) {
			node.process(msg);
		}
	}

	/**
	 * represents a node in the network. Class only represents leader monitoring
	 * and election process. This would exist as a separate communication path
	 * or embedded within the main communication traffic. If embedded, a
	 * priority for reading leader messages should be used.
	 * 
	 * @author gash1
	 * 
	 */
	public static class BullyNode extends Node {
		private static final long sWaitFor = 3000;

		protected MessageTransport transport;
		protected int delay;

		protected int leader;
		protected long leaderWait;

		protected int challengeLeader;
		protected long challengeWait;

		// HACK to ensure leader election in a stable system
		private int leaderLife = 4;

		public BullyNode(int id) {
			super(id);

			this.leader = LeaderMessage.sNobody;
			this.leaderWait = 0;
			this.challengeLeader = LeaderMessage.sNobody;
			this.challengeWait = 0;

			// Heart beat: monitor leader health and election process
			this.delay = 1000;
		}

		@Override
		public void run() {
			try {
				while (true) {
					sleep(delay);

					while (inbox.size() > 0) {
						LeaderMessage msg = (LeaderMessage) inbox.remove(0);
						process(msg);
					}

					if (leader != LeaderMessage.sNobody)
						leaderAck();
					else
						checkChallengeStatus();

					checkLeaderStatus();

					// HACK to demonstrate alive heart-beat failing and the
					// nodes go into a leader election mode
					if (leader == getNodeId()) {
						leaderLife--;
						if (leaderLife == 0) {
							System.out.println("*** " + getNodeId() + " retiring as leader ***");
							return;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void process(Message amsg) {
			if (amsg instanceof LeaderMessage) {
				LeaderMessage msg = (LeaderMessage) amsg;
				if (msg.getAction() == LeaderMessage.Action.LeaderAlive) {
					leaderWait = System.currentTimeMillis();
					leader = msg.getOriginator();
				} else if (msg.getAction() == LeaderMessage.Action.LeaderElection) {
					leader = LeaderMessage.sNobody;
					leaderWait = 0;

					// the challenge leader is used to only issue challenges if
					// the challenger (ID) is less than myself
					challengeLeader = Math.max(challengeLeader, msg.getOriginator());
					System.out.println("   " + getNodeId() + " accepts " + challengeLeader + " as potiental leader");

					if (challengeLeader < getNodeId())
						issueChallenge();
				} else if (msg.getAction() == LeaderMessage.Action.LeaderDeclared) {
					System.out.println("leader declared: " + msg.getOriginator());
					leader = msg.getOriginator();
					leaderWait = System.currentTimeMillis();
					challengeWait = 0;
					challengeLeader = LeaderMessage.sNobody;
				}
			} else {
				// normal message processing goes here
				System.out.println("--> unhandled message: " + amsg.getMessage());
			}
		}

		private void leaderAck() {
			if (leader != getNodeId())
				return;

			// slow checking down as we are not electing a leader
			delay = 2000;

			// I'm the leader
			System.out.println("* I'm the leader: " + getNodeId() + " *");
			LeaderMessage m = new LeaderMessage();
			m.setOriginator(getNodeId());
			m.setAction(LeaderMessage.Action.LeaderAlive);
			m.setDeliverAs(Message.Delivery.Broadcast);
			transport.sendMessage(m);
		}

		private void checkLeaderStatus() {
			if (challengeLeader != LeaderMessage.sNobody)
				return;
			else if (System.currentTimeMillis() - leaderWait > sWaitFor) {
				if (leaderWait != 0)
					System.out.println("! " + getNodeId() + " declares the leader dead !");

				// assume leader is dead (or startup)
				leader = LeaderMessage.sNobody;
				issueChallenge();
			}
		}

		private void checkChallengeStatus() {
			if (challengeLeader == LeaderMessage.sNobody || challengeLeader != getNodeId())
				return;

			// I'm the current potential leader
			if (System.currentTimeMillis() - challengeWait > sWaitFor) {
				System.out.println("   " + getNodeId() + " declaring self as leader");
				LeaderMessage m = new LeaderMessage();
				m.setOriginator(getNodeId());
				m.setAction(LeaderMessage.Action.LeaderDeclared);
				m.setDeliverAs(Message.Delivery.Broadcast);
				transport.sendMessage(m);
			}
		}

		private void issueChallenge() {
			System.out.println(getNodeId() + " issues a challenge");

			// entering leader election mode - want to process messages faster
			delay = 500;

			// issue a challenge
			challengeLeader = getNodeId();
			challengeWait = System.currentTimeMillis(); // countdown
			LeaderMessage m = new LeaderMessage();
			m.setOriginator(getNodeId());
			m.setAction(LeaderMessage.Action.LeaderElection);
			m.setDeliverAs(Message.Delivery.Broadcast);
			transport.sendMessage(m);
		}

		public void setTransport(MessageTransport t) {
			this.transport = t;
		}
	}

	/**
	 * message sent through the network
	 * 
	 * @author gash1
	 * 
	 */
	public static class LeaderMessage extends Message {

		public enum Action {
			LeaderAlive, LeaderElection, LeaderDeclared
		}

		private Action action;
		private int leader;

		public LeaderMessage() {
			super(msgID.incrementAndGet());
		}

		public int getLeader() {
			return leader;
		}

		public void setLeader(int leader) {
			this.leader = leader;
		}

		public Action getAction() {
			return action;
		}

		public void setAction(Action action) {
			this.action = action;
		}
	}
}
