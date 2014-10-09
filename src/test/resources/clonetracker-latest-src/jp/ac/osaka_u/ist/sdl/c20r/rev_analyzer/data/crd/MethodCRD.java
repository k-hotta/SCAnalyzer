package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd;

/**
 * メソッドに関するCRDを表すクラス
 * 
 * @author k-hotta
 * 
 */
public class MethodCRD implements CRDElement {

	private final String signature;

	private final CorroborationMetric cm;

	public MethodCRD(String signature, CorroborationMetric cm) {
		this.signature = signature;
		this.cm = cm;
	}

	public String getSignature() {
		return signature;
	}

	public CorroborationMetric getCM() {
		return cm;
	}

	@Override
	public String toString() {
		return signature;
		//return signature + "," + cm.getCC() + "," + cm.getFO() + ","
				//+ cm.getDD();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MethodCRD)) {
			return false;
		}
	
		return this.toString().equals(o.toString());
	}
	
}
