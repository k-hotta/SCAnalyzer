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
public class XMLCommonConfigurationNode extends AbstractConfigXMLNode {

	private XMLSingleValueNode maxRetrievedNode;

	private XMLSingleValueNode outputFilePatternNode;

	public XMLCommonConfigurationNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_COMMON_CONFIGURATION);
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_MAX_RETRIEVED)) {
				maxRetrievedNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_MAX_RETRIEVED);
				maxRetrievedNode.accept(visitor);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_OUTPUT_FILE_PATTERN)) {
				outputFilePatternNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_OUTPUT_FILE_PATTERN);
				outputFilePatternNode.accept(visitor);
			}
		}
	}

	public final XMLSingleValueNode getMaxRetrievedNode() {
		return maxRetrievedNode;
	}

	public final XMLSingleValueNode getOutputFilePatternNode() {
		return outputFilePatternNode;
	}

}
