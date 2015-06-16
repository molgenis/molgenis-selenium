package org.molgenis.selenium.model;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.util.MenuUtil;
import org.molgenis.selenium.util.RuntimePropertyUtil;
import org.molgenis.selenium.util.SeleniumUtils;
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
		RuntimePropertyUtil.updateRuntimePropertyValue(molgenisClient, token,
				"plugin.dataexplorer.mod.annotators", "true", LOG);
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

	public void clickHGNC()
	{
		LOG.info("click HGNC");
		driver.findElement(By.cssSelector("#enabled-annotator-selection-container input[value=HGNC-Symbol]")).click();
	}

	public void clickOMIM()
	{
		LOG.info("click OMIM");
		driver.findElement(By.cssSelector("#enabled-annotator-selection-container input[value=OmimHpo]")).click();
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

		List<WebElement> elements = new ArrayList<WebElement>(driver.findElements(By
				.cssSelector(".molgenis-table-container tr")));
		Set<String> expected = new TreeSet<String>();
		expected.addAll(Arrays
				.asList("edit\ntrash\nsearch\nABCA4 601691 Fundus flavimaculatus,Macular degeneration, age...   3 ABCA4,ABCR,STGD1,FFM,RP19,CORD3,ARMD2 1p22.1 153800,248200,604116,601718 HP:0008736,HP:0007703,HP:0007868,HP:0000551,HP:... ABCA4 Glaucoma,Hyperinsulinemia,Retinitis pigmentosa,... ORPHANET,OMIM 1872,791,153800,248200,604116,601718 24",
						"edit\ntrash\nsearch\nESPN 606351 Deafness, autosomal recessive 36   3 ESPN 1p36.31 609006 HP:0000407,HP:0000007,HP:0008568 ESPN Sensorineural hearing impairment,Autosomal rece... OMIM 609006 83715",
						"edit\ntrash\nsearch\nH6PD 138090 Cortisone reductase deficiency 1   3 H6PD,GDH,G6PDH,CORTRD1 1p36.22 604931 HP:0001061,HP:0000007,HP:0000876,HP:0001513,HP:... H6PD Obesity,Infertility,Acne,Hirsutism,Autosomal re... OMIM 604931 9563",
						"edit\ntrash\nsearch\nHSPG2 142461 Dyssegmental dysplasia, Silverman-Handmaker typ...   3 HSPG2,PLC,SJS,SJA,SJS1 1p36.12 224410,255800 HP:0002673,HP:0000252,HP:0010978,HP:0004298,HP:... HSPG2 Abnormality of the pharynx,Wrist flexion contra... OMIM 224410,255800 3339",
						"plus\nHGNC_SYMBOL OMIM_Causal_ID OMIM_Disorders OMIM_IDs OMIM_Type OMIM_HGNC_IDs OMIM_Cytogenic_Location OMIM_Entry HPO_IDs HPO_Gene_Name HPO_Descriptions HPO_Disease_Database HPO_Disease_Database_Entry HPO_Entrez_ID"));

		Set<String> rows = elements.stream().map(WebElement::getText)
				.collect(Collectors.toCollection(TreeSet::new));

		LOG.info("Data table rows:\n" + Joiner.on('\n').join(rows));

		Assert.assertEquals(rows, expected);

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

