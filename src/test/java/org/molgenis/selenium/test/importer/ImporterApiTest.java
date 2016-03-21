package org.molgenis.selenium.test.importer;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.data.rest.client.bean.LoginResponse;
import org.molgenis.util.GsonConfig;
import org.molgenis.util.GsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, GsonConfig.class })
public class ImporterApiTest extends AbstractTestNGSpringContextTests
{
	@Value("${test.baseurl}")
	private String baseUrl;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	private String adminToken;

	private MolgenisClient client;
	@Autowired
	private GsonHttpMessageConverter gsonHttpMessageConverter;

	private final Resource testVcf = new ClassPathResource("vcf/test.vcf", getClass());
	private final Resource weirdnameVcf = new ClassPathResource("vcf/di@gno$&.vcf", getClass());
	private final Resource testXlsx = new ClassPathResource("org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx");
	private final Resource testEmxZip = new ClassPathResource("org/molgenis/selenium/emx/csv.zip/emx_all_datatypes_csv.zip");

	RestTemplate template = new RestTemplate();
	RestTemplate template2 = new RestTemplate();

	private final HttpHeaders headers = new HttpHeaders();

	private final Pattern importRunUrl = Pattern.compile("\"/api/v2/ImportRun/(.*)\"");

	@BeforeClass
	public void loginAdmin()
	{
		String apiHref = baseUrl + "/api/v1";
		template2.setMessageConverters(asList(gsonHttpMessageConverter));
		client = new MolgenisClient(template2, apiHref);
		LoginResponse response = client.login(uid, pwd);
		adminToken = response.getToken();
		headers.set("x-molgenis-token", adminToken);
	}

	@BeforeMethod
	public void beforeMethod()
	{
		client.deleteMetadata(adminToken, "test");
		client.deleteMetadata(adminToken, "test_Sample");
		client.deleteMetadata(adminToken, "org_molgenis_test_TypeTest");
		client.deleteMetadata(adminToken, "TypeTestRef");
		client.deleteMetadata(adminToken, "Location");
		client.deleteMetadata(adminToken, "org_molgenis_test_Person");
		client.deleteMetadata(adminToken, "org_molgenis_test_Location");
		client.deleteMetadata(adminToken, "org_molgenis_test_TypeTestCSV");
		client.deleteMetadata(adminToken, "org_molgenis_test_TypeTestRefCSV");
		client.deleteMetadata(adminToken, "org_molgenis_test_PersonCSV");
		client.deleteMetadata(adminToken, "org_molgenis_test_LocationCSV");
	}

