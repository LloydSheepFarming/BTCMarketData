package com.lloyd.DeriMarketData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.lloyd.DeriMarketData.DataStructure.BookSummaryQuote;
import com.lloyd.DeriMarketData.Maths.BlackScholesFormulae;
import com.lloyd.DeriMarketData.Utilities.DateUtilities;
 
@Controller
@SpringBootApplication
public class Main {

	public static void main(String[] args) throws ParseException {
		
		
		double YF = DateUtilities.DateStringDeribitToYF("25DEC20");
		double Spot = 10749d;
		double Strike = 10000d;
		double Vol = 0.625;
		
		double CallPx = BlackScholesFormulae.BlackScholesCallPrice(Spot, Strike, YF, Vol);
		double IV = BlackScholesFormulae.BlackScholesCallIV(Spot, Strike, YF, CallPx);
		
		System.out.println(CallPx);
		System.out.println(IV);
		
		//return;
		
		SpringApplication.run(Main.class, args);

	}
	
	@RequestMapping("/")
	@ResponseBody
	String root() {
		return "<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" integrity=\"sha384-JcKb8q3iqJ61gNV9KGb8thSsNjpSL0n8PARn9HuZOnIxN0hoP+VmmDGMN5t9UJ0Z\" crossorigin=\"anonymous\"> <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js\" integrity=\"sha384-aJ21OjlMXNL5UyIl/XNwTMqvzeRMZH2w8c5cRVpzpU8Y5bApTppSuUkhZXN0VxHd\" crossorigin=\"anonymous\"></script>" + 
				"<a href=\"/listTickers/BTC/\">List BTC</a><br/><a href=\"/listTickers/ETH/\">List ETH</a>";
	}
	
	@RequestMapping("/listTickers/")
	@ResponseBody
	String redirectRoot() {
		return "<script>window.location.href = \"/\"</script>";
	}
	
	@RequestMapping("/listTickers/{Ccy}")
	@ResponseBody
	String redirectRoot(@PathVariable String Ccy) {
		return "<script>window.location.href = \"/listTickers/" + Ccy + "/ALL\"</script>";
	}
	
