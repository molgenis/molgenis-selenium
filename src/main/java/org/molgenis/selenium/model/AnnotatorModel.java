package org.molgenis.selenium.model;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.molgenis.MolegnisSeleniumUtils;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.data.rest.client.bean.QueryResponse;
import org.molgenis.util.GsonHttpMessageConverter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;

import com.google.common.base.Joiner;

/**
 * This is a model of the MOLGENIS login user interface
 */
public class AnnotatorModel
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorModel.class);

	private MolgenisClient molgenisClient;
	private String token;
	private WebDriver driver;
	
	public AnnotatorModel(WebDriver driver){
		this.driver = driver;
	}

	public void createMolgenisClient(String baseUrl)
	{
		String apiURL = String.format("%s/api/v1", baseUrl);
		LOG.info("apiURL = " + apiURL);
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		template.setMessageConverters(asList(new GsonHttpMessageConverter(true)));
		molgenisClient = new MolgenisClient(template, apiURL);
	}

	public void loginRestApi(String uid, String pwd)
	{
		LOG.info("login REST api");
		token = molgenisClient.login(uid, pwd).getToken();
	}

	public void enableAnnotators()
	{
		LOG.info("Enable annotators");
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

	public void deleteTestEntity()
	{
		LOG.info("Delete test_entity");
		molgenisClient.deleteMetadata("test_entity");
	}

	public void uploadDataFile(String baseUrl) throws Exception
	{
		LOG.info("upload datafile");
		driver.get(baseUrl + "/");
		String uploadLinkText = "Import data";
		uploadLinkText = "Upload";
		MolegnisSeleniumUtils.waitForElement(By.linkText(uploadLinkText), driver);
		driver.findElement(By.linkText(uploadLinkText)).click();
		MolegnisSeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(1).active"), driver);
		MolegnisSeleniumUtils.waitForElement(By.name("upload"), driver);

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

		MolegnisSeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(2).active"), driver);
		MolegnisSeleniumUtils.waitForElement(By.linkText("Next →"), driver);
		driver.findElement(By.linkText("Next →")).click();

		MolegnisSeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(3).active"), driver);
		MolegnisSeleniumUtils.waitForElement(By.linkText("Next →"), driver);
		driver.findElement(By.linkText("Next →")).click();

		MolegnisSeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(4).active"), driver);
		MolegnisSeleniumUtils.waitForElement(By.linkText("Next →"), driver);
		driver.findElement(By.linkText("Next →")).click();

		MolegnisSeleniumUtils.waitForElement(By.cssSelector("div.panel-success"), driver);
	}

	public void openDataset(String baseUrl) throws InterruptedException
	{
		LOG.info("openDataset ...");
		driver.get(baseUrl + "/menu/main/dataexplorer?entity=test_entity");
		MolegnisSeleniumUtils.waitFor(() -> MolegnisSeleniumUtils.isElementPresent(By.id("entity-class-name"), driver)
				&& driver.findElement(By.id("entity-class-name")).getText().contains("test_entity"));
		LOG.info("openDataset done.");
	}

	public void clickAnnotators() throws InterruptedException
	{
		MolegnisSeleniumUtils.waitForElement(By.linkText("Annotators"), driver);
		LOG.info("click annotators...");
		driver.findElement(By.linkText("Annotators")).click();
		LOG.info("click annotators done.");
		MolegnisSeleniumUtils.waitForElement(By.cssSelector("#annotate-dataset-form a.select-all-btn"), driver);
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
		MolegnisSeleniumUtils.waitFor(() -> MolegnisSeleniumUtils.isElementPresent(By.linkText("Show result"), driver)
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
		MolegnisSeleniumUtils.waitForElement(By.cssSelector("a.tree-deselect-all-btn"), driver);
		driver.findElement(By.cssSelector("a.tree-deselect-all-btn")).click();

		Thread.sleep(1000);

		driver.findElements(By.cssSelector("div.molgenis-tree span.fancytree-has-children span.fancytree-checkbox"))
				.forEach(WebElement::click);

		List<WebElement> elements = new ArrayList<WebElement>(driver.findElements(By
				.cssSelector(".molgenis-table-container tr")));
		Set<String> expected = new TreeSet<String>();
		expected.addAll(Arrays
				.asList("ABCA4 601691 Fundus flavimaculatus,Macular degeneration, age...   3 ABCA4,ABCR,STGD1,FFM,RP19,CORD3,ARMD2 1p22.1 153800,248200,604116,601718 HP:0008736,HP:0007703,HP:0007868,HP:0000551,HP:... ABCA4 Glaucoma,Hyperinsulinemia,Retinitis pigmentosa,... ORPHANET,OMIM 1872,791,153800,248200,604116,601718 24",
						"ESPN 606351 Deafness, autosomal recessive 36   3 ESPN 1p36.31 609006 HP:0000407,HP:0000007,HP:0008568 ESPN Sensorineural hearing impairment,Autosomal rece... OMIM 609006 83715",
						"H6PD 138090 Cortisone reductase deficiency 1   3 H6PD,GDH,G6PDH,CORTRD1 1p36.22 604931 HP:0001061,HP:0000007,HP:0000876,HP:0001513,HP:... H6PD Obesity,Infertility,Acne,Hirsutism,Autosomal re... OMIM 604931 9563",
						"HSPG2 142461 Dyssegmental dysplasia, Silverman-Handmaker typ...   3 HSPG2,PLC,SJS,SJA,SJS1 1p36.12 224410,255800 HP:0002673,HP:0000252,HP:0010978,HP:0004298,HP:... HSPG2 Abnormality of the pharynx,Wrist flexion contra... OMIM 224410,255800 3339"));
		Set<String> rows = elements.stream().map(WebElement::getText).collect(Collectors.toCollection(TreeSet::new));
		LOG.info("Data table rows:\n" + Joiner.on('\n').join(rows));

		System.out.println(rows);
		System.out.println(expected);
		Assert.assertEquals(rows, expected);

		LOG.info("output is as expected");
	}

}

