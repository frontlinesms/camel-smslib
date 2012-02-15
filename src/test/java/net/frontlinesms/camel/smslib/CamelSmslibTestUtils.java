package net.frontlinesms.camel.smslib;

import java.lang.reflect.Field;

public class CamelSmslibTestUtils {
	public static void inject(Object o, String fieldName, Object value) {
		try {
			Field field = o.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(o, value);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
