/**
 * 
 */
package com.availability;

import java.util.Set;

/**
 * Model to hold the data that needs to be replicated to target host from source host
 * @author jecharles
 *
 */
public class ReBalanceData {

	// file names that need to be rebalanced.
	private Set<String> fileNames;

	// host where file is present.
	private String sourceHost;

	// host where file needs to be replicated.
	private String targetHost;

	public ReBalanceData(Set<String> fileName, String sourceHost, String targetHost) {
		super();
		this.fileNames = fileName;
		this.sourceHost = sourceHost;
		this.targetHost = targetHost;
	}

	/**
	 * get  file names that needs to be rebalanced.
	 * @return - list of filenames
	 */
	public Set<String> getFileNames() {
		return fileNames;
	}

	/**
	 * set file names that needs to be rebalanced.
	 * @param fileNames - list of filenames
	 */
	public void setFileNames(Set<String> fileName) {
		this.fileNames = fileName;
	}

	/**
	 * get source host name where files already present.
	 * @return - source host name
	 */
	public String getSourceHost() {
		return sourceHost;
	}

	/**
	 * get source host name where files already present.
	 * @return - source host name
	 */
	public void setSourceHost(String sourceHost) {
		this.sourceHost = sourceHost;
	}

	/**
	 * get target host name where files needs to be copied.
	 * @return - target host name
	 */
	public String getTargetHost() {
		return targetHost;
	}

	/**
	 * set target host name where files needs to be copied
	 * @param targetHost - target host name
	 */
	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	/**
	 * String represntation of reBalanceData.
	 */
	@Override
	public String toString() {

		if (fileNames == null || fileNames.size() == 0) {
			return "";
		}
		StringBuilder toPrint = new StringBuilder();
		for (String file : fileNames) {
			toPrint.append(file);
			toPrint.append(" , ");
			toPrint.append(sourceHost);
			toPrint.append(" , ");
			toPrint.append(targetHost);
			toPrint.append("\n");
		}
		return toPrint.toString();
	}

}
