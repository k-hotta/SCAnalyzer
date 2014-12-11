package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import org.w3c.dom.Node;

public class XMLSingleValueNode extends AbstractConfigXMLNode {

	public XMLSingleValueNode(Node node, final String nodeName) {
		super(node, nodeName);
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);
	}

}
