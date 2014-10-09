package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block;

import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.AbstractRetrievedElementInfo;

public class RetrievedBlockInfo extends AbstractRetrievedElementInfo {

	/**
	 * ファイルID
	 */
	private final long fileId;

	/**
	 * クローンセットID
	 */
	private long cloneSetId;

	/**
	 * ひとつ外側のユニットのID
	 */
	private final long parentUnitId;

	/**
	 * 開始リビジョンID
	 */
	private final long startRevId;

	/**
	 * 終了リビジョンID
	 */
	private final long endRevId;

	/**
	 * 開始行番号
	 */
	private final int startLine;

	/**
	 * 終了行番号
	 */
	private final int endLine;

	/**
	 * ハッシュ値
	 */
	private final int hash;

	/**
	 * CRDの文字列表記
	 */
	private String crdStr;

	/**
	 * ブロックのCMの値
	 */
	private final int cm;

	/**
	 * このブロックを含むブロックペアのID
	 */
	private long blockPairId;

	/**
	 * このブロックが次のリビジョンへの遷移時に削除されたファイル中に含まれるかどうか
	 */
	private final boolean inDeletedFile;

	/**
	 * このブロックの長さ(= トークン数)
	 */
	private final int length;

	/**
	 * このブロックのサイクロマチック数
	 */
	private final int cc;

	/**
	 * このブロックのFan-Out数
	 */
	private final int fo;

	/**
	 * このブロックのみのCRD
	 */
	private final String discriminator;

	/**
	 * このブロックが追加されたかどうか
	 */
	private final boolean isAdded;

	/**
	 * このブロックが削除されたかどうか
	 */
	private final boolean isDeleted;

	/**
	 * このブロックを所有するクラスの名前
	 */
	private final String rootClassName;

	/**
	 * このブロックを所有するメソッドの名前
	 */
	private final String rootMethodName;

	private final List<String> rootMethodParams;

	private final String crdAfterRootMethod;
	
	private final String blockType;

	/**
	 * コンストラクタ
	 * 
	 * @param id
	 * @param revisionId
	 * @param fileId
	 * @param cloneSetId
	 * @param startLine
	 * @param endLine
	 * @param crdStr
	 */
	public RetrievedBlockInfo(final long id, final long fileId,
			final long parentUnitId, final long startRevId,
			final long endRevId, final int startLine, final int endLine,
			final int hash, final String crdStr, final int cm,
			final boolean inDeletedFile, final int length, final int cc,
			final int fo, final String discriminator, final boolean isAdded,
			final boolean isDeleted, final String rootClassName,
			final String rootMethodName, final String rootMethodParamsAsString,
			final String crdAfterRootMethod, final String blockType) {
		super(id);
		this.fileId = fileId;
		this.cloneSetId = -1;
		this.parentUnitId = parentUnitId;
		this.startRevId = startRevId;
		this.endRevId = endRevId;
		this.startLine = startLine;
		this.endLine = endLine;
		this.hash = hash;
		this.crdStr = crdStr;
		this.cm = cm;
		this.blockPairId = -1;
		this.inDeletedFile = inDeletedFile;
		this.length = length;
		this.cc = cc;
		this.fo = fo;
		this.discriminator = discriminator;
		this.isAdded = isAdded;
		this.isDeleted = isDeleted;
		this.rootClassName = rootClassName;
		this.rootMethodName = rootMethodName;
		this.rootMethodParams = new ArrayList<String>();
		if (rootMethodParamsAsString.length() != 0
				&& rootMethodParamsAsString.equals("N/A")) {
			for (final String param : rootMethodParamsAsString.split(",")) {
				this.rootMethodParams.add(param);
			}
		}
		this.crdAfterRootMethod = crdAfterRootMethod;
		this.blockType = blockType;
	}

	/**
	 * ファイルIDを取得する
	 * 
	 * @return
	 */
	public final long getFileId() {
		return fileId;
	}

	/**
	 * ひとつ外側のユニットのIDを取得する．なければ-1．
	 */
	public final long getParentUnitId() {
		return parentUnitId;
	}

	/**
	 * クローンセットIDを取得する
	 * 
	 * @return
	 */
	public final long getCloneSetId() {
		return cloneSetId;
	}

