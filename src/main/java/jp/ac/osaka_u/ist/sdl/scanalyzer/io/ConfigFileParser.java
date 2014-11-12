package jp.ac.osaka_u.ist.sdl.scanalyzer.io;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a parser for xml files that contain configurations.
 * 
 * @author k-hotta
 *
 */
public class ConfigFileParser {

	/**
	 * This map connects each node in the configuration file to its value.
	 */
	private final Map<String, String> configValues;

	/**
	 * Construct
	 */
	public ConfigFileParser() {
		this.configValues = new TreeMap<>();
	}

	/**
	 * Get the value of configuration of the specified key.
	 * 
	 * @param key
	 *            the key, which represents a node in the xml file
	 * 
	 * @return the value of the key
	 * 
	 * @throws UnsupportedOperationException
	 *             if the xml file does not have the specified key
	 */
	public String getValue(final String key) {
		if (configValues.containsKey(key)) {
			return configValues.get(key);
		} else {
			throw new UnsupportedOperationException(key
					+ ": no such node in the config file");
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
	private void visit(ConfigXMLNode node) {
		final String nodeName = node.getCoreNode().getNodeName();

		if (nodeName != null && !nodeName.isEmpty()
				&& !nodeName.equals("#text")) {
			final Node firstChild = node.getCoreNode().getFirstChild();
			if (firstChild.getNodeValue() != null) {
				final String firstChildValue = firstChild.getNodeValue().trim();

				if (firstChild.getNodeName().equals("#text")
						&& !firstChildValue.isEmpty()) {
					this.configValues.put(nodeName, firstChild.getNodeValue());
				}
			}
		}
	}

	/**
	 * This inner class makes it possible to traverse the node tree with Visitor
	 * pattern
	 * 
	 * @author k-hotta
	 *
	 */
	private class ConfigXMLNode {

		/**
		 * The core xml node
		 */
		private final Node node;

		private ConfigXMLNode(final Node node) {
			this.node = node;
		}

		/**
		 * Process this node. First this method analyzes this node itself, and
		 * then continue to process its child nodes if exist.
		 * 
		 * @param parser
		 *            the parser
		 */
		private void accept(ConfigFileParser parser) {
			parser.visit(this);

			final NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				final Node child = children.item(i);
				final ConfigXMLNode childNode = new ConfigXMLNode(child);
				childNode.accept(parser);
			}
		}

		/**
		 * Get the core node.
		 * 
		 * @return the core node
		 */
		private Node getCoreNode() {
			return node;
		}

	}

}
