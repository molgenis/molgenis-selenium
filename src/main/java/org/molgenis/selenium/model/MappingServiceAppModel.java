package org.molgenis.selenium.model;

import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.mappingservice.AttributeMappingScreenModel;
import org.molgenis.selenium.model.mappingservice.MappingProjectAddSourceDataModel;
import org.molgenis.selenium.model.mappingservice.MappingProjectImportDataModel;
import org.molgenis.selenium.model.mappingservice.MappingProjectOverviewModel;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingServiceAppModel
{
	private MappingProjectOverviewModel mappingProjectOverviewModel;
	private MappingProjectImportDataModel mappingProjectImportDataModel;
	private MappingProjectAddSourceDataModel mappingProjectAddSourceDataModel;
	private AttributeMappingScreenModel attributeMappingScreenModel;

	public static final Logger LOG = LoggerFactory.getLogger(MappingServiceAppModel.class);

	public static final String MAPPING_SERVICE_BASE_URL = "/menu/dataintegration/mappingservice";
	public static final String MAPPING_PROJECT_NAME = "mapping_project_test";
	public static final String MAPPING_PROJECT_ENTITY_NAME = "MappingProject";
	public static final String TARGET_ENTITY_NAME = "HOP_selenium";
	public static final String INTEGRATED_DATASET_ENTITY_NAME = "selenium_test_integrated_data";

	public MappingServiceAppModel(WebDriver webDriver, MolgenisClient molgenisClient)
	{
		this.mappingProjectOverviewModel = new MappingProjectOverviewModel(webDriver);
		this.mappingProjectImportDataModel = new MappingProjectImportDataModel(webDriver, molgenisClient);
		this.mappingProjectAddSourceDataModel = new MappingProjectAddSourceDataModel(webDriver);
		this.attributeMappingScreenModel = new AttributeMappingScreenModel(webDriver);
	}

	public void removeTestMappingProject() throws InterruptedException
	{
		mappingProjectOverviewModel.removeTestMappingProject();
	}

	public void deleteMappingServiceTestData(String uid, String pwd) throws InterruptedException
	{
		mappingProjectImportDataModel.deleteMappingServiceTestData(uid, pwd);
	}

	public void importMappingServiceTestData()
	{
		mappingProjectImportDataModel.importMappingServiceTestData();
	}

	public void cancelAddMappingProject() throws InterruptedException
	{
		mappingProjectOverviewModel.cancelAddMappingProject();
	}

	public void addOneMappingProjectWithoutName() throws InterruptedException
	{
		mappingProjectOverviewModel.addOneMappingProjectWithoutName();
	}

	public void addOneMappingProject() throws InterruptedException
	{
		mappingProjectOverviewModel.addOneMappingProject();
	}

	public void addLifeLinesSourceToMappingProject() throws InterruptedException
	{
		mappingProjectAddSourceDataModel.addLifeLinesSourceToMappingProject();
	}

	public void cancelAddLifeLinesSourceToMappingProject() throws InterruptedException
	{
		mappingProjectAddSourceDataModel.cancelAddLifeLinesSourceToMappingProject();
	}

	public void cancelRemoveLifeLinesSourceToMappingProject() throws InterruptedException
	{
		mappingProjectAddSourceDataModel.cancelRemoveLifeLinesSourceToMappingProject();
	}

	public void removeLifeLinesSourceToMappingProject() throws InterruptedException
	{
		mappingProjectAddSourceDataModel.removeLifeLinesSourceToMappingProject();
	}

	public void removeFastingGlucoseAttributeForLifeLinesSource() throws InterruptedException
	{
		mappingProjectAddSourceDataModel.removeFastingGlucoseAttributeForLifeLinesSource();
	}

	public void cancelRemoveFastingGlucoseAttributeForLifeLinesSource() throws InterruptedException
	{
		mappingProjectAddSourceDataModel.cancelRemoveFastingGlucoseAttributeForLifeLinesSource();
	}

	public void integrateSourceData() throws InterruptedException
	{
		mappingProjectAddSourceDataModel.integrateSourceData();
	}

	public void openOneMappingProject() throws InterruptedException
	{
		attributeMappingScreenModel.openOneMappingProject();
	}

	public void clickGenderAttributeForLifeLinesSource() throws InterruptedException
	{
		attributeMappingScreenModel.clickGenderAttributeForLifeLinesSource();
		attributeMappingScreenModel.goBackToMappingOneMappingProject();
	}

	public void clickFastingGlucoseAttributeForLifeLinesSource() throws InterruptedException
	{
		attributeMappingScreenModel.clickFastingGlucoseAttributeForLifeLinesSource();
		attributeMappingScreenModel.goBackToMappingOneMappingProject();
	}
}
