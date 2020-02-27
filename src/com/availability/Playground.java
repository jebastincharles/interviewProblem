/**
 * 
 */
package com.availability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jecharles
 *
 */
public class Playground {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Cluster cus = new Cluster("My cluster");

		System.out.println("****************Test case 1****************");

		cus.addHost(new Host("host1", 1000, "Intel"));
		cus.addHost(new Host("host2", 1000, "Intel"));
		cus.addHost(new Host("host3", 1000, "Intel"));
		cus.addHost(new Host("host4", 1000, "Intel"));

		cus.addFile("/file1", "host1", "host3");
		cus.addFile("/file2", "host1", "host2");
		cus.addFile("/file3", "host2", "host3");
		cus.addFile("/file4", "host1", "host3");
		cus.addFile("/file5", "host1", "host4");

		System.out.println("Input data.......");
		cus.print();

		List<String> removeHosts = new ArrayList<String>();
		removeHosts.add("host2");
		boolean isHostRemoved = cus.removeHost(Arrays.asList("host2"));

		System.out.println("Output data.......");
		cus.print();

	}
	
}
