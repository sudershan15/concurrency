package gash.demo.ws.client;


public class DemoClient {
	public static final String sEndPoint = "http://localhost:8080/axis2/services/Demo";

	private String endPoint;
	//private ws.demo.Demo stub;

	/**
	 * use default URL (endpoint = localhost)
	 */
	public DemoClient() {
		this(sEndPoint);
	}

	/**
	 * Specify URL (endpoint)
	 * 
	 * @param endPoint
	 */
	public DemoClient(String endPoint) {
		this.endPoint = endPoint;
	}

	public void demo(String name) {
		try {
			ws.demo.Demo svr = connect();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * @return
	 * @throws Exception
	 */
	private synchronized ws.demo.DemoStub connect() throws Exception {
		if (stub == null) {
			stub = new ws.demo.DemoStub(endPoint);
		}

		return stub;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DemoClient demo =
			// new DemoClient();
			new DemoClient("http://centosvr:8080/axis2/services/Demo");
			demo.demo("hello");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
