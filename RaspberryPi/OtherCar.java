import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class OtherCar extends Car {

	private Socket sock;

	public OtherCar(String num, Point departure, Point destination) {
		super(num, departure, destination);
	}
	
	public OtherCar(CarAttribute attr, Point departure, Point destination) {
		super(attr, departure, destination);
	}

	public OtherCar(CarAttribute attr, Point destination) {
		super(attr, destination);
	}

	public void startConnectedCar_Client(String serv_ip) throws Exception {
		sock = new Socket(serv_ip, Environment._PORT_NUM);

		while (true) {
			int reqCode = readReqCode();
			Object obj = null;
			switch (reqCode) {
			case Environment._RQ_FIRST_LEG:
				System.out.println("Send First Leg");
				obj = route.get(0);
				break;
			case Environment._RQ_FULL_LEGS:
				System.out.println("Send Full Legs");
				obj = route;
				break;
			case Environment._RQ_INFO:
				System.out.println("Send Attribute");
				obj = attr;
				break;
			}
			writePacket(obj);
		}
	}


	private int readReqCode() throws Exception {
		ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
		Packet pk = (Packet) in.readObject();
		return pk.getRequestCode();
	}


	private void writePacket(Object obj) throws Exception {
		ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());

		int chnType = 0;
		if (obj instanceof Point) {
			chnType = Environment._CCH;

		} else if (obj instanceof ArrayList<?>) {
			chnType = Environment._SCH;
			
		} else if (obj instanceof CarAttribute){
			chnType = Environment._CCH;
		}
		Packet pk = new Packet(chnType, Environment._RQ_NONE, obj);
		out.writeObject(pk);
	}
}