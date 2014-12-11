package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the node "target".
 * 
 * @author k-hotta
 *
 */
public class XMLTargetNode extends AbstractConfigXMLNode {

	private XMLSingleValueNode languageNode;

	private XMLSingleValueNode elementNode;

	private XMLSingleValueNode repositoryNode;

	private XMLSingleValueNode relativeNode;

	private XMLSingleValueNode versionControlNode;

	private XMLSingleValueNode databaseNode;

	private XMLSingleValueNode startNode;

	private XMLSingleValueNode endNode;

	public XMLTargetNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_TARGET);
		construct();
	}

	private void construct() {
		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_LANGUAGE)) {
				languageNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_LANGUAGE);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_ELEMENT)) {
				elementNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_ELEMENT);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_REPOSITORY)) {
				repositoryNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_REPOSITORY);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_RELATIVE)) {
				relativeNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_RELATIVE);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_VERSION_CONTROL)) {
				versionControlNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_VERSION_CONTROL);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_DATABASE)) {
				databaseNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_DATABASE);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_START)) {
				startNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_START);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_END)) {
				endNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_END);
			}
		}
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		if (languageNode != null) {
			languageNode.accept(visitor);
		}

		if (elementNode != null) {
			elementNode.accept(visitor);
		}

		if (repositoryNode != null) {
			repositoryNode.accept(visitor);
		}

		if (relativeNode != null) {
			relativeNode.accept(visitor);
		}

		if (versionControlNode != null) {
			versionControlNode.accept(visitor);
		}

		if (databaseNode != null) {
			databaseNode.accept(visitor);
		}

		if (startNode != null) {
			startNode.accept(visitor);
		}

		if (endNode != null) {
			endNode.accept(visitor);
		}

	}

	public final XMLSingleValueNode getLanguageNode() {
		return languageNode;
	}

	public final XMLSingleValueNode getElementNode() {
		return elementNode;
	}

	public final XMLSingleValueNode getRepositoryNode() {
		return repositoryNode;
	}

	public final XMLSingleValueNode getVersionControlNode() {
		return versionControlNode;
	}

	public final XMLSingleValueNode getDatabaseNode() {
		return databaseNode;
	}

	public final XMLSingleValueNode getStartNode() {
		return startNode;
	}

	public final XMLSingleValueNode getEndNode() {
		return endNode;
	}

}
