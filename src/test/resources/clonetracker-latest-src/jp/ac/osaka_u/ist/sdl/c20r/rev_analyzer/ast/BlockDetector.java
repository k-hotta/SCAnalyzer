package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.UnitManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.CatchBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.ClassInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.DoBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.ElseBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.EnhancedForBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.FinallyBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.ForBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.IfBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.MethodInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.SwitchBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.SynchronizedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.TryBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.UnitInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.WhileBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

public class BlockDetector extends StringCreateVisitor {

	private static final int threshold = Settings.getIntsance().getThreshold();

	private final List<UnitInfo> detectedUnits;

	private final String ownerFileName;

	private final CompilationUnit root;

	private final UnitManager manager;

	private final long ownerRevisionId;

	private final long ownerFileId;

	private int cc = 0;

	private int fo = 0;

	private String normalizedDiscriminator;

	public BlockDetector(String ownerFile, CompilationUnit root,
			long ownerRevisionId, long ownerFileId) {
		this.detectedUnits = new LinkedList<UnitInfo>();
		this.ownerFileName = ownerFile;
		this.root = root;
		this.manager = DataManagerManager.getInstance().getUnitManager();
		this.ownerRevisionId = ownerRevisionId;
		this.ownerFileId = ownerFileId;
	}

	public List<UnitInfo> getDetectedUnits() {
		return detectedUnits;
	}

	/**
	 * サイクロマチック数を取得
	 * 
	 * @return
	 */
	public int getCC() {
		return cc;
	}

	/**
	 * fan-out の数を取得
	 * 
	 * @return
	 */
	public int getFO() {
		return fo;
	}

	/**
	 * 引数で受け取ったユニットを登録する
	 * 
	 * @param detectedUnit
	 *            登録したいユニット
	 * @param descendentUnits
	 *            登録したいユニットの子孫ユニットのリスト
	 */
	private void registDetectedUnit(UnitInfo detectedUnit,
			List<UnitInfo> descendentUnits, boolean isSatisfyThreshold) {
		for (UnitInfo descendentUnit : descendentUnits) {
			descendentUnit.addAncestorUnit(detectedUnit);
		}
		if (isSatisfyThreshold) {
			this.detectedUnits.add(detectedUnit);
		}
		getBuffer().append(detectedUnit.getReplaceStatement() + "\n");
		this.cc += detectedUnit.getCM().getCC() - 1;
		this.fo += detectedUnit.getCM().getFO();
	}

	/**
	 * 引数で与えられたノードの開始行番号を取得
	 * 
	 * @param node
	 * @return
	 */
	private int getStartLineNumber(ASTNode node) {
		return root.getLineNumber(node.getStartPosition());
	}

	/**
	 * 引数で与えられたノードの終了行番号を取得
	 * 
	 * @param node
	 * @return
	 */
	private int getEndLineNumber(ASTNode node) {
		if (node instanceof IfStatement) {
			final ASTNode elseStatement = ((IfStatement) node)
					.getElseStatement();
			final int thenEnd = (elseStatement == null) ? node
					.getStartPosition() + node.getLength() : elseStatement
					.getStartPosition() - 1;
			return root.getLineNumber(thenEnd);
		} else if (node instanceof TryStatement) {
			final TryStatement tryStatement = (TryStatement) node;
			int tryEnd = 0;
			for (Object obj : tryStatement.catchClauses()) {
				CatchClause catchClause = (CatchClause) obj;
				tryEnd = catchClause.getStartPosition() - 1;
				break;
			}
			if (tryEnd == 0) {
				final Block finallyBlock = tryStatement.getFinally();
				if (finallyBlock != null) {
					tryEnd = finallyBlock.getStartPosition() - 1;
				}
			}
			if (tryEnd == 0) {
				tryEnd = node.getStartPosition() + node.getLength();
			}
			return root.getLineNumber(tryEnd);
		} else {
			return root.getLineNumber(node.getStartPosition()
					+ node.getLength());
		}
	}

	/**
	 * decision density の算出
	 * 
	 * @param cc
	 * @param loc
	 * @return
	 */
	private double calcDD(int cc, int loc) {
		if (loc <= 0) {
			assert false;
			return 0.0;
		}
		return (double) cc / (double) loc;
	}

