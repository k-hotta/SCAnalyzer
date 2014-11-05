package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.settings.TracerSettings;

/**
 * Tracer �ɂ���͌��ʂ��t�@�C���ɏo�͂��邽�߂̃N���X
 * 
 * @author k-hotta
 * 
 */
public class ResultWriter {

	/**
	 * �V���O���g���I�u�W�F�N�g
	 */
	private static ResultWriter SINGLETON = null;

	/**
	 * ���݃v�[������Ă��郊�r�W�����ԍ�
	 */
	private final ConcurrentLinkedQueue<Integer> pooledRevisions;

	/**
	 * �o�͂��镶����̃}�b�v <br>
	 * key�@�̓��r�W�����ԍ� (r �������Ă���ꍇ�� r �� r+1 �̍���) <br>
	 * value �͂��̃��r�W�����Ԃŏo�͂��镶����
	 */
	private final ConcurrentMap<Integer, String> toWrite;

	/**
	 * ���郊�r�W�����Ƃ��̎��̉�͑Ώۃ��r�W�����Ƃ̑Ή���\���}�b�v
	 */
	private final ConcurrentMap<Integer, Integer> nextRevNum;

	/**
	 * ���ʂ̏o�͐�f�B���N�g�� <br>
	 * ���̉��ɁCr-r+1.csv �Ƃ������O�Ō��ʂ��o�͂���Ă���
	 */
	private final String resultDir;

	private ResultWriter() {
		this.pooledRevisions = new ConcurrentLinkedQueue<Integer>();
		this.toWrite = new ConcurrentHashMap<Integer, String>();
		this.nextRevNum = new ConcurrentHashMap<Integer, Integer>();
		this.resultDir = TracerSettings.getInstance().getResultDir();
	}

	/**
	 * �C���X�^���X���擾
	 * 
	 * @return
	 */
	public static ResultWriter getInstance() {
		synchronized (ResultWriter.class) {
			if (SINGLETON == null) {
				SINGLETON = new ResultWriter();
			}
		}

		return SINGLETON;
	}

	/**
	 * �t�@�C���ɏo�͂�������v�[������
	 * 
	 * @param beforeRevNum
	 * @param afterRevNum
	 * @param str
	 */
	public synchronized void pool(final int beforeRevNum,
			final int afterRevNum, final String str) {
		pooledRevisions.add(beforeRevNum);
		toWrite.put(beforeRevNum, str);
		nextRevNum.put(beforeRevNum, afterRevNum);
	}

	/**
	 * �v�[�����󂩂ǂ������擾����
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return pooledRevisions.isEmpty();
	}

	/**
	 * ���݃v�[������Ă��郊�r�W�����ԍ���1�擾���� <br>
	 * �����v�[������Ă��Ȃ���� -1
	 * 
	 * @return
	 */
	public int getTargetRevisionNum() {
		if (pooledRevisions.isEmpty()) {
			return -1;
		}

		return pooledRevisions.poll();
	}

	/**
	 * �����Ŏw�肳�ꂽ���r�W�����ԍ��̏����v�[�������������
	 * 
	 * @param key
	 */
	public synchronized void remove(final int key) {
		if (toWrite.containsKey(key)) {
			toWrite.remove(key);
		}
	}

	/**
	 * �v�[������Ă������S�o��
	 */
	public void writeAll() {
		for (final int revNum : toWrite.keySet()) {
			write(revNum);
		}
	}

	/**
	 * �����Ŏw�肳�ꂽ���r�W�����ԍ��Ƃ��̎��̃��r�W�����Ƃ̍������o��
	 * 
	 * @param revNum
	 */
	public void write(final int revNum) {
		if (!toWrite.containsKey(revNum) || !nextRevNum.containsKey(revNum)) {
			assert false; // here shouldn't be reached!!
			return;
		}

		final int next = nextRevNum.get(revNum);
		final String str = toWrite.get(revNum);
		final String resultFile = getResultFile(revNum, next);

		write(resultFile, str);
	}
	
	public void write(final int beforeRevNum, final int afterRevNum, final String str) {
		final String resultFile = getResultFile(beforeRevNum, afterRevNum);
		write(resultFile, str);
	}

	private String getResultFile(final int beforeRevNum, final int afterRevNum) {
		return resultDir + File.separator + beforeRevNum + "-" + afterRevNum
				+ ".csv";
	}

	private void write(final String filePath, final String str) {
		PrintWriter pw = null;
		try {
			File resultFile = new File(filePath);
			pw = new PrintWriter(new BufferedWriter(new FileWriter(resultFile)));
			pw.print(str);
			pw.close();
		} catch (Exception e) {
			System.err.println("failed to write " + filePath);
			if (pw != null) {
				pw.close();
			}
		}
	}

}
