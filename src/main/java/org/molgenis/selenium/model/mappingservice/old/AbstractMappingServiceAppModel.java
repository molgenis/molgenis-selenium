package org.molgenis.selenium.model.mappingservice.old;

import java.util.Objects;

import org.molgenis.data.rest.client.MolgenisClient;
import org.openqa.selenium.WebDriver;

public abstract class AbstractMappingServiceAppModel
{
	public static final String MAPPING_SERVICE_BASE_URL = "/menu/dataintegration/mappingservice";
	public static final String MAPPING_PROJECT_NAME = "mapping_project_test";
	public static final String MAPPING_PROJECT_ENTITY_NAME = "MappingProject";
	public static final String TARGET_ENTITY_NAME = "HOP_selenium";
	public static final String INTEGRATED_DATASET_ENTITY_NAME = "selenium_test_integrated_data";

	protected final WebDriver driver;
	protected final MolgenisClient molgenisClient;

	public AbstractMappingServiceAppModel(WebDriver driver, MolgenisClient molgenisClient)
	{
		this.driver = Objects.requireNonNull(driver);
		this.molgenisClient = Objects.requireNonNull(molgenisClient);
	}
}
