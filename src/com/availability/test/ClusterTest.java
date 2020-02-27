package com.availability.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.availability.Cluster;
import com.availability.Host;
import com.availability.ReBalanceData;

@RunWith(JUnit4.class)
public class ClusterTest {
	
	private Cluster cluster;
	
	@Before
	public  void setup() {
		cluster = new Cluster("Test Cluster");
	}
	/**
	 * Validate addHost method in Cluster.java
	 */
	@Test
	public void addHostTest() {
		
		Host host1 = new Host("Host1",1000, "INTEL");
		cluster.addHost(host1);
		Map<String, Host> hostMap = cluster.getClusterHosts();
		assertEquals(1, hostMap.size());
		assertEquals(host1.getHostName(), hostMap.entrySet().iterator().next().getValue().getHostName());
		
		try {
			cluster.addHost(null);
			assertFalse("should throw exception with message 'host should have valid information'", true);
		} catch(IllegalArgumentException excp) {
			assertEquals("host should have valid information", excp.getMessage());
		}
		
		try {
			Host host2 = new Host(null, 1000, "INTEL");
			cluster.addHost(host2);
			assertFalse("should throw exception with message 'host should have valid information'", true);
		} catch(IllegalArgumentException excp) {
			assertEquals("host should have valid information", excp.getMessage());
		}
		
		try {
			cluster.addHost(host1);
			assertFalse("should throw exception with message 'Host already exists'", true);
		} catch(IllegalArgumentException excp) {
			assertEquals("Host already exists", excp.getMessage());
		}
	}
	
	
	/**
	 * Validate removeHost method in Cluster.java
	 */
	@Test
	public void removeHostTest() {
		
		Host host10 = new Host("Host10",1000, "INTEL");
		cluster.addHost(host10);
		
		Host host11 = new Host("Host11",1000, "INTEL");
		cluster.addHost(host11);
		
		Host host12 = new Host("Host12",1000, "INTEL");
		cluster.addHost(host12);
		
		Host host13 = new Host("Host13",1000, "INTEL");
		
		Host dummyHost = new Host(null,1000, "INTEL");
		
		
		assertEquals("Total host is 3", 3,  cluster.getClusterHosts().size());
		
		String file1 = "/File1";
		String file2 = "/File2";
		List<String> filenames = new ArrayList<>();
		filenames.add("File1");
		filenames.add("File2");
		cluster.addFile(file1, host10.getHostName(), host11.getHostName());
		cluster.addFile(file2, host12.getHostName(), host11.getHostName());
		
		boolean isSuccess = cluster.removeHost(Arrays.asList(host11.getHostName()));
		assertTrue(isSuccess);
		
		assertEquals("Total host is 2", 2,  cluster.getClusterHosts().size());
		
		try {
			cluster.removeHost(Arrays.asList(host10.getHostName()));
		} catch(IllegalStateException excp) {
			assertEquals("Removing host leads to loose high stability", excp.getMessage());
		}
		
		Host host14 = new Host("Host14",1000, "INTEL");
		cluster.addHost(host14);
		
		try {
			cluster.removeHost(Arrays.asList(host13.getHostName()));
		} catch(IllegalArgumentException excp) {
			assertEquals("Host is not associated with this cluster.", excp.getMessage());
		}
		
		try {
			cluster.removeHost(Arrays.asList(dummyHost.getHostName()));
		} catch(IllegalArgumentException excp) {
			assertEquals("host name is not valid.", excp.getMessage());
		}
		
		try {
			cluster.removeHost(Arrays.asList(dummyHost.getHostName(), host13.getHostName(), host12.getHostName()));
		} catch(IllegalArgumentException excp) {
			assertEquals("More than 2 host names cannt go down in same time", excp.getMessage());
		}
		
	}
	
	/**
	 * Validate reBalanceData method in Cluster.java
	 */
	@Test
	public void reBalanceDataTest() {
		
		Host host9 = new Host("Host9",1000, "INTEL");
		cluster.addHost(host9);
		
		Host host8 = new Host("Host8",1000, "INTEL");
		cluster.addHost(host8);
		
		Set<String> fileNames = new HashSet<>();
		fileNames.add("File5");
		fileNames.add("File6");
		ReBalanceData data = new ReBalanceData(fileNames, host9.getHostName(), host8.getHostName());
		
		
		cluster.reBalanceData(Arrays.asList(data));
		
		assertEquals("Should have 2 files added to host8", 2, cluster.getClusterHosts().get(host8.getHostName()).getContent().size());
	}
	
	/**
	 * Validate addFile method in Cluster.java
	 */
	@Test
	public void addFileTest() {

		Host host1 = new Host("Host1", 1000, "INTEL");
		cluster.addHost(host1);

		Host host2 = new Host("Host2", 1000, "INTEL");
		cluster.addHost(host2);

		Host host3 = new Host("Host3", 1000, "INTEL");
		cluster.addHost(host3);

		Host notAddedHost = new Host("Host4", 1000, "INTEL");

		String file1 = "/file1";
		String file2 = "/file2";
		String file3 = "/file3";
		String file4 = "/file4";
		String file5 = "/file5";

		cluster.addFile(file1, host1.getHostName(), host3.getHostName());

		cluster.addFile(file2, host2.getHostName(), host3.getHostName());
		
		assertEquals("Should have 2 files added to host3", 2, cluster.getClusterHosts().get(host3.getHostName()).getContent().size());
		assertEquals("Should have 1 file added to host1", 1, cluster.getClusterHosts().get(host1.getHostName()).getContent().size());
		assertEquals("Should have 1 file added to host2", 1, cluster.getClusterHosts().get(host2.getHostName()).getContent().size());

		try {
			cluster.addFile(file3, host1.getHostName(), host1.getHostName());
		} catch (IllegalArgumentException excp) {
			assertEquals(host1.getHostName() + " host name can not be same as replicated host " + host1.getHostName(),
					excp.getMessage());
		}

		try {
			cluster.addFile(file4, host1.getHostName(), notAddedHost.getHostName());
		} catch (IllegalArgumentException excp) {
			assertEquals(notAddedHost.getHostName() + " host not valid.", excp.getMessage());
		}
		try {
			cluster.addFile(file5, notAddedHost.getHostName(), host1.getHostName());
		} catch (IllegalArgumentException excp) {
			assertEquals(notAddedHost.getHostName() + " host not valid.", excp.getMessage());
		}
	}
	
	@After
	public void clean() {
		if (cluster != null) {
			cluster = null;
		}
	}

}
