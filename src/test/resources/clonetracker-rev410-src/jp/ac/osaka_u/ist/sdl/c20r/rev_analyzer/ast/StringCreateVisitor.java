package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast;

public class StringCreateVisitor extends ExtendedNaiveASTFlattener {

	private String str = null;

	public StringCreateVisitor() {
		super();
	}

	public String getString() {
		if (str == null) {
			str = getStringWhiteSpacesRemoved();
		}
		return str;
	}

	public StringBuffer getBuffer() {
		return super.buffer;
	}

	/**
	 * builder �Ɋi�[����Ă��镶���񂩂��s�C�C���f���g���폜����
	 * 
	 * @return
	 */
	protected String getStringWhiteSpacesRemoved() {
		//String before = builder.toString();
		String before = buffer.toString();
		StringBuilder tmpBuilder = new StringBuilder();

		for (String line : before.split("\n")) {
			if (line.isEmpty()) {
				continue;
			}
			tmpBuilder.append(line.trim() + "\n");
		}

		return tmpBuilder.toString();
	}
	
}
