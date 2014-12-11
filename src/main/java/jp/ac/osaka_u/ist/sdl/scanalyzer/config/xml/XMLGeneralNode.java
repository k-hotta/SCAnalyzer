package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the node "general".
 * 
 * @author k-hotta
 *
 */
public class XMLGeneralNode extends AbstractConfigXMLNode {

	private XMLSingleValueNode dbmsNode;

	private XMLSingleValueNode overwriteNode;

	public XMLGeneralNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_GENERAL);
		construct();
	}

	private void construct() {
		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_DBMS)) {
				dbmsNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_DBMS);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_OVERWRITE)) {
				overwriteNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_OVERWRITE);
			}
		}
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		if (dbmsNode != null) {
			dbmsNode.accept(visitor);
		}

		if (overwriteNode != null) {
			overwriteNode.accept(visitor);
		}
	}

	public final XMLSingleValueNode getDbmsNode() {
		return dbmsNode;
	}

	public final XMLSingleValueNode getOverwriteNode() {
		return overwriteNode;
	}

}
