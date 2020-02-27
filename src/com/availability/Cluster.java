/**
 * 
 */
package com.availability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Cluster holds list of host and each host hold list of files. For the given
 * problem , the following operations are considered 
 * 1. removeHost 
 * 2. RebalaceData
 * 
 * RebalanceData is made public, so if needed it can used as individual function from external.
 * 
 * But indirectly, the problem expects addFile method too.
 * 
 * Assumptions : 1. The file copy functionality is not added, since this problem
 * revolves around how files are highly available in cluster.
 * 
 * @author jecharles
 *
 */
public class Cluster {

	// name of cluster
	private String clusterName;

	// Hold the host name and host information
	private Map<String, Host> clusterHosts = new ConcurrentHashMap<String, Host>();

	public Cluster(String clusterName) {
		super();
		this.clusterName = clusterName;
	}

	/**
	 * Add new host into cluster
	 * 
	 * @param host - Host (Contains hostname). Initally files will not be present.
	 */
	public void addHost(Host host) {
		if (host == null || isEmpty(host.getHostName())) {
			throw new IllegalArgumentException("host should have valid information");
		}
		if (clusterHosts.get(host.getHostName()) != null) {
			throw new IllegalArgumentException("Host already exists");
		}
		clusterHosts.put(host.getHostName(), host);
	}

	/**
	 * This method removes the given host from the cluster and return information
	 * which contains files that are present in lost file , new host where the file
	 * can be added. This methods validates that hostname is valid or not. This
	 * method wont allow to remove any . if the cluster have less than or equal to 2
	 * host
	 * 
	 * @param hostNames - List<String>, hosts that went down.
	 * @return List<ReBalanceData> - information which contains files that are
	 *         present in lost file , new host and source host where file is
	 *         present.
	 * @throws IllegalArgumentException - when hostname is not valid
	 * @throws IllegalStateException    - when remove operation is called when
	 *                                  cluster have 2 host or less.
	 */
	public boolean removeHost(List<String> hostNames) {

		if (hostNames == null || hostNames.size() == 0) {
			throw new IllegalArgumentException("Hostnames are not valid.");
		}
		if (hostNames.size() > 2) {
			throw new IllegalArgumentException("More than 2 host names cannt go down in same time");
		}

		if (clusterHosts.size() <= 2) {
			throw new IllegalStateException("Removing host leads to loose high stability");
		}

		hostNames.forEach(hostName -> {
			if (isEmpty(hostName)) {
				throw new IllegalArgumentException("host name is not valid.");
			}

			if (clusterHosts.get(hostName) == null) {
				throw new IllegalArgumentException("Host is not associated with this cluster.");
			}
			// remove the lost host from the cluster.
			clusterHosts.remove(hostName);
		});

		// Collect the list of files present in removing hostname.

		Map<String, Set<String>> filesPresentinHost = collectFilesPresentinHost(hostNames);

		// files present in the lost host , new hostname, and source host.
		Optional<List<ReBalanceData>> rebalanceData = buildRebalanceData(filesPresentinHost, hostNames);

		if (rebalanceData.isPresent()) {
			reBalanceData(rebalanceData.get());
		}

		return true;
	}

	/**
	 * replicate the files into the new host.
	 * 
	 * @param datas - information which contains files that are present in lost file
	 *              , new host and source host where file is present.
	 */
	public void reBalanceData(List<ReBalanceData> datas) {

		if (datas == null || datas.size() == 0) {
			return;
		}
		datas.stream().filter(dataStream -> clusterHosts.get(dataStream.getTargetHost()) != null).forEach((data) -> {
			clusterHosts.get(data.getTargetHost()).addFiles(data.getFileNames(), data.getSourceHost());
			clusterHosts.get(data.getSourceHost()).addFiles(data.getFileNames(), data.getTargetHost());
			;
		});
	}

