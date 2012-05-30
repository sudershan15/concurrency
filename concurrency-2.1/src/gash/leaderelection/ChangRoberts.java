package gash.leaderelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class ChangRoberts {
	private int msgID = 1;
	private Transport bus;

	public ChangRoberts() {
		bus = new Transport();
		bus.start();
	}

	public void addNode(Node node) {
		if (node == null)
			return;

		node.setBus(bus);
		bus.addNode(node);
	}

	/**
	 * represent a bus communication network, any organization will do
	 * 
	 * @author gash1
	 * 
	 */
	public class Transport extends Thread {
		private List<Node> bus;
		private List<Message> inbox;

		public Transport() {
			inbox = Collections.synchronizedList(new ArrayList<Message>());
			bus = Collections.synchronizedList(new ArrayList<Node>());
		}

		public void addNode(Node n) {
			if (n != null && !bus.contains(n))
				bus.add(n);
		}

		public void removeNode(Node n) {
			bus.remove(n);
		}

		public void sendMessage(Message msg) {
			if (msg != null) {
				msg.setId(msgID++);
				inbox.add(msg);
			}
		}

		public void run() {
			try {
				while (true) {
					if (inbox.size() == 0)
						// HACK should block on read
						sleep(50);
					else if (inbox.size() > 0) {
						Message msg = inbox.remove(0);
						for (Node n : bus)
							if (n.isAlive())
								n.message(msg);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	public static class Node extends Thread {
		private static final long sWaitFor = 3000;

		protected int id;
		protected List<Message> inbox;
		protected Transport bus;
		protected int delay;

		protected int leader;
		protected long leaderWait;

		protected int challengeLeader;
		protected long challengeWait;

		// HACK to ensure leader election in a stable system
		private int leaderLife = 4;

		public Node(int id) {
			this.id = id;
			this.leader = Message.sNobody;
			this.leaderWait = 0;
			this.challengeLeader = Message.sNobody;
			this.challengeWait = 0;

			// Heart beat: monitor leader health and election process
			this.delay = 1000;

			this.inbox = Collections.synchronizedList(new ArrayList<Message>());
		}

		public void run() {
			try {
				while (true) {
					sleep(delay);

					while (inbox.size() > 0) {
						Message msg = inbox.remove(0);
						process(msg);
					}

					if (leader != Message.sNobody)
						leaderAck();
					else
						checkChallengeStatus();

					checkLeaderStatus();

					// HACK to demonstrate alive heart-beat failing and the
					// nodes go into a leader election mode
					if (leader == id) {
						leaderLife--;
						if (leaderLife == 0) {
							System.out.println("*** " + id + " retiring as leader ***");
							return;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void message(Message msg) {
			inbox.add(msg);
		}

		public void process(Message msg) {
			if (msg.getAction() == Message.Action.LeaderAlive) {
				leaderWait = System.currentTimeMillis();
				leader = msg.getOrigination();
			} else if (msg.getAction() == Message.Action.LeaderElection) {
				leader = Message.sNobody;
				leaderWait = 0;

				// the challenge leader is used to only issue challenges if the
				// challenger (ID) is less than myself
				challengeLeader = Math.max(challengeLeader, msg.getOrigination());
				System.out.println("   " + id + " accepts " + challengeLeader + " as potiental leader");

				if (challengeLeader < id)
					issueChallenge();
			} else if (msg.getAction() == Message.Action.LeaderDeclared) {
				// System.out.println("leader declared: " +
				// msg.getOrigination());
				leader = msg.getOrigination();
				leaderWait = System.currentTimeMillis();
				challengeWait = 0;
				challengeLeader = Message.sNobody;
			} else {
				// normal message processing goes here
			}
		}

		private void leaderAck() {
			if (leader != id)
				return;

			// slow checking down as we are not electing a leader
			delay = 2000;

			// I'm the leader
			System.out.println("* I'm the leader: " + id + " *");
			Message m = new Message();
			m.setOrigination(id);
			m.setAction(Message.Action.LeaderAlive);
			bus.sendMessage(m);
		}

		private void checkLeaderStatus() {
			if (challengeLeader != Message.sNobody)
				return;
			else if (System.currentTimeMillis() - leaderWait > sWaitFor) {
				if (leaderWait != 0)
					System.out.println("! " + id + " declares the leader dead !");

				// assume leader is dead (or startup)
				leader = Message.sNobody;
				issueChallenge();
			}
		}

		private void checkChallengeStatus() {
			if (challengeLeader == Message.sNobody || challengeLeader != id)
				return;

			// I'm the current potiental leader
			if (System.currentTimeMillis() - challengeWait > sWaitFor) {
				System.out.println("   " + id + " declaring self as leader");
				Message m = new Message();
				m.setOrigination(id);
				m.setAction(Message.Action.LeaderDeclared);
				bus.sendMessage(m);
			}
		}

		private void issueChallenge() {
			System.out.println(id + " issues a challenge");

			// entering leader election mode - want to process messages faster
			delay = 500;

			// issue a challenge
			challengeLeader = id;
			challengeWait = System.currentTimeMillis(); // countdown
			Message m = new Message();
			m.setOrigination(id);
			m.setAction(Message.Action.LeaderElection);
			bus.sendMessage(m);
		}

		public void setBus(Transport bus) {
			this.bus = bus;
		}
	}

	/**
	 * message sent through the network
	 * 
	 * @author gash1
	 * 
	 */
	public static class Message {
		public static final int sNobody = -1;

		public enum Action {
			LeaderAlive, LeaderElection, LeaderDeclared
		}

		private Action action;
		private int id;
		private int origination = sNobody;
		private int destination = sNobody;
		private int leader;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getDestination() {
			return destination;
		}

		public void setDestination(int destination) {
			this.destination = destination;
		}

		public int getLeader() {
			return leader;
		}

		public void setLeader(int leader) {
			this.leader = leader;
		}

		public int getOrigination() {
			return origination;
		}

		public void setOrigination(int origination) {
			this.origination = origination;
		}

		public Action getAction() {
			return action;
		}

		public void setAction(Action action) {
			this.action = action;
		}
	}
}