	/**
	 * クローンセットIDをセットする
	 * 
	 * @param cloneSetId
	 */
	public final void setCloneSetId(final long cloneSetId) {
		this.cloneSetId = cloneSetId;
	}

	/**
	 * 開始リビジョンIDを取得する
	 * 
	 * @return
	 */
	public final long getStartRevId() {
		return startRevId;
	}

	/**
	 * 終了リビジョンIDを取得する
	 * 
	 * @return
	 */
	public final long getEndRevId() {
		return endRevId;
	}

	/**
	 * 開始行番号を取得する
	 * 
	 * @return
	 */
	public final int getStartLine() {
		return startLine;
	}

	/**
	 * 終了行番号を取得する
	 * 
	 * @return
	 */
	public final int getEndLine() {
		return endLine;
	}

	/**
	 * ハッシュ値を取得する
	 * 
	 * @return
	 */
	public final int getHash() {
		return hash;
	}

	/**
	 * CRDの文字列表記を取得する
	 * 
	 * @return
	 */
	public final String getCrdStr() {
		return crdStr;
	}

	/**
	 * CMの値を取得する
	 * 
	 * @return
	 */
	public final int getCm() {
		return cm;
	}

	/**
	 * このブロックを含むブロックペアのIDを取得する
	 * 
	 * @return
	 */
	public final long getBlockPairId() {
		return blockPairId;
	}

	/**
	 * このブロックが次のリビジョンへの遷移時に削除されたファイル中に含まれるかどうかを取得する
	 * 
	 * @return
	 */
	public final boolean isInDeletedFile() {
		return inDeletedFile;
	}

	/**
	 * このブロックのトークン数を取得する
	 * 
	 * @return
	 */
	public final int getLength() {
		return length;
	}

	/**
	 * このブロックのサイクロマチック数を取得する
	 * 
	 * @return
	 */
	public final int getCC() {
		return cc;
	}

	/**
	 * このブロックのFan-Out数を取得する
	 * 
	 * @return
	 */
	public final int getFO() {
		return fo;
	}

	/**
	 * このブロックのみのCRD表記を取得する
	 * 
	 * @return
	 */
	public final String getDiscriminator() {
		return discriminator;
	}

	/**
	 * このブロックを含むブロックペアのIDを設定する
	 * 
	 * @param blockPairId
	 */
	public final void setBlockPairId(final long blockPairId) {
		this.blockPairId = blockPairId;
	}

	/**
	 * CMを考慮しなかった場合に，引数のブロックがこのブロックとマッチするかを判定する
	 * 
	 * @param anotherBlock
	 * @return
	 */
	public final boolean isMatchWithoutCm(RetrievedBlockInfo anotherBlock) {
		return crdStr.equals(anotherBlock.getCrdStr());
	}

	/**
	 * CMを考慮した場合に，引数のブロックがこのブロックとマッチするかを判定する
	 * 
	 * @param anotherBlock
	 * @return
	 */
	public final boolean isMatchWithCm(final RetrievedBlockInfo anotherBlock) {
		return isMatchWithoutCm(anotherBlock)
				&& this.cm == anotherBlock.getCm();
	}

	/**
	 * このブロックが，引数で与えられたIDのリビジョンとその次のリビジョンとの間で修正されたかどうかを取得する
	 * 
	 * @param beforeRevId
	 * @return
	 */
	public final boolean isInChangedFile(final long revId) {
		return this.startRevId == revId || this.endRevId == revId;
	}

	/**
	 * このブロックが生成されたかを取得する
	 * 
	 * @return
	 */
	public final boolean isAdded() {
		return isAdded;
	}

	/**
	 * このブロックが削除されたかを取得する
	 * 
	 * @return
	 */
	public final boolean isDeleted() {
		return isDeleted;
	}

	public final String getRootClassName() {
		return rootClassName;
	}

	public final String getRootMethodName() {
		return rootMethodName;
	}

	public final List<String> getRootMethodParams() {
		return rootMethodParams;
	}

	public final void setCrdStr(final String crdStr) {
		this.crdStr = crdStr;
	}

	public final String getCrdAfterRootMethod() {
		return this.crdAfterRootMethod;
	}
	
	public final String getBlockType() {
		return this.blockType;
	}
	
}
