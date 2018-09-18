package vn.itt.msales.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class ArrayListUtils {

	protected static Logger logger = Logger.getLogger(ArrayListUtils.class
			.getName());
	
	/**
	 * Writes all params for report
	 */
	public static Map<String, Object> getAllProperties(final Object object) {
		final Map<String, Object> allProperties = new HashMap<String, Object>();
		try {
			final BeanInfo beanInfo = Introspector.getBeanInfo(object
					.getClass());
			for (final PropertyDescriptor descriptor : beanInfo
					.getPropertyDescriptors()) {
				try {
					final Object propertyValue = descriptor.getReadMethod()
							.invoke(object);
					if (propertyValue != null) {
						allProperties.put(descriptor.getName(), propertyValue);
					}
				} catch (final IllegalArgumentException e) {
					logger.error("Method getAllProperties error: "+ e.getMessage());
				} catch (final IllegalAccessException e) {
					logger.error("Method getAllProperties error: "+ e.getMessage());
				} catch (final InvocationTargetException e) {
					logger.error("Method getAllProperties error: "+ e.getMessage());
				}
			}
		} catch (final IntrospectionException e) {
			logger.error("Unable to process download");
			throw new RuntimeException(e);
		}
		return allProperties;
	}

}
