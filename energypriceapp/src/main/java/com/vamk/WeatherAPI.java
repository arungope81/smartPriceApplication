package com.vamk;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

public class WeatherAPI {

	private static final String apiKey = "8c3f06c23c8640d1a75190644240610";

	private final static RestTemplate restTemplate = new RestTemplate();



	public static void main(String[] args) {

		String location = "Espoo";

		// Create the request URL
		String weatherApiUrl = "https://api.weatherapi.com/v1/forecast.json?q=" + location  + "&days=" + 1 + "&key=" + apiKey;


		EnergyFactor weatherNow = restTemplate.getForObject(weatherApiUrl, EnergyFactor.class);
		System.out.println("weatherNow ::"+weatherNow.getLocation().getLocaltime());

		System.out.println("weather prediction at :"+weatherNow.getLocation().getName()+ " at LocalTime ::"+weatherNow.getLocation().getLocaltime() + " is "+weatherNow.getCurrent().getCondition().getText());

	}

	public Map<String, Object> getWetaherDetails(){
		String location = "Espoo";
		Map<String, Object> weatherMap = new HashMap<String, Object>();

		String weatherApiUrl = "https://api.weatherapi.com/v1/forecast.json?q=" + location  + "&days=" + 1 + "&key=" + apiKey;
		EnergyFactor weatherNow = restTemplate.getForObject(weatherApiUrl, EnergyFactor.class);

		if(weatherNow != null) {
			weatherMap.put("name",  weatherNow.getLocation().getName());
			weatherMap.put("LocalTime", weatherNow.getLocation().getLocaltime());
			weatherMap.put("region",weatherNow.getLocation().getRegion());
			weatherMap.put("country",weatherNow.getLocation().getCountry());
			weatherMap.put("latitude",weatherNow.getLocation().getLat());
			weatherMap.put("longitude",weatherNow.getLocation().getLon());
			weatherMap.put("wind_mph",weatherNow.getCurrent().getWind_mph());
			weatherMap.put("wind_kph",weatherNow.getCurrent().getWind_kph());
			weatherMap.put("wind_degree",weatherNow.getCurrent().getWind_degree());
			weatherMap.put("wind_dir",weatherNow.getCurrent().getWind_dir());
			weatherMap.put("pressure_mb",weatherNow.getCurrent().getPressure_mb());
			weatherMap.put("humidity",weatherNow.getCurrent().getHumidity());
			weatherMap.put("cloud",weatherNow.getCurrent().getCloud());
			weatherMap.put("feelslike_c",weatherNow.getCurrent().getFeelslike_c());
			weatherMap.put("Condtion", weatherNow.getCurrent().getCondition().getText());		
		}

		return weatherMap;

	}

}
