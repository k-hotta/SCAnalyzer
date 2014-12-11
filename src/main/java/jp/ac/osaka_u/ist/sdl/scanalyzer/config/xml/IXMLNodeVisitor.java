package jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml;

/**
 * This interface a visitor of xml nodes for configuration.
 * 
 * @author k-hotta
 *
 */
public interface IXMLNodeVisitor {

	public default void visit(final XMLSingleValueNode node) {

	}

	public default void visit(final XMLGeneralNode node) {

	}

	public default void visit(final XMLTargetNode node) {

	}

}
