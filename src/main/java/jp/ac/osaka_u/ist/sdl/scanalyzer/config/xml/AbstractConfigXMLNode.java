package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import org.w3c.dom.Node;

/**
 * An abstract class that refers to xml nodes in the configuration file.
 * 
 * @author k-hotta
 *
 */
public abstract class AbstractConfigXMLNode {

	/**
	 * The core node
	 */
	protected final Node core;

	/**
	 * The node name
	 */
	protected final String nodeName;

	/**
	 * The value of this node. NOTE: this can be <code>null</code>
	 */
	protected String value;

	public AbstractConfigXMLNode(final Node core, final String nodeName) {
		this.core = core;
		this.nodeName = nodeName;
		this.value = null;
	}

	public final Node getCore() {
		return core;
	}

	public final String getNodeName() {
		return nodeName;
	}

	public final String getValue() {
		return value;
	}

	public final void setValue(String value) {
		this.value = value;
	}

	public abstract void accept(final IXMLNodeVisitor visitor);

}
