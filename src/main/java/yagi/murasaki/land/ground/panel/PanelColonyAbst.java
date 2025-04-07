package yagi.murasaki.land.ground.panel;

import java.io.*;
import java.util.*;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;

//これのフェイスを作ってみたが、”振る舞いを変えるようなことがない”のでやめた
/**
* パネラの管理。PanelFace実装クラスごとにこのクラスがあるように
*/
public abstract class PanelColonyAbst<T extends PanelAbst> {

	/** * 共通のattMapを持つPanelColonyAbstのインスタンスをグループ化して保持 */
	protected Set<PanelColonyAbst> colonyBindSet;
	
	/** * パネルマップ */
	protected Map<Point2D, T> pnlMap;
	/** * 歩けるところ */
	protected Collection<Point2D> walkSet;
	/** * 属性マップ。いわば＜Point2D, T.att()＞ */
	protected Map<Point2D, Att> attMap;
	/** * 舞台の大きさ */
	protected int width, height;
	/** * リセット時用ストック */
	protected final int[][] ground;

	/**
	* コンストラクタ
	* @param ground 舞台
	* @param walkSet 歩けるところ
	*/
	public PanelColonyAbst(int[][] ground, Collection<Point2D> walkSet) {
		this.ground = ground;
		this.walkSet = walkSet;
//		this.kariPf = kariPf;
		this.pnlMap = new HashMap<>();
		this.attMap = new HashMap<>();
		this.height = ground.length;
		this.width = ground[0].length;
		
		this.colonyBindSet = new HashSet<>();
		colonyBindSet.add(this);
	}
	
	/**
	* パネルマップにプット。createで呼ばれる想定
	* @param pd 座標
	* @param t 実装パネル
	*/
	public void putPnl(Point2D pd, T t) {
		pnlMap.put(pd, t);
		setMyAtt(pd, t.att());
	}
	
		/**
		* パネルマップにプット。createで呼ばれる想定
		* @param t 実装パネル
		*/
		public void putPnl(T t) {
			pnlMap.put(t.getPd(), t);
			setMyAtt(t.getPd(), t.att());
		}
	
	/**
	* Attの中身でなくAttごと変える。Attを共有している場合、これで他のコロニーにも影響する
	* @param pd 座標
	* @param att 属性
	*/
	public void setMyAtt(Point2D pd, Att att) {
		pnlMap.get(pd).setAtt(att);
		attMap.put(pd, att);
	}
		//属性変更時
		//pnlMap.get().getAtt() == attMap.get()
		/**
		* 属性数値を変える。Attを共有している場合、これで他のコロニーにも影響する
		* @param pd 座標
		* @param num 属性数値
		*/
		public void setMyAtt(Point2D pd, int num) {
			pnlMap.get(pd).setAtt(num);
		}
		/**
		* 属性文字列を変える。Attを共有している場合、これで他のコロニーにも影響する
		* @param pd 座標
		* @param str 属性文字列
		*/
		public void setMyAtt(Point2D pd, String str) {
			pnlMap.get(pd).setAtt(str);
		}
		/**
		* 属性値を変える。Attを共有している場合、これで他のコロニーにも影響する
		* @param pd 座標
		* @param key キー
		* @param obj オブジェクト
		*/
		public void setMyAtt(Point2D pd, String key, Object obj) {
			pnlMap.get(pd).setAtt(key, obj);
		}

	/**
	* 　バインドされたコロニーとともに、属性のインスタンスごと変更する
	* 　自分の或る座標のAttと、別コロニーの同じ座標のAttは同じインスタンスにする。
	* なのでSubPanelごとのメンバーを持ち、このクラスのpnl()で呼び出した後、キャストして操作する
	* @param pd 座標
	* @param att 属性
	*/
	public void setAtt(Point2D pd, Att att) {
		for(PanelColonyAbst pc : colonyBindSet) {
//			pc.pnl(pd).setAtt(att.copy());//該当のPanelに対し
			pc.pnl(pd).setAtt(att);//該当のPanelに対し
			pc.setMyAtt(pd, att);//Managerに対し
		}
	}
		//属性変更前
		/**
		* バインドセットのAttが別である場合、同じ値を入れたいときはこれを使う
		* @param pd 座標
		* @param num 属性数値
		*/
		public void setAtt(Point2D pd, int num) {
			for(PanelColonyAbst pc : colonyBindSet) {
				pc.setMyAtt(pd, num);
			}
		}
		/**
		* バインドセットのAttが別である場合、同じ値を入れたいときはこれを使う
		* @param pd 座標
		* @param str 属性文字列
		*/
		public void setAtt(Point2D pd, String str) {
			for(PanelColonyAbst pc : colonyBindSet) {
				pc.setMyAtt(pd, str);
			}
		}
		/**
		* バインドセットのAttが別である場合、同じ値を入れたいときはこれを使う
		* @param pd 座標
		* @param key キー
		* @param obj オブジェクト
		*/
		public void setAtt(Point2D pd, String name, Object obj) {
			for(PanelColonyAbst pc : colonyBindSet) {
				pc.setMyAtt(pd, name, obj);
			}
		}

