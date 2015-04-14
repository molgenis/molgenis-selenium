package org.molgenis;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.data.rest.client.bean.QueryResponse;
import org.molgenis.util.GsonHttpMessageConverter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;

@ContextConfiguration(classes = JenkinsConfig.class)
public class AnnotatorTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorTest.class);

	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	@Value("${anntest.baseurl}")
	private String baseUrl;
	@Value("${anntest.uid}")
	private String uid;
	@Value("${anntest.pwd}")
	private String pwd;
	private MolgenisClient molgenisClient;
	private String token;

	@BeforeTest
	public void setUp() throws Exception
	{
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	public void deleteTestEntity()
	{
		molgenisClient.deleteMetadata("test_entity");
	}

	public void enableAnnotators()
	{
		QueryResponse queryResponse = molgenisClient.queryEquals(token, "RuntimeProperty", "Name",
				"plugin.dataexplorer.mod.annotators");
		Map<String, Object> item = queryResponse.getItems().get(0);
		String id = item.get("id").toString();
		if (item.get("id") instanceof Double)
		{
			id = "" + Math.round((Double) item.get("id"));
		}
		molgenisClient.update(token, "RuntimeProperty", id, "Value", "true");
	}

	public void login() throws Exception
	{
		driver.get(baseUrl + "/");
		waitForElement(By.linkText("Sign in"));
		driver.findElement(By.linkText("Sign in")).click();
		driver.findElement(By.id("loginUsername")).clear();
		driver.findElement(By.id("loginUsername")).sendKeys(uid);
		driver.findElement(By.id("loginPassword")).clear();
		driver.findElement(By.id("loginPassword")).sendKeys(pwd);
		driver.findElement(By.id("login-btn")).click();
	}

	public void upload() throws Exception
	{
		driver.get(baseUrl + "/");
		String uploadLinkText = "Import data";
		uploadLinkText = "Upload";
		waitForElement(By.linkText(uploadLinkText));
		driver.findElement(By.linkText(uploadLinkText)).click();
		waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(1).active"));
		waitForElement(By.name("upload"));

		// http://stackoverflow.com/questions/5610256/file-upload-using-selenium-webdriver-and-java
		File file;
		try
		{
			file = new File(getClass().getClassLoader().getResource("test_file.xlsx").toURI());
		}
		catch (Exception ex)
		{
			file = new File("test-classes/test_file.xlsx");
		}
		assertTrue(file.exists());

		driver.findElement(By.name("upload")).sendKeys(file.getAbsolutePath());
		driver.findElement(By.linkText("Next →")).click();

		waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(2).active"));
		waitForElement(By.linkText("Next →"));
		driver.findElement(By.linkText("Next →")).click();

		waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(3).active"));
		waitForElement(By.linkText("Next →"));
		driver.findElement(By.linkText("Next →")).click();

		waitForElement(By.cssSelector("div.panel-success"));
	}

	@Test
	public void testAnnotateAll() throws Exception
	{
		String apiURL = String.format("%s/api/v1", baseUrl);
		LOG.info("apiURL = " + apiURL);
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		template.setMessageConverters(asList(new GsonHttpMessageConverter(true)));
		molgenisClient = new MolgenisClient(template, apiURL);
		LOG.info("login REST api");
		token = molgenisClient.login(uid, pwd).getToken();
		LOG.info("Enable annotators");
		enableAnnotators();
		LOG.info("Delete test_entity");
		deleteTestEntity();

		LOG.info("login");
		login();
		LOG.info("upload datafile");
		upload();

		LOG.info("openDataset ...");
		openDataset();
		LOG.info("openDataset done.");

		waitForElement(By.linkText("Annotators"));
		LOG.info("click annotators...");
		driver.findElement(By.linkText("Annotators")).click();
		LOG.info("click annotators done.");
		waitForElement(By.cssSelector("#annotate-dataset-form a.select-all-btn"));
		Thread.sleep(1000);
		LOG.info("click HGNC");
		driver.findElement(By.cssSelector("#enabled-annotator-selection-container input[value=HGNC-Symbol]")).click();
		LOG.info("click OMIM");
		driver.findElement(By.cssSelector("#enabled-annotator-selection-container input[value=OmimHpo]")).click();
		LOG.info("click annotate button");
		driver.findElement(By.id("annotate-dataset-button")).click();
		LOG.info("Wait for result");
		waitFor(() -> isElementPresent(By.linkText("Show result"))
				&& driver.findElement(By.linkText("Show result")).isDisplayed(), 240);
		LOG.info("found result, click Show Result");
		driver.findElement(By.linkText("Show result")).click();

		LOG.info("Wait for annotator attribute");
		waitForElement(By.cssSelector("a.tree-deselect-all-btn"));
		driver.findElement(By.cssSelector("a.tree-deselect-all-btn")).click();

		Thread.sleep(1000);

		driver.findElements(By.cssSelector("div.molgenis-tree span.fancytree-has-children span.fancytree-checkbox"))
				.forEach(WebElement::click);

		List<WebElement> elements = driver.findElements(By.cssSelector(".molgenis-table-container tr"));
		Set<String> expected = new TreeSet<String>();
		expected.addAll(Arrays
				.asList("ABCA4 601691 Fundus flavimaculatus,Macular degeneration, age...   3 RP19,CORD3,ARMD2,ABCA4,STGD1,FFM,ABCR 1p22.1 153800,248200,604116,601718 HP:0008736,HP:0007703,HP:0007868,HP:0000551,HP:... ABCA4 Glaucoma,Hyperinsulinemia,Retinitis pigmentosa,... ORPHANET,OMIM 1872,791,153800,248200,604116,601718 24",
						"ESPN 606351 Deafness, autosomal recessive 36   3 ESPN 1p36.31 609006 HP:0000407,HP:0000007,HP:0008568 ESPN Sensorineural hearing impairment,Autosomal rece... OMIM 609006 83715",
						"H6PD 138090 Cortisone reductase deficiency 1   3 H6PD,G6PDH,GDH,CORTRD1 1p36.22 604931 HP:0001061,HP:0000007,HP:0000876,HP:0001513,HP:... H6PD Obesity,Infertility,Acne,Hirsutism,Autosomal re... OMIM 604931 9563",
						"HSPG2 142461 Dyssegmental dysplasia, Silverman-Handmaker typ...   3 SJS1,PLC,SJA,HSPG2,SJS 1p36.12 224410,255800 HP:0002673,HP:0000252,HP:0010978,HP:0004298,HP:... HSPG2 Abnormality of the pharynx,Wrist flexion contra... OMIM 224410,255800 3339"));
		Set<String> rows = elements.stream().map(WebElement::getText).collect(Collectors.toCollection(TreeSet::new));
		LOG.info("Data table rows:\n" + Joiner.on('\n').join(rows));
		Assert.assertEquals(rows, expected);
		LOG.info("output is as expected");
	}

	private void openDataset() throws InterruptedException
	{
		driver.get(baseUrl + "/menu/main/dataexplorer?entity=test_entity");
		waitFor(() -> isElementPresent(By.id("entity-class-name"))
				&& driver.findElement(By.id("entity-class-name")).getText().contains("test_entity"));
	}

	@AfterTest
	public void tearDown() throws Exception
	{
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString))
		{
			fail(verificationErrorString);
		}
	}

	private static void waitFor(BooleanSupplier p) throws InterruptedException
	{
		waitFor(p, 60);
	}

	private static void waitFor(BooleanSupplier p, int timeout) throws InterruptedException
	{
		for (int second = 0;; second++)
		{
			if (second >= timeout) fail("timeout");
			try
			{
				if (p.getAsBoolean()) break;
			}
			catch (Exception e)
			{
			}
			Thread.sleep(1000);
		}
	}

	private void waitForElement(By by) throws InterruptedException
	{
		waitFor(() -> isElementPresent(by));
	}

	private void waitForElementInvisible(By by) throws InterruptedException
	{
		waitFor(() -> !driver.findElement(by).isDisplayed());
	}

	private boolean isElementPresent(By by)
	{
		try
		{
			driver.findElement(by);
			return true;
		}
		catch (NoSuchElementException e)
		{
			return false;
		}
	}
}
