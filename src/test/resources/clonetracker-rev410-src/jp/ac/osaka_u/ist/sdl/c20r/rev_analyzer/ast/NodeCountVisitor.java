package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Comment;

public class NodeCountVisitor extends ASTVisitor {

	private int nodeCount;

	public NodeCountVisitor() {
		this.nodeCount = 0;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	@Override
	public void postVisit(ASTNode node) {
		if (isCountTarget(node)) {
			nodeCount++;
		}
	}

	private boolean isCountTarget(ASTNode node) {
		if (node instanceof Comment) {
			return false;
		}
		return true;
	}

}
