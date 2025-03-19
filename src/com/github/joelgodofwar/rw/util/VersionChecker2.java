package com.github.joelgodofwar.rw.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.joelgodofwar.rw.RotationalWrench;

public class VersionChecker2 {
	@SuppressWarnings("unused") private JavaPlugin plugin;
	private int projectID;
	private String currentVersion;
	private String githubURL;
	private List<String> releaseList = new ArrayList<>();
	private List<String> developerList = new ArrayList<>();
	private String recommendedVersion = "uptodate";
	private RotationalWrench rw;

	public VersionChecker2(RotationalWrench plugin, int projectID, String githubURL) {
		this.rw = plugin;
		this.projectID = projectID;
		this.currentVersion = plugin.getDescription().getVersion();
		this.githubURL = githubURL;
	}

	public String getReleaseUrl() {
		return "https://spigotmc.org/resources/" + projectID;
	}

	public boolean checkForUpdates() throws Exception {
		URL url = new URL(githubURL);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		URLConnection connection = url.openConnection();
		connection.setUseCaches(false);
		connection.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
		Document doc = db.parse(connection.getInputStream());
		doc.getDocumentElement().normalize();

		// Populate releaseList and developerList
		NodeList releaseNodes = doc.getElementsByTagName("release");
		for (int i = 0; i < releaseNodes.getLength(); i++) {
			Node node = releaseNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				releaseList.add(element.getElementsByTagName("version").item(0).getTextContent().replace("<version>", "").replace("</version>", ""));
				releaseList.add(element.getElementsByTagName("notes").item(0).getTextContent().replace("<notes>", "").replace("</notes>", ""));
				releaseList.add(element.getElementsByTagName("link").item(0).getTextContent().replace("<link>", "").replace("</link>", ""));
			}
		}
		NodeList developerNodes = doc.getElementsByTagName("developer");
		for (int i = 0; i < developerNodes.getLength(); i++) {
			Node node = developerNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				developerList.add(element.getElementsByTagName("version").item(0).getTextContent().replace("<version>", "").replace("</version>", ""));
				developerList.add(element.getElementsByTagName("notes").item(0).getTextContent().replace("<notes>", "").replace("</notes>", ""));
				developerList.add(element.getElementsByTagName("link").item(0).getTextContent().replace("<link>", "").replace("</link>", ""));
			}
		}

		if (connection != null) {
			connection.getInputStream().close();
		}

		String releaseVersion = releaseList.get(0);
		String developerVersion = developerList.get(0);
		rw.LOGGER.debug(ChatColor.RED + "currentVersion=" + currentVersion);
		rw.LOGGER.debug(ChatColor.RED + "releaseVersion=" + releaseVersion);
		rw.LOGGER.debug(ChatColor.RED + "developerVersion=" + developerVersion);

		// Get base version of current version (strip .D suffix if present)
		String currentBaseVersion = currentVersion.contains(".D") ? currentVersion.split("\\.D")[0] : currentVersion;

		// Compare versions semantically
		int releaseComparison = compareVersions(currentBaseVersion, releaseVersion);

		if (releaseComparison < 0) {
			// Current version is older than the release version
			recommendedVersion = "release";
			return true;
		} else if (releaseComparison >= 0) {
			// Current version is equal to or ahead of release
			if (currentVersion.contains(".D")) {
				// If it's a dev build, check for a newer dev version
				if (compareVersions(developerVersion, currentVersion) > 0) {
					recommendedVersion = "developer";
					return true;
				}
			}
			// No update needed if equal to or ahead of release and no newer dev version
			recommendedVersion = "uptodate";
			return false;
		}

		// Default to up to date if no other conditions met
		recommendedVersion = "uptodate";
		return false;
	}

	// Custom version comparison method
	private int compareVersions(String v1, String v2) {
		String[] parts1 = v1.split("[._]");
		String[] parts2 = v2.split("[._]");

		int length = Math.max(parts1.length, parts2.length);
		for (int i = 0; i < length; i++) {
			String p1 = (i < parts1.length) ? parts1[i].replaceAll("[^0-9]", "") : "0";
			String p2 = (i < parts2.length) ? parts2[i].replaceAll("[^0-9]", "") : "0";
			int n1 = p1.isEmpty() ? 0 : Integer.parseInt(p1);
			int n2 = p2.isEmpty() ? 0 : Integer.parseInt(p2);
			if (n1 != n2) {
				return Integer.compare(n1, n2);
			}
		}
		return 0; // Versions are equal if all parts match
	}

	public List<String> getReleaseList() {
		return releaseList;
	}

	public List<String> getDeveloperList() {
		return developerList;
	}

	public String getRecommendedVersion() {
		return recommendedVersion;
	}

	public String oldVersion() {
		return currentVersion;
	}

	public String newVersion() {
		if (recommendedVersion.equalsIgnoreCase("release")) {
			return releaseList.get(0);
		} else if (recommendedVersion.equalsIgnoreCase("developer")) {
			return developerList.get(0);
		} else {
			return "UpToDate";
		}
	}

	public String newVersionNotes() {
		if (recommendedVersion.equalsIgnoreCase("release")) {
			return releaseList.get(1);
		} else if (recommendedVersion.equalsIgnoreCase("developer")) {
			return developerList.get(1);
		} else {
			return "UpToDate";
		}
	}

	public String getDownloadLink() {
		if (recommendedVersion.equalsIgnoreCase("release")) {
			return releaseList.get(2);
		} else if (recommendedVersion.equalsIgnoreCase("developer")) {
			return developerList.get(2);
		} else {
			return "UpToDate";
		}
	}
}