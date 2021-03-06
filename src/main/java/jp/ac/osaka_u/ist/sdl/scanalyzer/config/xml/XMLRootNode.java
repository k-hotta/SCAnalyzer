package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the root node of the XML file. Being different from
 * other node classes, this node class is expected to be used only for internal
 * processing. Hence the name, the value, and any other attributes of this node
 * have no means.
 * 
 * @author k-hotta
 *
 */
public class XMLRootNode extends AbstractConfigXMLNode {

	private XMLConfigNode configNode;

	public XMLRootNode(Node core) {
		super(core, "root");
		construct();
	}

	private void construct() {
		final NodeList listChildren = core.getChildNodes();

		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_ROOT)) {
				configNode = new XMLConfigNode(child);
			}
		}

		if (configNode == null) {
			throw new IllegalStateException(
					"the XML file has a wrong format: the root node of the XML file must be "
							+ ConfigConstant.NODE_NAME_ROOT);
		}
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		assert configNode != null;
		
		configNode.accept(visitor);
	}

}
