package org.molgenis.selenium.model.mappingservice.old;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.data.rest.client.bean.QueryResponse;
import org.molgenis.selenium.model.MenuModel;
import org.molgenis.selenium.util.RestApiV1Util;
import org.molgenis.selenium.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class MappingProjectImportDataModel extends AbstractMappingServiceAppModel
{
	public static final List<String> FILE_PATHS = Arrays.asList(
			"org/molgenis/selenium/mappingservice/mappingservice-test.xlsx",
			"org/molgenis/selenium/mappingservice/test-javascript_magma.xls",
			"org/molgenis/selenium/mappingservice/biobank_ontology_test.owl.zip",
			"org/molgenis/selenium/mappingservice/uo_test.owl.zip");

	public static final List<String> TARGET_RELATED_ENTITY_NAMES = Arrays.asList("HOP_selenium",
			"HOP_GENDER_Ref_selenium", "FOOD_POTATOES_Ref_selenium", "DIS_HBP_Ref_selenium");
	public static final List<String> LIFELINES_RELATED_ENTITY_NAMES = Arrays.asList("lifelines_test",
			"test_GENDER_Ref_test", "test_NUCHTER1_Ref_test", "test_FOOD59A1_Ref_test", "test_HEALTH351_Ref_test");
	public static final List<String> PREVEND_RELATED_ENTITY_NAMES = Arrays.asList("prevend_test", "test_SEX_Ref_test");
	public static final List<String> ONTOLOGY_RELATED_ENTITIY_NAMES = Arrays.asList("Ontology_OntologyTerm",
			"Ontology_OntologyTermDynamicAnnotation", "Ontology_OntologyTermNodePath", "Ontology_OntologyTermSynonym",
			"Ontology_Ontology");

	public static final String SCRIPT_ENTITY_NAME = "Script";
	public static final String PARAMETER_ENTITY_NAME = "ScriptParameter";
	public static final List<String> MAGMA_JAVASCRIPT_NAMES = Arrays.asList("bmi", "fasting glucose", "hypertension");
	public static final List<String> MAGMA_JAVASCRIPT_PARAMETERS = Arrays.asList("height", "weight", "fasting",
			"glucose", "Systolic Blood Pressure", "Diastolic Blood Pressure");

	public static final Logger LOG = LoggerFactory.getLogger(MappingProjectImportDataModel.class);

	public MappingProjectImportDataModel(WebDriver driver, MolgenisClient molgenisClient)
	{
		super(driver, molgenisClient);
	}

	public void deleteMappingServiceTestData(String uid, String pwd) throws InterruptedException
	{
		// Remove the existing integrated dataset
		String token = RestApiV1Util.loginRestApiV1(molgenisClient, uid, pwd, LOG);
		deleteDataAndMetadata(token, INTEGRATED_DATASET_ENTITY_NAME);

		// Remove the existing mappingProject
		deleteExistingMappingProject(token);
		Thread.sleep(5000);

		// Remove the target entity
		TARGET_RELATED_ENTITY_NAMES.stream().forEachOrdered(e -> this.deleteDataAndMetadata(token, e));
		Thread.sleep(5000); // Important!

		// Remove lifelines data
		LIFELINES_RELATED_ENTITY_NAMES.stream().forEachOrdered(e -> this.deleteDataAndMetadata(token, e));
		Thread.sleep(5000); // Important!

		// Remove prevend data
		PREVEND_RELATED_ENTITY_NAMES.stream().forEachOrdered(e -> this.deleteDataAndMetadata(token, e));
		Thread.sleep(5000); // Important!

		// Remove ontologies
		ONTOLOGY_RELATED_ENTITIY_NAMES.stream().forEachOrdered(e -> this.deleteData(token, e));
		Thread.sleep(5000); // Important!

		// Remove magma javascript
		deleteMagmaJavaScriptData(token);

		Thread.sleep(5000); // Important!

		deleteMagmaJavaScriptParameters(token);

		molgenisClient.logout(token);
	}

	public void importMappingServiceTestData()
	{
		FILE_PATHS.forEach(this::importDataForOneFile);
	}

	private void importDataForOneFile(String filePath)
	{
		try
		{
			MenuModel.openPageByClickOnMenuItem("Upload", driver);
			// Step 1: UploadFile
			this.uploadFile(filePath);

			this.next();

			// Step 2: Options
			this.next();

			// Step 3: packages
			this.next();

			// Step 4: validation
			this.next();

			SeleniumUtils.waitForElement(By.cssSelector("li.next:not(.disabled)"), driver);

			// Success message header
			Assert.assertEquals(this.isImportedSuccess(), true);

			// Step 5
			this.finish();

			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{
			LOG.error("Importing " + filePath + " failed");
		}
	}

	private void finish() throws InterruptedException
	{
		By finish = By.cssSelector("li.next:not(.disabled)");
		SeleniumUtils.waitForElement(finish, driver);

		By click = By.id("wizard-finish-button");
		SeleniumUtils.waitForElement(click, driver);
		driver.findElement(click).click();

		// Wait until the upload page is completely loaded
		By selectAFile = By.name("upload");
		SeleniumUtils.waitForElement(selectAFile, driver);
	}

	private boolean isImportedSuccess() throws InterruptedException
	{
		By heading = By.cssSelector("#message-panel .panel-heading");
		SeleniumUtils.waitForElement(heading, driver);
		String innerHTML = driver.findElement(heading).getAttribute("innerHTML");
		return "Import success".equals(innerHTML);
	}

	private void next() throws InterruptedException
	{
		By next = By.id("wizard-next-button");
		driver.findElement(next).click();
		// To accommodate for Ajax stuff
		Thread.sleep(2000);
	}

	private void uploadFile(String relativePath) throws InterruptedException
	{
		By selectAFile = By.name("upload");
		SeleniumUtils.waitForElement(selectAFile, driver);

		// http://stackoverflow.com/questions/5610256/file-upload-using-selenium-webdriver-and-java
		File file;
		try
		{
			file = new File(getClass().getClassLoader().getResource(relativePath).toURI());
		}
		catch (Exception ex)
		{
			file = new File("test-classes/" + relativePath);
		}
		assertTrue(file.exists());

		SeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(1).active"), driver);

		driver.findElement(selectAFile).sendKeys(file.getAbsolutePath());
	}

	private void deleteDataAndMetadata(String token, String entityFullName)
	{
		try
		{
			molgenisClient.get(token, entityFullName);
			molgenisClient.deleteMetadata(token, entityFullName);
			LOG.info("Delete " + entityFullName);
		}
		catch (Exception e)
		{
			LOG.info(e.getMessage() + " " + entityFullName + " is not deleted");
		}
	}

	private void deleteData(String token, String entityFullName)
	{
		try
		{
			QueryResponse queryResponse = molgenisClient.get(token, entityFullName);
			Set<String> exisingEntityIds = queryResponse.getItems().stream().map(this::getEntityId)
					.collect(Collectors.toSet());
			exisingEntityIds.forEach(id -> molgenisClient.delete(token, entityFullName, id));
			LOG.info("Delete all data for: " + entityFullName);
		}
		catch (Exception e)
		{
			LOG.info(e.getMessage() + " " + entityFullName + " is not deleted");
		}
	}

	private void deleteExistingMappingProject(String token)
	{
		try
		{
			QueryResponse queryResponse = molgenisClient.get(token, MAPPING_PROJECT_ENTITY_NAME);
			Set<String> exisingMappingProjectHrefs = queryResponse.getItems().stream()
					.filter(this::isItemExistingMappingProject).map(this::getEntityId).collect(Collectors.toSet());
			exisingMappingProjectHrefs.forEach(id -> molgenisClient.delete(token, MAPPING_PROJECT_ENTITY_NAME, id));
			LOG.info("Delete all test mapping projects");
		}
		catch (Exception e)
		{
			LOG.info(e.getMessage() + " " + SCRIPT_ENTITY_NAME + " is not deleted");
		}
	}

	private void deleteMagmaJavaScriptData(String token)
	{
		try
		{
			QueryResponse queryResponse = molgenisClient.get(token, SCRIPT_ENTITY_NAME);
			Set<String> magmaJavaScriptEntityIds = queryResponse.getItems().stream().filter(this::isItemMagmaJavaScript)
					.map(this::getEntityId).collect(Collectors.toSet());
			magmaJavaScriptEntityIds.forEach(id -> molgenisClient.delete(token, SCRIPT_ENTITY_NAME, id));
			LOG.info("Delete all magma javascript entities");
		}
		catch (Exception e)
		{
			LOG.info(e.getMessage() + " " + SCRIPT_ENTITY_NAME + " is not deleted");
		}
	}

	private void deleteMagmaJavaScriptParameters(String token)
	{
		try
		{
			QueryResponse queryResponse1 = molgenisClient.get(token, PARAMETER_ENTITY_NAME);
			Set<String> parameterEntityIds = queryResponse1.getItems().stream().filter(this::isItemMagmaJavaParameter)
					.map(this::getEntityId).collect(Collectors.toSet());
			parameterEntityIds.forEach(id -> molgenisClient.delete(token, PARAMETER_ENTITY_NAME, id));
			LOG.info("Delete all magma javascript parameter entities");
		}
		catch (Exception e)
		{
			LOG.info(e.getMessage() + " " + PARAMETER_ENTITY_NAME + " is not deleted");
		}
	}

	private String getEntityId(Map<String, Object> map)
	{
		String href = map.get("href").toString();
		String[] split = href.split("/");
		return split[split.length - 1].replaceAll("%20", " ");
	}

	private boolean isItemExistingMappingProject(Map<String, Object> map)
	{
		return map.containsKey("name") && MAPPING_PROJECT_NAME.equalsIgnoreCase(map.get("name").toString());
	}

	private boolean isItemMagmaJavaScript(Map<String, Object> map)
	{
		return map.containsKey("name") && MAGMA_JAVASCRIPT_NAMES.contains(map.get("name").toString());
	}

	private boolean isItemMagmaJavaParameter(Map<String, Object> map)
	{
		return map.containsKey("name") && MAGMA_JAVASCRIPT_PARAMETERS.contains(map.get("name").toString());
	}

}