package gash.gossip.util;

import java.io.Serializable;

public class Payload implements Serializable {
	private String message;
	private int maxHops;

	public String toString() {
		return message + " (" + maxHops + ")";
	}

	public void decrementHops() {
		if (maxHops > 0)
			maxHops--;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMaxHops() {
		return maxHops;
	}

	public void setMaxHops(int maxHops) {
		this.maxHops = maxHops;
	}
}
