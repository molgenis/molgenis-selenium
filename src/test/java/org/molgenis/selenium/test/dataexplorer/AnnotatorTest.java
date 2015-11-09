package org.molgenis.selenium.test.dataexplorer;

import static java.util.Arrays.asList;

import java.util.concurrent.TimeUnit;

import org.molgenis.JenkinsConfig;
import org.molgenis.rest.model.SettingsModel;
import org.molgenis.selenium.model.dataexplorer.AnnotatorModel;
import org.molgenis.selenium.model.dataexplorer.DataExplorerModel;
import org.molgenis.selenium.model.dataexplorer.DataExplorerModel.DeleteOption;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.molgenis.selenium.test.Config;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class AnnotatorTest extends AbstractSeleniumTest
{
	private AnnotatorModel model;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("test_entity");
		new SettingsModel(restClient, token).updateDataExplorerSettings("mod_annotators", true);
		restClient.logout(token);
		importFiles("test_file.xlsx");
	}

	@AfterClass
	public void afterClass()
	{
		tryDeleteEntities("test_entity");
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		model = homepage.menu().selectDataExplorer().selectEntity("test_entity").selectAnnotatorTab();
	}

	@Test
	public void testAnnotateWithCaddAndSnpEffCopyDelete() throws Exception
	{
		DataExplorerModel dataExplorerModel = model.clickSnpEff().clickCADD().clickCopy()
				.clickAnnotateButtonAndWait(5, TimeUnit.MINUTES).goToResult().deselectAll().selectCompoundAttributes();
		Assert.assertEquals(dataExplorerModel.getTableData(),
				asList(asList("edit", "trash", "search", "", "", "missense_variant", "MODERATE", "ESPN", "ESPN",
						"transcript", "NM_031475.2", "Coding", "9/13", "c.2044G>A", "p.Gly682Arg", "2212/3531",
						"2044/2565", "682/854", "", "", "", ""),
				asList("edit", "trash", "search", "", "", "missense_variant", "MODERATE", "H6PD", "H6PD", "transcript",
						"NM_001282587.1", "Coding", "5/5", "c.1763G>A", "p.Arg588Gln", "1915/9027", "1763/2409",
						"588/802", "", "", "", ""),
				asList("edit", "trash", "search", "", "", "missense_variant", "MODERATE", "HSPG2", "HSPG2",
						"transcript", "NM_001291860.1", "Coding", "86/97", "c.11728A>C", "p.Thr3910Pro", "11808/14343",
						"11728/13179", "3910/4392", "", "", "", ""),
				asList("edit", "trash", "search", "3.852093", "23.4", "missense_variant", "MODERATE", "ABCA4", "ABCA4",
						"transcript", "NM_000350.2", "Coding", "33/50", "c.4685T>C", "p.Ile1562Thr", "4789/7325",
						"4685/6822", "1562/2273", "", "", "", "")));
		dataExplorerModel.deleteEntity(DeleteOption.DATA_AND_METADATA);
	}

}