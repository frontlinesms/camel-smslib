package serial.mock;

import java.util.HashMap;
import java.util.Map;

import serial.SerialClassFactory;

/** This class is not thread-safe and should not be used in concurrent tests. FIXME make sure the POM is not set to run concurrent unit tests. */
public class MockSerial {
	private static final Map<String, serial.CommPortIdentifier> identifiers = new HashMap<String, serial.CommPortIdentifier>();
	
	public static void setIdentifier(String portName, serial.CommPortIdentifier identifier) {
		identifiers.put(portName, identifier);
	}

	public static serial.CommPortIdentifier getIdentifier(String portName) {
		return identifiers.get(portName);
	}

	public static void init() {
		SerialClassFactory.init("serial.mock"); // FIXME make serial mock a separate maven package, include it as a test dependency, and possibly make the serial package attempt to load it before any other package
		assert(SerialClassFactory.getInstance().getSerialPackageName().equals("serial.mock"));
	}

	public static void clearIdentifiers() {
		identifiers.clear();
	}
}
