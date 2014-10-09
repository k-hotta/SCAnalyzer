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
 * クラス，メソッド，ブロックなどのかたまりを表す抽象クラス
 * 
 * @author k-hotta
 * 
 */
public abstract class UnitInfo extends AbstractElementInfo {

	/**
	 * ユニットを所有するリビジョンのID
	 */
	protected final long ownerRevisionId;

	/**
	 * ユニットを所有するファイルのID
	 */
	protected final long ownerFileId;

	/**
	 * ユニットを構成する文字列
	 */
	protected final String core;

	/**
	 * ハッシュ値
	 */
	protected final int hash;

	/**
	 * プログラムの構造上上位にいるユニットのリスト
	 */
	protected final List<UnitInfo> ancestorUnits;

	/**
	 * このユニットの corroboration metric
	 */
	protected final CorroborationMetric cm;

	/**
	 * このユニットの種類
	 */
	protected final UnitType uType;

	/**
	 * 開始行番号
	 */
	protected final int startLine;

	/**
	 * 終了行番号
	 */
	protected final int endLine;

	/**
	 * このユニット単体のCRD表記
	 */
	protected CRDElement crdElement;

	/**
	 * このユニットの完全な CRD
	 */
	protected CRD crd;

	/**
	 * このユニットのサイズ(ASTノード数)
	 */
	protected final int length;

	/**
	 * ブロック全体が追加されたかどうか
	 */
	protected final boolean whollyAdded;

	/**
	 * ブロック全体が削除されたかどうか
	 */
	protected final boolean whollyDeleted;

	/**
	 * ブロック識別用文字列
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
	 * このユニット単体のCRD表記を取得する
	 * 
	 * @return
	 */
	public CRDElement getCRDElement() {
		return crdElement;
	}

	/**
	 * このユニットの識別用文字列を取得する
	 * 
	 * @return
	 */
	public String getDiscriminator() {
		return discriminator;
	}

	/**
	 * このユニットを置換する際に用いる特殊文字列を取得する
	 * 
	 * @return
	 */
	public abstract String getReplaceStatement();

}
