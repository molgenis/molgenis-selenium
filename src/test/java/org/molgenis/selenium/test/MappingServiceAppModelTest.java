package org.molgenis.selenium.test;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.MappingServiceAppModel;
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
public class MappingServiceAppModelTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(MappingServiceAppModelTest.class);

	private WebDriver driver;
	private MappingServiceAppModel model;
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
		driver = DriverType.FIREFOX.getDriver();
		MolgenisClient molgenisClient = RestApiV1Util.createMolgenisClientApiV1(baseURL, LOG);
		model = new MappingServiceAppModel(driver, molgenisClient);
		tagWizardScreenModel = new TagWizardScreenModel(driver);
	}

	@Test
	public void testImportMappingServiceData() throws InterruptedException
	{
		model.deleteMappingServiceTestData(uid, pwd);

		SignUtil.signIn(driver, baseURL, uid, pwd);

		model.importMappingServiceTestData();

		SignUtil.signOut(this.driver);
	}

	@Test
	public void testTagWizard() throws InterruptedException
	{
		SignUtil.signIn(driver, baseURL, uid, pwd);

		tagWizardScreenModel.tagAllBodyMassIndexAttributesManually();

		SignUtil.signOut(this.driver);
	}

	@Test
	public void testAddOneProject() throws InterruptedException
	{
		SignUtil.signIn(driver, baseURL, uid, pwd);

		model.addOneMappingProject();

		model.cancelAddMappingProject();

		model.addOneMappingProjectWithoutName();

		model.removeTestMappingProject();

		model.addOneMappingProject();

		SignUtil.signOut(this.driver);
	}

	@Test
	public void testAddLifeLinesSourceToMappingProject() throws InterruptedException
	{
		SignUtil.signIn(driver, baseURL, uid, pwd);

		model.openOneMappingProject();

		model.cancelAddLifeLinesSourceToMappingProject();

		model.addLifeLinesSourceToMappingProject();

		model.cancelRemoveLifeLinesSourceToMappingProject();

		model.removeLifeLinesSourceToMappingProject();

		model.addLifeLinesSourceToMappingProject();

		SignUtil.signOut(this.driver);
	}

	@Test
	public void testBasicFunctionalitiesInAttributeMappingScreen() throws InterruptedException
	{
		SignUtil.signIn(driver, baseURL, uid, pwd);

		model.openOneMappingProject();

		model.clickGenderAttributeForLifeLinesSource();

		model.cancelRemoveFastingGlucoseAttributeForLifeLinesSource();

		model.removeFastingGlucoseAttributeForLifeLinesSource();

		model.clickFastingGlucoseAttributeForLifeLinesSource();

		SignUtil.signOut(this.driver);
	}

	@Test
	public void testIntegrateDataForLifeLines() throws InterruptedException
	{
		SignUtil.signIn(driver, baseURL, uid, pwd);

		model.openOneMappingProject();

		model.integrateSourceData();

		SignUtil.signOut(this.driver);
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
