package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the node "mapping".
 * 
 * @author k-hotta
 *
 */
public class XMLMappingNode extends AbstractConfigXMLNode {

	private XMLSingleValueNode relocationNode;

	private XMLSingleValueNode equalizerNode;

	private XMLSingleValueNode cloneMappingNode;

	private XMLSingleValueNode elementMappingNode;

	public XMLMappingNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_MAPPING);
		construct();
	}

	private void construct() {
		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_RELOCATION)) {
				relocationNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_RELOCATION);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_EQUALIZER)) {
				equalizerNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_EQUALIZER);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_CLONE_MAPPING)) {
				cloneMappingNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_CLONE_MAPPING);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_ELEMENT_MAPPING)) {
				elementMappingNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_ELEMENT_MAPPING);
			}
		}
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		if (relocationNode != null) {
			relocationNode.accept(visitor);
		}

		if (equalizerNode != null) {
			equalizerNode.accept(visitor);
		}

		if (cloneMappingNode != null) {
			cloneMappingNode.accept(visitor);
		}

		if (elementMappingNode != null) {
			elementMappingNode.accept(visitor);
		}
	}

	public final XMLSingleValueNode getRelocationNode() {
		return relocationNode;
	}

	public final XMLSingleValueNode getEqualizerNode() {
		return equalizerNode;
	}

	public final XMLSingleValueNode getCloneMappingNode() {
		return cloneMappingNode;
	}

	public final XMLSingleValueNode getElementMappingNode() {
		return elementMappingNode;
	}

}
