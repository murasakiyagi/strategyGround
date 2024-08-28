package strategyGround.event;

import java.io.*;
import java.util.*;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
//import javafx.scene.image.ImageView;
//import javafx.scene.Camera;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;
//import javafx.scene.layout.Pane;
//import javafx.geometry.Bounds;

import utilCompo.information.Information;

import strategyGround.ground.Paneler;
import strategyGround.ground.GroundManager;

/*
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

	//座標
//	double ssx, ssy, fsx, fsy;//getScreenX: start, final : PC画面の絶対値
//	double sx, sy, fx, fy;//getX：イベントの元になったNodeの座標
////	double csx, csy, cfx, cfy;//scene：の座標
//	double tlx, tly;//translate：移動分
//	String type;
	int row, col;
//	
//	Information info;
//	
//	boolean isEntered;//エントリーするだけで反応するのが邪魔ならfalseにする
//	boolean isPrimary;
//	boolean isSecondary;
	
	GroundManager gm;
	
	public MouseHandler() {}
	public MouseHandler(GroundManager gm) {
		this.gm = gm;
	}

//プレス
	public void handle(MouseEvent e) {//press
		super.subHandle(e);
		Node nd = info.getNode();
			print(isPrimary, isSecondary);
			
		ifRect(nd);
	}

		public void ifRect(Node nd) {
			if(nd.getTypeSelector().equals("Rectangle")) {
				Paneler pnl = Paneler.pnl((Rectangle)nd);
//				pnl.info();

				gm.setPnlAttNum(pnl, iCnter(pnl.getAttNum(), 10, 0));
				row = pnl.getRow();
				col = pnl.getCol();
				
//				gm.zeroEquals(new Point2D(col, row));
			}
		}


	private int iCnter(int num, int max, int min) {
		if(isPrimary) {
			return iPlus(num, max, min);
		} else {
			return iMinus(num, max, min);
		}
	}

	private int iPlus(int num, int max, int min) {
		if(num++ >= max) {
			num = min;
		}
		return num;
	}

	private int iMinus(int num, int max, int min) {
		if(num-- <= min) {
			num = max;
		}
		return num;
	}

}