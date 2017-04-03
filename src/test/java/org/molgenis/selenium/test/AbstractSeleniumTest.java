package org.molgenis.selenium.test;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.HomepageModel;
import org.molgenis.selenium.model.importer.ImporterModel;
import org.molgenis.selenium.model.importer.ImporterModel.EntitiesOptions;
import org.molgenis.util.GsonConfig;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
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

	@Value("${webdriver.gecko.driver}")
	protected String webdriverGeckoDriver;

	@Autowired
	protected MolgenisClient restClient;

	protected HomepageModel homepage;

	@BeforeClass
	public void abstractBeforeClass()
	{
		System.setProperty("webdriver.gecko.driver", webdriverGeckoDriver);
		driver = DriverType.FIREFOX.getDriver();
		driver.manage().timeouts().implicitlyWait(AbstractModel.IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
		this.setBrowserDefaultSize();
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
	}

	protected void importEMXFiles(String... relativePaths)
	{
		driver.get(baseURL);
		homepage = PageFactory.initElements(driver, HomepageModel.class).menu().openSignInDialog().signIn(uid, pwd);
		for (String path : relativePaths)
		{
			LOG.info("Import file {}...", newArrayList(relativePaths));
			File annotatorTestFile = ImporterModel.getFile(path);
			driver.get(baseURL);
			homepage.menu().selectImporter().importFile(annotatorTestFile, EntitiesOptions.ADD).finish();
		}
		homepage.menu().signOut();
	}

	protected void importVcf(String relativePath, String entityName)
	{
		driver.get(baseURL);
		homepage = PageFactory.initElements(driver, HomepageModel.class).menu().openSignInDialog().signIn(uid, pwd);
		File annotatorTestFile = ImporterModel.getFile(relativePath);
		driver.get(baseURL);
		homepage.menu().selectImporter().importVcf(annotatorTestFile, entityName).finish();
		homepage.menu().signOut();
	}

	private static void sort(List<List<String>> list)
	{
		Collections.sort(list, new Comparator<List<String>>()
		{
			@Override
			public int compare(List<String> a, List<String> b)
			{
				return a.get(0).compareTo(b.get(0));
			}
		});
	}

	protected static void compareTableData(List<List<String>> actual, List<List<String>> expected)
	{
		sort(actual);
		sort(expected);

		LOG.debug("Compare table data...");
		try
		{
			assertEquals(actual.size(), expected.size());
			for (int i = 0; i < expected.size(); i++)
			{
				List<String> actualRow = actual.get(i);
				List<String> expectedRow = expected.get(i);
				assertEquals(actualRow.size(), expectedRow.size());
				for (int j = 0; j < expectedRow.size(); j++)
				{
					String actualCell = actualRow.get(j);
					String expectedCell = expectedRow.get(j);

					if (actualCell == null)
					{
						assertNull(expectedCell);
					}
					else
					{
						if (!actualCell.equals(expectedCell))
						{
							// could be a float, compare them for being reasonably close
							float actualFloat = Float.parseFloat(actualCell);
							float expectedFloat = Float.parseFloat(expectedCell);
							Assert.assertEquals(actualFloat, expectedFloat, 1e-6 * actualFloat);
						}
					}
				}
			}
		}
		catch (Throwable t)
		{
			Assert.fail("Error comparing table data. Expected:<" + getJavaInitializerString(expected) + "> but was:<"
					+ getJavaInitializerString(actual) + ">", t);
		}
	}

	public static String getJavaInitializerString(List<List<String>> tableData)
	{
		if (tableData == null)
		{
			return "null";
		}
		return tableData.stream().map(AbstractSeleniumTest::getJavaInitializerStringForRow)
				.collect(Collectors.joining(",", "asList(", ")"));
	}

	public static String getJavaInitializerStringForRow(List<String> row)
	{
		return row.stream().map(Object::toString).collect(Collectors.joining("\",\"", "asList(\"", "\")\n"));
	}

	/**
	 * This is a workaround to avoid the exception when the size of the browser is (width = 1280, height = 800):
	 * 
	 * "org.openqa.selenium.WebDriverException: Element is not clickable at point (XX, XX). Other element would receive
	 * the click: <a href="/menu/main/dataexplorer"></a> (WARNING: The server did not provide any stacktrace
	 * information)"
	 */
	public void setBrowserDefaultSize()
	{
		driver.manage().window().setPosition(new Point(0, 0));
		driver.manage().window().setSize(new Dimension(1920, 1080));
		// FIXME MOLGENIS should be able to work with this browser size
		// driver.manage().window().setSize(new Dimension(1280, 800));
	}
}
