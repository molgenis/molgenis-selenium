package org.molgenis.selenium.test;

import static com.google.common.collect.Lists.newArrayList;
import static org.molgenis.selenium.model.ImporterModel.EntitiesOptions.ADD;

import java.io.File;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.HomepageModel;
import org.molgenis.selenium.model.ImporterModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.google.common.collect.Lists;

/**
 * Base class that does the general setup and tear down of the tests.
 */
@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public abstract class AbstractSeleniumTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSeleniumTest.class);

	protected WebDriver driver;
	protected String token;

	@Value("${test.baseurl}")
	protected String baseURL;

	@Value("${test.uid}")
	protected String uid;

	@Value("${test.pwd}")
	protected String pwd;

	@Autowired
	protected MolgenisClient restClient;

	protected HomepageModel homepage;

	@BeforeClass
	public void abstractBeforeClass()
	{
		driver = DriverType.FIREFOX.getDriver();
	}

	@AfterClass
	public void abstractAfterClass()
	{
		this.driver.close();
	}

	@BeforeMethod
	public void abstractBeforeMethod()
	{
		driver.get(baseURL);
		homepage = PageFactory.initElements(driver, HomepageModel.class).openSignInDialog().signIn(uid, pwd);
		token = restClient.login(uid, pwd).getToken();
	}

	@AfterMethod
	public void abstractAfterMethod()
	{
		homepage.signOut();
		restClient.logout(token);
	}

	protected void tryDeleteEntities(String... names)
	{
		LOG.info("Delete entities if present...");
		for (String name : names)
		{
			try
			{
				LOG.info("Delete {}...", name);
				restClient.deleteMetadata(token, name);
			}
			catch (Exception ex)
			{
				LOG.info("Failed to delete entity {}. {}", name, ex.getMessage());
			}
		}
	}

	protected void importFiles(String... relativePaths)
	{
		for (String path : relativePaths)
		{
			LOG.info("Import file {}", newArrayList(relativePaths));
			File annotatorTestFile = ImporterModel.getFile(path);
			driver.get(baseURL);
			HomepageModel homePage = PageFactory.initElements(driver, HomepageModel.class);
			homePage.openSignInDialog().signIn(uid, pwd).selectImporter().importFile(annotatorTestFile, ADD).finish()
					.signOut();
		}
	}

}
