package com.testvagrant.testcases;

import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;

import com.testvagrant.api.APIHelper;
import com.testvagrant.model.WeatherModel;
import com.testvagrant.utils.PropertyReader;

import io.restassured.response.Response;

public class OpenWeatherAPITest {
	
	private final PropertyReader property = new PropertyReader();
	private final String path = "data/2.5/weather";
	APIHelper apiHelper;
	
	
	@BeforeMethod
	public void setup() {
		property.readPropertiesFile("src/test/resources/config.properties");
		apiHelper = new APIHelper();
	}
	
	@Test
	public void validateResponseCodeWithValidKey() {
		HashMap<String, String> queryParams = new HashMap<>();
		queryParams.put("q", "chennai");
		queryParams.put("units", "metric");
		queryParams.put("appid", property.getProperty("apiKey"));
		
		apiHelper.setQueryParams(queryParams);
		Response val = apiHelper.createRequest("get", path);
		
		assertThat(val.getStatusCode()).isEqualTo(200);
	}

	@Test
	public void validateResponseCodeAndErrorMessageWithInValidKey() {
		HashMap<String, String> queryParams = new HashMap<>();
		queryParams.put("q", "chennai");
		queryParams.put("units", "metric");
		queryParams.put("appid", "invalid");
		
		apiHelper.setQueryParams(queryParams);
		Response val = apiHelper.createRequest("get", path);
		String errorMsg = val.getBody().jsonPath().get("message");
		
		assertThat(val.getStatusCode()).isEqualTo(401);
		assertThat(errorMsg).contains("Invalid API key");
	}
	
	@Test
	public void validateResponseCodeAndErrorMessageWithInValidCityName() {
		HashMap<String, String> queryParams = new HashMap<>();
		queryParams.put("q", "invalid");
		queryParams.put("units", "metric");
		queryParams.put("appid", property.getProperty("apiKey"));
		
		apiHelper.setQueryParams(queryParams);
		Response val = apiHelper.createRequest("get", path);
		String errorMsg = val.getBody().jsonPath().get("message");
		
		assertThat(val.getStatusCode()).isEqualTo(404);
		assertThat(errorMsg).contains("city not found");
	}
	
	@DataProvider(name = "Cities")
	public Object[] cityNamesProvider() {
		return new Object[] { "Ahmedabad", "Mumbai", "Chennai" };
	}

	@Test(dataProvider = "Cities")
	public void checkResponseContainsCityName(String city) {
		HashMap<String, String> queryParams = new HashMap<>();
		queryParams.put("q", city.toLowerCase());
		queryParams.put("units", "metric");
		queryParams.put("appid", property.getProperty("apiKey"));
		
		apiHelper.setQueryParams(queryParams);
		Response response = apiHelper.createRequest("get", path);
		String cityName = response.getBody().jsonPath().get("name");
		
		assertThat(cityName).isEqualTo(city);
	}

	@Test(dataProvider = "Cities")
	public void validateWeatherDetailsFromAPI(String city) {
		HashMap<String, String> queryParams = new HashMap<>();
		queryParams.put("q", city.toLowerCase());
		queryParams.put("units", "metric");
		queryParams.put("appid", property.getProperty("apiKey"));
		
		apiHelper.setQueryParams(queryParams);
		Response responseMetric = apiHelper.createRequest("get", path);
		
		queryParams.replace("units", "metric");
		apiHelper.setQueryParams(queryParams);
		Response responseImperial = apiHelper.createRequest("get", path);
		
		WeatherModel obj = apiHelper.JsonTOObject(responseMetric, responseImperial);
		
		assertThat(obj).isNotNull().matches(element -> element.getHumidity().floatValue() >= 0
				&& element.getTempInDegrees().floatValue() >= 0 && element.getTempInFahrenheit().floatValue() >= 0);
		
		
	}
	
}
