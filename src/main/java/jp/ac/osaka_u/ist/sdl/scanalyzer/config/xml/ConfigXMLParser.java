package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;

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
