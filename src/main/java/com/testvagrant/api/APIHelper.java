package com.testvagrant.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

import com.testvagrant.model.WeatherModel;
import com.testvagrant.utils.PropertyReader;

public class APIHelper {
	
	private RequestSpecification request;
	private PropertyReader property = new PropertyReader();
	
	private void setBaseURI() {
		property.readPropertiesFile("src/test/resources/config.properties");
		RestAssured.baseURI = property.getProperty("uri");
	}

	private RequestSpecification getRequest() {
		setBaseURI();
		if (request == null)
			request = RestAssured.given();
		return request;
	}
	
	public RequestSpecification setQueryParams(Map<String, String> queryParams) {
		return getRequest().queryParams(queryParams);
	}
	
	public Response createRequest(String requestType, String path) {
		Response response = null;

		switch (requestType) {
		case "get":
			response = getRequest().get(path);
			break;

		case "post":
			response = getRequest().post(path);
			break;

		default:
			response = getRequest().get(path);
			break;
		}
		return response;
	}
	
	public WeatherModel JsonTOObject(Response metric, Response imperial) {
		Number hum=null, cel=null, far=null;
		

		String humTemp = metric.getBody().jsonPath().getString("main.humidity");
		String celTemp = metric.getBody().jsonPath().getString("main.temp");
		String farTemp = imperial.getBody().jsonPath().getString("main.temp");

		
		try {
			hum = NumberFormat.getInstance().parse(humTemp);
			cel = NumberFormat.getInstance().parse(celTemp);
			far = NumberFormat.getInstance().parse(farTemp);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new WeatherModel(hum,cel,far);
	}

	
}
