package com.lloyd.DeriMarketData.Models;

import com.lloyd.DeriMarketData.Instruments.EuropeanOption;

public class BlackScholes {
	
	private double Spot;
	//private double Strike;
	private double RiskFreeRate;
	private double DividendYield;
	//private double YearFraction;
	private double Volatility;
	
	public void main(double Spot, double Volatility) {
		this.Spot = Spot;
		this.RiskFreeRate = 0;
		this.DividendYield = 0;
		this.Volatility =  Volatility;
	}
	
	public double getSpot() {
		return this.Spot;
	}
	
	public double getRiskFreeRate() {
		return this.RiskFreeRate;
	}
	
	public double getDividendYield() {
		return this.DividendYield;
	}
	
	public double getVolatility() {
		return this.Volatility;
	}

	public double Price(EuropeanOption thisOption) {
		return 0;
	}
	
}
