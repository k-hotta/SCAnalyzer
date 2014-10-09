package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd;

/**
 * クラスに関するCRDを表すクラス <br>
 * file, class, CM
 * 
 * @author k-hotta
 * 
 */
public class ClassCRD implements CRDElement {

	private final String qualifiedName;

	private final CorroborationMetric cm;

	public ClassCRD(String qualifiedName, CorroborationMetric cm) {
		this.qualifiedName = qualifiedName;
		this.cm = cm;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public CorroborationMetric getCM() {
		return cm;
	}

	@Override
	public String toString() {
		return qualifiedName;
		//return qualifiedName + "," + cm.getCC() + "," + cm.getFO() + ","
				//+ cm.getDD();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ClassCRD)) {
			return false;
		}

		return this.toString().equals(o.toString());
	}

}
