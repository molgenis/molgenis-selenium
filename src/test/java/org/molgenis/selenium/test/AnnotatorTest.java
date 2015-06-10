package org.molgenis.selenium.test;

import org.molgenis.AbstractSeleniumTests;
import org.molgenis.DriverType;
import org.molgenis.selenium.model.AnnotatorModel;
import org.molgenis.selenium.model.SignInAppModel;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.Test;

public class AnnotatorTest extends AbstractSeleniumTests
{
	private WebDriver driver = DriverType.FIREFOX.getDriver();
	private AnnotatorModel model = new AnnotatorModel(driver);

	@Value("${anntest.baseurl}")
	private String baseUrl;

	@Value("${anntest.uid}")
	private String uid;

	@Value("${anntest.pwd}")
	private String pwd;

	@Test
	public void testAnnotateAll() throws Exception
	{
		model.createMolgenisClient(baseUrl);
		model.loginRestApi(uid, pwd);
		model.enableAnnotators();
		model.deleteTestEntity();
		SignInAppModel.login(driver, baseUrl, uid, pwd);
		model.uploadDataFile(baseUrl);
		model.openDataset(baseUrl);
		model.clickAnnotators();
		model.clickHGNC();
		model.clickOMIM();
		model.clickAnnotateButton();
		model.clickShowResults();
		model.checkResults();
	}

	@Override
	public WebDriver getDriver()
	{
		return this.driver;
	}
}
