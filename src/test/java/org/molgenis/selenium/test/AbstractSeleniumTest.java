package org.molgenis.selenium.test;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.HomepageModel;
import org.molgenis.selenium.model.component.SpinnerModel;
import org.molgenis.selenium.model.importer.ImporterModel;
import org.molgenis.selenium.model.importer.ImporterModel.EntitiesOptions;
import org.molgenis.util.GsonConfig;
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

/**
 * Base class that does the general setup and tear down of the tests.
 */
@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class, GsonConfig.class })
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
		driver.manage().timeouts().implicitlyWait(SpinnerModel.IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
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
		homepage = PageFactory.initElements(driver, HomepageModel.class).menu().openSignInDialog().signIn(uid, pwd);
		token = restClient.login(uid, pwd).getToken();
	}

	@AfterMethod
	public void abstractAfterMethod()
	{
		homepage.menu().signOut();
		restClient.logout(token);
	}

	protected void tryDeleteEntities(String... entityNames)
	{
		LOG.info("Delete entities if present...");
		for (String name : entityNames)
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
		try
		{
			Thread.sleep(5000);
			// If you log out after firing the delete, you get a 401
		}
		catch (InterruptedException e)
		{
		}
	}

	protected void tryDeleteData(String... entityNames)
	{
		LOG.info("Delete entities if present...");
		for (String name : entityNames)
		{
			try
			{
				LOG.info("Delete {} data...", name);
				restClient.deleteData(token, name);
			}
			catch (Exception ex)
			{
				LOG.info("Failed to delete data for {}. {}", name, ex.getMessage());
			}
		}
		try
		{
			Thread.sleep(5000);
			// If you log out after firing the delete, you get a 401
		}
		catch (InterruptedException e)
		{
		}
	}

	protected void importFiles(String... relativePaths)
	{
		driver.get(baseURL);
		homepage = PageFactory.initElements(driver, HomepageModel.class).menu().openSignInDialog().signIn(uid, pwd);
		for (String path : relativePaths)
		{
			LOG.info("Import file {}", newArrayList(relativePaths));
			File annotatorTestFile = ImporterModel.getFile(path);
			driver.get(baseURL);
			homepage.menu().selectImporter().importFile(annotatorTestFile, EntitiesOptions.ADD).finish();
		}
		homepage.menu().signOut();
	}

}
