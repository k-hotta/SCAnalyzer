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
 * Tracer による解析結果をファイルに出力するためのクラス
 * 
 * @author k-hotta
 * 
 */
public class ResultWriter {

	/**
	 * シングルトンオブジェクト
	 */
	private static ResultWriter SINGLETON = null;

	/**
	 * 現在プールされているリビジョン番号
	 */
	private final ConcurrentLinkedQueue<Integer> pooledRevisions;

	/**
	 * 出力する文字列のマップ <br>
	 * key　はリビジョン番号 (r が入っている場合は r と r+1 の差分) <br>
	 * value はそのリビジョン間で出力する文字列
	 */
	private final ConcurrentMap<Integer, String> toWrite;

	/**
	 * あるリビジョンとその次の解析対象リビジョンとの対応を表すマップ
	 */
	private final ConcurrentMap<Integer, Integer> nextRevNum;

	/**
	 * 結果の出力先ディレクトリ <br>
	 * この下に，r-r+1.csv という名前で結果が出力されていく
	 */
	private final String resultDir;

	private ResultWriter() {
		this.pooledRevisions = new ConcurrentLinkedQueue<Integer>();
		this.toWrite = new ConcurrentHashMap<Integer, String>();
		this.nextRevNum = new ConcurrentHashMap<Integer, Integer>();
		this.resultDir = TracerSettings.getInstance().getResultDir();
	}

	/**
	 * インスタンスを取得
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
	 * ファイルに出力する情報をプールする
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
	 * プールが空かどうかを取得する
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return pooledRevisions.isEmpty();
	}

	/**
	 * 現在プールされているリビジョン番号を1つ取得する <br>
	 * 何もプールされていなければ -1
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
	 * 引数で指定されたリビジョン番号の情報をプールから消去する
	 * 
	 * @param key
	 */
	public synchronized void remove(final int key) {
		if (toWrite.containsKey(key)) {
			toWrite.remove(key);
		}
	}

	/**
	 * プールされている情報を全出力
	 */
	public void writeAll() {
		for (final int revNum : toWrite.keySet()) {
			write(revNum);
		}
	}

	/**
	 * 引数で指定されたリビジョン番号とその次のリビジョンとの差分を出力
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
