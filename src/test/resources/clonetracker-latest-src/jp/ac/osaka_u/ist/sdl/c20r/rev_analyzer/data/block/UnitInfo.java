package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import jp.ac.osaka_u.ist.sdl.c20r.diff.DifferenceManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast.HashCalculator;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.AbstractElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CRDElement;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * �N���X�C���\�b�h�C�u���b�N�Ȃǂ̂����܂��\�����ۃN���X
 * 
 * @author k-hotta
 * 
 */
public abstract class UnitInfo extends AbstractElementInfo {

	/**
	 * ���j�b�g�����L���郊�r�W������ID
	 */
	protected final long ownerRevisionId;

	/**
	 * ���j�b�g�����L����t�@�C����ID
	 */
	protected final long ownerFileId;

	/**
	 * ���j�b�g���\�����镶����
	 */
	protected final String core;

	/**
	 * �n�b�V���l
	 */
	protected final int hash;

	/**
	 * �v���O�����̍\�����ʂɂ��郆�j�b�g�̃��X�g
	 */
	protected final List<UnitInfo> ancestorUnits;

	/**
	 * ���̃��j�b�g�� corroboration metric
	 */
	protected final CorroborationMetric cm;

	/**
	 * ���̃��j�b�g�̎��
	 */
	protected final UnitType uType;

	/**
	 * �J�n�s�ԍ�
	 */
	protected final int startLine;

	/**
	 * �I���s�ԍ�
	 */
	protected final int endLine;

	/**
	 * ���̃��j�b�g�P�̂�CRD�\�L
	 */
	protected CRDElement crdElement;

	/**
	 * ���̃��j�b�g�̊��S�� CRD
	 */
	protected CRD crd;

	/**
	 * ���̃��j�b�g�̃T�C�Y(AST�m�[�h��)
	 */
	protected final int length;

	/**
	 * �u���b�N�S�̂��ǉ����ꂽ���ǂ���
	 */
	protected final boolean whollyAdded;

	/**
	 * �u���b�N�S�̂��폜���ꂽ���ǂ���
	 */
	protected final boolean whollyDeleted;

	/**
	 * �u���b�N���ʗp������
	 */
	protected final String discriminator;

	public UnitInfo(long ownerRevisionId, long ownerFileId, long id,
			String core, CorroborationMetric cm, UnitType uType, int startLine,
			int endLine, int length, String ownerFilePath, String discriminator) {
		super(id);
		this.ownerRevisionId = ownerRevisionId;
		this.ownerFileId = ownerFileId;
		this.core = core;
		this.hash = HashCalculator.getInstance().getHash(core);
		this.ancestorUnits = new LinkedList<UnitInfo>();
		this.cm = cm;
		this.uType = uType;
		this.startLine = startLine;
		this.endLine = endLine;
		this.crd = null;
		this.length = length;
		this.whollyAdded = DifferenceManager.getInstance()
				.isContainedInAfterDiff(ownerFilePath, startLine, endLine);
		this.whollyDeleted = DifferenceManager.getInstance()
				.isContainedInBeforeDiff(ownerFilePath, startLine, endLine);
		this.discriminator = discriminator;
	}

	public final long getOwnerRevisionId() {
		return ownerRevisionId;
	}

	public final long getOwnerFileId() {
		return ownerFileId;
	}

	public final String getCore() {
		return core;
	}

	public final int getHash() {
		return hash;
	}

	@Override
	public final String toString() {
		return core;
	}

	public final void addAncestorUnit(UnitInfo ancestorUnit) {
		this.ancestorUnits.add(ancestorUnit);
	}

	public final List<UnitInfo> getAncestorUnits() {
		return ancestorUnits;
	}

	public final CorroborationMetric getCM() {
		return cm;
	}

	public final UnitType getUnitType() {
		return uType;
	}
	
	public String getUnitTypeString() {
		return uType.name();
	}

	public final int getStartLine() {
		return startLine;
	}

	public final int getEndLine() {
		return endLine;
	}

	public final int getLength() {
		return length;
	}

	public final boolean isWhollyAdded() {
		return whollyAdded;
	}

	public final boolean isWhollyDeleted() {
		return whollyDeleted;
	}
	
	public final ClassInfo detectRootClass() {
		if (this instanceof ClassInfo) {
			return (ClassInfo) this;
		}
		
		ClassInfo result = null;
		for (final UnitInfo ancestor : ancestorUnits) {
			if (ancestor instanceof ClassInfo) {
				result = (ClassInfo) ancestor;
			}
		}
		
		return result;
	}
	
	public final MethodInfo detectRootMethod() {
		if (this instanceof MethodInfo) {
			return (MethodInfo) this;
		}
		
		MethodInfo result = null;
		for (final UnitInfo ancestor : ancestorUnits) {
			if (ancestor instanceof MethodInfo) {
				result = (MethodInfo) ancestor;
			}
		}
		return result;
	}
	
	public final String getRootClassName() {
		final ClassInfo rootClass = detectRootClass();
		return (rootClass == null) ? "N/A" : rootClass.getClassName();
	}
	
	public final String getRootMethodName() {
		final MethodInfo rootMethod = detectRootMethod();
		return (rootMethod == null) ? "N/A" : rootMethod.getName();
	}
	
	public final String getRootMethodParametersAsString() {
		final MethodInfo rootMethod = detectRootMethod();
		if (rootMethod == null) {
			return "N/A";
		}
		if (rootMethod.getParamTypes().isEmpty()) {
			return "";
		}
		final StringBuilder builder = new StringBuilder();
		for (String paramType : rootMethod.getParamTypes()) {
			builder.append(paramType + ",");
		}
		return builder.deleteCharAt(builder.length() - 1).toString();
	}

	public final CRD getCRD() {
		if (crd == null) {
			List<CRDElement> elements = new LinkedList<CRDElement>();
			for (ListIterator<UnitInfo> iterator = ancestorUnits
					.listIterator(ancestorUnits.size()); iterator.hasPrevious();) {
				UnitInfo unit = iterator.previous();
				elements.add(unit.getCRDElement());
			}
			elements.add(this.getCRDElement());
			crd = new CRD(elements, getUnitType());
		}

		return crd;
	}
	
	public final CRD getCRDAfterRootMethod() {
		List<CRDElement> elements = new LinkedList<CRDElement>();
		final MethodInfo rootMethod = detectRootMethod();
		boolean afterRootMethod = false;
		for (ListIterator<UnitInfo> iterator = ancestorUnits
				.listIterator(ancestorUnits.size()); iterator.hasPrevious();) {
			UnitInfo unit = iterator.previous();
			if (afterRootMethod) {
				elements.add(unit.getCRDElement());
			} else {
				if (unit == rootMethod) {
					afterRootMethod = true;
				}
			}
		}
		if (afterRootMethod) {
			elements.add(this.getCRDElement());
		}
		return new CRD(elements, getUnitType());
	}

	public final long getParentUnitId() {
		if (ancestorUnits.isEmpty()) {
			return -1;
		} else {
			return ancestorUnits.get(0).getId();
		}
	}

	/**
	 * ���̃��j�b�g�P�̂�CRD�\�L���擾����
	 * 
	 * @return
	 */
	public CRDElement getCRDElement() {
		return crdElement;
	}

	/**
	 * ���̃��j�b�g�̎��ʗp��������擾����
	 * 
	 * @return
	 */
	public String getDiscriminator() {
		return discriminator;
	}

	/**
	 * ���̃��j�b�g��u������ۂɗp������ꕶ������擾����
	 * 
	 * @return
	 */
	public abstract String getReplaceStatement();

}
