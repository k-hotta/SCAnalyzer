package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the node "common-configuration".
 * 
 * @author k-hotta
 *
 */
public class XMLCommonConfigurationNode extends AbstractConfigXMLNode {

	private XMLSingleValueNode unitNode;

	private XMLSingleValueNode maxRetrievedNode;

	private XMLSingleValueNode outputFilePatternNode;

	public XMLCommonConfigurationNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_COMMON_CONFIGURATION);
		construct();
	}

	private void construct() {
		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_UNIT)) {
				unitNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_UNIT);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_MAX_RETRIEVED)) {
				maxRetrievedNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_MAX_RETRIEVED);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_OUTPUT_FILE_PATTERN)) {
				outputFilePatternNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_OUTPUT_FILE_PATTERN);
			}
		}
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		if (unitNode != null) {
			unitNode.accept(visitor);
		}

		if (maxRetrievedNode != null) {
			maxRetrievedNode.accept(visitor);
		}

		if (outputFilePatternNode != null) {
			outputFilePatternNode.accept(visitor);
		}
	}

	public final XMLSingleValueNode getMaxRetrievedNode() {
		return maxRetrievedNode;
	}

	public final XMLSingleValueNode getOutputFilePatternNode() {
		return outputFilePatternNode;
	}

}
