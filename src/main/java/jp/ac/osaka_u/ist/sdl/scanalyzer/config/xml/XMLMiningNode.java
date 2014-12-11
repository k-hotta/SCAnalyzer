package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the node "mining".
 * 
 * @author k-hotta
 *
 */
public class XMLMiningNode extends AbstractConfigXMLNode {

	private XMLCommonConfigurationNode commonConfigurationNode;

	private XMLStrategiesNode strategiesNode;

	public XMLMiningNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_MINING);
		commonConfigurationNode = null;
		strategiesNode = null;
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(
					ConfigConstant.NODE_NAME_COMMON_CONFIGURATION)) {
				commonConfigurationNode = new XMLCommonConfigurationNode(child);
				commonConfigurationNode.accept(visitor);
			}

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_STRATEGIES)) {
				strategiesNode = new XMLStrategiesNode(child);
				strategiesNode.accept(visitor);
			}
		}
	}

	public final XMLCommonConfigurationNode getCommonConfigurationNode() {
		return commonConfigurationNode;
	}

	public final XMLStrategiesNode getStrategiesNode() {
		return strategiesNode;
	}

}
