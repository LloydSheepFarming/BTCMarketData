package com.lloyd.DeriMarketData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.lloyd.DeriMarketData.DataStructure.BookSummaryQuote;

@Controller
@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);

	}
	
	@RequestMapping("/")
	String root() {
		return "It Works!";
	}
	
	@RequestMapping("/listTickers")
	String listTickers() throws Exception{
		
		long timerStart = System.currentTimeMillis();
		
		String strResult = "";
		
		strResult = strResult.concat("<!-- CSS only --> <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" integrity=\"sha384-JcKb8q3iqJ61gNV9KGb8thSsNjpSL0n8PARn9HuZOnIxN0hoP+VmmDGMN5t9UJ0Z\" crossorigin=\"anonymous\"> <!-- JS, Popper.js, and jQuery --> <script src=\"https://code.jquery.com/jquery-3.5.1.slim.min.js\" integrity=\"sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj\" crossorigin=\"anonymous\"></script> <script src=\"https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js\" integrity=\"sha384-9/reFTGAW83EW2RDu2S0VKaIzap3H66lZH81PoYlFhbGU+6BZp6G7niu735Sk7lN\" crossorigin=\"anonymous\"></script> <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js\" integrity=\"sha384-B4gt1jrGC7Jh4AgTPSdUtOBvfO8shuf57BaghqFfPlYxofvL8/KUEfYiJOMMV+rV\" crossorigin=\"anonymous\"></script>");
		
		String urlOption = "https://www.deribit.com/api/v2/public/get_book_summary_by_currency?currency=BTC&kind=option";
		String result = HttpClient.doGet(urlOption);
		
		Gson gson = new Gson();
		BookSummaryQuote thisQuote = gson.fromJson(result, BookSummaryQuote.class);
	    
		strResult = strResult.concat(TimeString(thisQuote.usOut/1000));
		strResult = strResult.concat("<p>");
		
		strResult = strResult.concat("<table><tr><th>Contract</th><th>Bid</th><th>Ask</th><th>Mid</th><th>Mark</th></tr><tr>");
		
		
		Map<String, Integer> ContractToID = new HashMap<String, Integer>();
		
		for(int i=0; i<thisQuote.result.length; i++) {
			ContractToID.put(thisQuote.result[i].instrument_name, i);
		}
		
		ArrayList<String>SortedExpiries = SortExpiry(ContractToID);
		
		ArrayList<Long>SortedStrikes = SortStrike(ContractToID);
		
		strResult = strResult.concat(SortedExpiries.toString());
		strResult = strResult.concat("<p>");
		
		strResult = strResult.concat(SortedStrikes.toString());
		strResult = strResult.concat("<p>");
		
		for(int i=0; i<thisQuote.result.length; i++) {
			
			strResult = strResult.concat("<tr>");
			
			strResult = strResult.concat("<td>");
			strResult = strResult.concat(thisQuote.result[i].instrument_name);
			strResult = strResult.concat("</td><td>");
			strResult = strResult.concat(Double.toString(thisQuote.result[i].bid_price));
			strResult = strResult.concat("</td><td>");
			strResult = strResult.concat(Double.toString(thisQuote.result[i].ask_price));
			strResult = strResult.concat("</td><td>");
			strResult = strResult.concat(Double.toString(thisQuote.result[i].mid_price));
			strResult = strResult.concat("</td><td>");
			strResult = strResult.concat(Double.toString(thisQuote.result[i].mark_price));
			strResult = strResult.concat("</td>");
			
			strResult = strResult.concat("</tr>");
			
		}
		
		strResult = strResult.concat("</table>");
		
		long timerEnd = System.currentTimeMillis();
		String timerDiff = Long.toString((timerEnd - timerStart)).toString().concat("ms");
		
		strResult = strResult.concat("</table>");
		
		strResult = timerDiff.concat(strResult);
		return strResult;
	}
	
	private String TimeString(long Millis) {
		SimpleDateFormat formatter= new SimpleDateFormat("dd-MMM-yyyy HHmmssz");
		Date date = new Date(Millis);
		return formatter.format(date);
	}
	
	private ArrayList<String> SortExpiry(Map<String, Integer> ContractIDMap) throws ParseException{
		
		ArrayList<String> AllContracts = new ArrayList<String>(ContractIDMap.keySet());
				
		ArrayList<String> AllExpiries = new ArrayList<String>();
		
		for(int i = 0; i < AllContracts.size(); i++) {
			String thisContract = AllContracts.get(i);
			String thisExpiry = thisContract.split("-")[1];
			if(!AllExpiries.contains(thisExpiry)) {
				AllExpiries.add(thisExpiry);
			}
		}
		
		ArrayList<Long> DateExpiries = new ArrayList<Long>();
		for(int i = 0; i<AllExpiries.size();i++) {
			String thisExpiry = AllExpiries.get(i);
			SimpleDateFormat formatter = new SimpleDateFormat("ddMMMyy");
			Date thisDate = formatter.parse(thisExpiry); 
			DateExpiries.add(thisDate.toInstant().toEpochMilli());
		}
		
		Collections.sort(AllExpiries, new Comparator<Object>() {
		    public int compare(Object left, Object right) {
		    	return Long.compare(DateExpiries.get(AllExpiries.indexOf(left)), DateExpiries.get(AllExpiries.indexOf(right)));
		    }
		});
		
		System.out.println(DateExpiries.toString());
		System.out.println(AllExpiries.toString());
		return AllExpiries;
	}
	
	private ArrayList<Long> SortStrike(Map<String, Integer> ContractIDMap) {
		
		ArrayList<String> AllContracts = new ArrayList<String>(ContractIDMap.keySet());
				
		ArrayList<Long> AllStrikes = new ArrayList<Long>();
		
		for(int i = 0; i < AllContracts.size(); i++) {
			String thisContract = AllContracts.get(i);
			Long thisStrike = Long.parseLong(thisContract.split("-")[2]);
			if(!AllStrikes.contains(thisStrike)) {
				AllStrikes.add(thisStrike);
			}
		}

		Collections.sort(AllStrikes, new Comparator<Long>() {
		    public int compare(Long left, Long right) {
		    	return Long.compare(left, right);
		    }
		});
		
		return AllStrikes;
	}
	
}
