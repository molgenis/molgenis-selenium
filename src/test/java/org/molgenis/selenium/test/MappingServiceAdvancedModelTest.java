package org.molgenis.selenium.test;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.mappingservice.AttributeMappingScreenAdvancedModel;
import org.molgenis.selenium.model.mappingservice.MappingProjectAddSourceDataModel;
import org.molgenis.selenium.model.mappingservice.MappingProjectImportDataModel;
import org.molgenis.selenium.model.mappingservice.MappingProjectOverviewModel;
import org.molgenis.selenium.model.mappingservice.TagWizardScreenModel;
import org.molgenis.selenium.util.RestApiV1Util;
import org.molgenis.selenium.util.SignUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes = JenkinsConfig.class)
public class MappingServiceAdvancedModelTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(MappingServiceAdvancedModelTest.class);

	private WebDriver driver;
	private AttributeMappingScreenAdvancedModel attributeMappingScreenAdvancedModel;
	private MappingProjectAddSourceDataModel mappingProjectAddSourceDataModel;
	private MappingProjectImportDataModel mappingProjectImportDataModel;
	private MappingProjectOverviewModel mappingProjectOverviewModel;
	private TagWizardScreenModel tagWizardScreenModel;

	@Value("${test.baseurl}")
	private String baseURL;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeSuite() throws InterruptedException
	{
		MolgenisClient molgenisClient = RestApiV1Util.createMolgenisClientApiV1(baseURL, LOG);

		this.driver = DriverType.FIREFOX.getDriver();
		this.attributeMappingScreenAdvancedModel = new AttributeMappingScreenAdvancedModel(driver, molgenisClient);
		this.mappingProjectAddSourceDataModel = new MappingProjectAddSourceDataModel(driver, molgenisClient);
		this.mappingProjectImportDataModel = new MappingProjectImportDataModel(driver, molgenisClient);
		this.mappingProjectOverviewModel = new MappingProjectOverviewModel(driver, molgenisClient);
		this.tagWizardScreenModel = new TagWizardScreenModel(driver);
	}

	@Test
	public void test() throws InterruptedException
	{
		this.deleteAllTestDataSetsFromTheApp();

		SignUtil.signIn(driver, baseURL, uid, pwd);

		this.importMappingServiceTestData();

		this.tagBodyMassIndex();

		this.testAddOneProject();

		this.testAddLifeLinesSourceToMappingProject();

		this.testAdvancedFunctionalitiesInAttributeMappingScreen();

		this.deleteAllTestDataSetsFromTheApp();

		SignUtil.signOut(driver);
	}

	private void tagBodyMassIndex() throws InterruptedException
	{
		tagWizardScreenModel.testBodyMassIndexAttributes();
	}

	public void deleteAllTestDataSetsFromTheApp() throws InterruptedException
	{
		mappingProjectImportDataModel.deleteMappingServiceTestData(uid, pwd);

	}

	public void importMappingServiceTestData() throws InterruptedException
	{
		mappingProjectImportDataModel.importMappingServiceTestData();
	}

	public void testAddOneProject() throws InterruptedException
	{
		mappingProjectOverviewModel.addOneMappingProject();
	}

	public void testAddLifeLinesSourceToMappingProject() throws InterruptedException
	{
		mappingProjectOverviewModel.openOneMappingProject();

		mappingProjectAddSourceDataModel.addLifeLinesSourceToMappingProject();
	}

	public void testAdvancedFunctionalitiesInAttributeMappingScreen() throws InterruptedException
	{
		mappingProjectOverviewModel.openOneMappingProject();

		attributeMappingScreenAdvancedModel.testGenderAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testHeightAlgorithmMeterSymbolLifeLines();

		attributeMappingScreenAdvancedModel.testHeightMeterAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testWeightGramAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testWeightKiloGramAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testTriglyceridesAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testFastingGlucoseAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testBodyMassIndexOneAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testBodyMassIndexTwoAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testBodyMassIndexThreeAlgorithmInLifeLines();

		attributeMappingScreenAdvancedModel.testPotatoConsumptionAlgorithmInLifeLines();
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		// Clear cookies
		this.driver.manage().deleteAllCookies();

		// Clear cookies
		this.driver.close();
	}

}
