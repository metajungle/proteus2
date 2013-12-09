/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.proteus.queryset;

import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.iai.proteus.common.model.sos_v1.GetObservationRequest;
import com.iai.proteus.common.sos.data.SensorData;
import com.iai.proteus.common.sos.exception.ExceptionReportException;
import com.iai.proteus.common.sos.util.SosUtil;

/**
 * Cache for expensive, remote queries for observation data.
 *
 * @author eclark
 */
public class GetObservationCache
{
	private Map<String, SensorData> cache;

	private GetObservationCache()
	{
		this(100);
	}

	private GetObservationCache(final int cache_size)
	{
		cache = new LinkedHashMap<String, SensorData>(cache_size, .75f, true)
		{
		    public boolean removeEldestEntry(Map.Entry<String, SensorData> eldest)
		    {
		        return size() > cache_size;
		    }
		};
	}

	public void clear()
	{
		cache.clear();
	}

	/**
	 *
	 * @param service
	 * @param request
	 * @return
	 * @throws ExceptionReportException
	 * @throws SocketTimeoutException
	 */
	public SensorData getObservationData(String service, GetObservationRequest request)
			throws ExceptionReportException, SocketTimeoutException
	{
		String key = getRequestUID(service, request);

		if (cache.containsKey(key))
		{
			return cache.get(key);
		}
		else
		{
			int timeoutConnection = 10000;
			int timeoutRead = 10000;

			SensorData data =
					SosUtil.getObservationData(service, request,
							timeoutConnection, timeoutRead);

			// store the response in the cache
			cache.put(key, data);

			return data;
		}
	}

	private String getRequestUID(String service, GetObservationRequest request)
	{
		String UID = service + " - " + request.getXmlRequest();
		return UID;
	}

	/*
	 * Singleton holder
	 *
	 */

	private static class SingletonHolder {
		public static final GetObservationCache instance = new GetObservationCache();
	}

	public static GetObservationCache getInstance() {
		return SingletonHolder.instance;
	}
}
