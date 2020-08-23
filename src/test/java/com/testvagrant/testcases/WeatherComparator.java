package com.testvagrant.testcases;

import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.testvagrant.comparator.IWeatherComparator;
import com.testvagrant.model.VarianceModel;
import com.testvagrant.model.WeatherModel;
import com.testvagrant.utils.PropertyReader;

public class WeatherComparator {

	private PropertyReader property = new PropertyReader();

	@DataProvider(name = "Cities")
	public Object[] cityNamesProvider() {
		return new Object[] { "Ahmedabad", "Mumbai", "Chennai" };
	}

	@Test(dataProvider = "Cities")
	public void compare(String city) {
		property.readPropertiesFile("src/test/resources/comparator.properties");

		HashMap<String, WeatherModel> ui = NDTVWeatherPageTest.uiObjects();
		WeatherModel uiTmp = ui.get(city);
		HashMap<String, WeatherModel> api = OpenWeatherAPITest.apiObjects();
		WeatherModel apiTmp = api.get(city);

		int humTemp = Integer.parseInt(property.getProperty("humidityVariance"));
		int celTemp = Integer.parseInt(property.getProperty("celsiusVariance"));
		int varTemp = Integer.parseInt(property.getProperty("fahrenheitVariance"));
		VarianceModel var = new VarianceModel(humTemp, celTemp, varTemp);

		IWeatherComparator result = (uiObj, apiObj, varObj) -> {
			
			MathContext mtx = new MathContext(4);
			BigDecimal humRes = new BigDecimal(uiObj.getHumidity().floatValue())
					.subtract(new BigDecimal(apiObj.getHumidity().floatValue())).round(mtx);
			
			BigDecimal celRes = new BigDecimal(uiObj.getTempInDegrees().floatValue())
					.subtract(new BigDecimal(apiObj.getTempInDegrees().floatValue())).round(mtx);
			
			BigDecimal farRes = new BigDecimal(uiObj.getTempInFahrenheit().floatValue())
					.subtract(new BigDecimal(apiObj.getTempInFahrenheit().floatValue())).round(mtx);

			if (humRes.floatValue() > varObj.getHumidity() || celRes.floatValue() > varObj.getTempInDegrees()
					|| farRes.floatValue() > varObj.getTempInFahrenheit()) {
				return false;
			}

			return true;
		};

		assertTrue(result.compareWeather(uiTmp, apiTmp, var));

	}

}
