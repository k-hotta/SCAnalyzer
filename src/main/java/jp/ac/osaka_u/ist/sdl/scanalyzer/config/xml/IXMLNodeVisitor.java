package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

/**
 * This interface a visitor of xml nodes for configuration.
 * 
 * @author k-hotta
 *
 */
public interface IXMLNodeVisitor {

	public void visit(final XMLDBMSNode node);
	
}
