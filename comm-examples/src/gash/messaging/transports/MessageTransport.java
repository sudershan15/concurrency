package gash.messaging.transports;

import gash.messaging.Message;
import gash.messaging.Node;

public interface MessageTransport {

	/**
	 * this is only for testing - normally we would not expose this
	 * 
	 * @return
	 */
	Node[] getNodes();
	
	void addNode(Node node);

	void sendMessage(Message msg);

	void sendMessage(int fromNodeId, int toNodeId, String text);

	void broadcastMessage(int fromNodeId, String text);
}