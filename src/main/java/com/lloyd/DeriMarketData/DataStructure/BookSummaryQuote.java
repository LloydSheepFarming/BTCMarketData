package com.lloyd.DeriMarketData.DataStructure;

public class BookSummaryQuote {
	
	public String jsonrpc;
	
	public long usIn;
	public long usOut;
	public long usDiff;
	
	public boolean testnet;
	
	public OptionQuote[] result;
	
}