	/**
	 * Add file into cluster. Each file will be added to 2 hosts.
	 * 
	 * @param filepath       - path of file that needs to be added to cluster.
	 * @param sourceHost     - source host where file will be added.
	 * @param replicatedHost - replication host where file will be added.
	 * @throws IllegalArgumentException - if the sourcehost and replication host are
	 *                                  not valid.
	 */
	public void addFile(String filepath, String sourceHost, String replicatedHost) {
		if (clusterHosts.get(sourceHost) == null) {
			throw new IllegalArgumentException(sourceHost + " host not valid.");
		}

		if (clusterHosts.get(replicatedHost) == null) {
			throw new IllegalArgumentException(replicatedHost + " host not valid.");
		}

		if (sourceHost != null && sourceHost.equalsIgnoreCase(replicatedHost)) {
			throw new IllegalArgumentException(
					sourceHost + " host name can not be same as replicated host " + replicatedHost);
		}

		clusterHosts.get(sourceHost).addFile(filepath, replicatedHost);

		clusterHosts.get(replicatedHost).addFile(filepath, sourceHost);

	}

	// All helper methods

	/**
	 * Find and collection files which was present in removed host using other hosts
	 * of cluster.
	 * 
	 * @param removedHosts - hosts that went down.
	 * @return - map of source host and list of files which are present in removed
	 *         host.
	 */
	private Map<String, Set<String>> collectFilesPresentinHost(final List<String> removedHosts) {
		Map<String, Set<String>> fileNameAndSource = new HashMap<>();
		clusterHosts.keySet().stream().filter(name -> !removedHosts.contains(name)).forEach(hostName -> {
			Set<String> fileNameOfHost = clusterHosts.get(hostName).getFilesUsingReplicaHost(removedHosts);
			if (fileNameOfHost != null && fileNameOfHost.size() > 0) {
				fileNameAndSource.put(hostName, fileNameOfHost);
			}
		});
		return fileNameAndSource;
	}

	/**
	 * Find the random host from the list of host. Excluding the source host.
	 * 
	 * @param hosts      - host which are present in cluster.
	 * @param sourceHost - host where the file is present. This host will be
	 *                   excluded from cluster list before finding the new random
	 *                   host.
	 * @return - new host name to save the files.
	 */
	private String findRandomTargetHost(List<String> hosts, String sourceHost) {
		List<String> hostCopy = new ArrayList<String>(hosts);
		hostCopy.remove(sourceHost);
		int randomInt = (int) (Math.random() * hostCopy.size());
		return hostCopy.get(randomInt);
	}

	/**
	 * Build rebalance data. For the given replicated files which got lost when the
	 * host was down, find the new replicated host and bind it together as model.
	 * 
	 * @param filesPresentInOldHost - map of current source and list of files which
	 *                              needs to be replicated.
	 * @param hostNames           - list of hosts that went down.
	 * @return - List<ReBalanceData> . List of rebalanced file and host information.
	 *         If no file present, then the result will be empty..
	 */
	private Optional<List<ReBalanceData>> buildRebalanceData(Map<String, Set<String>> filesPresentInOldHost,
			List<String> hostNames) {
		if (filesPresentInOldHost == null || filesPresentInOldHost.isEmpty()) {
			return Optional.empty();
		}

		List<String> clusterHostNames = new ArrayList<String>(clusterHosts.keySet());
		clusterHostNames.removeAll(hostNames);

		List<ReBalanceData> rebalanceList = filesPresentInOldHost.entrySet().stream()
				.map(entry -> new ReBalanceData(entry.getValue(), entry.getKey(),
						findRandomTargetHost(clusterHostNames, entry.getKey())))
				.collect(Collectors.toList());
		return Optional.of(rebalanceList);
	}

	/**
	 * Method to print the cluster name and its contents.
	 */
	public void print() {

		System.out.println("Cluster name :: " + clusterName);
		if (clusterHosts == null) {
			return;
		}
		String fileNames = null;
		for (Host host : clusterHosts.values()) {
			fileNames = host.getContent().stream().collect(Collectors.joining(","));
			System.out.println(host.getHostName() + " : " + fileNames);
		}
		System.out.println();
	}

	/**
	 * Method to validate whether the string is empty or not.
	 * 
	 * @param data - string
	 * @return boolean - return true is the string is empty . False otherwise.
	 */
	private boolean isEmpty(String data) {
		return data == null || "".equals(data.trim());
	}

	public String getClusterName() {
		return clusterName;
	}

	public Map<String, Host> getClusterHosts() {
		return clusterHosts;
	}
}
