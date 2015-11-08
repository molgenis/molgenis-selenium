package org.molgenis.selenium.model;

import static com.google.common.collect.Lists.transform;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.List;

import org.molgenis.selenium.model.mappingservice.MappingProjectsModel;
import org.molgenis.selenium.model.mappingservice.TagWizardModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a util for the use of the Molgenis Menu
 */
public class MenuModel
{
	private static final Logger LOG = LoggerFactory.getLogger(MenuModel.class);

	@FindBy(id = "spinner")
	WebElement spinner;

	@FindBy(id = "open-button")
	WebElement signinButton;

	@FindBy(id = "signout-button")
	WebElement signoutButton;

	@FindBy(linkText = "Upload")
	WebElement uploadMenuItem;

	@FindBy(linkText = "Data Explorer")
	private WebElement dataExplorerMenuItem;

	@FindBy(linkText = "Data Integration")
	private WebElement dataIntegrationMenuItem;

	@FindBy(linkText = "Mapping Service")
	private WebElement mappingServiceMenuItem;

	@FindBy(linkText = "Tag Wizard")
	private WebElement tagWizardMenuItem;

	protected final WebDriver driver;
	protected WebDriverWait tenSecondWait;

	public MenuModel(WebDriver driver)
	{
		this.driver = driver;
		this.tenSecondWait = new WebDriverWait(driver, 10);
	}

	public SignInModel openSignInDialog()
	{
		// click the sign in button on home page
		signinButton.click();
		return PageFactory.initElements(driver, SignInModel.class);
	}

	public boolean isLoggedIn()
	{
		return signoutButton.isDisplayed();
	}

	public HomepageModel signOut()
	{
		LOG.info("signOut");
		signoutButton.click();
		return PageFactory.initElements(driver, HomepageModel.class);
	}

	public boolean isSignedOut()
	{
		return signinButton.isDisplayed();
	}

	public ImporterModel selectImporter()
	{
		LOG.info("Select Importer...");
		uploadMenuItem.click();
		return PageFactory.initElements(driver, ImporterModel.class);
	}

	public DataExplorerModel selectDataExplorer()
	{
		LOG.info("Select Data explorer...");
		dataExplorerMenuItem.click();
		return PageFactory.initElements(driver, DataExplorerModel.class);
	}

	public MappingProjectsModel selectMappingService()
	{
		LOG.info("Select Mapping Service...");
		dataIntegrationMenuItem.click();
		mappingServiceMenuItem.click();
		return PageFactory.initElements(driver, MappingProjectsModel.class);
	}

	public TagWizardModel selectTagWizard()
	{
		LOG.info("Select Tag wizard...");
		dataIntegrationMenuItem.click();
		tagWizardMenuItem.click();
		return PageFactory.initElements(driver, TagWizardModel.class);
	}

	/**
	 * Waits for a while until the spinner appears and then for the spinner to disappear again.
	 */
	public void waitForSpinner()
	{
		LOG.info("Wait for spinner...");
		// Takes a while for the spinner to appear
		trySleep(500);
		tenSecondWait.until(not(visibilityOf(spinner)));
	}

	private static void trySleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException ex)
		{
		}
	}

	protected List<List<String>> getTableData(List<WebElement> tableRows)
	{
		return tableRows.stream().map(elt -> elt.findElements(By.cssSelector("td")))
				.map(tds -> transform(tds, WebElement::getText)).collect(toList());
	}
}
