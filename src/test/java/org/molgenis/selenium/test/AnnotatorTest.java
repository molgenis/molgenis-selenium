package org.molgenis.selenium.test;

import java.util.Arrays;

import org.molgenis.JenkinsConfig;
import org.molgenis.rest.model.SettingsModel;
import org.molgenis.selenium.model.AnnotatorModel;
import org.molgenis.selenium.model.DataExplorerModel;
import org.molgenis.selenium.model.DataExplorerModel.DeleteOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorTest.class);

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
		model = homepage.selectDataExplorer().selectEntity("test_entity").selectAnnotatorTab();
	}

	@Test
	public void testAnnotateWithCaddAndSnpEffCopyDelete() throws Exception
	{
		DataExplorerModel dataExplorerModel = model.clickSnpEff().clickCADD().clickCopy().clickAnnotateButton()
				.goToResult().deselectAll().selectCompoundAttributes();
		Assert.assertEquals(dataExplorerModel.getTableData(), Arrays.asList(
				"plus\nCADDABS CADDSCALED Annotation Putative_impact Gene_Name Gene_ID Feature_type Feature_ID Transcript_biotype Rank_total HGVS_c HGVS_p cDNA_position CDS_position Protein_position Distance_to_feature Errors LOF NMD",
				"edit\ntrash\nsearch\nmissense_variant MODERATE ESPN ESPN transcript NM_031475.2 Coding 9/13 c.2044G>A p.Gly682Arg 2212/3531 2044/2565 682/854",
				"edit\ntrash\nsearch\nmissense_variant MODERATE H6PD H6PD transcript NM_001282587.1 Coding 5/5 c.1763G>A p.Arg588Gln 1915/9027 1763/2409 588/802",
				"edit\ntrash\nsearch\nmissense_variant MODERATE HSPG2 HSPG2 transcript NM_001291860.1 Coding 86/97 c.11728A>C p.Thr3910Pro 11808/14343 11728/13179 3910/4392",
				"edit\ntrash\nsearch\n3.852093 23.4 missense_variant MODERATE ABCA4 ABCA4 transcript NM_000350.2 Coding 33/50 c.4685T>C p.Ile1562Thr 4789/7325 4685/6822 1562/2273"));
		dataExplorerModel.deleteEntity(DeleteOption.DATA_AND_METADATA);
	}

}
