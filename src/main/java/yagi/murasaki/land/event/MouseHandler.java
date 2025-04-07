package yagi.murasaki.land.event;

import java.io.*;
import java.util.*;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;

import yagi.murasaki.utilCompo.information.Information;
import yagi.murasaki.utilCompo.event.eventAbst.MouseHandlerAbst;

import yagi.murasaki.land.ground.GroundManager;

/*
* マウスイベントに対応する動作<br><br>

MouseEventの座標について
	getX(), getScreenX(), getSceneX()
	getSceneXはSceneの相対位置で、マウスカーソルが移動していなくてもsceneの座標が変わればマウスカーソルの相対位置は変わる。
	EventはNodeかSceneのどちらかのソースに適用される。getXは適用されたソースの相対位置
	getScreenXはウィンドウの左上を起点にした絶対位置


Node.mouseTransparent
	マウスイベントに対する透過性

Node.viewOrder
	Nodeのレンダリング順序
	通常、Paneなどが持つObservableListの子の順序でレンダリングされるが、このオーダーが優先される

*/
public class MouseHandler extends MouseHandlerAbst {

	/** * 動作確認用クラス */
	GroundManager gm;
	
	/** * 空のコンストラクタ */
	public MouseHandler() {}
	/**
	* コンストラクタ
	* @param gm 動作確認用クラス
	*/
	public MouseHandler(GroundManager gm) {
		this.gm = gm;
		info.isInfo(false);
	}

//プレス
	/**
	* マウス・プレス・イベント対応
	* @param e マウスイベント
	*/
	public void handle(MouseEvent e) {//press
		super.subHandle(e);
		Node nd = info.getNode();
//			print(isPrimary, isSecondary);
			
		ifRect(nd);
	}

		/**
		* 選択ノードがRectangleだった場合の対応
		* @param nd ノード
		*/
		public void ifRect(Node nd) {
			if(nd.getTypeSelector().equals("Rectangle")) {
				//Shapeから座標を割り出す
//				var spnl = ShapePanel.KARI.spnl((Rectangle)nd);
				//ShapePanelを使わない方法。長い
				var spnl = gm.getShapePc().pnl(Point2D.ZERO).spnl((Rectangle)nd);

					print(spnl);

				gm.setAttShape(spnl, iCnter(spnl.att().intValue("num"), 10, 0));
				row = spnl.getRow();
				col = spnl.getCol();
				
//				gm.zeroEquals(new Point2D(col, row));
			}
		}

	/**
	* 左クリックでカウントアップ、右でダウン
	* @param num 入力値
	* @param max 許容最大値
	* @param min 許容最小値
	* @return 範囲内でカウントアップダウンした値
	*/
	private int iCnter(int num, int max, int min) {
		if(isPrimary) {
			return iPlus(num, max, min);
		} else {
			return iMinus(num, max, min);
		}
	}

	/**
	* クリックごとにカウントアップした値を返す
	* 指定の最大値以上なら最小値にループする
	* @param num 入力値
	* @param max 許容最大値
	* @param min 許容最小値
	* @return 範囲内でカウントアップした値
	*/
	private int iPlus(int num, int max, int min) {
		//アップした時点で最大値を超えるなら
		if(num++ >= max) {
			num = min;
		}
		return num;
	}

	/**
	* クリックごとにカウントダウンした値を返す
	* 指定の最大値以上なら最小値にループする
	* @param num 入力値
	* @param max 許容最大値
	* @param min 許容最小値
	* @return 範囲内でカウントダウンした値
	*/
	private int iMinus(int num, int max, int min) {
		if(num-- <= min) {
			num = max;
		}
		return num;
	}

}