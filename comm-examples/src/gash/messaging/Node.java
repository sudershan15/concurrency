package gash.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node extends Thread {
	protected int nodeId;
	
	protected Node previous;
	protected Node next;
	protected List<Message> inbox;

	public Node(int id) {
		inbox = Collections.synchronizedList(new ArrayList<Message>());
		nodeId = id;
	}

	public abstract void process(Message msg);

	public void message(Message msg) {
		if (msg != null) {
			msg.incrementHops(nodeId);
			getInbox().add(msg);
		}
	}

	public void run() {
		try {
			while (true) {
				if (getInbox().size() == 0)
					sleep(100);
				else {
					Message msg = getInbox().remove(0);
					process(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int id) {
		this.nodeId = id;
	}

	public Node getPrevious() {
		return previous;
	}

	public void setPrevious(Node previous) {
		this.previous = previous;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public List<Message> getInbox() {
		return inbox;
	}

	public void setInbox(List<Message> inbox) {
		this.inbox = inbox;
	}
}