package yagi.murasaki.land.ground.panel;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;

/**
* １マスの情報
* <pre>実装要件
	独立して使えるが、このクラスの複数のインスタンスをまとめるPanelColony<T extends PanelAbst>ありきで作っている。
	各実装クラスでstatic create, setNextsを作る。
		private Panel[] nexts;
		private int nextsSize;//nullでないnextの数
		public class Panel {
			//ジェネリクスとnewには当該クラスを充てる
			public static void create(int[][] ground, PanelColony<Panel> pc) {
				for(int i = 0; i < ground.length; i++) {
					for(int j = 0; j < ground[0].length; j++) {
						pc.putPnl(new Point2D(j,i), new Panel(j, i, new AttA(ground[i][j])));
					}
				}
			}
			
			public static void setNexts(Collection<Point2D> walkSet, Map<Point2D, Panel> pnlMap) {
				for(Point2D pd : pnlMap.keySet()) {
					Panel pnl = pnlMap.get(pd);
					Point2D[] pds = P2Dcustom.nexts(pd);//[4]
					for(int i = 0; i < 4; i++) {
						if(walkSet.contains(pds[i])) {
							pnl.nexts[i] = pnlMap.get(pds[i]);//newにしない
							pnl.nextsSize++;
						}
					}
				}
			}
		}
	<pre>
*/
public abstract class PanelAbst {

//	final public static PanelFace KARI = new Panel(0,0,0);
	/** * 座標値 */
	protected int col, row;
	/** * 属性 */
	protected Att att;
	/** * 隣 */
	private PanelAbst[] nexts;
	/** * nullでないnextの数 */
	private int nextsSize;

//コンストラクタ------
	/**
	* コンストラクタ
	* @param col 横
	* @param row 縦
	* @param att 属性
	*/
	protected PanelAbst(int col, int row, Att att) {
		this.col = col;
		this.row = row;
		this.att = att;
		this.nexts = new PanelAbst[4];
	}
//------------

	/**
	* 戻り値はフェイスだが具象クラスを返す
	*/
//	abstract public PanelFace create(int col, int row, Att att);
//	/**
//	* panelの隣接するマス。最大４マスでwalkSetに準ずる。
//	*/
//	abstract public void setNexts(Collection<Point2D> walkSet, Map<Point2D, ? extends PanelAbst> pnlMap);

	
	/**
	* 属性を根本から書き換える
	* @param att 新しい属性
	*/
	public void setAtt(Att att) {
		this.att = att;
	}
	/**
	* 属性の数値を変える
	* @param attNum int数値
	*/
	public void setAtt(int attNum) {
			print("A");
		att.put("num", attNum);
	}
	/**
	* 属性の文字列を変える
	* @param attStr 文字列
	*/
	public void setAtt(String attStr) {
		att.put("str", attStr);
	}
	/**
	* 属性を追加、もしくは書き換え
	* @param key 文字列キー
	* @param obj オブジェクト型の値
	*/
	public void setAtt(String key, Object obj) {
		att.put(key, obj);
	}
//	/**
//	* 属性数値
//	* @return int値
//	*/
//	public int getNum() { return att.getNum(); }
//	/**
//	* 属性文字列
//	* @return 文字列
//	*/
//	public String getStr() { return att.getStr(); }
//	/**
//	* 属性文字列
//	* @param key 文字列キー
//	* @return オブジェクト型の値
//	*/
//	public Object getVal(String key) { return att.get(key); }
//	/**
//	* 属性文字列
//	* @param key 文字列キー
//	* @return オブジェクト型の値
//	*/
//	public int intValue(String key) { return att.intValue(key); }
//	public double doubleValue(String key) { return att.doubleValue(key); }
//	public void removeVal(String key) { att.remove(key); }
//	public void report() { att.report(); }

	/**
	* 何かするところ。何かを受け取って、何かして、何かを返す
	* @param obj オブジェクト
	*/
	abstract public Object something(Object obj);
	
	/** * インフォメーション */
	public void info() {
		print("----- PANELER INFO -----");
		print("COL : ", col, "   ROW : ", row, "ATT : ", att);
	}

	
	/**
	* 属性を返す
	* @return 属性
	*/
	public Att att() { return att; }
	/**
	* 縦座標を返す
	* @return 縦座標
	*/
	public int getRow() { return row; }
	/**
	* 横座標を返す
	* @return 横座標
	*/
	public int getCol() { return col; }
	/**
	* Point2Dを返す
	* @return 属性
	*/
	public Point2D getPd() { return new Point2D(col, row); }


	/** * 便利機能 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	
}



