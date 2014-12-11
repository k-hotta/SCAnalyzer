package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the node "config", which is the actual root node of the
 * XML file.
 * 
 * @author k-hotta
 *
 */
public class XMLConfigNode extends AbstractConfigXMLNode {

	private XMLGeneralNode generalNode;

	private XMLTargetNode targetNode;

	private XMLCloneDetectionNode cloneDetectionNode;

	private XMLMappingNode mappingNode;

	private XMLMiningNode miningNode;

	public XMLConfigNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_ROOT);
		construct();
	}

	private void construct() {
		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_GENERAL)) {
				generalNode = new XMLGeneralNode(child);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_TARGET)) {
				targetNode = new XMLTargetNode(child);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_CLONE_DETECTION)) {
				cloneDetectionNode = new XMLCloneDetectionNode(child);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_MAPPING)) {
				mappingNode = new XMLMappingNode(child);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_MINING)) {
				miningNode = new XMLMiningNode(child);
			}
		}
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		if (generalNode != null) {
			generalNode.accept(visitor);
		}

		if (targetNode != null) {
			targetNode.accept(visitor);
		}

		if (cloneDetectionNode != null) {
			cloneDetectionNode.accept(visitor);
		}

		if (mappingNode != null) {
			mappingNode.accept(visitor);
		}

		if (miningNode != null) {
			miningNode.accept(visitor);
		}
	}

	public final XMLGeneralNode getGeneralNode() {
		return generalNode;
	}

	public final XMLTargetNode getTargetNode() {
		return targetNode;
	}

	public final XMLCloneDetectionNode getCloneDetectionNode() {
		return cloneDetectionNode;
	}

	public final XMLMappingNode getMappingNode() {
		return mappingNode;
	}

	public final XMLMiningNode getMiningNode() {
		return miningNode;
	}

}