	/**
	 * このメソッドが呼び出された時点で保持している文字列を Discriminator として保存
	 */
	public void setNormalizedDiscriminator() {
		this.normalizedDiscriminator = getStringWhiteSpacesRemoved();
	}

	public void setNormalizedDiscriminator(final String normalizedDiscriminator) {
		this.normalizedDiscriminator = normalizedDiscriminator;
	}

	public String getNormalizedDiscriminator() {
		return this.normalizedDiscriminator;
	}

	/**
	 * Blockの時は子ノードを探索
	 */
	@Override
	public boolean visit(Block node) {
		getBuffer().append("{\n");
		for (Iterator it = node.statements().iterator(); it.hasNext();) {
			Statement s = (Statement) it.next();
			s.accept(this);
		}
		getBuffer().append("}\n");
		return false;
	}

	/**
	 * メソッド呼び出しの時は，foをインクリメントしてから親クラスのvisitメソッドの呼び出しを行う
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		this.fo++;
		return super.visit(node);
	}

	/*
	 * **********************************************************
	 * 以下，各ブロック文から対応するブロックのインスタンスを生成する処理 <br>
	 * ブロック文に到達したら，新しいビジターを生成してそのブロック文以下のノードを探索 <br>
	 * 最後に着目中ブロック文のインスタンスを生成して終了する<br>
	 * **********************************************************
	 */

	/*
	 * クラス
	 */

