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
	 * �T�C�N���}�`�b�N�����擾
	 * 
	 * @return
	 */
	public int getCC() {
		return cc;
	}

	/**
	 * fan-out �̐����擾
	 * 
	 * @return
	 */
	public int getFO() {
		return fo;
	}

	/**
	 * �����Ŏ󂯎�������j�b�g��o�^����
	 * 
	 * @param detectedUnit
	 *            �o�^���������j�b�g
	 * @param descendentUnits
	 *            �o�^���������j�b�g�̎q�����j�b�g�̃��X�g
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
	 * �����ŗ^����ꂽ�m�[�h�̊J�n�s�ԍ����擾
	 * 
	 * @param node
	 * @return
	 */
	private int getStartLineNumber(ASTNode node) {
		return root.getLineNumber(node.getStartPosition());
	}

	/**
	 * �����ŗ^����ꂽ�m�[�h�̏I���s�ԍ����擾
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
	 * decision density �̎Z�o
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
	 * ���̃��\�b�h���Ăяo���ꂽ���_�ŕێ����Ă��镶����� Discriminator �Ƃ��ĕۑ�
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
	 * Block�̎��͎q�m�[�h��T��
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
	 * ���\�b�h�Ăяo���̎��́Cfo���C���N�������g���Ă���e�N���X��visit���\�b�h�̌Ăяo�����s��
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		this.fo++;
		return super.visit(node);
	}

	/*
	 * **********************************************************
	 * �ȉ��C�e�u���b�N������Ή�����u���b�N�̃C���X�^���X�𐶐����鏈�� <br>
	 * �u���b�N���ɓ��B������C�V�����r�W�^�[�𐶐����Ă��̃u���b�N���ȉ��̃m�[�h��T�� <br>
	 * �Ō�ɒ��ڒ��u���b�N���̃C���X�^���X�𐶐����ďI������<br>
	 * **********************************************************
	 */

	/*
	 * �N���X
	 */

	/**
	 * �N���X or �C���^�[�t�F�[�X�錾�ɑ΂��鏈��
	 */
	@Override
	public boolean visit(TypeDeclaration node) {
		// �C���^�[�t�F�[�X�錾���͉������Ȃ�
		if (node.isInterface()) {
			return false;
		}

		// �q�m�[�h��T��
		BlockDetector newVisitor = visitChildNode(node);

		// ClassInfo�̃C���X�^���X�����ɕK�v�ȏ������
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

		// ClassInfo�̃C���X�^���X�𐶐����ēo�^
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

		// modifier �𕶎��񉻂��ăo�b�t�@�ɓ˂�����
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

		// class + �N���X���@���o�b�t�@�ɓ˂�����
		// �C���^�[�t�F�[�X�ɂ͂Ȃ蓾�Ȃ��͂�
		newVisitor.getBuffer().append(" class ");
		// newVisitor.getBuffer().append(node.getName().toString());
		newVisitor.getBuffer().append("$");

		// �^�������o�b�t�@�ɓ˂�����
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

		// �e�N���X
		if (node.getSuperclassType() != null) {
			newVisitor.getBuffer().append("extends ");
			node.getSuperclassType().accept(newVisitor);
			// newVisitor.getBuilder().append(node.getSuperclassType().toString());
			newVisitor.getBuffer().append(" ");
		}

		// �����C���^�[�t�F�[�X
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

		// �{�� (= �q�m�[�h)�̏���
		newVisitor.getBuffer().append("{\n");
		for (Object obj : node.bodyDeclarations()) {
			BodyDeclaration body = (BodyDeclaration) obj;
			body.accept(newVisitor);
		}
		newVisitor.getBuffer().append("}\n");

		// �q�m�[�h�ȉ���T�����ē��肵���SUnitInfo��o�^
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/**
	 * ���S���薼����肷�� <br>
	 * node �̌^�ɂ��Ăяo���惁�\�b�h�̐U�蕪�����s�� <br>
	 * TypeDeclaration, CompilationUnit �̎��͂���ɉ��������\�b�h���Ăяo���C <br>
	 * ����ȊO�̏ꍇ�͖������Đe�m�[�h���������� <br>
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
	 * ���\�b�h
	 */

	/**
	 * ���\�b�h�錾�ɑ΂��鏈��
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		// �V�O�l�`���̓���
		// String signature = detectSignature(node);
		final String signature = detectCanonicalSignature(node);
		final List<String> paramTypes = detectParameterTypes(node);

		// �q�m�[�h�̒T��
		BlockDetector newVisitor = visitChildNode(node);

		// �K�v���̓���
		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// MethodInfo�C���X�^���X�𐶐����ēo�^
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

		// modifiers �̏���
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

		// �^�����̏���
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

		// �R���X�g���N�^�ȊO�̏ꍇ�C�Ԃ�l�̏���
		if (!node.isConstructor()) {
			if (node.getReturnType2() != null) {
				node.getReturnType2().accept(newVisitor);
			} else {
				newVisitor.getBuffer().append("void");
			}
			newVisitor.getBuffer().append(" ");
		}

		// ���\�b�h���̏���
		// newVisitor.getBuffer().append(node.getName().toString());
		newVisitor.getBuffer().append("$");

		// �����̏���
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

		// �X���[�����O�̏���
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

		// �{���̏���
		if (node.getBody() == null) {
			newVisitor.getBuffer().append(";\n");
		} else {
			node.getBody().accept(newVisitor);
		}

		// ���肵���S���j�b�g��o�^
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
	 * ���\�b�h�̃V�O�l�`������肷�� <br>
	 * �ϐ����u�����͖���
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
	 * for��
	 */

	/**
	 * for���̏���
	 */
	@Override
	public boolean visit(ForStatement node) {
		// �q�m�[�h�̒T��
		BlockDetector newVisitor = visitChildNode(node);

		// �C���X�^���X�����ɕK�v�ȏ������
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

		// ForBlockInfo �̃C���X�^���X�𐶐����C�o�^
		ForBlockInfo detectedForBlock = new ForBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(),
				initializer, expressionStr, updater, cm,
				getStartLineNumber(node), getEndLineNumber(node), nodesCount,
				ownerFileName, newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedForBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		this.cc++; // ���򂪂���̂ŃC���N�������g

		return false;
	}

	private BlockDetector visitChildNode(ForStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);
		newVisitor.getBuffer().append("for (");

		// �C�j�V�����C�U�̏���
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

		// �������̏���
		if (node.getExpression() != null) {
			node.getExpression().accept(newVisitor);
			// newVisitor.getBuilder().append(node.getExpression().toString());
		}
		newVisitor.getBuffer().append("; ");

		// updater (?) �̏���
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

		// �����܂ł����ʗp������Ƃ���
		newVisitor.setNormalizedDiscriminator();

		// �{���̏���
		node.getBody().accept(newVisitor);

		// �S���j�b�g��o�^
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * �g��for��
	 */

	/**
	 * for-each���̏���
	 */
	@Override
	public boolean visit(EnhancedForStatement node) {
		// �q�m�[�h�̒T��
		BlockDetector newVisitor = visitChildNode(node);

		// �K�v���̓���
		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// EnhancedForBlockInfo �̃C���X�^���X�𐶐����C�o�^
		EnhancedForBlockInfo detectedEnhancedForBlock = new EnhancedForBlockInfo(
				ownerRevisionId, ownerFileId, manager.getNextId(),
				newVisitor.getString(), node.getParameter().toString(), node
						.getExpression().toString(), cm,
				getStartLineNumber(node), getEndLineNumber(node), nodesCount,
				ownerFileName, newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedEnhancedForBlock,
				newVisitor.getDetectedUnits(), (nodesCount >= threshold));

		this.cc++; // ���򂪂���̂ŃC���N�������g

		return false;
	}

	private BlockDetector visitChildNode(EnhancedForStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("for (");

		// : �̍���������
		node.getParameter().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getParameter().toString());

		newVisitor.getBuffer().append(" : ");

		// : �̉E��������
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());
		newVisitor.getBuffer().append(") ");

		// �����܂ł����ʗp������
		newVisitor.setNormalizedDiscriminator();

		// �{��������
		node.getBody().accept(newVisitor);

		// �S���j�b�g��o�^
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * while��
	 */

	/**
	 * while���̏���
	 * 
	 * @param node
	 * @return
	 */
	@Override
	public boolean visit(WhileStatement node) {
		// �q�m�[�h��T��
		BlockDetector newVisitor = visitChildNode(node);

		// �K�v���̓���
		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final String expressionStr = (node.getExpression() == null) ? "" : node
				.getExpression().toString();

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// WhileBlockInfo�̃C���X�^���X�𐶐����C�o�^
		WhileBlockInfo detectedWhileBlock = new WhileBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(),
				expressionStr, cm, getStartLineNumber(node),
				getEndLineNumber(node), nodesCount, ownerFileName,
				newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedWhileBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		this.cc++; // ���򂪂���̂ŃC���N�������g

		return false;
	}

	private BlockDetector visitChildNode(WhileStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("while (");

		// �������̏���
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());
		newVisitor.getBuffer().append(") ");

		// �����܂ł����ʗp������
		newVisitor.setNormalizedDiscriminator();

		// �{���̏���
		node.getBody().accept(newVisitor);

		// �S���j�b�g��o�^
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * do-while��
	 */

	/**
	 * do-while���̏���
	 * 
	 * @param node
	 * @return
	 */
	@Override
	public boolean visit(DoStatement node) {
		// �q�m�[�h��T��
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

		// DoBlockInfo�̃C���X�^���X�𐶐����C�o�^
		DoBlockInfo detectedDoBlock = new DoBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(),
				expressionStr, cm, getStartLineNumber(node),
				getEndLineNumber(node), nodesCount, ownerFileName,
				newVisitor.getNormalizedDiscriminator());

		registDetectedUnit(detectedDoBlock, newVisitor.getDetectedUnits(),
				(nodesCount >= threshold));

		this.cc++; // ���򂪂���̂ŃC���N�������g

		return false;
	}

	private BlockDetector visitChildNode(DoStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("do ");

		// �{���̏���
		node.getBody().accept(newVisitor);

		final int bodyLength = newVisitor.getBuffer().toString().length();

		newVisitor.getBuffer().append(" while (");

		// �������̏���
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());

		newVisitor.getBuffer().append(");");

		final String discriminator = newVisitor.getBuffer().toString()
				.substring(bodyLength);
		newVisitor.setNormalizedDiscriminator(discriminator);

		newVisitor.getBuffer().append("\n");

		// �S���j�b�g�̓o�^
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * if��
	 */

	/**
	 * if���̏���
	 */
	@Override
	public boolean visit(IfStatement node) {
		// �q�m�[�h�̒T��
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor allCounter = new NodeCountVisitor();
		node.accept(allCounter);
		final int nodesCount = allCounter.getNodeCount();

		// else �߂̏���
		Statement elseStatement = node.getElseStatement();

		final NodeCountVisitor elseCounter = new NodeCountVisitor();
		if (elseStatement != null) {
			elseStatement.accept(elseCounter);
		}
		final int nodesCountInElse = elseCounter.getNodeCount();

		final int nodesCountInThen = nodesCount - nodesCountInElse;

		// IfBlockInfo �̃C���X�^���X�𐶐����C�o�^
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
				// else-if �̏ꍇ
				// else-if �u���b�N���r�W�b�g
				IfStatement elseIf = (IfStatement) elseStatement;
				elseIf.accept(this);
			} else {
				// else �̏ꍇ
				// else �u���b�N�̃C���X�^���X�𐶐����C�o�^
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

		this.cc++; // ���򂪂���̂ŃC���N�������g

		return false;
	}

	private BlockDetector visitChildNode(IfStatement node) {
		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("if (");

		// �������̏���
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());
		newVisitor.getBuffer().append(") ");

		// �����܂łŎ��ʗp������
		newVisitor.setNormalizedDiscriminator();

		// �{���̏���
		node.getThenStatement().accept(newVisitor);

		// �S���j�b�g��o�^
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/**
	 * ElseBlockInfo ����肵�ăC���X�^���X�𐶐�����
	 * 
	 * @param elseStatement
	 * @return
	 */
	private void detectElseBlock(Statement elseStatement, int nodesCount,
			String ifDiscriminator) {
		BlockDetector elseVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);
		elseVisitor.getBuffer().append("else");

		// else �u���b�N�{����T��
		elseStatement.accept(elseVisitor);

		// ���肵�����j�b�g��o�^
		this.detectedUnits.addAll(elseVisitor.getDetectedUnits());

		int loc = getEndLineNumber(elseStatement)
				- getStartLineNumber(elseStatement) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				elseVisitor.getCC() + 1, elseVisitor.getFO(), calcDD(
						elseVisitor.getCC() + 1, loc));

		// else �߂͏������������Ȃ��̂ŁC����else�u���b�N���Ԃ牺�����Ă��� if&else-if ���ׂĂ̏�������A������
		List<String> predicates = detectElsePredicates(elseStatement);

		// ElseBlockInfo�̃C���X�^���X�𐶐����ĕԂ�
		ElseBlockInfo detectedElseBlock = new ElseBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), elseVisitor.getString(),
				predicates, cm, getStartLineNumber(elseStatement),
				getEndLineNumber(elseStatement), nodesCount, ownerFileName,
				ifDiscriminator);

		registDetectedUnit(detectedElseBlock, elseVisitor.getDetectedUnits(),
				(nodesCount >= threshold));
	}

	/**
	 * else�ߗp�̏���������肷��
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
	 * switch��
	 */

	/**
	 * switch���̏���
	 */
	@Override
	public boolean visit(SwitchStatement node) {
		// �q�m�[�h�̒T��
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// SwitchBlockInfo �̃C���X�^���X�𐶐����C�o�^����
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

		// �������̏���
		node.getExpression().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getExpression().toString());

		newVisitor.getBuffer().append(") ");

		// �����܂łŎ��ʗp������
		newVisitor.setNormalizedDiscriminator();

		newVisitor.getBuffer().append("{\n");

		// �{���̏���
		for (Iterator it = node.statements().iterator(); it.hasNext();) {
			Statement s = (Statement) it.next();
			s.accept(newVisitor);
		}

		newVisitor.getBuffer().append("}\n");

		// �S���j�b�g��o�^
		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * try�� & finally��
	 */

	/**
	 * try���̏���
	 */
	@Override
	public boolean visit(TryStatement node) {
		// �q�m�[�h��T��
		BlockDetector newVisitor = visitChildNode(node);

		// �K�v���̓���
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

		// TryBlockInfo �̃C���X�^���X�𐶐����C�o�^
		TryBlockInfo detectedTryBlock = new TryBlockInfo(ownerRevisionId,
				ownerFileId, manager.getNextId(), newVisitor.getString(),
				caughtExceptionTypes, cm, getStartLineNumber(node),
				getEndLineNumber(node), nodesCountInTry, ownerFileName,
				caughtExceptionTypesStringBuilder.toString());

		registDetectedUnit(detectedTryBlock, newVisitor.getDetectedUnits(),
				(nodesCountInTry >= threshold));

		// catch�߂�����
		for (Object obj : node.catchClauses()) {
			CatchClause catchClause = (CatchClause) obj;
			catchClause.accept(this);
		}

		// finally �߂̏���
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

		// �{���̏���
		node.getBody().accept(newVisitor);

		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/**
	 * finally �߂��������ăC���X�^���X�𐶐�����
	 * 
	 * @param node
	 * @param caughtExceptionTypes
	 * @return
	 */
	private void detectFinallyBlock(TryStatement node,
			List<String> caughtExceptionTypes, int nodesCount,
			final String caughtExceptionTypesStr) {
		// �O�̂��� finally �߂̑��݂��m�F
		if (node.getFinally() == null) {
			assert false;
			return;
		}

		BlockDetector newVisitor = new BlockDetector(ownerFileName, this.root,
				ownerRevisionId, ownerFileId);

		newVisitor.getBuffer().append("finally ");

		// �{���̏���
		node.getFinally().accept(newVisitor);

		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		int loc = getEndLineNumber(node.getFinally())
				- getStartLineNumber(node.getFinally()) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		// FinallyBlockInfo �̃C���X�^���X�𐶐����ĕԂ�
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
	 * catch�� <br>
	 */

	/**
	 * catch�߂̏���
	 */
	@Override
	public boolean visit(CatchClause node) {
		// �q�m�[�h��T��
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// CatchBlockInfo �̃C���X�^���X�𐶐����o�^
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

		// �L���b�`�����O�̏���
		node.getException().accept(newVisitor);
		// newVisitor.getBuilder().append(node.getException().toString());
		newVisitor.getBuffer().append(") ");

		// �{���̏���
		node.getBody().accept(newVisitor);

		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

	/*
	 * synchronized�� <br>
	 */

	/**
	 * synchronized �u���b�N�̏���
	 */
	@Override
	public boolean visit(SynchronizedStatement node) {
		// �q�m�[�h��T��
		BlockDetector newVisitor = visitChildNode(node);

		int loc = getEndLineNumber(node) - getStartLineNumber(node) + 1;

		CorroborationMetric cm = new CorroborationMetric(
				newVisitor.getCC() + 1, newVisitor.getFO(), calcDD(
						newVisitor.getCC() + 1, loc));

		final NodeCountVisitor counter = new NodeCountVisitor();
		node.accept(counter);
		final int nodesCount = counter.getNodeCount();

		// SynchronizedBlockInfo �̃C���X�^���X�𐶐����ēo�^
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

		// �������̏���
		node.getExpression().accept(newVisitor);
		newVisitor.getBuffer().append(") ");

		newVisitor.setNormalizedDiscriminator();

		// �{���̏���
		node.getBody().accept(newVisitor);

		this.detectedUnits.addAll(newVisitor.getDetectedUnits());

		return newVisitor;
	}

}
