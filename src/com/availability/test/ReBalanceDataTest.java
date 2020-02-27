/**
 * 
 */
package com.availability.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.availability.ReBalanceData;

/**
 * @author jecharles
 *
 */
@RunWith(JUnit4.class)
public class ReBalanceDataTest {

	private ReBalanceData data;

	@Before
	public void setup() {
		data = new ReBalanceData(null, "sourceHost", "targetHost");
	}

	@Test
	public void fileNamesTest() {

		data.setFileNames(null);
		assertNull(data.getFileNames());

		Set<String> fileNames = new HashSet<>();
		data.setFileNames(fileNames);
		assertNotNull(data.getFileNames());
		assertEquals("Filename size is zero", 0, data.getFileNames().size());

		fileNames.add("FileName1");
		fileNames.add("FileName2");
		data.setFileNames(fileNames);
		assertNotNull(data.getFileNames());
		assertEquals("Filename size is two", 2, data.getFileNames().size());
		List<String> validFileNames = new ArrayList<String>(data.getFileNames());
		assertTrue("Filename1 validation", validFileNames.contains("FileName1"));
		assertTrue("Filename2 validation", validFileNames.contains("FileName2"));
	}

	@Test
	public void sourceHostTest() {
		data.setSourceHost(null);
		assertNull(data.getSourceHost());

		String sourceHost = "";
		data.setSourceHost(sourceHost);
		assertNotNull(sourceHost);
		assertEquals("Source host is empty", sourceHost, data.getSourceHost());

		sourceHost = "host1";
		data.setSourceHost(sourceHost);
		assertNotNull(sourceHost);
		assertEquals("Source host is host1", sourceHost, data.getSourceHost());
	}

	@Test
	public void targetHostTest() {
		data.setTargetHost(null);
		assertNull(data.getTargetHost());

		String targetHost = "";
		data.setTargetHost(targetHost);
		assertNotNull(targetHost);
		assertEquals("Target host is empty", targetHost, data.getTargetHost());

		targetHost = "host2";
		data.setTargetHost(targetHost);
		assertNotNull(targetHost);
		assertEquals("Source host is host2", targetHost, data.getTargetHost());
	}

	@Test
	public void ReBalanceDataCreationTest() {
		Set<String> fileNames = new HashSet<>();
		fileNames.add("FileName1");
		fileNames.add("FileName2");

		String sourceHost = "host3";

		String targetHost = "host4";

		data = new ReBalanceData(fileNames, sourceHost, targetHost);
		assertNotNull(data);
		assertEquals("Filename size is two", 2, data.getFileNames().size());
		assertEquals("Source host is host3", sourceHost, data.getSourceHost());
		assertEquals("Source host is host4", targetHost, data.getTargetHost());

	}

	@After
	public void clean() {
		if (data != null) {
			data = null;
		}
	}

}
