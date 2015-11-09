package org.molgenis.selenium.test.mappingservice.old;

import static java.util.Arrays.asList;

import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.mappingservice.MappingProjectsModel;
import org.molgenis.selenium.model.mappingservice.TagWizardModel;
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

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class AlgorithmEditorTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmEditorTest.class);
	private MappingProjectsModel model;

	@BeforeClass
	public void beforeClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("HOP_selenium", "HOP_GENDER_Ref_selenium", "FOOD_POTATOES_Ref_selenium",
				"DIS_HBP_Ref_selenium", "lifelines_test", "test_GENDER_Ref_test", "test_NUCHTER1_Ref_test",
				"test_FOOD59A1_Ref_test", "test_HEALTH351_Ref_test", "prevend_test", "test_SEX_Ref_test");
		tryDeleteData("Ontology_OntologyTerm", "Ontology_OntologyTermDynamicAnnotation",
				"Ontology_OntologyTermNodePath", "Ontology_OntologyTermSynonym", "Ontology_Ontology");
		restClient.logout(token);
		importFiles("org/molgenis/selenium/mappingservice/mappingservice-test.xlsx",
				"org/molgenis/selenium/mappingservice/biobank_ontology_test.owl.zip",
				"org/molgenis/selenium/mappingservice/uo_test.owl.zip");
	}

	@AfterClass
	public void afterClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("HOP_selenium", "HOP_GENDER_Ref_selenium", "FOOD_POTATOES_Ref_selenium",
				"DIS_HBP_Ref_selenium", "lifelines_test", "test_GENDER_Ref_test", "test_NUCHTER1_Ref_test",
				"test_FOOD59A1_Ref_test", "test_HEALTH351_Ref_test", "prevend_test", "test_SEX_Ref_test");
		tryDeleteData("Ontology_OntologyTerm", "Ontology_OntologyTermDynamicAnnotation",
				"Ontology_OntologyTermNodePath", "Ontology_OntologyTermSynonym", "Ontology_Ontology");
		restClient.logout(token);
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		model = homepage.selectTagWizard().selectEntity("HOP_selenium")
				.selectOntologies("biobank_ontology_test", "uo_test")
				.tagAttributeWithTerms("Body_Mass_Index", "Weight", "Height")
				.tagAttributeWithTerms("Body_Mass_Index_1", "Weight", "Height")
				.tagAttributeWithTerms("Body_Mass_Index_2", "Weight", "Height").selectMappingService();
	}
}
