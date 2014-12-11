package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the node "clone-detection".
 * 
 * @author k-hotta
 *
 */
public class XMLCloneDetectionNode extends AbstractConfigXMLNode {

	private XMLSingleValueNode detectorNode;

	private XMLSingleValueNode resultDirectoryNode;

	private XMLSingleValueNode filenameFormatNode;

	public XMLCloneDetectionNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_CLONE_DETECTION);
		construct();
	}

	private void construct() {
		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_DETECTOR)) {
				detectorNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_DETECTOR);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_RESULT_DIRECTORY)) {
				resultDirectoryNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_RESULT_DIRECTORY);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_FILENAME_FORMAT)) {
				filenameFormatNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_FILENAME_FORMAT);
			}
		}
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		if (detectorNode != null) {
			detectorNode.accept(visitor);
		}

		if (resultDirectoryNode != null) {
			resultDirectoryNode.accept(visitor);
		}

		if (filenameFormatNode != null) {
			filenameFormatNode.accept(visitor);
		}
	}

	public final XMLSingleValueNode getDetectorNode() {
		return detectorNode;
	}

	public final XMLSingleValueNode getResultDirectoryNode() {
		return resultDirectoryNode;
	}

	public final XMLSingleValueNode getFilenameFormatNode() {
		return filenameFormatNode;
	}

}
