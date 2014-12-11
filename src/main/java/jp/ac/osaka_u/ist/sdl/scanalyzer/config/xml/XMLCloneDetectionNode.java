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
		detectorNode = null;
		resultDirectoryNode = null;
		filenameFormatNode = null;
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_DETECTOR)) {
				detectorNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_DETECTOR);
				detectorNode.accept(visitor);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_RESULT_DIRECTORY)) {
				resultDirectoryNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_RESULT_DIRECTORY);
				resultDirectoryNode.accept(visitor);
			}

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_FILENAME_FORMAT)) {
				filenameFormatNode = new XMLSingleValueNode(child,
						ConfigConstant.NODE_NAME_FILENAME_FORMAT);
				filenameFormatNode.accept(visitor);
			}
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