	//strとかは適宜追加
	/**
	* 属性数値をgroundを元に変更する
	*/
	public void reset() {
		for(int i = 0; i < ground.length; i++) {
			for(int j = 0; j < ground[0].length; j++) {
				Point2D pd = new Point2D(j,i);
				//pnlMap.get().setAtt()と同義
				attMap.get(pd).put("num", ground[i][j]);
			}
		}
	}

//バインド
	/**
	* 別種のコロニーをバインドセットに追加。相互作用
	* @param pcs 可変長各種コロニー
	*/
	public void colonyBind(PanelColonyAbst... pcs) {
		for(PanelColonyAbst pc : pcs) {
			colonyBindSet.add(pc);
			if(!pc.getManaBindSet().contains(this)) {
				pc.colonyBind(this);
			}
		}
	}

	/**
	* 自信をバインドセットから削除。相互作用
	* @param pc 各種コロニー
	*/
	public void removeManaBindSet() {
		for(PanelColonyAbst pc : colonyBindSet) {
			if(pc.getManaBindSet().contains(this)) {
				pc.removeBindSet(this);
			}
		}
		colonyBindSet.remove(this);
	}

		/**
		* シンプルな別種のコロニーをバインドセットから削除。単体作用
		* @param pc 各種コロニー
		*/
		private void removeBindSet(PanelColonyAbst pc) {
			colonyBindSet.remove(pc);
		}

	/**
	* コロニーバインドセットを取得
	* @return コロニーセット
	*/
	public Set<PanelColonyAbst> getManaBindSet() {
		return colonyBindSet;
	}

	/** * コロニーバインドセットを表示 */
	public void bindCheck() {
		int cnt = 0;
		for(PanelColonyAbst pc : colonyBindSet) {
			if(pc != this) {
				for(Point2D pd : attMap.keySet()) {
					if(getAtt(pd).get("num") == pc.getAtt(pd).get("num")) {
						print(true);
					} else {
						print(false);
					}
				}
				print();
			}
		}
	}

//ゲッター
	/**
	* 指定の座標は歩けるか
	* @param pd 座標
	* @return 歩ければ真
	*/
	public boolean isWalk(Point2D pd) {
		return walkSet.contains(pd);
	}
	/**
	* 歩ける実装パネルを返す
	* @param pd 座標
	* @return 歩ければ実装パネルを、なければnull。
	*/
	public T walkPnl(Point2D pd) {
		if(walkSet.contains(pd)) {
			return pnl(pd);
		} else {
			return null;
		}
	}

	/**
	* 実装パネルを返す
	* @param pd 座標
	* @return 実装パネル
	*/
	public T pnl(Point2D pd) {
		return pnlMap.get(pd);
	}
	/**
	* 実装パネルを返す
	* @param col 横座標
	* @param row 縦座標
	* @return 実装パネル
	*/
	public T pnl(int col, int row) {
		return pnlMap.get(new Point2D(col, row));
	}
	/**
	* 属性マップを取得
	* @return 属性マップ
	*/
	public Map<Point2D, Att> getAttMap() { return attMap; }
	
	/**
	* groundを返す
	* @return ground
	*/
	public int[][] getGround() { return ground; }
	/**
	* 歩けるマップを取得
	* @return 歩けるマップ
	*/
	public Collection<Point2D> getWalkSet() { return walkSet; }

	/**
	* 実装パネルマップを取得
	* @return 実装パネルマップ
	*/
	public Map<Point2D, T> getPnlMap() { return pnlMap; }
	/**
	* groundの高さ
	* @return groundの高さ
	*/
	public int getHeight() { return height; }
	/**
	* groundの幅
	* @return groundの幅
	*/
	public int getWidth() { return width; }
	/**
	* 指定座標の属性を返す
	* @param pd 座標
	* @return 属性
	*/
	public Att getAtt(Point2D pd) { return attMap.get(pd); }
	/**
	* 指定座標の属性数値を返す
	* @param pd 座標
	* @return 属性数値
	*/
	public int getNum(Point2D pd) { return attMap.get(pd).intValue("num"); }
	/**
	* 指定座標の属性数値を返す
	* @param pd 座標
	* @param key キー
	* @return 属性数値
	*/
	public int intValue(Point2D pd, String key) { return attMap.get(pd).intValue(key); }
	/**
	* 指定座標の属性数値を返す
	* @param key キー
	* @return 属性数値
	*/
	public double doubleValue(Point2D pd, String key) { return attMap.get(pd).doubleValue(key); }

//プリント
	/** * 属性数値を表示 */
	public void numMapPrint() {
		print("--- NUM MAP ---");
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				int kari = getNum(new Point2D(j, i));
				if(kari >= 10) {
					System.out.print("  " + kari);
				} else {
					System.out.print("   " + kari);
				}
			}
			System.out.println();
		}
		print("--- NUM MAP END ---\n");
	}

	/** * 便利機能 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}
	
}



