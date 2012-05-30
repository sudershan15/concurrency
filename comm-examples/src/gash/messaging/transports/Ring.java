package gash.messaging.transports;

import java.util.ArrayList;

import gash.messaging.Message;
import gash.messaging.Node;
//import gash.messaging.transports.Line.LineNode;

/**
 * a ring is a closed line (daisy chained) group of services/nodes.
 * 
 * @author gash1
 * 
 */
public class Ring implements MessageTransport {

	private int msgID;
	private ArrayList<RingNode> nodes;

	public Ring(int numNodes) {

		nodes = new ArrayList<RingNode>(numNodes);
		for (int n = 0; n < numNodes; n++) {
			RingNode node = new RingNode(n);
			nodes.add(node);
			node.start();
		}

		// setup network (linked list)
		Node[] list = getNodes();
		for (int n = 0; n < numNodes; n++) {
			if (n + 1 < numNodes)
				list[n].setNext(list[n + 1]);

			if (n - 1 >= 0)
				list[n].setPrevious(list[n - 1]);
		}

		// close the ring
		list[0].setPrevious(list[numNodes - 1]);
		list[numNodes - 1].setNext(list[0]);

		// show the ring
		// for (int n = 0; n < numNodes; n++)
		// System.out.println(n + ") P: " + nodes[n].previous + ", N: " +
		// nodes[n].next);
	}

	@Override
	public Node[] getNodes() {
		if (nodes == null)
			return null;

		Node[] r = new Node[nodes.size()];
		return (Node[]) nodes.toArray(r);
	}

	@Override
	public void addNode(Node node) {
		throw new RuntimeException("not implemented");
	}

	private Node getNode(int id) {
		for (Node n : nodes) {
			if (id == n.getNodeId())
				return n;
		}

		throw new RuntimeException("Node not found, id = " + id);
	}

	@Override
	public void sendMessage(Message msg) {
		if (msg == null)
			return;

		if (msg.getOriginator() < msg.getDestination())
			msg.setDirection(Message.Direction.Forward);
		else
			msg.setDirection(Message.Direction.Backward);

		getNode(msg.getOriginator()).message(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gash.messaging.MessageTransport#sendMessage(int, int,
	 * java.lang.String)
	 */
	@Override
	public void sendMessage(int fromNodeId, int toNodeId, String text) {
		msgID++;

		Message msg = new Message(msgID);
		msg.setMessage(text);
		msg.setDestination(toNodeId);
		msg.setOriginator(fromNodeId);

		// if we could calculate distances (hops), we could determine the
		// optimum traversal direction. For now, set direction based on > or <
		if (fromNodeId < toNodeId)
			msg.setDirection(Message.Direction.Forward);
		else
			msg.setDirection(Message.Direction.Backward);

		getNode(fromNodeId).message(msg);
	}

	@Override
	public void broadcastMessage(int fromNodeId, String text) {
		Message msg = new Message(msgID);
		msg.setMessage(text);
		msg.setDestination(fromNodeId);
		msg.setOriginator(fromNodeId);
		msg.setDirection(Message.Direction.Forward);
		getNode(fromNodeId).message(msg);
	}

	/**
	 * represents a node in the network
	 * 
	 * @author gash1
	 * 
	 */
	public class RingNode extends Node {
		public RingNode(int id) {
			super(id);
		}

		public String toString() {
			return String.valueOf(nodeId);
		}

		public void process(Message msg) {
			if (msg.getDestination() == nodeId) {
				System.out.println("Node " + nodeId + " got a message from node " + msg.getOriginator() + " (hops = "
						+ msg.getHops() + ", clock = " + msg.getClock() + "), msg: " + msg.getMessage());
				return;
			}

			// stopping traversal for an unknown node ID
			if (nodeId == msg.getOriginator() && msg.getHops() > 0 && !msg.isReverse()) {
				System.out.println("--> failed (msg id = " + msg.getId() + ", clock = " + msg.getClock()
						+ ") unknown node " + msg.getDestination() + ", hops = " + msg.getHops());
				return;
			} else if (msg.isReverse() && previous == null) {
				System.out.println("--> failed (msg id = " + msg.getId() + ", clock = " + msg.getClock()
						+ ") unreachable node " + msg.getDestination() + ", hops = " + msg.getHops());
				return;
			}

			msg.incrementHops(nodeId);
			if (msg.getDirection() == Message.Direction.Forward) {
				if (next != null)
					next.message(msg);
				else if (previous != null) {
					// broken ring (not closed)
					msg.setReverse(true);
					previous.message(msg);
				}
			} else {
				// Direction.backward
				if (previous != null)
					previous.message(msg);
				else if (next != null) {
					// broken ring (not closed)
					msg.setReverse(true);
					next.message(msg);
				}
			}
		}
	}

	/**
	 * emulates the message sent through network
	 * 
	 * @author gash1
	 * 
	 */

}
