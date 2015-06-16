package org.molgenis.selenium.util;

import static java.util.Arrays.asList;

import org.molgenis.data.rest.client.MolgenisClient;
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
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		template.setMessageConverters(asList(new GsonHttpMessageConverter(true)));
		return new MolgenisClient(template, apiURL);
	}

	public static String loginRestApiV1(MolgenisClient molgenisClient, String uid, String pwd, Logger logger)
	{
		logger.info("RestApiV1Util -- login REST api v1");
		return molgenisClient.login(uid, pwd).getToken();
	}
}
