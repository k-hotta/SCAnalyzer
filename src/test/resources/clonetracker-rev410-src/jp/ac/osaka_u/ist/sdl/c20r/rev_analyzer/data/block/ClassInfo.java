package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.ClassCRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * クラスを表すクラス
 * 
 * @author k-hotta
 * 
 */
public class ClassInfo extends UnitInfo {

	private final String ownerFileName;

	private final String className;

	private final String fullyQualifiedName;

	public ClassInfo(long ownerRevisionId, long ownerFileId, long id,
			String core, String ownerFileName, String className,
			String fullyQualifiedName, CorroborationMetric cm, int startLine,
			int endLine, int length, String ownerFilePath) {
		super(ownerRevisionId, ownerFileId, id, core, cm, UnitType.CLASS,
				startLine, endLine, length, ownerFilePath, fullyQualifiedName);
		this.ownerFileName = ownerFileName;
		this.className = className;
		this.fullyQualifiedName = fullyQualifiedName;
		this.crdElement = new ClassCRD(fullyQualifiedName, cm);
	}

	@Override
	public String getReplaceStatement() {
		return "CLASS: " + fullyQualifiedName;
	}

	public String getOwnerFileName() {
		return ownerFileName;
	}

	public String getClassName() {
		return className;
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

}
