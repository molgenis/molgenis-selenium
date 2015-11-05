package org.molgenis.selenium.util;

import static java.util.Arrays.asList;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.data.rest.client.bean.LoginResponse;
import org.molgenis.util.GsonHttpMessageConverter;
import org.slf4j.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestApiV1Util
{
	public static MolgenisClient createMolgenisClientApiV1(String baseURL, Logger logger)
	{
		logger.info("RestApiV1Util -- apiURL = " + baseURL);
		String apiURL = String.format("%s/api/v1", baseURL);
		HttpClient client = HttpClientBuilder.create().disableCookieManagement().build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
		RestTemplate template = new RestTemplate(requestFactory);
		template.setMessageConverters(asList(new GsonHttpMessageConverter(true)));
		return new MolgenisClient(template, apiURL);
	}

	public static String loginRestApiV1(MolgenisClient molgenisClient, String uid, String pwd, Logger logger)
	{
		logger.info("RestApiV1Util -- login REST api v1");
		LoginResponse response = molgenisClient.login(uid, pwd);
		logger.info("RestApiV1Util -- login succesful. {}", response);
		return response.getToken();
	}
}
