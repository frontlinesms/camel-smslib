package serial.mock;

public class CommPortIdentifier {
	public static final int PORT_SERIAL = 0; // 0 should be default value for getPortType() called on mocks
	public static final int PORT_PARALLEL = 1;
	
	public static serial.CommPortIdentifier getCommPortIdentifier(String portName) {
		return MockSerial.getIdentifier(portName);
	}
}
