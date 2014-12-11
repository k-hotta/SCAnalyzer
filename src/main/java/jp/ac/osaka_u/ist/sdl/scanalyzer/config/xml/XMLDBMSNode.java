package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;

public class XMLDBMSNode extends AbstractConfigXMLNode {

	public XMLDBMSNode(Node node) {
		super(node, ConfigConstant.NODE_NAME_DBMS, true);
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		// TODO Auto-generated method stub

	}

}
