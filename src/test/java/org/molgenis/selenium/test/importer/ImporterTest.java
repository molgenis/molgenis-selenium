package org.molgenis.selenium.test.importer;

import java.io.File;
import java.io.IOException;

import org.molgenis.selenium.model.importer.ImporterModel;
import org.molgenis.selenium.model.importer.ImporterModel.EntitiesOptions;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ImporterTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(ImporterTest.class);

	private ImporterModel model;

	@BeforeMethod
	public void beforeMethod()
	{
		model = homepage.menu().selectImporter();
	}
	
	@Test
	public void xlsx() throws IOException, InterruptedException
	{
		LOG.info("Test XLSX upload...");
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");
		File xlsxFile = ImporterModel.getFile("org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx");

		LOG.info("Import XLSX file in ADD mode...");
		model.importFile(xlsxFile, EntitiesOptions.ADD);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 TypeTestRef entities.\nImported 38 org_molgenis_test_TypeTest entities.");
		model.finish();

		LOG.info("Import XLSX file in ADD_UPDATE mode...");
		model.importFile(xlsxFile, EntitiesOptions.ADD_UPDATE);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 TypeTestRef entities.\nImported 38 org_molgenis_test_TypeTest entities.");
		model.finish();

		LOG.info("Import XLSX file in UPDATE mode...");
		model.importFile(xlsxFile, EntitiesOptions.UPDATE);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 TypeTestRef entities.\nImported 38 org_molgenis_test_TypeTest entities.");
		model.finish();

		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");

	}

	@Test
	public void csvZip() throws IOException, InterruptedException
	{
		tryDeleteEntities("org_molgenis_test_TypeTestCSV", "org_molgenis_test_TypeTestRefCSV",
				"org_molgenis_test_PersonCSV", "org_molgenis_test_LocationCSV");
		File csvZipFile = ImporterModel.getFile("org/molgenis/selenium/emx/csv.zip/emx_all_datatypes_csv.zip");
		LOG.info("Import CSV Zipfile in ADD mode...");
		model.importFile(csvZipFile, EntitiesOptions.ADD);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 org_molgenis_test_TypeTestRefCSV entities.\nImported 38 org_molgenis_test_TypeTestCSV entities.");
		model.finish();

		LOG.info("Import CSV Zipfile in ADD_UPDATE mode...");
		model.importFile(csvZipFile, EntitiesOptions.ADD_UPDATE);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 org_molgenis_test_TypeTestRefCSV entities.\nImported 38 org_molgenis_test_TypeTestCSV entities.");
		model.finish();

		LOG.info("Import CSV Zipfile in UPDATE mode...");
		model.importFile(csvZipFile, EntitiesOptions.UPDATE);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 org_molgenis_test_TypeTestRefCSV entities.\nImported 38 org_molgenis_test_TypeTestCSV entities.");
		model.finish();
		tryDeleteEntities("org_molgenis_test_TypeTestCSV", "org_molgenis_test_TypeTestRefCSV",
				"org_molgenis_test_PersonCSV", "org_molgenis_test_LocationCSV");
	}

}
