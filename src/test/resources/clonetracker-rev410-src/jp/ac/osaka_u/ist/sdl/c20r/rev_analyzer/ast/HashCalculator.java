package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

/**
 * �n�b�V���l�̌v�Z���s�����ۃN���X<br>
 * ��̓I�Ȍv�Z�͎q�N���X�Œ�߂�
 * 
 * @author k-hotta
 * 
 */
public abstract class HashCalculator {

	/**
	 * �V���O���g���I�u�W�F�N�g
	 */
	private static HashCalculator SINGLETON = null;

	protected HashCalculator() {

	}

	/**
	 * �C���X�^���X���擾
	 * 
	 * @return
	 */
	public static HashCalculator getInstance() {
		if (SINGLETON == null) {
			SINGLETON = createInstance();
		}
		return SINGLETON;
	}

	/**
	 * �C���X�^���X���쐬
	 * 
	 * @return
	 */
	private static HashCalculator createInstance() {
		switch (Settings.getIntsance().getHashType()) {
		case JAVA_STR_HASH:
			return new JavaStringHashCalculator();
		default:
			assert false; // here shouldn't be reached
			return null;
		}
	}

	/**
	 * �����ŗ^����ꂽ�����񃊃X�g��A�����ăn�b�V���l���v�Z����
	 * 
	 * @param lines
	 * @return
	 */
	public int getHash(List<String> lines) {
		StringBuilder builder = new StringBuilder();
		for (String line : lines) {
			builder.append(line);
		}
		return getHash(builder.toString());
	}

	/**
	 * �n�b�V���l�̋�̓I�Ȍv�Z���@���L�q
	 * 
	 * @param str
	 * @return
	 */
	public abstract int getHash(String str);

}
