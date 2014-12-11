package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigXMLNode {

	/**
	 * The core xml node
	 */
	private final Node node;

	/**
	 * The name of the node
	 */
	private final String nodeName;


	public ConfigXMLNode(final Node node) {
		this.node = node;
		this.nodeName = node.getNodeName();
	}

	/**
	 * Process this node. First this method analyzes this node itself, and
	 * then continue to process its child nodes if exist.
	 * 
	 * @param parser
	 *            the parser
	 */
	public void accept(ConfigFileParser parser) {
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

}
