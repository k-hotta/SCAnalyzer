package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.UnitType;

/**
 * CRD‚ð•\‚·ƒNƒ‰ƒX
 * 
 * @author k-hotta
 * 
 */
public class CRD {

	private final List<CRDElement> elements;

	private final UnitType unitType;

	public CRD(List<CRDElement> elements, UnitType unitType) {
		this.elements = elements;
		this.unitType = unitType;
	}

	public final UnitType getUnitType() {
		return unitType;
	}

	public final List<CRDElement> getElements() {
		return elements;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CRD)) {
			return false;
		}

		CRD target = (CRD) o;

		if (this.unitType != target.getUnitType()) {
			return false;
		}

		return isEqual(target);
	}

	private boolean isEqual(CRD target) {
		List<CRDElement> targetElements = target.getElements();

		if (this.elements.size() != targetElements.size()) {
			return false;
		}

		for (int i = 0; i < this.elements.size(); i++) {
			if (!this.elements.get(i).equals(targetElements.get(i))) {
				return false;
			}
		}

		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (CRDElement element : elements) {
			buffer.append(element.toString() + "\n");
		}
		return buffer.toString();
	}
	
}
