package jp.ac.osaka_u.ist.sdl.c20r.genealogy.graphcreator;

/**
 * グラフ描画時の設定(ノードの色とか)を定義するインターフェース
 * 
 * @author k-hotta
 * 
 */
public interface GraphSettings {

	/*
	 * ノードの色
	 */
	public static final String NODE_COLOR_CHANGEDPAIR = "yellow";
	
	public static final String NODE_COLOR_ELEMENTS_ADDED = "#3399ff";
	
	public static final String NODE_COLOR_ELEMENTS_DELTED = "red";
	
	public static final String NODE_COLOR_ELEMENTS_ADDED_AND_DELETED = "purple";
	
	public static final String NODE_COLOR_ADDED_HASHCHANGED = "green";
	
	public static final String NODE_COLOR_DELETED_HASHCHANGED = "orange";
	
	public static final String NODE_COLOR_ADDED_DELETED_HASHCHANGED = "1.0 0.3 1.0";

	public static final String NODE_COLOR_START = "#A9A9A9";

	public static final String NODE_COLOR_END = "#A9A9A9";

	/*
	 * ノードの形
	 */
	public static final String NODE_SHAPE_START = "box";

	public static final String NODE_SHAPE_END = "box";

	/*
	 * エッジの色
	 */
	public static final String EDGE_COLOR_HASHCHANGED = "red";

	public static final String EDGE_FONTCOLOR_HASHCHANGED = "red";

}
