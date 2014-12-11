package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

import org.w3c.dom.Node;

/**
 * This class represents the node "strategy".
 * 
 * @author k-hotta
 *
 */
public class XMLStrategyNode extends AbstractConfigXMLNode {

	public XMLStrategyNode(Node core) {
		super(core, ConfigConstant.NODE_NAME_STRATEGY);
	}

	@Override
	public void accept(IXMLNodeVisitor visitor) {
		visitor.visit(this);
	}

}
