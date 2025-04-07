package yagi.murasaki.land.event;

import java.io.*;
import java.util.*;

import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.Camera;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.event.HandlerFace;

/**
* スクロール・イベント対応（マウスホイール）
*/
public class ScrollHandler implements HandlerFace<ScrollEvent> {
	
	/** * 操作するカメラ */
	Camera camera;

	/** * 空のコンストラクタ */
	public ScrollHandler() {}
	/**
	* コンストラクタ
	* @param camera 操作するカメラ
	*/
	public ScrollHandler(Camera camera) {
		this.camera = camera;
		print(camera);
	}

	/**
	* スクロールイベント
	* @param e スクロールイベント
	*/
	public void handle(ScrollEvent e) {
// 		print("SCROLL HAND");
		cameraScaler(e);
	}

		/** 
		* カメラのスケール変更
		* 前に転がすと、、、。後に転がすと、、、。
		* @param e スクロール・イベント
		*/
		private void cameraScaler(ScrollEvent e) {
			double snX = e.getSceneX();//シーン上の座標
			double snY = e.getSceneY();
// 			double bai = 0.10;//１スクロールの拡大倍率
// 			double d = camera.getScaleY() - ( bai * Math.signum(e.getDeltaY()) );//e.getDeltaはYのみ
			double tz = camera.getTranslateZ() - (20 * Math.signum(e.getDeltaY()) );
			camera.setTranslateZ(tz);
// 				print(bai, d, tz);
		}


	/**
	* ブランク
	* @param e スクロールイベント
	*/
	public void handle2(ScrollEvent e) {}//null
	/**
	* ブランク
	* @param e スクロールイベント
	*/
	public void handle3(ScrollEvent e) {}//null
	/**
	* ブランク
	* @param e スクロールイベント
	*/
	public void handle4(ScrollEvent e) {}//null
	/**
	* ブランク
	* @param e スクロールイベント
	*/
	public void handle5(ScrollEvent e) {}//null

	/** * 便利機能 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}

}