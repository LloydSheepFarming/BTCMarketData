package com.lloyd.DeriMarketData.Maths;

public class BlackScholesFormulae {
	
	public static double BlackScholesCallPrice(double Spot, double Strike, double RiskFreeRate, double DividendYield, double YearFraction, double Volatility) {
		double d1 = (Math.log(Spot / Strike) + (RiskFreeRate - DividendYield + 0.5 * Math.pow(Volatility,2)) * YearFraction) / Volatility / Math.sqrt(YearFraction);
		double d2 = d1 - Volatility * Math.sqrt(YearFraction);
		return Spot * Math.exp(- DividendYield * YearFraction) * Distribution.NormCDF(d1) - Math.exp(- RiskFreeRate * YearFraction) * Strike * Distribution.NormCDF(d2);
	}
	
	
	public static double BlackScholesCallPrice(double Spot, double Strike, double YearFraction, double Volatility) {
		double d1 = (Math.log(Spot / Strike) + (0.5 * Math.pow(Volatility,2)) * YearFraction) / Volatility / Math.sqrt(YearFraction);
		double d2 = d1 - Volatility * Math.sqrt(YearFraction);
		return Spot * Distribution.NormCDF(d1) - Strike * Distribution.NormCDF(d2);
	}
	
	public static double BlackScholesCallIV(double Spot, double Strike, double YearFraction, double CallPrice) {
		
		if(CallPrice <= Spot - Strike) return 0;
		
		if(CallPrice == 0) return 0;
		
		double PxTolerance = 0.00001d * Spot;
		double Increment = 0.00001d;
		double InitialGuess = 0.5d;
		double thisIV = InitialGuess;
		double thisCallPrice = BlackScholesCallPrice(Spot, Strike, YearFraction, thisIV);
		
		int MaxIter = 20;
		int CountIter = 0;
		
		while(Math.abs(CallPrice - thisCallPrice) >= PxTolerance) {
			CountIter += 1;
			if(CountIter>MaxIter) {return 0;}
			double thisIVP = thisIV + Increment; // 0.001% Increment of IV
			double thisCallPriceP = BlackScholesCallPrice(Spot, Strike, YearFraction, thisIVP);
			double dCallPx = (thisCallPriceP - thisCallPrice) / Increment;
			thisIV = thisIV - (thisCallPrice - CallPrice) / dCallPx;
			thisCallPrice = BlackScholesCallPrice(Spot, Strike, YearFraction, thisIV);
		}
		
		if(thisIV == Double.NEGATIVE_INFINITY || thisIV == Double.POSITIVE_INFINITY) {thisIV = 0;} 
		
		return thisIV;
		
	}
	
	public static double BlackScholesPutPrice(double Spot, double Strike, double RiskFreeRate, double DividendYield, double YearFraction, double Volatility) {
		double d1 = (Math.log(Spot / Strike) + (RiskFreeRate - DividendYield + 0.5 * Math.pow(Volatility,2)) * YearFraction) / Volatility / Math.sqrt(YearFraction);
		double d2 = d1 - Volatility * Math.sqrt(YearFraction);
		return Math.exp(- RiskFreeRate * YearFraction) * Strike * Distribution.NormCDF(- d2) - Spot * Math.exp(- DividendYield * YearFraction) * Distribution.NormCDF(-d1);
	}
	
	
	public static double BlackScholesPutPrice(double Spot, double Strike, double YearFraction, double Volatility) {
		double d1 = (Math.log(Spot / Strike) + (0.5 * Math.pow(Volatility,2)) * YearFraction) / Volatility / Math.sqrt(YearFraction);
		double d2 = d1 - Volatility * Math.sqrt(YearFraction);
		return Strike * Distribution.NormCDF(- d2) - Spot * Distribution.NormCDF(-d1);
	}
	
	public static double BlackScholesPutIV(double Spot, double Strike, double YearFraction, double PutPrice) {
		
		if(PutPrice <= Strike - Spot) return 0;
		
		if(PutPrice == 0) return 0;
		
		double PxTolerance = 0.00001d * Spot;
		double Increment = 0.00001d;
		double InitialGuess = 0.5d;
		double thisIV = InitialGuess;
		double thisPutPrice = BlackScholesPutPrice(Spot, Strike, YearFraction, thisIV);
		
		int MaxIter = 20;
		int CountIter = 0;
		
		while(Math.abs(PutPrice - thisPutPrice) >= PxTolerance) {
			CountIter += 1;
			if(CountIter>MaxIter) {return 0;}
			double thisIVP = thisIV + Increment; // 0.001% Increment of IV
			double thisPutPriceP = BlackScholesPutPrice(Spot, Strike, YearFraction, thisIVP);
			double dPutPx = (thisPutPriceP - thisPutPrice) / Increment;
			thisIV = thisIV - (thisPutPrice - PutPrice) / dPutPx;
			thisPutPrice = BlackScholesPutPrice(Spot, Strike, YearFraction, thisIV);
		}
		
		if(thisIV == Double.NEGATIVE_INFINITY || thisIV == Double.POSITIVE_INFINITY) {thisIV = 0;} 
		
		return thisIV;
		
	}
	
	
}
