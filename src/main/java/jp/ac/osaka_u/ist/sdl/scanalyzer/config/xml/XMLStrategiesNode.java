package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the node "strategies".
 * 
 * @author k-hotta
 *
 */
public class XMLStrategiesNode extends AbstractConfigXMLNode {

	private final List<XMLSingleValueNode> strategyNodes;

	public XMLStrategiesNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_STRATEGIES);
		strategyNodes = new ArrayList<>();
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);

		final NodeList listChildren = core.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			if (child.getNodeName().equals(ConfigConstant.NODE_NAME_STRATEGY)) {
				final XMLSingleValueNode strategyNode = new XMLSingleValueNode(
						child, ConfigConstant.NODE_NAME_STRATEGY);
				strategyNode.accept(visitor);
				strategyNodes.add(strategyNode);
			}

		}
	}

	public final List<XMLSingleValueNode> getStrategyNodes() {
		return Collections.unmodifiableList(strategyNodes);
	}

}
