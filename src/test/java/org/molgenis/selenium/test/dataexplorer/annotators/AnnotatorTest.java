package org.molgenis.selenium.test.dataexplorer.annotators;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.ImmutableSet.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.testng.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.molgenis.JenkinsConfig;
import org.molgenis.rest.model.SettingsModel;
import org.molgenis.selenium.model.dataexplorer.DataExplorerModel;
import org.molgenis.selenium.model.dataexplorer.DataExplorerModel.DeleteOption;
import org.molgenis.selenium.model.dataexplorer.annotators.AnnotatorModel;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.molgenis.selenium.test.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class AnnotatorTest extends AbstractSeleniumTest
{
	private static final List<List<String>> VCF_CGD_ANNOTATION = asList(
			asList("", "", "", "", "", "", "", "", "", "", "", "", ""),
			asList("rs151276478", "", "", "", "", "", "", "", "", "", "", "", ""),
			asList("rs1749913", "29557", "91624", "Cardiomyopathy, familial hypertrophic, 20; Card...", "AD",
					"DOMINANT", "Pediatric", "", "Cardiovascular", "Cardiovascular", "",
					"Surveillance (eg, including echocardiogram/elec...", "19881492; 20970104"),
			asList("", "", "", "", "", "", "", "", "", "", "", "", ""),
			asList("rs72885464", "", "", "", "", "", "", "", "", "", "", "", ""),
			asList("", "", "", "", "", "", "", "", "", "", "", "", ""),
			asList("rs2063690", "", "", "", "", "", "", "", "", "", "", "", ""),
			asList("rs149296338", "2195", "80781", "Knobloch syndrome 1", "AR", "RECESSIVE", "N/A", "N/A",
					"Craniofacial; Musculoskeletal; Neurologic; Opht...", "General", "",
					"Genetic knowledge may be beneficial related to ...",
					"1554013; 7802003; 10942434; 14695535; 17546652;..."),
			asList("rs3122407", "17342", "254065", "Mental retardation, X-linked 93", "XL", "XLINKED", "N/A", "N/A",
					"Craniofacial; Genitourinary; Musculoskeletal; N...", "General", "",
					"Genetic knowledge may be beneficial related to ...", "7943039; 17668385; 19377476"));

	private static final List<List<String>> EMX_SNPEFF_ANNOTATION = asList(
			asList("1", "intergenic_region", "MODIFIER", "LOC729737-LOC100132062", "LOC729737-LOC100132062",
					"intergenic_region", "LOC729737-LOC100132062", "", "", "n.158796A>C", "", "", "", "", "", "", "",
					""),
			asList("2", "splice_region_variant&synonymous_variant", "LOW", "STAT4", "STAT4", "transcript",
					"NM_001243835.1", "Coding", "16/24", "c.1338C>A", "p.Thr446Thr", "1602/2775", "1338/2247",
					"446/748", "", "", "", ""),
			asList("3", "intron_variant", "MODIFIER", "ICOSLG", "ICOSLG", "transcript", "NM_001283050.1", "Coding",
					"5/6", "c.863-37_863-36insG", "", "", "", "", "", ",A", "", ""),
			asList("4", "frameshift_variant", "HIGH", "COL18A1", "COL18A1", "transcript", "NM_030582.3", "Coding",
					"33/42", "c.3358_3365delCCCCCCGGCCCCCCAGG", "p.Pro1120fs", "3379/5894", "3358/4551", "1120/1516",
					"", ",CCCCCCCAGG", "(COL18A1|COL18A1|1|1.00)", ""));

	private static final List<List<String>> EMX_EXAC_ANNOTATION = asList(asList("1", "", "", ""),
			asList("2", "0.003739", "5", "444"), asList("3", "0.155,6.339E-5", "428,0", "1644,0"),
			asList("4", "1.079E-5,1.079E-5", "0,0", "1,1"));

	private static final List<List<String>> EMX_GONL_ANNOTATION = asList(asList("1", "", ""),
			asList("2", "495,3,0", "0.0030120481927710845"), asList("3", "", ""), asList("4", "", ""));

	private static final List<List<String>> EMX_EMPTY_ANNOTATION_TWO_COLUMN = asList(asList("1", "", ""),
			asList("2", "", ""), asList("3", "", ""), asList("4", "", ""));

	private static final List<List<String>> EMX_EMPTY_ANNOTATION = asList(asList("1", ""), asList("2", ""),
			asList("3", ""), asList("4", ""));

	private static final List<List<String>> EMX_CADD_ANNOTATION = asList(asList("1", "-0.667351", "1.08"),
			asList("2", "", ""), asList("3", "", ""), asList("4", "", ""));

	private static final List<List<String>> VCF_SNPEFF_ANNOTATION = asList(
			asList("", "non_coding_exon_variant", "MODIFIER", "DDX11L1", "DDX11L1", "transcript", "NR_046018.2",
					"Noncoding", "3/3", "n.623C>G", "", "", "", "", "", "", "", ""),
			asList("rs151276478", "non_coding_exon_variant", "MODIFIER", "DDX11L1", "DDX11L1", "transcript",
					"NR_046018.2", "Noncoding", "3/3", "n.1223T>C", "", "", "", "", "", "", "", ""),
			asList("rs1749913", "intron_variant", "MODIFIER", "NEXN", "NEXN", "transcript", "NM_144573.3", "Coding",
					"3/12", "c.219+25G>A", "", "", "", "", "", "", "", ""),
			asList("", "splice_region_variant&intron_variant", "LOW", "TTC13", "TTC13", "transcript", "NM_024525.4",
					"Coding", "2/22", "c.367-7_367-6delTT", "", "", "", "", "", ",GA", "", ""),
			asList("rs72885464", "intron_variant", "MODIFIER", "LOC101926913", "LOC101926913", "transcript",
					"NR_110185.1", "Noncoding", "5/5", "n.376+9863G>A", "", "", "", "", "", ",T", "", ""),
			asList("", "intron_variant", "MODIFIER", "UGT2B7", "UGT2B7", "transcript", "NM_001074.2", "Coding", "1/5",
					"c.722-23delT", "", "", "", "", "", ",CTT", "", ""),
			asList("rs2063690", "missense_variant", "MODERATE", "TIPIN", "TIPIN", "transcript", "NM_017858.2", "Coding",
					"5/8", "c.332C>T", "p.Ala111Val", "418/1280", "332/906", "111/301", "", ",C", "", ""),
			asList("rs149296338",
					"frameshift_variant&splice_acceptor_variant&splice_donor_variant&splice_region_variant&intron_variant",
					"HIGH", "COL18A1", "COL18A1", "transcript", "NM_030582.3", "Coding", "33/42",
					"c.3364_3365-2delGGCCCCCCA", "p.Pro1123fs", "3385/5894", "3364/4551", "1122/1516", "", "",
					"(COL18A1|COL18A1|1|1.00)", ""),
			asList("rs3122407", "missense_variant&splice_region_variant", "MODERATE", "BRWD3", "BRWD3", "transcript",
					"NM_153252.4", "Coding", "34/41", "c.3863A>G", "p.Lys1288Arg", "4126/12793", "3863/5409",
					"1288/1802", "", "", "", ""));

	private static final List<List<String>> VCF_EXAC_ANNOTATION = asList(asList("", "0.001209", "0", "23"),
			asList("rs151276478", "", "", ""), asList("rs1749913", "0.809", "38057", "17422"), asList("", "", "", ""),
			asList("rs72885464", "0.267", "911", "3366"),
			asList("", "0.394,0.131,0.005366", "3743,120,0", "27251,7162,414"),
			asList("rs2063690", "2.471E-5,0.112,1.648E-5", "0,1094,0", "1,11380,2"), asList("rs149296338", "", "", ""),
			asList("rs3122407", "0.997", "26864", "173"));

	private static final List<List<String>> VCF_GONL_ANNOTATION = asList(asList("", "", ""),
			asList("rs151276478", "485,13,0", "0.013052208835341365"),
			asList("rs1749913", "6,120,372", "0.8674698795180723"), asList("", "", ""),
			asList("rs72885464", "241,207,50", "0.30823293172690763"), asList("", "", ""),
			asList("rs2063690", "|412,77,9|", "|0.09538152610441768|"), asList("rs149296338", "", ""),
			asList("rs3122407", "0,1,496", "0.9989939637826962"));

	private static final List<List<String>> VCF_EMPTY_ANNOTATION_TWO_COLUMNS = asList(asList("", "", ""),
			asList("rs151276478", "", ""), asList("rs1749913", "", ""), asList("", "", ""),
			asList("rs72885464", "", ""), asList("", "", ""), asList("rs2063690", "", ""),
			asList("rs149296338", "", ""), asList("rs3122407", "", ""));

	private static final List<List<String>> VCF_EMPTY_ANNOTATION = asList(asList("", ""), asList("rs151276478", ""),
			asList("rs1749913", ""), asList("", ""), asList("rs72885464", ""), asList("", ""), asList("rs2063690", ""),
			asList("rs149296338", ""), asList("rs3122407", ""));

	private static final List<List<String>> VCF_CADD_ANNOTATION = asList(asList("", "-1.358693", "0.027"),
			asList("rs151276478", "-0.25518", "2.773"), asList("rs1749913", "", ""), asList("", "", ""),
			asList("rs72885464", "", ""), asList("", "", ""), asList("rs2063690", "", ""),
			asList("rs149296338", "", ""), asList("rs3122407", "", ""));

	private static final Logger LOG = LoggerFactory.getLogger(AbstractSeleniumTest.class);

	private AnnotatorModel model;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("AnnotatorTestSelenium", "VcfSelenium", "VcfSelenium_Sample");
		new SettingsModel(restClient, token).updateDataExplorerSettings("mod_annotators", true);
		restClient.logout(token);
		importEMXFiles("annotator_test.xlsx");
		importVcf("corner_cases.vcf", "VcfSelenium");
	}

	@AfterClass
	public void afterClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("AnnotatorTestSelenium", "VcfSelenium", "VcfSelenium_Sample");
		restClient.logout(token);
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		model = homepage.menu().selectDataExplorer().selectEntity("VcfSelenium").selectAnnotatorTab();
	}

	@Test
	public void testSelectAllDeselectAll()
	{
		LOG.info("Test select all / deselect all...");
		model.selectAll();
		assertEquals(model.getSelectedAnnotators(),
				Arrays.asList("cadd", "dann", "clinvar", "gonl", "exac", "fitcon", "snpEff", "thousand_genomes"));
		model.deselectAll().select("cadd");
		assertEquals(model.getSelectedAnnotators(), Arrays.asList("cadd"));
	}

	@Test
	public void testAnnotateVcfWithAllAvailableAnnotatorsCopyDelete()
	{
		LOG.info("Test annotation of VCF with all available annotators...");
		DataExplorerModel dataExplorerModel = model.selectAll().clickCopy()
				.clickAnnotateButtonAndWait(5, TimeUnit.MINUTES).goToResult();

		LOG.info("Check results...");
		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("cadd").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		List<List<String>> tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated cadd table data: {}", tableData);
		compareTableData(tableData, VCF_CADD_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("dann").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated dann table data: {}", tableData);
		compareTableData(tableData, VCF_EMPTY_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("clinvar").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated clinvar table data: {}", tableData);
		compareTableData(tableData, VCF_EMPTY_ANNOTATION_TWO_COLUMNS);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("gonl").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated gonl table data: {}", tableData);
		compareTableData(tableData, VCF_GONL_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("exac").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated exac table data: {}", tableData);
		compareTableData(tableData, VCF_EXAC_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("fitcon").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated fitcon table data: {}", tableData);
		compareTableData(tableData, VCF_EMPTY_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("snpEff").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated snpEff table data: {}", getJavaInitializerString(tableData));
		compareTableData(tableData, VCF_SNPEFF_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("thousand_genomes").spinner()
				.waitTillDone(1, TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("thousand_genomes table data: {}", tableData);
		compareTableData(tableData, VCF_EMPTY_ANNOTATION);

		dataExplorerModel.deleteEntity(DeleteOption.DATA_AND_METADATA);
	}

	@Test
	public void testEmxWithAllAvailableAnnotatorsCopyDelete()
	{
		LOG.info("Test annotation with all available annotators...");
		DataExplorerModel dataExplorerModel = model.selectDataTab().selectEntity("AnnotatorTestSelenium")
				.selectAnnotatorTab().selectAll().clickCopy().clickAnnotateButtonAndWait(5, TimeUnit.MINUTES)
				.goToResult();

		LOG.info("Check results...");
		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("cadd").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		List<List<String>> tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated cadd table data: {}", tableData);
		compareTableData(tableData, EMX_CADD_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("dann").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated dann table data: {}", tableData);
		compareTableData(tableData, EMX_EMPTY_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("clinvar").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated clinvar table data: {}", tableData);
		compareTableData(tableData, EMX_EMPTY_ANNOTATION_TWO_COLUMN);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("gonl").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated gonl table data: {}", tableData);
		compareTableData(tableData, EMX_GONL_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("exac").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated exac table data: {}", tableData);
		compareTableData(tableData, EMX_EXAC_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("fitcon").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated fitcon table data: {}", tableData);
		compareTableData(tableData, EMX_EMPTY_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("snpEff").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated snpEff table data: {}", getJavaInitializerString(tableData));
		compareTableData(tableData, EMX_SNPEFF_ANNOTATION);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("thousand_genomes").spinner()
				.waitTillDone(1, TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("thousand_genomes table data: {}", tableData);
		compareTableData(tableData, EMX_EMPTY_ANNOTATION);

		dataExplorerModel.deleteEntity(DeleteOption.DATA_AND_METADATA);
	}

	@Test
	public void testAnnotateSnpEffCGDAndHPOOnVCFCopyAndDelete()
	{
		LOG.info("Test annotation of VCF file with SnpEff and then HPO and CGD, using copy ...");
		DataExplorerModel dataExplorerModel = model.select("snpEff").clickCopy()
				.clickAnnotateButtonAndWait(5, TimeUnit.MINUTES).goToResult().selectAnnotatorTab().select("hpo")
				.select("CGD").clickAnnotateButtonAndWait(5, TimeUnit.MINUTES).goToResult();

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("hpo").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		List<List<String>> tableData = dataExplorerModel.getTableData();
		LOG.info("HPO table data: {}", tableData);
		compareTableData(tableData, VCF_EMPTY_ANNOTATION_TWO_COLUMNS);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("CGD").spinner().waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("CGD table data: {}", tableData);
		compareTableData(tableData, VCF_CGD_ANNOTATION);
	}

	@Test
	public void testAnnotateSnpEffCGDAndHPOOnEMXCopyAndDelete()
	{
		LOG.info("Test annotation of EMX file with SnpEff and then HPO and CGD, using copy ...");
		DataExplorerModel dataExplorerModel = model.selectDataTab().selectEntity("AnnotatorTestSelenium")
				.selectAnnotatorTab().select("snpEff").clickCopy().clickAnnotateButtonAndWait(5, TimeUnit.MINUTES)
				.goToResult().selectAnnotatorTab().select("hpo").select("CGD")
				.clickAnnotateButtonAndWait(5, TimeUnit.MINUTES).goToResult();

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("hpo").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		List<List<String>> tableData = dataExplorerModel.getTableData();
		LOG.info("HPO table data: {}", tableData);
		compareTableData(tableData, EMX_EMPTY_ANNOTATION_TWO_COLUMN);

		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("CGD").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("CGD table data: {}", tableData);
		compareTableData(tableData,
				asList(asList("1", "", "", "", "", "", "", "", "", "", "", "", ""),
						asList("2", "", "", "", "", "", "", "", "", "", "", "", ""),
						asList("3", "", "", "", "", "", "", "", "", "", "", "", ""),
						asList("4", "2195", "80781", "Knobloch syndrome 1", "AR", "RECESSIVE", "N/A", "N/A",
								"Craniofacial; Musculoskeletal; Neurologic; Opht...", "General", "",
								"Genetic knowledge may be beneficial related to ...",
								"1554013; 7802003; 10942434; 14695535; 17546652;...")));
	}

	@Test(enabled = false)
	public void testRunCaddTwiceOnEMXWithCopy() throws URISyntaxException
	{
		LOG.info("Test annotating EMX with CADD twice, using copy ...");
		LOG.info("Going once..");
		DataExplorerModel dataExplorerModel = model.selectDataTab().selectEntity("AnnotatorTestSelenium")
				.selectAnnotatorTab().select("cadd").clickCopy().clickAnnotateButtonAndWait(1, TimeUnit.MINUTES)
				.goToResult();
		String entityName = dataExplorerModel.getEntityNameFromURL().get();
		Assert.assertEquals(restClient.getMeta(token, entityName).getBody().getAttributes().keySet(),
				of("ID", "#CHROM", "POS", "REF", "ALT", "Comment", "MOLGENIS_cadd"));

		Map<String, Object> entity = Maps.newHashMap(restClient.get(token, entityName, "1"));
		entity.put("CADDABS", 0);
		entity.put("CADDSCALED", 0);
		restClient.update(token, entityName, "1", copyOf(entity));

		LOG.info("Going twice..");
		dataExplorerModel.selectAnnotatorTab().select("cadd").clickAnnotateButtonAndWait(1, TimeUnit.MINUTES)
				.goToResult().deselectAll().clickAttribute("ID").clickAttribute("cadd").spinner()
				.waitTillDone(1, TimeUnit.SECONDS);
		Assert.assertEquals(restClient.getMeta(token, entityName).getBody().getAttributes().keySet(),
				of("ID", "#CHROM", "POS", "REF", "ALT", "Comment", "MOLGENIS_cadd"));

		List<List<String>> tableData = dataExplorerModel.getTableData();
		LOG.info("Done! Table data after annotating twice: {}", tableData);
		compareTableData(tableData, asList(asList("2", "", ""), asList("3", "", ""), asList("4", "", ""),
				asList("1", "-0.667351", "1.08")));

		restClient.deleteMetadata(token, entityName);
	}

	@Test(priority = 10) // destructive so this is the last test to run on the VCF
	public void testRunCaddTwiceOnVcfNoCopy()
	{
		LOG.info("Test annotating VCF with CADD twice, on the VCF entity, no copy ...");
		LOG.info("First annotation with CADD and EXAC on VcfSelenium...");
		DataExplorerModel dataExplorerModel = model.selectDataTab().selectEntity("VcfSelenium").selectAnnotatorTab()
				.select("cadd").select("exac").clickAnnotateButtonAndWait(2, TimeUnit.MINUTES).goToResult();

		LOG.info("Check cadd annotations...");
		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("cadd").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		List<List<String>> tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated cadd table data: {}", tableData);
		compareTableData(tableData, VCF_CADD_ANNOTATION);

		LOG.info("Check exac annotations...");
		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("exac").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("Annotated exac table data: {}", tableData);
		compareTableData(tableData, VCF_EXAC_ANNOTATION);

		LOG.info("Clear cadd scores in first row...");
		Map<String, Object> entity = restClient.get(token, "VcfSelenium").getItems().get(0);
		entity.put("CADDABS", 0);
		entity.put("CADDSCALED", 0);
		entity.put("SAMPLES_ENTITIES", emptyList()); // easiest update
		LOG.info("entity: {}", entity);
		restClient.update(token, "VCFSelenium", entity.get("INTERNAL_ID").toString(), copyOf(entity));

		LOG.info("Reannotate with CADD...");
		dataExplorerModel.selectAnnotatorTab().select("cadd").clickAnnotateButtonAndWait(1, TimeUnit.MINUTES)
				.goToResult();

		LOG.info("Check cadd annotations...");
		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("cadd").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("CADD table data after annotating twice: {}", tableData);
		compareTableData(tableData, moveFirstRowToLast(VCF_CADD_ANNOTATION));

		LOG.info("Check exac annotations...");
		dataExplorerModel.deselectAll().clickAttribute("ID").clickAttribute("exac").spinner()
				.waitTillDone(1,
				TimeUnit.SECONDS);
		tableData = dataExplorerModel.getTableData();
		LOG.info("exac table data after annotating twice: {}", tableData);
		compareTableData(tableData, moveFirstRowToLast(VCF_EXAC_ANNOTATION));
	}

	private List<List<String>> moveFirstRowToLast(List<List<String>> expected)
	{
		return Stream.<List<String>> concat(expected.stream().skip(1), Stream.<List<String>> of(expected.get(0)))
				.collect(Collectors.toList());
	}

}
