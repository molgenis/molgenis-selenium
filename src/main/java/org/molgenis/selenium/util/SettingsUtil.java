package org.molgenis.selenium.util;

import org.molgenis.data.rest.client.MolgenisClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is a util for the use of the RuntimeProperty object in the Molgenis Selenium tests
 */
public class SettingsUtil
{
	@Autowired
	private MolgenisClient molgenisClient;

	/**
	 * Update an application setting
	 * 
	 * @param molgenisClient
	 * @param token
	 * @param name
	 *            String : The RuntimeProperty attribute name
	 * @param value
	 *            String : The RuntimeProperty attribute value
	 */
	public void updateDataExplorerSettings(String token, String attributeName, Object value, Logger logger)
	{
		logger.info("Update Data explorer setting " + attributeName + " with the value: " + value);
		molgenisClient.update(token, "settings_dataexplorer", "dataexplorer", attributeName, value);
	}
}
