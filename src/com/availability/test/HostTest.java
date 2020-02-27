/**
 * 
 */
package com.availability.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.availability.Host;

/**
 * @author jecharles
 *
 */

@RunWith(JUnit4.class)
public class HostTest {

	private Host host;

	@Before
	public void setup() {
		host = new Host("host1", 5000, "INTEL");
	}

	@Test
	public void memoryTest() {

		host.setMemory(0);
		assertEquals("Host memory is 0", 0, host.getMemory());

		host.setMemory(1000);
		assertEquals("Host memory is 1000", 1000, host.getMemory());
	}

	@Test
	public void processorNameTest() {
		String processorName = null;
		host.setProcessorName(processorName);
		assertNull(host.getProcessorName());

		processorName = "";
		host.setProcessorName(processorName);
		assertEquals("Processor name is empty", processorName, host.getProcessorName());

		processorName = "AMD";
		host.setProcessorName(processorName);
		assertEquals("Processor name is AMD", processorName, host.getProcessorName());
	}

	@Test
	public void hostNameTest() {

		host = new Host(null, 5000, "INTEL");
		assertNull(host.getHostName());

		host = new Host("", 5000, "INTEL");
		assertEquals("Host name is empty", "", host.getHostName());

		host = new Host("host2", 5000, "INTEL");
		assertEquals("Host name is host2", "host2", host.getHostName());
	}

	@Test
	public void addFileTest() {
		String replicatedHost3 = "host3";
		String replicatedHost4 = "host4";
		String replicatedHost5 = "host5";
		
		String[] filenames = {"/file1", "/file2", "/file3"};
		
		String file1 = "/file1";
		String file2 = "/file2";
		String file3 = "/file3";
		
		host.addFile(file1, replicatedHost3);
		host.addFile(file2, replicatedHost3);
		
		host.addFile(file3, replicatedHost4);
		
		Set<String> content = host.getContent();
		assertNotNull(content);
		assertEquals("This host have 3 files", 3, content.size());
		int position = Arrays.binarySearch(filenames, content.iterator().next());
		assertTrue("Validate atleast one file name", position >=0 && position < 3);
		
		Set<String> fileNamesByReplica = host.getFilesUsingReplicaHost(Arrays.asList(replicatedHost3));
		assertNotNull(fileNamesByReplica);
		assertEquals("This replica host have 2 files", 2, fileNamesByReplica.size()); 
		
		fileNamesByReplica = host.getFilesUsingReplicaHost(Arrays.asList(replicatedHost5));
		assertTrue(fileNamesByReplica.size() == 0);
		
		try {
			host.addFile(null, replicatedHost3);
			assertFalse("Should throw exception when the file is null", true);
		} catch(IllegalArgumentException exception) {
			assertEquals("File cannot be null", exception.getMessage());
		}
		
	}

	@Test
	public void addFilesTest() {
		//host.addFiles(files, replicatedHost);
	}


	@After
	public void clean() {
		if (host != null) {
			host = null;
		}
	}

}
