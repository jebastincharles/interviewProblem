/**
 * 
 */
package com.availability;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the host information and also have access methods to do operation on host.
 * @author jecharles
 *
 */
public class Host {

	// name of the host.
	private String hostName;

	// memory present in the host.
	private long memory;

	// processor name present in the host.
	private String processorName;
	/**
	 * Map of the files that are present in this host with replication host name. Will have host, List<Filename>
	 */
	private Map<String, Set<String>> replicationInfo = new ConcurrentHashMap<>();

	// filename with file content.
	private ConcurrentHashMap<String, File> content = new ConcurrentHashMap<>();

	public Host(String hostName, long memory, String processorName) {
		super();
		this.hostName = hostName;
		this.memory = memory;
		this.processorName = processorName;
	}

	/**
	 * Get the memory present in host.
	 * @return - memory size.
	 */
	public long getMemory() {
		return memory;
	}

	/**
	 * Set the memory present in this host.
	 * @param memory - memory of the host.
	 */
	public void setMemory(long memory) {
		this.memory = memory;
	}

	/**
	 * get processor that is used in this host.
	 * @return - name of processor present in this host.
	 */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * set processor that is used in this host.
	 * @param processorName - name of processor present in this host.
	 */
	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	/**
	 * get name of host.
	 * @return - host name.
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * this  returns the file names present in the host. since we are interested in that.
	 * @return - filenames present in ths host
	 */
	public Set<String> getContent() {
		return content.keySet();
	}

	/**
	 * Add new file to this host
	 * @param filename -  file that needs to be added to this host.
	 * @param replicatedHost - hold the reference of host where it has the copy.
	 * @throws IllegalArgumentException - when file is null.
	 */
	public void addFile(String filename, String replicatedHost) {
		if (filename == null || "".equals(filename)) {
			throw new IllegalArgumentException("File cannot be null");
		}
		File alreadyPresentFile = this.content.get(filename);

		if (alreadyPresentFile == null) {
			this.content.put(filename, new File(filename));
		}
		Set<String> fileNameList = replicationInfo.getOrDefault(replicatedHost, new HashSet<String>());
		fileNameList.add(filename);
		replicationInfo.put(replicatedHost, fileNameList);

	}

	/**
	 * Add files to replication host
	 * @param files - list of file path that needs to added
	 * @param replicatedHost - hold the reference of host where it has the copy.
	 */
	public void addFiles(Set<String> files, String replicatedHost) {
		if (files == null || files.size() == 0) {
			throw new IllegalArgumentException("Files cannot be null");
		}
		files.forEach(file -> addFile(file, replicatedHost));
	}

	/**
	 * Use the replication host, get all the files present in this host and the given replication host.
	 * @param replicationHostName - name of replication host name.
	 * @return - List of files present in this host as well as replication host.
	 */
	public Set<String> getFilesUsingReplicaHost(List<String> hostNames) {
		Set<String> files = new HashSet<String>();
		for (String hostname : hostNames) {
			Set<String> affectedFiles = replicationInfo.get(hostname);
			if (affectedFiles != null) {
				files.addAll(affectedFiles);
			}
		}
		return files;
	}

}
