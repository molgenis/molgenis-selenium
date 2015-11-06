package org.molgenis.selenium.model;

import org.molgenis.selenium.model.mappingservice.MappingProjectOverviewModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * This is a util for the use of the Molgenis Menu
 */
public class MenuModel
{
	@FindBy(linkText = "Upload")
	WebElement uploadMenuItem;

	@FindBy(id = "open-button")
	WebElement signinButton;

	@FindBy(id = "signout-button")
	WebElement signoutButton;

	@FindBy(linkText = "Data Explorer")
	private WebElement dataExplorerMenuItem;

	@FindBy(linkText = "Data Integration")
	private WebElement dataIntegrationMenuItem;

	@FindBy(linkText = "Mapping Service")
	private WebElement mappingServiceMenuItem;

	protected final WebDriver driver;

	public MenuModel(WebDriver driver)
	{
		this.driver = driver;
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
		signoutButton.click();
		return PageFactory.initElements(driver, HomepageModel.class);
	}

	public boolean isSignedOut()
	{
		return signinButton.isDisplayed();
	}

	public ImporterModel selectUpload()
	{
		uploadMenuItem.click();
		return PageFactory.initElements(driver, ImporterModel.class);
	}

	public DataExplorerModel selectDataExplorer()
	{
		dataExplorerMenuItem.click();
		return PageFactory.initElements(driver, DataExplorerModel.class);
	}

	public MappingProjectOverviewModel selectMappingService()
	{
		dataIntegrationMenuItem.click();
		mappingServiceMenuItem.click();
		return PageFactory.initElements(driver, MappingProjectOverviewModel.class);
	}
}
