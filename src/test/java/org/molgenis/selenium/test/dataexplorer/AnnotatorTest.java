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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class AnnotatorTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSeleniumTest.class);

	private AnnotatorModel model;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("AnnotatorTestSelenium");
		new SettingsModel(restClient, token).updateDataExplorerSettings("mod_annotators", true);
		restClient.logout(token);
		importFiles("annotator_test.xlsx");
	}

	@AfterClass
	public void afterClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("AnnotatorTestSelenium");
		restClient.logout(token);
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		model = homepage.menu().selectDataExplorer().selectEntity("AnnotatorTestSelenium").selectAnnotatorTab();
	}

	@Test
	public void testAnnotateWithCaddAndSnpEffCopyDelete() throws Exception
	{
		LOG.info("Test annotation with cadd and snpEff annotators...");
		DataExplorerModel dataExplorerModel = model.clickSnpEff().clickCADD().clickCopy()
				.clickAnnotateButtonAndWait(5, TimeUnit.MINUTES).goToResult().deselectAll().selectCompoundAttributes();
		compareTableData(dataExplorerModel.getTableData(),
				asList(asList("edit", "trash", "search", "-0.667351", "1.08", "intergenic_region", "MODIFIER",
						"LOC729737-LOC100132062", "LOC729737-LOC100132062", "intergenic_region",
						"LOC729737-LOC100132062", "", "", "n.158796A>C", "", "", "", "", "", "", "", ""),
				asList("edit", "trash", "search", "", "", "splice_region_variant&synonymous_variant", "LOW", "STAT4",
						"STAT4", "transcript", "NM_001243835.1", "Coding", "16/24", "c.1338C>A", "p.Thr446Thr",
						"1602/2775", "1338/2247", "446/748", "", "", "", ""),
				asList("edit", "trash", "search", "", "", "intron_variant", "MODIFIER", "ICOSLG", "ICOSLG",
						"transcript", "NM_001283050.1", "Coding", "5/6", "c.863-37_863-36insG", "", "", "", "", "",
						",A", "", ""),
				asList("edit", "trash", "search", "", "", "frameshift_variant", "HIGH", "COL18A1", "COL18A1",
						"transcript", "NM_030582.3", "Coding", "33/42", "c.3358_3365delCCCCCCGGCCCCCCAGG",
						"p.Pro1120fs", "3379/5894", "3358/4551", "1120/1516", "", ",CCCCCCCAGG",
						"(COL18A1|COL18A1|1|1.00)", "")));
		dataExplorerModel.deleteEntity(DeleteOption.DATA_AND_METADATA);
	}

}
