package strategyGround.event;

import java.io.*;
import java.util.*;

import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.Camera;

import utilCompo.quick.QuickUtil;
import utilCompo.event.HandlerFace;


public class ScrollHandler implements HandlerFace<ScrollEvent> {

	Camera camera;

	public ScrollHandler() {}
	public ScrollHandler(Camera camera) {
		this.camera = camera;
		print(camera);
	}

	public void handle(ScrollEvent e) {
// 		print("SCROLL HAND");
		cameraScaler(e);
	}

		//マウスホイールイベント
		private void cameraScaler(ScrollEvent e) {
			double snX = e.getSceneX();//シーン上の座標
			double snY = e.getSceneY();
// 			double bai = 0.10;//１スクロールの拡大倍率
// 			double d = camera.getScaleY() - ( bai * Math.signum(e.getDeltaY()) );//e.getDeltaはYのみ
			double tz = camera.getTranslateZ() - (20 * Math.signum(e.getDeltaY()) );
			camera.setTranslateZ(tz);
// 				print(bai, d, tz);
		}


	public void handle2(ScrollEvent e) {}//null
	public void handle3(ScrollEvent e) {}//null
	public void handle4(ScrollEvent e) {}//null
	public void handle5(ScrollEvent e) {}//null


	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}

}