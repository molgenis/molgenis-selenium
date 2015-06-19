package org.molgenis.selenium.test;

import java.util.Arrays;
import java.util.List;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.DataExplorerAppModel;
import org.molgenis.selenium.model.UploadAppModel;
import org.molgenis.selenium.model.UploadAppModel.EntitiesOptions;
import org.molgenis.selenium.util.SignUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes = JenkinsConfig.class)
public class DataExplorerAppTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerAppTest.class);
	private DataExplorerAppModel model;
	private UploadAppModel uploadAppModel;
	private WebDriver driver;

	@Value("${test.baseurl}")
	private String baseURL;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		this.driver = DriverType.FIREFOX.getDriver();
		this.model = new DataExplorerAppModel(this.driver);
		this.uploadAppModel = new UploadAppModel(this.driver);
	}

	@Test
	public void test() throws InterruptedException
	{
		this.driver.get(baseURL);
		SignUtil.signIn(this.driver, baseURL, uid, pwd, LOG);

		// Open upload app
		uploadAppModel.open();
		uploadAppModel.uploadXlsxEmxAllDatatypes(EntitiesOptions.ADD_UPDATE, LOG);

		// Test 1
		model.open();
		model.selectEntity("TypeTest");
		Assert.assertEquals(model.getSelectedEntityTitle(), "TypeTest");

		WebElement next = model.getNext();
		Assert.assertEquals(next.getText(), "Next");

		next.click();
		next.click();

		WebElement previous = model.getPrevious();
		Assert.assertEquals(previous.getText(), "Previous");

		// Test 2
		model.selectEntityFromUrl(baseURL, "org_molgenis_test_TypeTest");
		Assert.assertEquals("TypeTest", model.getSelectedEntityTitle());

		WebElement next2 = model.getNext();
		Assert.assertEquals(next2.getText(), "Next");

		// Test 3
		List<String> entitiesNames = Arrays.asList("org_molgenis_test_TypeTest", "org_molgenis_test_TypeTestRef",
				"org_molgenis_test_Person", "org_molgenis_test_Location");
		model.deleteEntitiesByFullName(driver, baseURL, entitiesNames, LOG);
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		// Clear cookies
		this.driver.manage().deleteAllCookies();

		// Sign out
		SignUtil.signOut(this.driver, LOG);

		// Close driver
		this.driver.close();
	}

}