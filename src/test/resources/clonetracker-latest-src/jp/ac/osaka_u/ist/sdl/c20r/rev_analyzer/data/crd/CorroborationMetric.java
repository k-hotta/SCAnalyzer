package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd;

/**
 * 各ブロックの衝突を回避するために用いるメトリクスを表すクラス
 * 
 * @author k-hotta
 * 
 */
public class CorroborationMetric {

	/**
	 * サイクロマチック数
	 */
	private final int cyclomaticComplexity;

	/**
	 * メソッド呼び出しの数
	 */
	private final int fanOut;

	/**
	 * 行数に対するサイクロマチック数の割合
	 */
	private final double decisionDensity;

	public CorroborationMetric(int cyclomaticComplexity, int fanOut,
			double decisionDensity) {
		this.cyclomaticComplexity = cyclomaticComplexity;
		this.fanOut = fanOut;
		this.decisionDensity = decisionDensity;
	}

	/**
	 * サイクロマチック数を取得
	 * 
	 * @return
	 */
	public int getCC() {
		return cyclomaticComplexity;
	}

	/**
	 * fan-outの値を取得
	 * 
	 * @return
	 */
	public int getFO() {
		return fanOut;
	}

	/**
	 * サイクロマチック数と行数の比を取得
	 * 
	 * @return
	 */
	public double getDD() {
		return decisionDensity;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CorroborationMetric)) {
			return false;
		}
		CorroborationMetric target = (CorroborationMetric) o;
		int ccTarget = target.getCC();
		int foTarget = target.getFO();
		double ddTarget = target.getDD();

		return ((this.cyclomaticComplexity == ccTarget)
				&& (this.fanOut == foTarget) && (this.decisionDensity == ddTarget));
	}
	
	@Override
	public String toString() {
		return "CC: " + cyclomaticComplexity + "\tFO: " + fanOut + "\tDD: " + decisionDensity;
	}

}
