package com.lloyd.DeriMarketData.Instruments;

import java.text.ParseException;

import com.lloyd.DeriMarketData.Utilities.DateUtilities;

public class EuropeanOption {
	
	enum Direction{
		PUT,
		CALL
	}
	
	private double thisStrike;
	private double thisYearFraction;
	private Direction thisPutOrCall;	
		
	public void main(double Strike, double YearFraction, Direction PutOrCall) {
		thisStrike = Strike;
		thisYearFraction = YearFraction;
		thisPutOrCall = PutOrCall;
	}
	
	public void main(double Strike, String DateStringDeribit, Direction PutOrCall) {
		thisStrike = Strike;
		
		try {
			thisYearFraction = DateUtilities.DateStringDeribitToYF(DateStringDeribit);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		thisPutOrCall = PutOrCall;
	}
	
}
