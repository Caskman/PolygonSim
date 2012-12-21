package caskman.polygonsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Profiler {

	static List<String> names;
	static List<Long> sums;
	static List<Long> counts;
	static List<Float> percentages;
	static long timer;
	static boolean initialized = false;
	static boolean useFrame;


	public static void initialize(boolean useFrame) {
		names = new ArrayList<String>();
		sums = new ArrayList<Long>();
		counts = new ArrayList<Long>();
		initialized = true;
		useFrame = useFrame;
	}

	public static void start() {
		if (!initialized)
			return;
		timer = System.currentTimeMillis();
	}

	public static void lap(String name) {
		if (!initialized)
			return;
		long end = System.currentTimeMillis();
		int index = names.indexOf(name);
		if (index < 0) {
			index = names.size();
			names.add(name);
			sums.add(0L);
			counts.add(0L);
		}

		long before = sums.get(index);
		sums.set(index,sums.get(index) + end - timer);
		if (sums.get(index) < before)
			throw new IndexOutOfBoundsException();// overflow
		counts.set(index,counts.get(index) + 1);
	}

	public static void lapRestart(String name) {
		if (!initialized)
			return;
		long end = System.currentTimeMillis();
		int index = names.indexOf(name);
		if (index < 0) {
			index = names.size();
			names.add(name);
			sums.add(0L);
			counts.add(0L);
		}

		long before = sums.get(index);
		sums.set(index,sums.get(index) + end - timer);
		if (sums.get(index) < before)
			throw new IndexOutOfBoundsException();// overflow
		counts.set(index,counts.get(index) + 1);
		timer = System.currentTimeMillis();
	}

	public static void close() {
		if (!initialized)
			return;
		Results results;
		if (useFrame)
			results = new ResultsFrame();
		else
			results = new ResultsToFile();
		calcPercentages();

		for (int i = 0 ; i < names.size(); i++) {
			String profile = names.get(i) + "\n\t\t\tSamples = " + counts.get(i) + "\tTime = " + sums.get(i) + "\t Percent = " + percentages.get(i) + "\n";
			results.print(profile);

		}
	}

	private static void calcPercentages() {
		percentages = new ArrayList<Float>();
		long sum = 0;
		for (Long l : sums) {
			sum += l;
		}

		for (int i = 0; i < sums.size(); i++) {
			percentages.add(((float)sums.get(i))/((float)sum));
		}
	}
}