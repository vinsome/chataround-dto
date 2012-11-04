package com.service.chataroundpush.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.util.CollectionUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;

public class GenericDatastore {
	public static final int MULTICAST_SIZE = 1000;
	private static final String DEVICE_APP_ID_PROPERTY = "appId";

	private static final FetchOptions DEFAULT_FETCH_OPTIONS = FetchOptions.Builder
			.withPrefetchSize(MULTICAST_SIZE).chunkSize(MULTICAST_SIZE);

	private static final Logger logger = Logger.getLogger(GenericDatastore.class
			.getName());
	
	private static final DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	private GenericDatastore() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Registers a device.
	 * 
	 * @param regId
	 *            device's registration id.
	 */
	public static String insert(String deviceType, String appId,
			Map<String, String> params) {

		logger.info("Registering type " + deviceType + " to appId " + appId +" with param["+params+"]");
		String response;
		Transaction txn = datastore.beginTransaction();
		try {
			Entity entity = findEntityByAppIdAndParams(deviceType,appId, params);
			if (entity != null) {
				logger.fine(appId + " params=["+params+"] is already registered; ignoring.");
				response = "insert.entity.exists";
				return response;
			}
			entity = new Entity(deviceType);
			entity.setProperty(DEVICE_APP_ID_PROPERTY, appId);
			if(!CollectionUtils.isEmpty(params)) {
				Iterator<String> it = params.keySet().iterator();
					while(it.hasNext()){
						String param = it.next();
						entity.setProperty(param, params.get(param));
					}
			}
			datastore.put(entity);
			txn.commit();
			response="insert.entity.ok";
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				response ="insert.entity.ko";
			}
		}
		return response;
	}
	/**
	 * Registers a device.
	 * 
	 * @param regId
	 *            device's registration id.
	 */
	public static String delete(String deviceType, String appId,
			Map<String, String> params) {

		logger.info("Deleting type " + deviceType + " to appId " + appId +" with param["+params+"]");
		String response;
		Transaction txn = datastore.beginTransaction();
		try {
			Entity entity = findEntityByAppIdAndParams(deviceType,appId, params);
			if (entity != null) {
				logger.fine(appId + " params=["+params+"] is already found...deleting");
		        Key key = entity.getKey();
		        datastore.delete(key);
				txn.commit();
						        
				response = "delete.entity.ok";
			}else{
				txn.commit();
				response = "delete.entity.notfound";
			}

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				response ="delete.entity.ko";
			}
		}
		return response;
	}
	/**
	 * Gets the number of total devices.
	 */
	public static int getTotalEntities(String deviceType) {
		Transaction txn = datastore.beginTransaction();
		try {
			Query query = new Query(deviceType).setKeysOnly();
			List<Entity> allKeys = datastore.prepare(query).asList(
					DEFAULT_FETCH_OPTIONS);
			int total = allKeys.size();
			logger.fine("Total number of devices: " + total);
			txn.commit();
			return total;
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	/**
	 * Gets all registered devices.
	 */
	public static <T> List<T> getListOfEntitiesProperty(String deviceType, String appId,String propertyName,
			Map<String, String> params) {
		List<T> devices;
		Transaction txn = datastore.beginTransaction();
		try {
			Query query = new Query(deviceType)
			.addFilter(DEVICE_APP_ID_PROPERTY, FilterOperator.EQUAL,
							appId);
			// add extra params to register the device
			if (!CollectionUtils.isEmpty(params)) {
				Iterator<String> it = params.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					query.addFilter(key, FilterOperator.EQUAL, params.get(key));
				}
			}

			Iterable<Entity> entities = datastore.prepare(query).asIterable(
					DEFAULT_FETCH_OPTIONS);
			devices = new ArrayList<T>();

			for (Entity entity : entities) {
				T device = (T) entity
						.getProperty(propertyName);
				devices.add(device);
			}
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return devices;
	}
	
	private static Entity findEntityByAppIdAndParams(String deviceType, String appId,
			Map<String, String> params) {
		Query query = new Query(deviceType)
				.addFilter(DEVICE_APP_ID_PROPERTY,FilterOperator.EQUAL, appId);
		// add extra params to register the device
		if (!CollectionUtils.isEmpty(params)) {
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				query.addFilter(key, FilterOperator.EQUAL, params.get(key));
			}
		}
		PreparedQuery preparedQuery = datastore.prepare(query);
		List<Entity> entities = preparedQuery.asList(DEFAULT_FETCH_OPTIONS);
		Entity entity = null;
		if (!entities.isEmpty()) {
			entity = entities.get(0);
		}
		int size = entities.size();
		if (size > 0) {
			logger.severe("Found " + size + " entities for appId " + appId
					+ ": " + entities);
		}
		return entity;
	}


}