	/**
	 * クラス or インターフェース宣言に対する処理
	 */
	@Override
	public boolean visit(TypeDeclaration node) {
		// インターフェース宣言時は何もしない
		if (node.isInterface()) {
			return false;
		}

		// 子ノードを探索
		BlockDetector newVisitor = visitChildNode(node);

		// ClassInfoのインスタンス生成に必要な情報を特定
		String core = newVisitor.getString();
		String name = node.getName().toString();

		StringBuilder builder = new StringBuilder();
		detectFullyQualifiedName(node, builder);
		String qualifiedName = builder.toString();

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// ClassInfoのインスタンスを生成して登録
		ClassInfo detectedClass = new ClassInfo(ownerRevisionId, ownerFileId,
				manager.getNextId(), core, ownerFileName, name, qualifiedName,
				cm, getStartLineNumber(node), getEndLineNumber(node),
				nodesCount, ownerFileName);

		registDetectedUnit(detectedClass, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		return false;
	}

	private BlockDetector visitChildNode(TypeDeclaration node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		// modifier を文字列化してバッファに突っ込む
		boolean isFirstModifier = true;
		for (Object obj : node.modifiers()) {
			if (!isFirstModifier) {
				newVisitor.getBuffer().append(" ");
			} else {
				isFirstModifier = false;
			}
			ASTNode modifier = (ASTNode) obj;
			modifier.accept(newVisitor);
			// newVisitor.getBuilder().append(obj.toString());
		}

		// class + クラス名　をバッファに突っ込む
		// インターフェースにはなり得ないはず
		newVisitor.getBuffer().append(" class ");
		// newVisitor.getBuffer().append(node.getName().toString());
		newVisitor.getBuffer().append("$");

		// 型引数をバッファに突っ込む
		if (!node.typeParameters().isEmpty()) {
			newVisitor.getBuffer().append("<");
			boolean isFirstTypeParameter = true;
			for (Object obj : node.typeParameters()) {
				if (!isFirstTypeParameter) {
					newVisitor.getBuffer().append(", ");
				} else {
					isFirstTypeParameter = false;
				}
				ASTNode typeParameter = (ASTNode) obj;
				typeParameter.accept(newVisitor);
				// newVisitor.getBuilder().append(obj.toString());
			}
			newVisitor.getBuffer().append(">");
		}
		newVisitor.getBuffer().append(" ");

		// 親クラス
		if (node.getSuperclassType() != null) {
			newVisitor.getBuffer().append("extends ");
			node.getSuperclassType().accept(newVisitor);
			// newVisitor.getBuilder().append(node.getSuperclassType().toString());
			newVisitor.getBuffer().append(" ");
		}

		// 実装インターフェース
		if (!node.superInterfaceTypes().isEmpty()) {
			newVisitor.getBuffer().append("implements ");
			boolean isFirstSuperInterface = true;
			for (Object obj : node.superInterfaceTypes()) {
				if (!isFirstSuperInterface) {
					newVisitor.getBuffer().append(", ");
				} else {
					isFirstSuperInterface = false;
				}
				ASTNode superInterface = (ASTNode) obj;
				superInterface.accept(newVisitor);
				// newVisitor.getBuilder().append(obj.toString());
			}
			newVisitor.getBuffer().append(" ");
		}

		// 本文 (= 子ノード)の処理
		newVisitor.getBuffer().append("{\n");
		for (Object obj : node.bodyDeclarations()) {
			BodyDeclaration body = (BodyDeclaration) obj;
			body.accept(newVisitor);
		}
		newVisitor.getBuffer().append("}\n");

		// 子ノード以下を探索して特定した全UnitInfoを登録
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/**
	 * 完全限定名を特定する <br>
	 * node の型による呼び出し先メソッドの振り分けを行う <br>
	 * TypeDeclaration, CompilationUnit の時はそれに応じたメソッドを呼び出し， <br>
	 * それ以外の場合は無視して親ノードを処理する <br>
	 * 
	 * @param node
	 * @param builder
	 */
	private void detectFullyQualifiedName(ASTNode node, StringBuilder builder) {
		if (node instanceof TypeDeclaration) {
			detectFullyQualifiedName((TypeDeclaration) node, builder);
		} else if (node instanceof CompilationUnit) {
			detectFullyQualifiedName((CompilationUnit) node, builder);
		} else {
			detectFullyQualifiedName(node.getParent(), builder);
		}
	}

	private void detectFullyQualifiedName(TypeDeclaration node,
			StringBuilder builder) {
		builder.insert(0, node.getName());
		detectFullyQualifiedName(node.getParent(), builder);
	}

	private void detectFullyQualifiedName(CompilationUnit node,
			StringBuilder builder) {
		if (node.getPackage() != null) {
			builder.insert(0, node.getPackage().getName() + ".");
		}
	}

	/*
	 * メソッド
	 */

	/**
	 * メソッド宣言に対する処理
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		// シグネチャの特定
		// String signature = detectSignature(node);
		final String signature = detectCanonicalSignature(node);
		final List<String> paramTypes = detectParameterTypes(node);

		// 子ノードの探索
		BlockDetector newVisitor = visitChildNode(node);

		// 必要情報の特定
		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// MethodInfoインスタンスを生成して登録
		MethodInfo detectedMethod = new MethodInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(), node
						.getName().toString(), signature, cm,
				getStartLineNumber(node), getEndLineNumber(node), nodesCount,
				ownerFileName, paramTypes);

		registDetectedUnit(detectedMethod, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		return false;
	}

	private BlockDetector visitChildNode(MethodDeclaration node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		// modifiers の処理
		//boolean isFirstModifier = true;
		for (Object obj : node.modifiers()) {
			// if (!isFirstModifier) {
			// newVisitor.getBuffer().append(" ");
			// } else {
			// isFirstModifier = false;
			// }
			((ASTNode) obj).accept(newVisitor);
		}
		// if (!node.modifiers().isEmpty()) {
		// newVisitor.getBuffer().append(" ");
		// }

		// 型引数の処理
		if (!node.typeParameters().isEmpty()) {
			newVisitor.getBuffer().append("<");
			boolean isFirstTypeParameter = true;
			for (Object obj : node.typeParameters()) {
				if (!isFirstTypeParameter) {
					newVisitor.getBuffer().append(",");
				} else {
					isFirstTypeParameter = false;
				}
				((ASTNode) obj).accept(newVisitor);
			}
			newVisitor.getBuffer().append("> ");
		}

		// コンストラクタ以外の場合，返り値の処理
		if (!node.isConstructor()) {
			if (node.getReturnType2() != null) {
				node.getReturnType2().accept(newVisitor);
			} else {
				newVisitor.getBuffer().append("void");
			}
			newVisitor.getBuffer().append(" ");
		}

		// メソッド名の処理
		// newVisitor.getBuffer().append(node.getName().toString());
		newVisitor.getBuffer().append("$");

		// 引数の処理
		newVisitor.getBuffer().append("(");
		boolean isFirstParameter = true;
		for (Object obj : node.parameters()) {
			if (!isFirstParameter) {
				newVisitor.getBuffer().append(",");
			} else {
				isFirstParameter = false;
			}
			((ASTNode) obj).accept(newVisitor);
		}
		newVisitor.getBuffer().append(")");
		for (int i = 0; i < node.getExtraDimensions(); i++) {
			newVisitor.getBuffer().append("[]");
		}

		// スローする例外の処理
		if (!node.thrownExceptions().isEmpty()) {
			newVisitor.getBuffer().append(" throws ");
			boolean isFirstException = true;
			for (Object obj : node.thrownExceptions()) {
				if (!isFirstException) {
					newVisitor.getBuffer().append(", ");
				} else {
					isFirstException = false;
				}

				newVisitor.getBuffer().append(obj.toString());
			}
			newVisitor.getBuffer().append(" ");
		}

		// 本文の処理
		if (node.getBody() == null) {
			newVisitor.getBuffer().append(";\n");
		} else {
			node.getBody().accept(newVisitor);
		}

		// 特定した全ユニットを登録
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}
	
	private List<String> detectParameterTypes(MethodDeclaration node) {
		final List<String> result = new ArrayList<String>();
		
		for (Object obj : node.parameters()) {
			SingleVariableDeclaration param = (SingleVariableDeclaration) obj;
			result.add(param.getType().toString());
		}
		
		return result;
	}

	private String detectCanonicalSignature(MethodDeclaration node) {
		StringBuilder builder = new StringBuilder();

		builder.append(node.getName().toString());

		builder.append("(");
		{
			boolean isFirstParam = true;
			for (Object obj : node.parameters()) {
				SingleVariableDeclaration param = (SingleVariableDeclaration) obj;
				if (!isFirstParam) {
					builder.append(", ");
				} else {
					isFirstParam = false;
				}
				builder.append(param.getType().toString());
			}
		}
		builder.append(")");

		return builder.toString();
	}

	/**
	 * メソッドのシグネチャを特定する <br>
	 * 変数名置換等は無し
	 * 
	 * @param node
	 * @return
	 */
	private String detectSignature(MethodDeclaration node) {
		StringBuilder builder = new StringBuilder();

		for (Object obj : node.modifiers()) {
			if (obj instanceof Annotation) {
				continue;
			}
			builder.append(obj.toString() + " ");
		}

		List typeParameters = node.typeParameters();
		if (!typeParameters.isEmpty()) {
			builder.append("<");

			boolean isFirstParam = true;
			for (Object obj : typeParameters) {
				if (!isFirstParam) {
					builder.append(", ");
				} else {
					isFirstParam = false;
				}
				builder.append(obj.toString());
			}

			builder.append("> ");
		}

		if (!node.isConstructor()) {
			builder.append(node.getReturnType2().toString() + " ");
		}

		builder.append(node.getName().toString());

		builder.append("(");
		{
			boolean isFirstParam = true;
			for (Object obj : node.parameters()) {
				if (!isFirstParam) {
					builder.append(", ");
				} else {
					isFirstParam = false;
				}
				builder.append(obj.toString());
			}
		}
		builder.append(")");

		for (int i = 0; i < node.getExtraDimensions(); i++) {
			builder.append("[]");
		}

		List thrownExceptions = node.thrownExceptions();
		if (!thrownExceptions.isEmpty()) {
			builder.append(" throws ");

			boolean isFirstParam = true;
			for (Object obj : thrownExceptions) {
				if (!isFirstParam) {
					builder.append(", ");
				} else {
					isFirstParam = false;
				}
				builder.append(obj.toString());
			}
		}

		return builder.toString();
	}

	/*
	 * for文
	 */

	/**
	 * for文の処理
	 */
	@Override
	public boolean visit(ForStatement node) {
		// 子ノードの探索
		BlockDetector newVisitor = visitChildNode(node);

		// インスタンス生成に必要な情報を特定
		StringBuilder initializerBuilder = new StringBuilder();
		boolean isFirst = true;
		for (Object obj : node.initializers()) {
			if (!isFirst) {
				initializerBuilder.append(" ");
			} else {
				isFirst = false;
			}
			initializerBuilder.append(obj.toString());
		}
		String initializer = initializerBuilder.toString();

		StringBuilder updaterBuilder = new StringBuilder();
		isFirst = true;
		for (Object obj : node.updaters()) {
			if (!isFirst) {
				updaterBuilder.append(" ");
			} else {
				isFirst = false;
			}
			updaterBuilder.append(obj.toString());
		}
		String updater = updaterBuilder.toString();

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final String expressionStr = (node.getExpression() == null) ? "" : node
				.getExpression().toString();

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// ForBlockInfo のインスタンスを生成し，登録
		ForBlockInfo detectedForBlock = new ForBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(),
				initializer, expressionStr, updater, cm,
				getStartLineNumber(node), getEndLineNumber(node), nodesCount,
				ownerFileName, newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedForBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		this.cc++; // 分岐があるのでインクリメント

		return false;
	}

	private BlockDetector visitChildNode(ForStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);
		newVisitor.getBuffer().append("for (");

		// イニシャライザの処理
		boolean isFirstInitializer = true;
		for (Object obj : node.initializers()) {
			if (!isFirstInitializer) {
				newVisitor.getBuffer().append(", ");
			} else {
				isFirstInitializer = false;
			}
			ASTNode initializer = (ASTNode) obj;
			initializer.accept(newVisitor);
			// newVisitor.getBuilder().append(obj.toString());
		}
		newVisitor.getBuffer().append("; ");

		// 条件式の処理
		if (node.getExpression() != null) {
			node.getExpression().accept(newVisitor);
			// newVisitor.getBuilder().append(node.getExpression().toString());
		}
		newVisitor.getBuffer().append("; ");

		// updater (?) の処理
		boolean isFirstUpdater = true;
		for (Object obj : node.updaters()) {
			if (!isFirstUpdater) {
				newVisitor.getBuffer().append(", ");
			} else {
				isFirstUpdater = false;
			}
			ASTNode updater = (ASTNode) obj;
			updater.accept(newVisitor);
			// newVisitor.getBuilder().append(obj.toString());
		}

		newVisitor.getBuffer().append(")");

		// ここまでを識別用文字列とする
		newVisitor.setNormalizedDiscriminator();

		// 本文の処理
		node.getBody().accept(newVisitor);

		// 全ユニットを登録
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * 拡張for文
	 */

	/**
	 * for-each文の処理
	 */
	@Override
	public boolean visit(EnhancedForStatement node) {
		// 子ノードの探索
		BlockDetector newVisitor = visitChildNode(node);

		// 必要情報の特定
		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// EnhancedForBlockInfo のインスタンスを生成し，登録
		EnhancedForBlockInfo detectedEnhancedForBlock = new EnhancedForBlockInfo(
				ownerRevisionId, ownerFileId, manager.getNextId(),
				newVisitor.getString(), node.getParameter().toString(), node
						.getExpression().toString(), cm,
				getStartLineNumber(node), getEndLineNumber(node), nodesCount,
				ownerFileName, newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedEnhancedForBlock,
				newVisitor.getDetectedUnits(), (nodesCount >= threshold));

		this.cc++; // 分岐があるのでインクリメント

		return false;
	}

	private BlockDetector visitChildNode(EnhancedForStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("for (");

		// : の左側を処理
		node.getParameter().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getParameter().toString());

		newVisitor.getBuffer().append(" : ");

		// : の右側を処理
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());
		newVisitor.getBuffer().append(") ");

		// ここまでが識別用文字列
		newVisitor.setNormalizedDiscriminator();

		// 本文を処理
		node.getBody().accept(newVisitor);

		// 全ユニットを登録
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * while文
	 */

	/**
	 * while文の処理
	 * 
	 * @param node
	 * @return
	 */
	@Override
	public boolean visit(WhileStatement node) {
		// 子ノードを探索
		BlockDetector newVisitor = visitChildNode(node);

		// 必要情報の特定
		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final String expressionStr = (node.getExpression() == null) ? "" : node
				.getExpression().toString();

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// WhileBlockInfoのインスタンスを生成し，登録
		WhileBlockInfo detectedWhileBlock = new WhileBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(),
				expressionStr, cm, getStartLineNumber(node),
				getEndLineNumber(node), nodesCount, ownerFileName,
				newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedWhileBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		this.cc++; // 分岐があるのでインクリメント

		return false;
	}

	private BlockDetector visitChildNode(WhileStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("while (");

		// 条件式の処理
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());
		newVisitor.getBuffer().append(") ");

		// ここまでが識別用文字列
		newVisitor.setNormalizedDiscriminator();

		// 本文の処理
		node.getBody().accept(newVisitor);

		// 全ユニットを登録
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * do-while文
	 */

	/**
	 * do-while文の処理
	 * 
	 * @param node
	 * @return
	 */
	@Override
	public boolean visit(DoStatement node) {
		// 子ノードを探索
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final String expressionStr = (node.getExpression() == null) ? "" : node
				.getExpression().toString();

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// DoBlockInfoのインスタンスを生成し，登録
		DoBlockInfo detectedDoBlock = new DoBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(),
				expressionStr, cm, getStartLineNumber(node),
				getEndLineNumber(node), nodesCount, ownerFileName,
				newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedDoBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		this.cc++; // 分岐があるのでインクリメント

		return false;
	}

	private BlockDetector visitChildNode(DoStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("do ");

		// 本文の処理
		node.getBody().accept(newVisitor);

		final int bodyLength = newVisitor.getBuffer().toString().length();

		newVisitor.getBuffer().append(" while (");

		// 条件式の処理
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());

		newVisitor.getBuffer().append(");");

		final String discriminator = newVisitor.getBuffer().toString()
				.substring(bodyLength);
		newVisitor.setNormalizedDiscriminator(discriminator);

		newVisitor.getBuffer().append("\n");

		// 全ユニットの登録
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * if文
	 */

	/**
	 * if文の処理
	 */
	@Override
	public boolean visit(IfStatement node) {
		// 子ノードの探索
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor allCounter = new NodeCountVisitor();
		node.accept(allCounter);
		final int nodesCount = allCounter.getNodeCount();

		// else 節の処理
		Statement elseStatement = node.getElseStatement();

		final NodeCountVisitor elseCounter = new NodeCountVisitor();
		if (elseStatement != null) {
			elseStatement.accept(elseCounter);
		}
		final int nodesCountInElse = elseCounter.getNodeCount();

		final int nodesCountInThen = nodesCount - nodesCountInElse;

		// IfBlockInfo のインスタンスを生成し，登録
		IfBlockInfo detectedIfBlock = new IfBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(), node
						.getExpression().toString(), cm,
				getStartLineNumber(node), getEndLineNumber(node),
				nodesCountInThen, ownerFileName,
				newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedIfBlock, newVisitor.getDetectedUnits(),
				(nodesCountInThen >= threshold));

		if (elseStatement != null) {
			if (elseStatement instanceof IfStatement) {
				// else-if の場合
				// else-if ブロックをビジット
				IfStatement elseIf = (IfStatement) elseStatement;
				elseIf.accept(this);
			} else {
				// else の場合
				// else ブロックのインスタンスを生成し，登録
				detectElseBlock(elseStatement, nodesCountInElse,
						newVisitor.getNormalizedDiscriminator());

				// ElseBlockInfo detectedElseBlock = detectElseBlock(
				// elseStatement, nodesCountInElse,
				// newVisitor.getNormalizedDiscriminator());
				// registDetectedUnit(detectedElseBlock,
				// newVisitor.getDetectedUnits(),
				// (nodesCountInElse >= threshold));

			}
		}

		this.cc++; // 分岐があるのでインクリメント

		return false;
	}