	//@Test
	public void testUploadVcfCleanStart() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testVcf);
		ResponseEntity<String> result = template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(result.getStatusCode(), HttpStatus.CREATED);
		String importRunId = getImportRunId(result);

		Map<String, Object> importRun = waitForImportRunToFinish(importRunId);
		assertEquals(importRun.get("status"), "FINISHED");
		assertEquals(importRun.get("message"),
				"Imported 10 test entities.<br />Imported 10 test_Sample entities.<br />");
		assertEquals(importRun.get("importedEntities"), "test_Sample,test");
		assertEquals(importRun.get("notify"), false);
	}

	@Test
	public void testUploadWeirdNameVcfWithExistingEntityName() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testVcf);
		ResponseEntity<String> result = template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(result.getStatusCode(), HttpStatus.CREATED);
		String importRunId = getImportRunId(result);
		waitForImportRunToFinish(importRunId);

		parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", weirdnameVcf);
		parts.add("entityName", "test");
		parts.add("action", "add");
		try
		{
			template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
					new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);
			Assert.fail("Should return 400 error");
		}
		catch (HttpClientErrorException expected)
		{
			assertEquals(expected.getStatusCode(), HttpStatus.BAD_REQUEST);
			assertEquals(expected.getResponseBodyAsString(), "\"A repository with name test already exists\"");
		}
	}

	@Test
	public void testUploadWeirdNameVcf() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", weirdnameVcf);
		parts.add("action", "aDd");
		try
		{
			template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
					new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);
			Assert.fail("Should return 400 error");
		}
		catch (HttpClientErrorException expected)
		{
			assertEquals(expected.getStatusCode(), HttpStatus.BAD_REQUEST);
			assertEquals(expected.getResponseBodyAsString(),
					"\"Invalid characters in: [di@gno$&] Only letters (a-z, A-Z), digits (0-9), underscores (_) and hashes (#) are allowed.\"");
		}
	}

	@Test
	public void testVcfDoesntSupportUpdate() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testVcf);
		parts.add("action", "update");
		try
		{
			template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
					new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);
			Assert.fail("Should return 400 error");
		}
		catch (HttpClientErrorException expected)
		{
			assertEquals(expected.getStatusCode(), HttpStatus.BAD_REQUEST);
			assertEquals(expected.getResponseBodyAsString(),
					"\"Update mode UPDATE is not supported, only ADD is supported for VCF\"");
		}
	}

	@Test
	public void testUploadVcfUnknownAction() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testVcf);
		parts.add("action", "BUG");
		try
		{
			template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
					new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);
			Assert.fail("Should return 400 error");
		}
		catch (HttpClientErrorException expected)
		{
			assertEquals(expected.getStatusCode(), HttpStatus.BAD_REQUEST);
			assertEquals(expected.getResponseBodyAsString(),
					"\"Invalid action:[BUG] valid values: [ADD, ADD_UPDATE_EXISTING, UPDATE, ADD_IGNORE_EXISTING]\"");
		}
	}

	/**
	 * @throws InterruptedException
	 */
	//@Test
	public void testUploadZipCleanStart() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testEmxZip);
		ResponseEntity<String> result = template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(result.getStatusCode(), HttpStatus.CREATED);
		String importRunId = getImportRunId(result);

		Map<String, Object> importRun = waitForImportRunToFinish(importRunId);
		System.out.println(importRun);
		assertEquals(importRun.get("status"), "FINISHED");
		assertEquals(importRun.get("message"),
				"Imported 5 org_molgenis_test_TypeTestRefCSV entities.<br />Imported 38 org_molgenis_test_TypeTestCSV entities.<br />");
		assertEquals(importRun.get("importedEntities"),
				"org_molgenis_test_PersonCSV,org_molgenis_test_TypeTestRefCSV,org_molgenis_test_LocationCSV,org_molgenis_test_TypeTestCSV");
		assertEquals(importRun.get("notify"), false);
	}

	@Test
	public void testUploadZipExistingEntities() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testEmxZip);
		ResponseEntity<String> response = template.exchange(baseUrl + "/plugin/importwizard/importFile",
				HttpMethod.POST, new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		String importRunId = getImportRunId(response);

		waitForImportRunToFinish(importRunId);

		parts.add("action", "ADD");
		response = template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		importRunId = getImportRunId(response);
		Map<String, Object> importRun = waitForImportRunToFinish(importRunId);
		System.out.println(importRun);
		assertEquals(importRun.get("status"), "FAILED");
		assertTrue(importRun.get("message").toString()
				.startsWith("Trying to add existing org_molgenis_test_TypeTestRefCSV entities as new insert: ref"));
	}

	/**
	 * @throws InterruptedException
	 */
	//@Test
	public void testUploadZipUpdateExistingEntities() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testEmxZip);
		ResponseEntity<String> response = template.exchange(baseUrl + "/plugin/importwizard/importFile",
				HttpMethod.POST, new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		String importRunId = getImportRunId(response);

		waitForImportRunToFinish(importRunId);

		parts.add("action", "update");
		response = template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		importRunId = getImportRunId(response);
		Map<String, Object> importRun = waitForImportRunToFinish(importRunId);
		System.out.println(importRun);
		assertEquals(importRun.get("status"), "FINISHED");
		assertEquals(importRun.get("message"),
				"Imported 5 org_molgenis_test_TypeTestRefCSV entities.<br />Imported 38 org_molgenis_test_TypeTestCSV entities.<br />");
		assertEquals(importRun.get("importedEntities"), "");
		assertEquals(importRun.get("notify"), false);
	}

	@Test
	public void testUploadZipUnknownAction() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testEmxZip);
		parts.add("action", "BUG");
		try
		{
			template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
					new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);
			Assert.fail("Should return 400 error");
		}
		catch (HttpClientErrorException expected)
		{
			assertEquals(expected.getStatusCode(), HttpStatus.BAD_REQUEST);
			assertEquals(expected.getResponseBodyAsString(),
					"\"Invalid action:[BUG] valid values: [ADD, ADD_UPDATE_EXISTING, UPDATE, ADD_IGNORE_EXISTING]\"");
		}
	}

	/**
	 * @throws InterruptedException
	 */
	//@Test
	public void testUploadExcelCleanStart() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testXlsx);
		ResponseEntity<String> result = template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(result.getStatusCode(), HttpStatus.CREATED);
		String importRunId = getImportRunId(result);

		Map<String, Object> importRun = waitForImportRunToFinish(importRunId);
		System.out.println(importRun);
		assertEquals(importRun.get("status"), "FINISHED");
		assertEquals(importRun.get("message"),
				"Imported 5 TypeTestRef entities.<br />Imported 38 org_molgenis_test_TypeTest entities.<br />");
		assertEquals(importRun.get("importedEntities"), "TypeTestRef,Location,org_molgenis_test_TypeTest");
		assertEquals(importRun.get("notify"), false);
	}

	/**
	 * @throws InterruptedException
	 */
	//@Test
	public void testUploadExcelExistingEntities() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testXlsx);
		ResponseEntity<String> response = template.exchange(baseUrl + "/plugin/importwizard/importFile",
				HttpMethod.POST, new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		String importRunId = getImportRunId(response);

		waitForImportRunToFinish(importRunId);

		parts.add("action", "ADD");
		response = template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		importRunId = getImportRunId(response);
		Map<String, Object> importRun = waitForImportRunToFinish(importRunId);
		System.out.println(importRun);
		assertEquals(importRun.get("status"), "FAILED");
		assertTrue(importRun.get("message").toString()
				.startsWith("Trying to add existing TypeTestRef entities as new insert: ref"));
	}

	//@Test
	public void testUploadExcelUpdateExistingEntities() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testXlsx);
		ResponseEntity<String> response = template.exchange(baseUrl + "/plugin/importwizard/importFile",
				HttpMethod.POST, new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		String importRunId = getImportRunId(response);

		waitForImportRunToFinish(importRunId);

		parts.add("action", "update");
		response = template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		importRunId = getImportRunId(response);
		Map<String, Object> importRun = waitForImportRunToFinish(importRunId);
		System.out.println(importRun);
		assertEquals(importRun.get("status"), "FINISHED");
		assertEquals(importRun.get("message"),
				"Imported 5 TypeTestRef entities.<br />Imported 38 org_molgenis_test_TypeTest entities.<br />");
		assertEquals(importRun.get("importedEntities"), "");
		assertEquals(importRun.get("notify"), false);
	}

	@Test
	public void testUploadExcelUnknownAction() throws InterruptedException
	{
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", testXlsx);
		parts.add("action", "BUG");
		try
		{
			template.exchange(baseUrl + "/plugin/importwizard/importFile", HttpMethod.POST,
					new HttpEntity<MultiValueMap<String, Object>>(parts, headers), String.class);
			Assert.fail("Should return 400 error");
		}
		catch (HttpClientErrorException expected)
		{
			assertEquals(expected.getStatusCode(), HttpStatus.BAD_REQUEST);
			assertEquals(expected.getResponseBodyAsString(),
					"\"Invalid action:[BUG] valid values: [ADD, ADD_UPDATE_EXISTING, UPDATE, ADD_IGNORE_EXISTING]\"");
		}
	}

	private synchronized String getImportRunId(ResponseEntity<String> response)
	{
		String url = response.getBody();
		Matcher matcher = importRunUrl.matcher(url);
		matcher.matches();
		return matcher.group(1);
	}

	private Map<String, Object> waitForImportRunToFinish(String importRunId) throws InterruptedException
	{
		Map<String, Object> importRun = getImportRun(importRunId);
		for (int i = 0; i < 100; i++)
		{
			if ("FINISHED".equals(importRun.get("status")))
			{
				continue;
			}
			Thread.sleep(200);
			importRun = getImportRun(importRunId);
		}
		return importRun;
	}

	private Map<String, Object> getImportRun(String importRunId)
	{
		return client.get(adminToken, "ImportRun", importRunId);
	}
}