	@RequestMapping("/listTickers/{Ccy}/{Expiry}")
	@ResponseBody
	String listTickers(@PathVariable String Ccy, @PathVariable String Expiry) throws Exception{
		
		long timerStart = System.currentTimeMillis();
		String strResult = "";
		
		String urlOption = "https://www.deribit.com/api/v2/public/get_book_summary_by_currency?currency=" + Ccy + "&kind=option";
		String result = HttpClient.doGet(urlOption);
		
		Gson gson = new Gson();
		BookSummaryQuote thisQuote = gson.fromJson(result, BookSummaryQuote.class);
		thisQuote.ComputeVol();
		
		long timerDataEnd = System.currentTimeMillis();
		
		Map<String, Integer> ContractToID = new HashMap<String, Integer>();
		for(int i=0; i<thisQuote.result.length; i++) {
			ContractToID.put(thisQuote.result[i].instrument_name, i);
		}

		Map<String, ArrayList<Long>> expiryStrikeList = ExpiryStrikeList(ContractToID);

		ArrayList<String> sortedExpiry = SortExpiry(ContractToID);
		
		
		strResult +="<table style='text-align: left;' class=\"table table-striped table-hover table-light\">";
		strResult += "<tr><th>Expiry</th></tr>"+
				"<tr><td><a href = \"/listTickers/"+ Ccy +"/ALL\">ALL</a><td></tr>";
		for(int j=0; j<sortedExpiry.size(); j++) {
			
			String thisExpiry = sortedExpiry.get(j);
			
			strResult += "<tr><td><a href = \"/listTickers/"+ Ccy +"/"  + thisExpiry + "\">" + thisExpiry + "</a><td></tr>";
		}
		strResult +="</table>";
		
		for(int j=0; j<sortedExpiry.size(); j++) {
			
			String thisExpiry = sortedExpiry.get(j);
			
			if (!(thisExpiry.equalsIgnoreCase(Expiry)) && !(Expiry.equalsIgnoreCase("ALL"))) {				
				continue;
			}
			
			strResult = strResult.concat("<p><table style='text-align: center;' class=\"table table-striped table-hover table-dark\"><tr><th style=\"width:10%\">Bid</th><th style=\"width:10%\">Ask</th><th style=\"width:10%\">Bid Vol</th><th style=\"width:10%\">Ask Vol</th><th style=\"width:20%\">"+ thisExpiry + "<th style=\"width:10%\">Bid Vol</th><th style=\"width:10%\">Ask Vol</th><th style=\"width:10%\">Bid</th><th style=\"width:10%\">Ask</th></tr><tr>");
			
			ArrayList<Long> thisStrikes = expiryStrikeList.get(thisExpiry);

			Collections.sort(thisStrikes, new Comparator<Long>() {
				public int compare(Long left, Long right) {
					return left > right ? 1:-1; 
				}
			});
			
			for(int i=0; i<thisStrikes.size(); i++) {
				
				String thisStrike = Long.toString(thisStrikes.get(i)); 
				
				String thisContractMom = Ccy + "-" + thisExpiry + "-" + thisStrike;
				String thisContractC = thisContractMom + "-C";
				String thisContractP = thisContractMom + "-P";
				
				int idC = ContractToID.get(thisContractC);
				int idP = ContractToID.get(thisContractP);

				strResult = strResult.concat("<tr>");
				
				strResult = strResult.concat("<td>");
				strResult = strResult.concat(String.format("%.4f", thisQuote.result[idC].bid_price));
				strResult = strResult.concat("</td><td>");
				strResult = strResult.concat(String.format("%.4f", thisQuote.result[idC].ask_price));
				strResult = strResult.concat("</td><td>");
				strResult = strResult.concat(String.format("%.2f%%", thisQuote.result[idC].bid_vol*100));
				strResult = strResult.concat("</td><td>");
				strResult = strResult.concat(String.format("%.2f%%", thisQuote.result[idC].ask_vol*100));
				strResult = strResult.concat("</td><td>");
				
				strResult = strResult.concat(thisContractMom);
				
				strResult = strResult.concat("</td><td>");
				strResult = strResult.concat(String.format("%.2f%%", thisQuote.result[idP].bid_vol*100));
				strResult = strResult.concat("</td><td>");
				strResult = strResult.concat(String.format("%.2f%%", thisQuote.result[idP].ask_vol*100));
				strResult = strResult.concat("</td><td>");
				strResult = strResult.concat(String.format("%.4f", thisQuote.result[idP].bid_price));
				strResult = strResult.concat("</td><td>");
				strResult = strResult.concat(String.format("%.4f", thisQuote.result[idP].ask_price));
				
				strResult = strResult.concat("</td>");
				
				strResult = strResult.concat("</tr>");
				
			}
			
			strResult = strResult.concat("</table>");
			
		}
		
		
		long timerEnd = System.currentTimeMillis();
		String strHeader =  "<div><a href=\"/\">Home</a></div>"  
				+  "<div>Data Retrieval: " + Long.toString((timerDataEnd - timerStart)).toString().concat("ms") + "</div>"
				+ "<div>Server Process: "+ Long.toString((timerEnd - timerDataEnd)).toString() + "ms</div>"
				+ "<div id=\"BrowserTime\">Browser Roundtrip: </div>"
				+ "<div>Data Time: "+TimeString(thisQuote.usOut/1000) + "</div>";
		
		strResult = strHeader +  strResult;
		
		strResult += "<script>window.onload=updateBrowserTime;function updateBrowserTime(){document.getElementById(\"BrowserTime\").innerHTML=\"Browser Roundtrip: \"+(performance.timing.responseEnd - performance.timing.requestStart)+\"ms\"}</script>";
		
		strResult = "<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" integrity=\"sha384-JcKb8q3iqJ61gNV9KGb8thSsNjpSL0n8PARn9HuZOnIxN0hoP+VmmDGMN5t9UJ0Z\" crossorigin=\"anonymous\"> <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js\" integrity=\"sha384-aJ21OjlMXNL5UyIl/XNwTMqvzeRMZH2w8c5cRVpzpU8Y5bApTppSuUkhZXN0VxHd\" crossorigin=\"anonymous\"></script>"
					+ strResult;
		
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

		SimpleDateFormat formatter = new SimpleDateFormat("ddMMMyy");
		
		Collections.sort(AllExpiries, new Comparator<String>() {
		    public int compare(String left, String right) {
		    	try {
					return Long.compare(formatter.parse(left).toInstant().toEpochMilli(), formatter.parse(right).toInstant().toEpochMilli());
				} catch (ParseException e) {
					e.printStackTrace();
					return 0;
				}
		    }
		});
		
		return AllExpiries;
	}

	private Map<String, ArrayList<Long>> ExpiryStrikeList(Map<String, Integer> ContractIDMap) throws ParseException{
		
		Map<String, ArrayList<Long>> expiryStrikeList = new HashMap<String, ArrayList<Long>>();
		
		ArrayList<String> SortedExpiries = SortExpiry(ContractIDMap);
		for(int i = 0; i < SortedExpiries.size(); i++) {
			expiryStrikeList.put(SortedExpiries.get(i), new ArrayList<Long>());
		}
		
		ArrayList<String> AllContracts = new ArrayList<String>(ContractIDMap.keySet());
		for(int i = 0; i < AllContracts.size(); i++) {
			
			String[] thisContractInfo = AllContracts.get(i).split("-");
			
			String thisContractExpiry = thisContractInfo[1];
			Long thisContractStrike = Long.parseLong(thisContractInfo[2]);
			
			if(!expiryStrikeList.get(thisContractExpiry).contains(thisContractStrike)) {
				expiryStrikeList.get(thisContractExpiry).add(thisContractStrike);
			}
		}
		return expiryStrikeList;
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
