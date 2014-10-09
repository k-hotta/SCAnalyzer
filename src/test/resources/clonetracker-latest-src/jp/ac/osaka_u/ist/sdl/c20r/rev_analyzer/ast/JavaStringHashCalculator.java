package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast;

public class JavaStringHashCalculator extends HashCalculator {
	
	protected JavaStringHashCalculator() {
		
	}
	
	@Override
	public int getHash(String str) {
		return str.hashCode();
	}

}
