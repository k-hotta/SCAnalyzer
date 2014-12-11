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
		dbmsNode = null;
		overwriteNode = null;
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_DBMS)) {
				dbmsNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_DBMS);
				dbmsNode.accept(visitor);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_OVERWRITE)) {
				overwriteNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_OVERWRITE);
				overwriteNode.accept(visitor);
			}
		}

		if (dbmsNode == null) {
			throw new IllegalStateException(
					"wrong format of the XML file: the node " + nodeName
							+ " must have " + ConfigConstant.NODE_NAME_DBMS
							+ " as its child");
		}
	}

	public final XMLSingleValueNode getDbmsNode() {
		return dbmsNode;
	}

	public final XMLSingleValueNode getOverwriteNode() {
		return overwriteNode;
	}

}
