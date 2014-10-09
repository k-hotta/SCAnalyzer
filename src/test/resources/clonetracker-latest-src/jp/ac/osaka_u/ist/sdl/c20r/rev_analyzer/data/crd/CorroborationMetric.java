package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd;

/**
 * �e�u���b�N�̏Փ˂�������邽�߂ɗp���郁�g���N�X��\���N���X
 * 
 * @author k-hotta
 * 
 */
public class CorroborationMetric {

	/**
	 * �T�C�N���}�`�b�N��
	 */
	private final int cyclomaticComplexity;

	/**
	 * ���\�b�h�Ăяo���̐�
	 */
	private final int fanOut;

	/**
	 * �s���ɑ΂���T�C�N���}�`�b�N���̊���
	 */
	private final double decisionDensity;

	public CorroborationMetric(int cyclomaticComplexity, int fanOut,
			double decisionDensity) {
		this.cyclomaticComplexity = cyclomaticComplexity;
		this.fanOut = fanOut;
		this.decisionDensity = decisionDensity;
	}

	/**
	 * �T�C�N���}�`�b�N�����擾
	 * 
	 * @return
	 */
	public int getCC() {
		return cyclomaticComplexity;
	}

	/**
	 * fan-out�̒l���擾
	 * 
	 * @return
	 */
	public int getFO() {
		return fanOut;
	}

	/**
	 * �T�C�N���}�`�b�N���ƍs���̔���擾
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
