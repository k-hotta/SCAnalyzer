package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a parser for the XML configuration file.
 * 
 * @author k-hotta
 *
 */
public class ConfigXMLParser implements IXMLNodeVisitor {

	/**
	 * This map contains values of nodes which can be exist at most once in the
	 * XML file
	 */
	private final Map<String, String> singleValues;

	/**
	 * This map contains values of nodes which can be exist two or more times in
	 * the XML file.
	 */
	private final Map<String, List<String>> multipleValues;

	public ConfigXMLParser() {
		this.singleValues = new TreeMap<>();
		this.multipleValues = new TreeMap<>();
	}

	/**
	 * Get the node value of a node which can be exist at most once in the file.
	 * 
	 * @param key
	 *            the name of the node
	 * 
	 * @return the value of the node
	 */
	public String getSingleValue(final String key) {
		if (singleValues.containsKey(key)) {
			return singleValues.get(key);
		} else {
			throw new UnsupportedOperationException(key
					+ ": no such node in the xml file");
		}
	}

	/**
	 * Get the node values of nodes which can be exist two or more times in the
	 * file.
	 * 
	 * @param key
	 *            the name of the node
	 * 
	 * @return a list of the values of the nodes
	 */
	public List<String> getMultipleValues(final String key) {
		if (multipleValues.containsKey(key)) {
			return multipleValues.get(key);
		} else {
			throw new UnsupportedOperationException(key
					+ ": no such node in the xml file");
		}
	}

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
	}

	private String retrieveSingleNodeValue(final Node core) {
		final String nodeName = core.getNodeName();

		if (nodeName != null && !nodeName.isEmpty()
				&& !nodeName.equals("#text")) {
			final Node firstChild = core.getFirstChild();

			if (firstChild.getNodeValue() != null) {
				final String firstChildValue = firstChild.getNodeValue().trim();

				if (firstChild.getNodeName().equals("#text")
						&& !firstChildValue.isEmpty()) {
					return firstChildValue;
				}
			}
		}

		// cannot find
		return null;
	}

	@Override
	public void visit(XMLSingleValueNode node) {
		final String value = retrieveSingleNodeValue(node.getCore());
		node.setValue(value);
		singleValues.put(node.getNodeName(), value);
	}

	@Override
	public void visit(XMLStrategyNode node) {
		final String value = retrieveSingleNodeValue(node.getCore());
		node.setValue(value);

		List<String> values = multipleValues.get(node.getNodeName());

		if (values == null) {
			values = new ArrayList<>();
			multipleValues.put(node.getNodeName(), values);
		}

		values.add(value);
	}

}
