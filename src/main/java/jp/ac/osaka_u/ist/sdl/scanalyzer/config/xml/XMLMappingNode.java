package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLMappingNode extends AbstractConfigXMLNode {

	private XMLSingleValueNode relocationNode;

	private XMLSingleValueNode equalizerNode;

	private XMLSingleValueNode cloneMappingNode;

	private XMLSingleValueNode elementMappingNode;

	public XMLMappingNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_MAPPING);
		relocationNode = null;
		equalizerNode = null;
		cloneMappingNode = null;
		elementMappingNode = null;
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_RELOCATION)) {
				relocationNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_RELOCATION);
				relocationNode.accept(visitor);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_EQUALIZER)) {
				equalizerNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_EQUALIZER);
				equalizerNode.accept(visitor);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_CLONE_MAPPING)) {
				cloneMappingNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_CLONE_MAPPING);
				cloneMappingNode.accept(visitor);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_ELEMENT_MAPPING)) {
				elementMappingNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_ELEMENT_MAPPING);
				elementMappingNode.accept(visitor);
			}
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
