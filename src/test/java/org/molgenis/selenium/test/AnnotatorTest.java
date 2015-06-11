package org.molgenis.selenium.test;

import org.molgenis.AbstractSeleniumTests;
import org.molgenis.DriverType;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.AnnotatorModel;
import org.molgenis.selenium.util.RestApiV1Util;
import org.molgenis.selenium.util.SignInUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AnnotatorTest extends AbstractSeleniumTests
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorTest.class);
	private AnnotatorModel model;

	@Value("${test.baseurl}")
	private String baseUrl;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeSuite()
	{
		MolgenisClient molgenisClient = RestApiV1Util.createMolgenisClientApiV1(baseUrl, LOG);
		WebDriver driver = DriverType.FIREFOX.getDriver();
		this.model = new AnnotatorModel(driver, molgenisClient, RestApiV1Util.loginRestApiV1(molgenisClient, uid, pwd,
				LOG));
	}

	@Test
	public void testAnnotateAll() throws Exception
	{
		model.enableAnnotatorsOnDataExplorer();
		model.deleteTestEntity();

		SignInUtil.login(model.getDriver(), baseUrl, uid, pwd, LOG);

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
		return this.model.getDriver();
	}
}
