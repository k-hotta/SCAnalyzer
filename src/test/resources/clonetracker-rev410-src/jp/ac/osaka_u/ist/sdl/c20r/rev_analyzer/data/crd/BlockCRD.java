package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.BlockType;

/**
 * ブロック文に関するCRDを表すクラス
 * 
 * @author k-hotta
 * 
 */
public class BlockCRD implements CRDElement {

	private final BlockType bType;

	private final String anchor;

	private final CorroborationMetric cm;

	public BlockCRD(BlockType bType, String anchor, CorroborationMetric cm) {
		this.bType = bType;
		this.anchor = anchor;
		this.cm = cm;
	}

	public BlockType getbType() {
		return bType;
	}

	public String getAnchor() {
		return anchor;
	}

	public CorroborationMetric getCm() {
		return cm;
	}

	@Override
	public String toString() {
		//return bType.name() + "," + anchor + "," + cm.getCC() + ","
				//+ cm.getFO() + "," + cm.getDD();
		return bType.name() + "," + anchor;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BlockCRD)) {
			return false;
		}
		
		return this.toString().equals(o.toString());
	}

}
