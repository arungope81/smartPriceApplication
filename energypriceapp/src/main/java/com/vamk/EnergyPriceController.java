package com.vamk;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class  EnergyPriceController {




	private final RestTemplate restTemplate = new RestTemplate();
	private final String energyPriceUrl = "https://api.spot-hinta.fi/JustNow";
	private final String energyPriceNextHourUrl = "https://api.spot-hinta.fi/JustNow?lookForwardHours=1&isHtmlRequest=true";
	private final int numberOfDecimals = 2;
	boolean buyOrSell = false;
	double Gb_i = 0;
	double Gd_i = 0;
	double H_sun = 2.65;

	double threshold = 0;

	// Endpoint to return "Hello World"
	@GetMapping("/hello")
	public Map<String, String> helloWorld() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Hello World!");
		return response;
	}

	// Endpoint to fetch energy prices
	@GetMapping("/energy-prices")
	public Map<String, Object> getEnergyPrices() {
		Map<String, Object> result = new HashMap<>();

		try {
			// Fetch the current and next hour prices
			JsonNode now = restTemplate.getForObject(energyPriceUrl, JsonNode.class);
			JsonNode nextHour = restTemplate.getForObject(energyPriceNextHourUrl, JsonNode.class);

			// Format the datetime
			String formattedDateNow = formatDateTime(now.get("DateTime").asText());
			String formattedDateNextHour = formatDateTime(nextHour.get("DateTime").asText());

			// Get the prices
			double priceNow = roundToDecimals(now.get("PriceWithTax").asDouble() * 100, numberOfDecimals);
			double priceNextHour = roundToDecimals(nextHour.get("PriceWithTax").asDouble() * 100, numberOfDecimals);

			// Populate the result map
			result.put("Energy price now", createPriceMap(formattedDateNow, priceNow));
			result.put("Energy price next hour", createPriceMap(formattedDateNextHour, priceNextHour));
		} catch (Exception e) {
			result.put("error", "Failed to fetch the energy prices"+e.getMessage());
		}

		return result;
	}

	// Helper to format the date
	private String formatDateTime(String isoDateTime) {

		OffsetDateTime offsetDateTime = OffsetDateTime.parse(isoDateTime);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		return offsetDateTime.format(formatter);
	}

	// Helper to create a price map
	private Map<String, Object> createPriceMap(String timestamp, double price) {
		Map<String, Object> priceMap = new HashMap<>();

		priceMap.put("Java timestamp", timestamp);
		priceMap.put("price", price);
		priceMap.put("unit", "snt/kWh");
		priceMap.put("WeatherCondition", decideWeather(Gb_i, Gd_i, H_sun, threshold));
		if(decideToBuyOrSell())
			priceMap.put("Buy/Sell", "Buy");
		else {
			priceMap.put("Buy/Sell", "Sell");
		}
		 Map<String, Object> weatherParms = decideWeather();
		
		 
		 if(weatherParms.size()>0) {
			 priceMap.put("name",weatherParms.get("name"));
			 priceMap.put("LocalTime",weatherParms.get("LocalTime"));
			 priceMap.put("region",weatherParms.get("region"));
			 priceMap.put("country",weatherParms.get("country"));
			 priceMap.put("latitude",weatherParms.get("latitude"));
			 priceMap.put("longitude",weatherParms.get("longitude"));
			 priceMap.put("wind_mph",weatherParms.get("wind_mph"));
			 priceMap.put("wind_kph",weatherParms.get("wind_kph"));
			 priceMap.put("wind_degree",weatherParms.get("wind_degree"));
			 priceMap.put("wind_dir",weatherParms.get("wind_dir"));
			 priceMap.put("pressure_mb",weatherParms.get("pressure_mb"));
			 priceMap.put("humidity",weatherParms.get("humidity"));
			 priceMap.put("cloud",weatherParms.get("cloud"));
			 priceMap.put("feelslike_c",weatherParms.get("feelslike_c"));
			 priceMap.put("Condtion",weatherParms.get("Condtion"));
		 } 
			
		
		return priceMap;
	}

	public static String decideWeather(double Gb_i, double Gd_i, double H_sun, double threshold) {
		if (Gb_i > threshold || H_sun > 0) {
			return "Sunny";
		} else if (Gb_i == 0 && Gd_i > 0) {
			return "Partly Cloudy";
		} else {
			return "Cloudy";
		}
	}
	
	public static Map<String, Object> decideWeather() {
		
		WeatherAPI weatehrApi = new WeatherAPI();
		Map<String, Object> weatherMap = new HashMap<String, Object>();
		
		weatherMap = weatehrApi.getWetaherDetails();
		
		return weatherMap;
		
	}


	/**
	 * This would be intelligent method which will fetch the data from set of API to decide to Buy/Sell
	 * @return
	 */
	private boolean decideToBuyOrSell() {


		String prediction = decideWeather(Gb_i, Gd_i, H_sun, threshold);

		if(prediction.equalsIgnoreCase("Sunny")) 
			return false;
		else if (prediction.equalsIgnoreCase("Cloudy")||prediction.equalsIgnoreCase("Partly Cloudy"))
			return true;


		return buyOrSell;
	}

	// Helper to round to decimals
	private double roundToDecimals(double value, int decimals) {
		double scale = Math.pow(10, decimals);
		return Math.round(value * scale) / scale;
	}


}
