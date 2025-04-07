package yagi.murasaki.land.ground.scout;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;

/**
* routeListを歩く実験用クラス
* 
*/
public class Mover {

	/** * 動く（予定の）駒 */
	Circle koma;
	/** * 現在地 */
	int col, row;
	
	/** * 目的地 */
	Point2D tgtPd;
	/** * 描画サイズ */
	int SZ;
	/** * ルートリスト */
	List<Point2D> routeList;
	/** * 進捗状況 */
	int progCnt;
	
	/**
	* ルート上を丸が動く予定
	* @param sz 描画サイズ
	* @param pane 画用紙
	*/
	public Mover(int sz, Pane pane) {
		this.SZ = sz;
		koma = new Circle(SZ/2 - 4, Color.GREEN);
		pane.getChildren().add(koma);
	}

	/**
	* routeListの取り込み
	* @param routeList ルートのリスト
	*/
	public void setRoute(List<Point2D> routeList) {
		this.routeList = routeList;
	}
	
	/**
	* ルーティング済みかrouteListの確認
	* @return ルーティング済みなら真
	*/
	public boolean isRouted() {
		if(routeList == null || routeList.size() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/** * 移動処理 */
	public boolean moving() {
		if(routeList.size() == progCnt) {
			return true;
		} else {
			setPos(routeList.get(progCnt++));
			return false;
		}
	}

	/**
	* 駒の移動
	* @param col 横
	* @param row 縦
	*/
	public void setPos(int col, int row) {
		this.col = col;
		this.row = row;
		koma.setCenterX(ajust(col));
		koma.setCenterY(ajust(row));
	}

		/**
		* 駒の移動
		* @param pd 移動する座標
		*/
		public void setPos(Point2D pd) {
			setPos((int)pd.getX(), (int)pd.getY());
		}


	/**
	* 目的地の変更
	* @param pd 目的地
	*/
	public void setTgtPd(Point2D pd) {
		this.tgtPd = tgtPd;
	}
	/**
	* 微調整
	* @param xy 座標値
	* @return 座標値に係数をかけた値
	*/
	private double ajust(int xy) {
		return xy * SZ + SZ * 1.5;
	}
	/**
	* こま
	* @return こま
	*/
	public Circle getShape() {
		return koma;
	}
	
	/** * 便利機能 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}
	

}



