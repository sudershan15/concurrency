package gash.gossip.util;

import java.io.Serializable;

public class Payload implements Serializable {
	private String message;
	private long ttl;

	public String toString() {
		return message + " (" + (ttl - System.currentTimeMillis()) + ")";
	}

	public boolean isViable() {
		return (ttl - System.currentTimeMillis()) > 0;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

}
