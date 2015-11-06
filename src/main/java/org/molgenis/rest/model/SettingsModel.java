package org.molgenis.rest.model;

import org.molgenis.data.rest.client.MolgenisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is a util for the use of the settings in the Molgenis Selenium tests
 */
public class SettingsModel
{
	private static final Logger LOG = LoggerFactory.getLogger(SettingsModel.class);
	@Autowired
	private final MolgenisClient molgenisClient;
	private final String token;

	public SettingsModel(MolgenisClient client, String token)
	{
		this.molgenisClient = client;
		this.token = token;
	}

	/**
	 * Update a DataExplorer setting
	 * 
	 * @param molgenisClient
	 * @param token
	 */
	public void updateDataExplorerSettings(String attributeName, Object value)
	{
		LOG.info("Update Data explorer setting {} with the value {}.", attributeName, value);
		molgenisClient.update(token, "settings_dataexplorer", "dataexplorer", attributeName, value);
	}
}
