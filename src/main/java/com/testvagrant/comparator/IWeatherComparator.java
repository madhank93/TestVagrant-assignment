package com.testvagrant.comparator;

import com.testvagrant.model.VarianceModel;
import com.testvagrant.model.WeatherModel;

public interface IWeatherComparator {

	boolean compareWeather(WeatherModel uiObj, WeatherModel apiObj, VarianceModel varObj);
	
}
