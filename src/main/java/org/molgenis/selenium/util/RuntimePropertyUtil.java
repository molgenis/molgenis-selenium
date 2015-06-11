package org.molgenis.selenium.util;

import java.util.Map;

import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.data.rest.client.bean.QueryResponse;
import org.slf4j.Logger;

/**
 * This is a util for the use of the RuntimeProperty object in the Molgenis Selenium tests
 */
public class RuntimePropertyUtil
{
	/**
	 * Update a runtime property value
	 * 
	 * @param molgenisClient
	 * @param token
	 * @param name
	 *            String : The RuntimeProperty attribute name
	 * @param value
	 *            String : The RuntimeProperty attribute value
	 */
	public static void updateRuntimePropertyValue(MolgenisClient molgenisClient, String token, String name,
			String value, Logger logger)
	{
		logger.info("Update runtime property: " + name + " with the value: " + value);
		QueryResponse queryResponse = molgenisClient.queryEquals(token, "RuntimeProperty", "Name", name);
		Map<String, Object> item = queryResponse.getItems().get(0);
		String id = item.get("id").toString();
		if (item.get("id") instanceof Double)
		{
			id = "" + Math.round((Double) item.get("id"));
		}
		molgenisClient.update(token, "RuntimeProperty", id, "Value", value);
	}
}

