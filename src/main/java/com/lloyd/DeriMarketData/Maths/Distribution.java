package com.lloyd.DeriMarketData.Maths;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Distribution {
	public static double NormCDF(double t) {
		NormalDistribution thisDist = new NormalDistribution(0,1);
		return thisDist.cumulativeProbability(t);			
	}
}
