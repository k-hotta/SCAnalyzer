package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast;

import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeLiteral;

/**
 * ASTノードの各toStringメソッドの挙動を決定するクラス <br>
 * NaiveASTFlattener がデフォルトのもので，一部変更するためにクラスを定義
 * 
 * @author k-hotta
 * 
 */
public class ExtendedNaiveASTFlattener extends MyNaiveAstFlattener {

	/**
	 * Javadoc に対する振る舞いを変更 <br>
	 * すべての Javadoc を無視する
	 */
	@Override
	public boolean visit(Javadoc node) {
		return false;
	}

	/**
	 * 識別子名等の場合 <br>
	 * すべて $ に置換 <br>
	 * 置換したくないものの場合は，このメソッド呼び出しに至る前に対処する
	 */
	@Override
	public boolean visit(SimpleName node) {
		this.buffer.append("$");
		return false;
	}

	/**
	 * booleanリテラルの場合 <br>
	 * すべて $ に置換
	 */
	@Override
	public boolean visit(BooleanLiteral node) {
		this.buffer.append("$");
		return false;
	}

	/**
	 * 文字リテラルの場合 <br>
	 * すべて $ に置換
	 */
	@Override
	public boolean visit(CharacterLiteral node) {
		this.buffer.append("$");
		return false;
	}

	/**
	 * 数字リテラルの場合 <br>
	 * すべて $ に置換
	 */
	@Override
	public boolean visit(NumberLiteral node) {
		this.buffer.append("$");
		return false;
	}

	/**
	 * 文字列リテラルの場合 <br>
	 * すべて $ に置換
	 */
	@Override
	public boolean visit(StringLiteral node) {
		this.buffer.append("$");
		return false;
	}

	/**
	 * 型リテラルの場合 <br>
	 * すべて $ に置換
	 */
	@Override
	public boolean visit(TypeLiteral node) {
		this.buffer.append("$");
		return false;
	}

	/**
	 * 型の場合 <br>
	 * $への置換を防ぐ
	 */
	@Override
	public boolean visit(SimpleType node) {
		this.buffer.append(node.getName().toString());
		return false;
	}

	/**
	 * アノテーションはすべて無視
	 */
	@Override
	public boolean visit(MarkerAnnotation node) {
		return false;
	}
	
	@Override
	public boolean visit(NormalAnnotation node) {
		return false;
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		return false;
	}
	
	/**
	 * Modifierは無視
	 */
	@Override
	public boolean visit(Modifier node) {
		return false;
	}
	
	/**
	 * メソッド呼び出しの場合 <br>
	 * 呼び出し先メソッド名のみ置換を防ぐ
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		// Qualifiers はすべて無視
		// if (node.getExpression() != null) {
		// node.getExpression().accept(this);
		// this.buffer.append(".");
		// }

		// 型引数も無視
		// if (!node.typeArguments().isEmpty()) {
		// this.buffer.append("<");
		// boolean isFirstTypeArgument = true;
		// for (Object obj : node.typeArguments()) {
		// Type t = (Type) obj;
		// if (!isFirstTypeArgument) {
		// this.buffer.append(",");
		// } else {
		// isFirstTypeArgument = false;
		// }
		// t.accept(this);
		// }
		// this.buffer.append(">");
		// }

		// メソッド名は置換しない
		this.buffer.append(node.getName().toString());

		this.buffer.append("(");
		boolean isFirstArgument = true;
		for (Object obj : node.arguments()) {
			Expression e = (Expression) obj;
			if (!isFirstArgument) {
				this.buffer.append(",");
			} else {
				isFirstArgument = false;
			}
			e.accept(this);
		}
		this.buffer.append(")");

		return false;
	}

}
