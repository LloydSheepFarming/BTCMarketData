package com.lloyd.DeriMarketData.Utilities;

import java.util.Date;
import java.util.TimeZone;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtilities {
	
	public static Date GetTimeNow() {
		Date date = new Date();
		return date;
	} 
	
	public static String DateToDateTimeString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");  
		return formatter.format(date);
	}
	
	public static String DateToDateString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");  
		return formatter.format(date);
	}
	
	public static String DateToDateStringDeribit(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMMyy");  
		return formatter.format(date);
	}
	
	public static Date DateStringDeribitToDate(String DeribitDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMMyy'T'HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return formatter.parse(DeribitDate + "T08:00:00");
	}
	
	public static double YearBetween(Date StartDate, Date EndDate) {
		return (EndDate.toInstant().toEpochMilli() - StartDate.toInstant().toEpochMilli()) / 31556952000d;
		
	}
	
	public static double DateStringDeribitToYF(String DeribitDate) throws ParseException {
		Date sd = GetTimeNow();
		Date ed = DateStringDeribitToDate(DeribitDate);
		return YearBetween(sd, ed);
	}
		
}
