package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;

/**
 * This is a parser for xml files that contain configurations.
 * 
 * @author k-hotta
 *
 */
public class ConfigFileParser {

	/**
	 * This map contains all the detected XML nodes.
	 */
	private final Map<String, List<ConfigXMLNode>> nodes;

	/**
	 * Construct
	 */
	public ConfigFileParser() {
		this.nodes = new TreeMap<>();
	}

	/**
	 * Get nodes whose names are the specified one.
	 * 
	 * @param nodeName
	 *            the name as a query
	 * 
	 * @return a list of nodes whose names are the given one
	 */
	public List<ConfigXMLNode> getNodes(final String nodeName) {
		if (nodes.containsKey(nodeName)) {
			return nodes.get(nodeName);
		} else {
			throw new UnsupportedOperationException(nodeName
					+ ": no such node in the file");
		}
	}

	/**
	 * Parse the given xml file.
	 * 
	 * @param xmlPath
	 *            the target xml file
	 * @throws Exception
	 *             If any errors occurred
	 */
	public void parse(final String xmlPath) throws Exception {
		final DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		final File file = new File(xmlPath);
		if (!file.exists()) {
			throw new IllegalArgumentException(xmlPath + " does not exist");
		}

		final Node root = builder.parse(new File(xmlPath));

		if (root.getNodeType() != Node.DOCUMENT_NODE) {
			throw new IllegalStateException("the root is not document");
		}

		final ConfigXMLNode rootNode = new ConfigXMLNode(root);

		// traverse the node tree
		rootNode.accept(this);
	}

	/**
	 * Visit the node and store its value into the map.
	 * 
	 * @param node
	 *            the node to be analyzed
	 */
	public void visit(ConfigXMLNode node) {
		final String nodeName = node.getCoreNode().getNodeName();
		boolean ofInterest = false;

		if (nodeName != null && !nodeName.isEmpty()
				&& !nodeName.equals("#text")) {
			ofInterest = true;
			final Node firstChild = node.getCoreNode().getFirstChild();
			if (firstChild.getNodeValue() != null) {
				final String firstChildValue = firstChild.getNodeValue().trim();

				if (firstChild.getNodeName().equals("#text")
						&& !firstChildValue.isEmpty()) {
					node.setValue(firstChild.getNodeValue());
				}
			}
		}

		node.setOfInterest(ofInterest);

		if (ofInterest) {
			List<ConfigXMLNode> list = nodes.get(nodeName);
			if (list == null) {
				list = new ArrayList<>();
				nodes.put(nodeName, list);
			}

			list.add(node);
		}
	}

}
