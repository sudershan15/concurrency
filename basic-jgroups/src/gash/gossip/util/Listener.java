package gash.gossip.util;

public interface Listener {
	public void listenTo(Gossip channel);
	public void detach();
}
