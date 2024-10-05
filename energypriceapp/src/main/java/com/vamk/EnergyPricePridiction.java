package com.vamk;

import java.util.Map;

public class EnergyPricePridiction {
	
	
	

	public static void main(String[] args) {
		
		EnergyPriceController epc = new EnergyPriceController();
		
		Map<String, Object> ep = epc.getEnergyPrices();
		
		System.out.println("ep ::" +ep);

	}

}
