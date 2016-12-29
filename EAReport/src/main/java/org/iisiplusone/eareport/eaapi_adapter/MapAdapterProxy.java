package org.iisiplusone.eareport.eaapi_adapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iisiplusone.eareport.generator.EADiagramImageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapAdapterProxy implements InvocationHandler {

	private static final Logger logger = LoggerFactory.getLogger(MapAdapterProxy.class);

	private Object innerObject;
	
	private final Map<String, Object> getValues = new HashMap<>();

	private boolean loaded = false;

	private PropertyCollector collector = new PropertyCollector();
	private ObjectWrapper wrapper = new ObjectWrapper();


	private static final List<String> supportedMethods = Arrays
			.asList(new String[] { "size", "get", "isEmpty", "entrySet", "keySet", "containsKey" });

	private MapAdapterProxy(Object innerObject){
		this.innerObject = innerObject;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> instance(Object wrap) {
		logger.debug("Creating proxy for "+wrap.getClass()+" : "+wrap);
		return (Map<String,Object>) Proxy.newProxyInstance(MapAdapterProxy.class.getClassLoader(),
				new Class[] { Map.class }, new MapAdapterProxy(wrap));
	}
	
	@Override
	public String toString() {
		return String.valueOf(innerObject);
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		logger.debug("Intercepting method "+method.getName()+" ( "+args+" ) ");
		
		if (supportedMethods.contains(method.getName())){
			this.ensureLoaded();
			return method.invoke(this.getValues, args);
			
		} else {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private void ensureLoaded() {
		if(!loaded){
			logger.debug("Loading data for "+this.innerObject);
			Map<String,Object> memberProperties = this.collector.collectProperties(this.innerObject);			
			for( String key : memberProperties.keySet()){
				logger.debug("Wrapping value for key "+key);
				this.getValues.put(key, this.wrapper.wrap(memberProperties.get(key) ) );
			}
			this.loaded = true;
			logger.debug("Done loading data for "+this.innerObject);
		}		
	}

}
