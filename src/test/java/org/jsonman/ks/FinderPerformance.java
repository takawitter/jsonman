package org.jsonman.ks;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.jsonman.Node;
import org.jsonman.NodeFactory;
import org.jsonman.NodeFinder;
import org.jsonman.NodeFinderTest;
import org.jsonman.finder.Reference;
import org.jsonman.util.BiConsumer;

public class FinderPerformance {
	public static void main(String[] args) throws Exception{
		int n = 100000;
		MemoryMXBean mmx = ManagementFactory.getMemoryMXBean();
		String path = "/tag";
		BiConsumer<List<Reference>, Node> consumer = new BiConsumer<List<Reference>, Node>() {
			@Override
			public void accept(List<Reference> value1, Node value2) {
				found++;
			}
		};
		byte[] bytes = null;
		try(InputStream is = NodeFinderTest.class.getResourceAsStream("NodeFilteringTest_1.json")){
			bytes = new byte[is.available()];
			is.read(bytes);
		}
		long sumMSec = 0, sumHeap = 0;
		for(int j = 0; j < 10; j++){
			found = 0;
			long s = System.nanoTime();
			for(int i = 0; i < n; i++){
				new NodeFinder(NodeFactory.create(JSON.decode(new ByteArrayInputStream(bytes)))).find(path, consumer);
			}
			long d = System.nanoTime() - s;
			long h = mmx.getHeapMemoryUsage().getUsed();
			System.out.println(String.format(
					"obj %d msec. %d times found. used heap: %d",
					d / 1000000, found, h));
			sumMSec += d / 1000000;
			sumHeap += h;
		}
		System.out.println(String.format("ave msec: %d  heap: %d", sumMSec / 10, sumHeap / 10));
		mmx.gc();
		Thread.sleep(100);
		mmx.gc();
		Thread.sleep(100);
		mmx.gc();
		Thread.sleep(100);

		sumMSec = 0;
		sumHeap = 0;
		for(int j = 0; j < 10; j++){
			found = 0;
			long s = System.nanoTime();
			for(int i = 0; i < n; i++){
				new NodeFinderByJSONParser(new ByteArrayInputStream(bytes)).find(path, consumer);
			}
			long d = System.nanoTime() - s;
			long h = mmx.getHeapMemoryUsage().getUsed();
			System.out.println(String.format(
					"par %d msec. %d times found. used heap: %d",
					d / 1000000, found, h));
			sumMSec += d / 1000000;
			sumHeap += h;
		}
		System.out.println(String.format("ave msec: %d  heap: %d", sumMSec / 10, sumHeap / 10));
	}

	private static int found;
}
