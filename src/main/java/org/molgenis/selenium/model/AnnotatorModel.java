package org.molgenis.selenium.model;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.util.MenuUtil;
import org.molgenis.selenium.util.SeleniumUtils;
import org.molgenis.selenium.util.SettingsUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.google.common.base.Joiner;

/**
 * This is a model of the MOLGENIS login user interface
 */
public class AnnotatorModel
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorModel.class);

	private final MolgenisClient molgenisClient;
	private final String token;
	private final WebDriver driver;

	public AnnotatorModel(WebDriver driver, MolgenisClient molgenisClient, String token)
	{
		this.driver = driver;
		this.molgenisClient = molgenisClient;
		this.token = token;
	}

	public void enableAnnotatorsOnDataExplorer()
	{
		SettingsUtil.updateDataExplorerSettings(molgenisClient, token, "mod_aggregates", true, LOG);
	}

	public void deleteTestEntity()
	{
		LOG.info("Delete test_entity");
		molgenisClient.deleteMetadata("test_entity");
	}

	public void uploadDataFile(String baseUrl) throws Exception
	{
		LOG.info("upload datafile");
		driver.get(baseUrl + "/");
		MenuUtil.openPageByClickOnMenuItem(UploadAppModel.MENUITEM, driver);
		SeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(1).active"), driver);
		SeleniumUtils.waitForElement(By.name("upload"), driver);

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

		SeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(2).active"), driver);
		SeleniumUtils.waitForElement(By.linkText("Next →"), driver);
		driver.findElement(By.linkText("Next →")).click();

		SeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(3).active"), driver);
		SeleniumUtils.waitForElement(By.linkText("Next →"), driver);
		driver.findElement(By.linkText("Next →")).click();

		SeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(4).active"), driver);
		SeleniumUtils.waitForElement(By.linkText("Next →"), driver);
		driver.findElement(By.linkText("Next →")).click();

		SeleniumUtils.waitForElement(By.cssSelector("div.panel-success"), driver);
	}

	public void openDataset(String baseUrl) throws InterruptedException
	{
		LOG.info("openDataset ...");
		driver.get(baseUrl + "/menu/main/dataexplorer?entity=test_entity");
		SeleniumUtils.waitFor(() -> SeleniumUtils.isElementPresent(By.id("entity-class-name"), driver)
				&& driver.findElement(By.id("entity-class-name")).getText().contains("test_entity"));
		LOG.info("openDataset done.");
	}

	public void clickAnnotators() throws InterruptedException
	{
		SeleniumUtils.waitForElement(By.linkText("Annotators"), driver);
		LOG.info("click annotators...");
		driver.findElement(By.linkText("Annotators")).click();
		LOG.info("click annotators done.");
		SeleniumUtils.waitForElement(By.cssSelector("#annotate-dataset-form a.select-all-btn"), driver);
		Thread.sleep(1000);
	}

	public void clickHGNC() throws InterruptedException
	{
		LOG.info("click HGNC");
		By hGNCSymbolCheckbox = By.cssSelector("#enabled-annotator-selection-container input[value=HGNC_Symbol]");
		SeleniumUtils.waitForElement(hGNCSymbolCheckbox, driver);
		driver.findElement(hGNCSymbolCheckbox).click();
	}

	public void clickOMIM() throws InterruptedException
	{
		LOG.info("click OMIM");
		By omimHpoCheckbox = By.cssSelector("#enabled-annotator-selection-container input[value=OmimHpo]");
		SeleniumUtils.waitForElement(omimHpoCheckbox, driver);
		driver.findElement(omimHpoCheckbox).click();
	}

	public void clickAnnotateButton() throws InterruptedException
	{
		LOG.info("click annotate button");
		driver.findElement(By.id("annotate-dataset-button")).click();
		LOG.info("Wait for result");
		SeleniumUtils.waitFor(() -> SeleniumUtils.isElementPresent(By.linkText("Show result"), driver)
				&& driver.findElement(By.linkText("Show result")).isDisplayed(), 240);
		LOG.info("found result, click Show Result");
	}

	public void clickShowResults()
	{
		LOG.info("Show result");
		driver.findElement(By.linkText("Show result")).click();
	}

	public void checkResults() throws InterruptedException
	{
		LOG.info("Wait for annotator attribute");
		SeleniumUtils.waitForElement(By.cssSelector("a.tree-deselect-all-btn"), driver);
		driver.findElement(By.cssSelector("a.tree-deselect-all-btn")).click();

		Thread.sleep(1000);

		driver.findElements(By.cssSelector("div.molgenis-tree span.fancytree-has-children span.fancytree-checkbox"))
				.forEach(WebElement::click);

		Thread.sleep(1000);

		List<WebElement> elements = new ArrayList<WebElement>(driver.findElements(By
				.cssSelector(".molgenis-table-container tr")));
		List<String> expected = Arrays
				.asList("edit\ntrash\nsearch\nABCA4",
						"edit\ntrash\nsearch\nESPN",
						"edit\ntrash\nsearch\nH6PD",
						"edit\ntrash\nsearch\nHSPG2",
						"plus\nOMIM_Causal_ID OMIM_Disorders OMIM_IDs OMIM_Type OMIM_HGNC_IDs OMIM_Cytogenic_Location OMIM_Entry HPO_IDs HPO_Gene_Name HPO_Descriptions HPO_Disease_Database HPO_Disease_Database_Entry HPO_Entrez_ID HGNC_SYMBOL");

		Set<String> rows = elements.stream().map(WebElement::getText).collect(Collectors.toCollection(TreeSet::new));

		LOG.info("Data table rows:\n" + Joiner.on('\n').join(rows));

		AtomicInteger index = new AtomicInteger();
		rows.stream().forEachOrdered(row -> Assert.assertEquals(row, expected.get(index.getAndIncrement())));

		LOG.info("output is as expected");
	}

	/**
	 * @return the molgenisClient
	 */
	public MolgenisClient getMolgenisClient()
	{
		return molgenisClient;
	}

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}
}
