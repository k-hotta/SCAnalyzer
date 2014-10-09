package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.MethodCRD;

public class MethodInfo extends UnitInfo {

	private final String name;

	private final String signature;
	
	private final List<String> paramTypes;

	public MethodInfo(long ownerRevisionId, long ownerFileId, long id,
			String core, String name, String signature, CorroborationMetric cm,
			int startLine, int endLine, int length, String ownerFilePath,
			List<String> paramTypes) {
		super(ownerRevisionId, ownerFileId, id, core, cm, UnitType.METHOD,
				startLine, endLine, length, ownerFilePath, signature);
		this.name = name;
		this.signature = signature;
		this.crdElement = new MethodCRD(signature, cm);
		this.paramTypes = paramTypes;
	}

	@Override
	public String getReplaceStatement() {
		return "METHOD: " + signature;
	}

	public String getName() {
		return name;
	}

	public String getSignature() {
		return signature;
	}
	
	public List<String> getParamTypes() {
		return Collections.unmodifiableList(paramTypes);
	}

}
