package com.lloyd.DeriMarketData.DataStructure;

import java.text.ParseException;

import com.lloyd.DeriMarketData.Maths.BlackScholesFormulae;
import com.lloyd.DeriMarketData.Utilities.DateUtilities;

public class BookSummaryQuote {
	
	public String jsonrpc;
	
	public long usIn;
	public long usOut;
	public long usDiff;
	
	public boolean testnet;
	
	public OptionQuote[] result;
	
	public void ComputeVol() throws ParseException {
		for(int i = 0; i< result.length; i++) {
			OptionQuote thisQuote = this.result[i];
			String[] thisQuoteInfo = thisQuote.instrument_name.split("-");
			
			String thisExpiry = thisQuoteInfo[1];
			double thisStrike = Double.parseDouble(thisQuoteInfo[2]);
			String PutOrCall = thisQuoteInfo[3];
			
			double thisYF = DateUtilities.DateStringDeribitToYF(thisExpiry);
			
			if(PutOrCall.equals("C")) {
				thisQuote.bid_vol = BlackScholesFormulae.BlackScholesCallIV(thisQuote.underlying_price, thisStrike, thisYF, thisQuote.bid_price * thisQuote.estimated_delivery_price);
				thisQuote.ask_vol = BlackScholesFormulae.BlackScholesCallIV(thisQuote.underlying_price, thisStrike, thisYF, thisQuote.ask_price * thisQuote.estimated_delivery_price);
			}else if(PutOrCall.equals("P")) {
				thisQuote.bid_vol = BlackScholesFormulae.BlackScholesPutIV(thisQuote.underlying_price, thisStrike, thisYF, thisQuote.bid_price * thisQuote.estimated_delivery_price);
				thisQuote.ask_vol = BlackScholesFormulae.BlackScholesPutIV(thisQuote.underlying_price, thisStrike, thisYF, thisQuote.ask_price * thisQuote.estimated_delivery_price);
			}
		}
	}
	
}