	private BlockDetector visitChildNode(IfStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("if (");

		// 条件式の処理
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());
		newVisitor.getBuffer().append(") ");

		// ここまでで識別用文字列
		newVisitor.setNormalizedDiscriminator();

		// 本文の処理
		node.getThenStatement().accept(newVisitor);

		// 全ユニットを登録
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/**
	 * ElseBlockInfo を特定してインスタンスを生成する
	 * 
	 * @param elseStatement
	 * @return
	 */
	private void detectElseBlock(Statement elseStatement, int nodesCount,
			String ifDiscriminator) {
		BlockDetector elseVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);
		elseVisitor.getBuffer().append("else");

		// else ブロック本文を探索
		elseStatement.accept(elseVisitor);

		// 特定したユニットを登録
		this.detectedUnits.addAll(elseVisitor.getDetectedUnits());

		int loc = getEndLineNumber(elseStatement)
				- getStartLineNumber(elseStatement) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				elseVisitor.getCC() + 1, elseVisitor.getFO(), calcDD(
						elseVisitor.getCC() + 1, loc));

		// else 節は条件式を持たないので，このelseブロックがぶら下がっている if&else-if すべての条件式を連結する
		List<String> predicates = detectElsePredicates(elseStatement);

		// ElseBlockInfoのインスタンスを生成して返す
		ElseBlockInfo detectedElseBlock = new ElseBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), elseVisitor.getString(),
				predicates, cm, getStartLineNumber(elseStatement),
				getEndLineNumber(elseStatement), nodesCount, ownerFileName,
				ifDiscriminator);

		registDetectedUnit(detectedElseBlock, elseVisitor.getDetectedUnits(),
				(nodesCount >= threshold));
	}

	/**
	 * else節用の条件式を特定する
	 * 
	 * @param elseStatement
	 * @return
	 */
	private List<String> detectElsePredicates(Statement elseStatement) {
		List<String> predicates = new LinkedList<String>();

		detectPredicates(elseStatement.getParent(), predicates);

		return predicates;
	}

	private void detectPredicates(ASTNode node, List<String> predicates) {
		if (!(node instanceof IfStatement)) {
			return;
		}

		detectPredicates(node.getParent(), predicates);

		IfStatement ifStatement = (IfStatement) node;
		predicates.add(ifStatement.getExpression().toString());
	}

	/*
	 * switch文
	 */

	/**
	 * switch文の処理
	 */
	@Override
	public boolean visit(SwitchStatement node) {
		// 子ノードの探索
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// SwitchBlockInfo のインスタンスを生成し，登録する
		SwitchBlockInfo detectedSwitchBlock = new SwitchBlockInfo(
				ownerRevisionId, ownerFileId, manager.getNextId(),
				newVisitor.getString(), node.getExpression().toString(), cm,
				getStartLineNumber(node), getEndLineNumber(node), nodesCount,
				ownerFileName, newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedSwitchBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		return false;
	}

	@Override
	public boolean visit(SwitchCase node) {
		this.cc++;
		return super.visit(node);
	}

	private BlockDetector visitChildNode(SwitchStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("switch (");

		// 条件式の処理
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());

		newVisitor.getBuffer().append(") ");

		// ここまでで識別用文字列
		newVisitor.setNormalizedDiscriminator();

		newVisitor.getBuffer().append("{\n");

		// 本文の処理
		for (Iterator it = node.statements().iterator(); it.hasNext();) {
			Statement s = (Statement) it.next();
			s.accept(newVisitor);
		}

		newVisitor.getBuffer().append("}\n");

		// 全ユニットを登録
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * try文 & finally文
	 */

	/**
	 * try文の処理
	 */
	@Override
	public boolean visit(TryStatement node) {
		// 子ノードを探索
		BlockDetector newVisitor = visitChildNode(node);

		// 必要情報の特定
		List caughtExceptions = node.catchClauses();
		List<String> caughtExceptionTypes = new ArrayList<String>();
		final StringBuilder caughtExceptionTypesStringBuilder = new StringBuilder();

		for (Object obj : caughtExceptions) {
			CatchClause catchClause = (CatchClause) obj;
			final String typeStr = catchClause.getException().getType()
					.toString();
			caughtExceptionTypes.add(typeStr);
			caughtExceptionTypesStringBuilder.append(typeStr + " ");
		}

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		int nodesCountInCatch = 0;
		for (Object obj : node.catchClauses()) {
			CatchClause catchClause = (CatchClause) obj;
			final NodeCountVisitor catchCounter = new NodeCountVisitor();
			catchClause.accept(catchCounter);
			nodesCountInCatch += catchCounter.getNodeCount();
		}

		int nodesCountInFinally = 0;
		if (node.getFinally() != null) {
			final ASTNode finallyNode = node.getFinally();
			final NodeCountVisitor finallyCounter = new NodeCountVisitor();
			finallyNode.accept(finallyCounter);
			nodesCountInFinally += finallyCounter.getNodeCount();
		}

		final int nodesCountInTry = nodesCount - nodesCountInFinally
				- nodesCountInCatch;

		// TryBlockInfo のインスタンスを生成し，登録
		TryBlockInfo detectedTryBlock = new TryBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(),
				caughtExceptionTypes, cm, getStartLineNumber(node),
				getEndLineNumber(node), nodesCountInTry, ownerFileName,
				caughtExceptionTypesStringBuilder.toString());

		registDetectedUnit(detectedTryBlock, newVisitor.getDetectedUnits(),
				(nodesCountInTry >= threshold));

		// catch節を処理
		for (Object obj : node.catchClauses()) {
			CatchClause catchClause = (CatchClause) obj;
			catchClause.accept(this);
		}

		// finally 節の処理
		if (node.getFinally() != null) {
			final ASTNode finallyNode = node.getFinally();
			final NodeCountVisitor finallyCounter = new NodeCountVisitor();
			finallyNode.accept(finallyCounter);
			final int finallyNodesCount = finallyCounter.getNodeCount();

			detectFinallyBlock(node, caughtExceptionTypes, finallyNodesCount,
					caughtExceptionTypesStringBuilder.toString());

			// FinallyBlockInfo detectedFinallyBlock = detectFinallyBlock(node,
			// caughtExceptionTypes, finallyNodesCount,
			// caughtExceptionTypesStringBuilder.toString());
			//
			// registDetectedUnit(detectedFinallyBlock,
			// newVisitor.getDetectedUnits(),
			// (finallyNodesCount >= threshold));

		}

		return false;
	}

	private BlockDetector visitChildNode(TryStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("try ");

		// 本文の処理
		node.getBody().accept(newVisitor);

		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/**
	 * finally 節を処理してインスタンスを生成する
	 * 
	 * @param node
	 * @param caughtExceptionTypes
	 * @return
	 */
	private void detectFinallyBlock(TryStatement node,
			List<String> caughtExceptionTypes, int nodesCount,
			final String caughtExceptionTypesStr) {
		// 念のため finally 節の存在を確認
		if (node.getFinally() == null) {
			assert false;
			return;
		}

		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("finally ");

		// 本文の処理
		node.getFinally().accept(newVisitor);

		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		int loc = getEndLineNumber(node.getFinally())
				- getStartLineNumber(node.getFinally()) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		// FinallyBlockInfo のインスタンスを生成して返す
		FinallyBlockInfo detectedFinallyBlock = new FinallyBlockInfo(
				ownerRevisionId, ownerFileId, manager.getNextId(),
				newVisitor.getString(), caughtExceptionTypes, cm,
				getStartLineNumber(node.getFinally()),
				getEndLineNumber(node.getFinally()), nodesCount, ownerFileName,
				caughtExceptionTypesStr);

		registDetectedUnit(detectedFinallyBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));
	}

	/*
	 * catch節 <br>
	 */

	/**
	 * catch節の処理
	 */
	@Override
	public boolean visit(CatchClause node) {
		// 子ノードを探索
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// CatchBlockInfo のインスタンスを生成し登録
		CatchBlockInfo detectedCatchBlock = new CatchBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(), node
						.getException().getType().toString(), cm,
				getStartLineNumber(node), getEndLineNumber(node), nodesCount,
				ownerFileName, node.getException().getType().toString());

		registDetectedUnit(detectedCatchBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		return false;
	}

	private BlockDetector visitChildNode(CatchClause node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("catch (");

		// キャッチする例外の処理
		node.getException().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getException().toString());
		newVisitor.getBuffer().append(") ");

		// 本文の処理
		node.getBody().accept(newVisitor);

		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * synchronized文 <br>
	 */

	/**
	 * synchronized ブロックの処理
	 */
	@Override
	public boolean visit(SynchronizedStatement node) {
		// 子ノードを探索
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// SynchronizedBlockInfo のインスタンスを生成して登録
		SynchronizedBlockInfo detectedSynchronizedBlock = new SynchronizedBlockInfo(
				ownerRevisionId, ownerFileId, manager.getNextId(),
				newVisitor.getString(), node.getExpression().toString(), cm,
				getStartLineNumber(node), getEndLineNumber(node), nodesCount,
				ownerFileName, newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedSynchronizedBlock,
				newVisitor.getDetectedUnits(), (nodesCount >= threshold));

		return false;
	}

	private BlockDetector visitChildNode(SynchronizedStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("synchronoized (");

		// 条件式の処理
		node.getExpression().accept(newVisitor);
		newVisitor.getBuffer().append(") ");

		newVisitor.setNormalizedDiscriminator();

		// 本文の処理
		node.getBody().accept(newVisitor);

		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

}
