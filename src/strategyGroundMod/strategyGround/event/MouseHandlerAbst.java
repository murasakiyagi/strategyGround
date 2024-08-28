package strategyGround.event;

import java.io.*;
import java.util.*;
import java.util.function.*;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
//import javafx.scene.image.ImageView;
//import javafx.scene.Camera;
import javafx.scene.Scene;
//import javafx.scene.shape.Rectangle;
//import javafx.geometry.Point2D;
//import javafx.scene.layout.Pane;
//import javafx.geometry.Bounds;


import utilCompo.quick.QuickUtil;
import utilCompo.event.HandlerFace;
import utilCompo.information.InfoSubject;
import utilCompo.information.Information;

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
public abstract class MouseHandlerAbst implements HandlerFace<MouseEvent> {

	//座標
	double ssx, ssy, fsx, fsy;//getScreenX: start, final : PC画面の絶対値
	double sx, sy, fx, fy;//getX：イベントの元になったNodeの座標
//	double csx, csy, cfx, cfy;//scene：の座標
	double tlx, tly;//translate：移動分
//	Camera camera;
	String type;
	int row, col;
//	Rectangle surround;//囲む
	
	Information info;
	
	boolean isEntered;//エントリーするだけで反応するのが邪魔ならfalseにする
	boolean isPrimary;
	boolean isSecondary;
	
	public MouseHandlerAbst() {
		this.info = new Information();
	}

//プレス
	public abstract void handle(MouseEvent e);

	public void subHandle(MouseEvent e) {//press
		
		start(e);
		info.mouseSelect(e);

		if( e.isPrimaryButtonDown() ) {//このメソッドはクリックではできない
			print("プライマリ・プレス");
			isPrimary = true;
//			nodeInfo();
		} else if( e.isSecondaryButtonDown() ) {
			print("セカンダリ・プレス");
			isSecondary = true;
			isEntered = !isEntered;
			print("isEntered", isEntered);
		}
	}
	
		/**
		* mousePressed開始位置取得
		*/
		private void start(MouseEvent e) {//mousePressed
			ssx = e.getScreenX();
			ssy = e.getScreenY();
			sx = e.getX();
			sy = e.getY();
		}

		/**
		* mouseReleaseかdoraggにて呼び出し
		*/
		private void finish(MouseEvent e) {
			fsx = e.getScreenX();
			fsy = e.getScreenY();
			fx = e.getX();
			fy = e.getY();
		}
		
		protected void nodeInfo() {
			Node nd = info.getNode();
				print("Node.typeSelect", nd.getTypeSelector());
		}

//リリース
	public void handle2(MouseEvent e) {//リリース
		if(isPrimary && isSecondary) {
			print("ボゥス・リリース（クリックより前）");
		} else if(isPrimary) {
			print("プライマリ・リリース（クリックより前）");
		} else {
			print("セカンダリ・リリース（クリックより前）");
		}
	}//release


//クリック。
	public void handle3(MouseEvent e) {//クリック
// 		クリックの場合ボタンダウンは絶対false
		if(isPrimary && isSecondary) {
			print("ボゥス・クリック");
			isPrimary = false;
			isSecondary = false;
		} else if(isPrimary) {
			print("プライマリ・クリック");
			isPrimary = false;
		} else {//secondary
			print("セカンダリ・クリック");
			isSecondary = false;
		}
	}//click


//ドラッグ
	public void handle4(MouseEvent e) {//ドラッグ
		finish(e);
		if(isPrimary) {
//			cameraMove(e);
		} else {//secondary
			
		}
	}//dragg


//エントリー
	public void handle5(MouseEvent e) {//エントリー（範囲内）
// 		NodeというかもうWindowに入った時だけ発生
// 		各Nodeの境界で発生すると思った。sceneに割り当ててるから
		if(isEntered) {
//			print("handle5 エントリード");
		}
	}//ENTERED
		


	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	/**
	* node.getTypeSelector() == node.getClass().getName()
	* 	なので少し違う
	*/
	String reType(Object obj) {//型名を調べる
		return obj.getClass().getSimpleName();
	}

}