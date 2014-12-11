package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents a node of XML file in the configuration file.
 * 
 * @author k-hotta
 *
 */
public class ConfigXMLNode {

	/**
	 * The core xml node
	 */
	private final Node node;

	/**
	 * The name of the node
	 */
	private final String nodeName;

	/**
	 * The child nodes of this node
	 */
	private final Map<String, List<ConfigXMLNode>> children;

	/**
	 * The value of this node, which can be <code>null</code>
	 */
	private String value;

	/**
	 * Whether this node should be considered
	 */
	private boolean ofInterest;

	public ConfigXMLNode(final Node node) {
		this.node = node;
		this.nodeName = node.getNodeName();
		this.children = new TreeMap<>();
		this.ofInterest = false;
	}

	/**
	 * Process this node. First this method analyzes this node itself, and then
	 * continue to process its child nodes if exist.
	 * 
	 * @param parser
	 *            the parser
	 */
	public void accept(ConfigFileParser parser) {
		parser.visit(this);

		final NodeList listChildren = node.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);
			final ConfigXMLNode childNode = new ConfigXMLNode(child);
			childNode.accept(parser);

			if (childNode.ofInterest) {
				List<ConfigXMLNode> list = children
						.get(childNode.getNodeName());

				if (list == null) {
					list = new ArrayList<>();
					children.put(childNode.getNodeName(), list);
				}

				list.add(childNode);
			}
		}
	}

	/**
	 * Get the core node.
	 * 
	 * @return the core node
	 */
	public Node getCoreNode() {
		return node;
	}

	/**
	 * Get the name of this node.
	 * 
	 * @return the name of this node
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * Get all the children of this node.
	 * 
	 * @return all the children
	 */
	public Map<String, List<ConfigXMLNode>> getAllChildren() {
		return Collections.unmodifiableMap(children);
	}

	/**
	 * Get the children whose node names are the given one.
	 * 
	 * @param childNodeName
	 *            the node name as a query
	 * 
	 * @return a list of nodes each of which is a child node of this node and
	 *         the name of it matches to that in this node
	 */
	public List<ConfigXMLNode> getChildren(final String childNodeName) {
		return children.get(childNodeName);
	}

	/**
	 * Get the value of this node.
	 * 
	 * @return the value of this node
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of this node.
	 * 
	 * @param value
	 *            the string to be set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Get whether this node is of interest or not.
	 * 
	 * @return whether this node is of interest
	 */
	public boolean ofInterest() {
		return ofInterest;
	}

	/**
	 * Set whether this node is of interest or not.
	 * 
	 * @param ofInterest
	 *            whether this node is of interest
	 */
	public void setOfInterest(final boolean ofInterest) {
		this.ofInterest = ofInterest;
	}

}