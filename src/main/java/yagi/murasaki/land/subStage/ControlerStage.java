package yagi.murasaki.land.subStage;

import java.io.*;
import java.util.*;
import java.util.function.*;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Control;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.gui.SatelliteStage;
import yagi.murasaki.land.ground.GroundManager;

/**
* デバッグ用なので、デザインとか気にしない
*/
public class ControlerStage extends SatelliteStage {

	/** * ボタン等配置スケール */
	private final int SZ = 30;

	/** * グラウンド関連の動作確認クラス */
	private GroundManager gm;
//	private Button scutBtn;
//	private Button resetBtn;
//	private Button renewalBtn;
//	private Button costBtn;
//	private Button moveBtn;
//	private Button areaBtn;
	
	/**
	* インスタンスするだけで出る。サブステージ
	* @param pare 親ステージ
	* @param gm グラウンド関連の動作確認クラス
	*/
	public ControlerStage(Stage pare, GroundManager gm) {
		super(pare, 300, 300, "ld");
		setTitle("SystemControlerStage");
		init();
		this.gm = gm;
	}

	/** * 初期化。色々準備 */
	private void init() {
		int ctrlCnt = 1;
		
//		scutBtn = new Button("SCOUT");
		initBtn(new Button("SCOUT"), ctrlCnt++, 1, e -> scutBtnAction(e));

//		resetBtn = new Button("RESET");
		initBtn(new Button("RESET"), ctrlCnt++, 1, e -> resetBtnAction(e));

//		renewalBtn = new Button("RENEWAL");
		initBtn(new Button("RENEWAL"), ctrlCnt++, 1, e -> renewalBtnAction(e));

//		costBtn = new Button("COST");
		initBtn(new Button("COST"), ctrlCnt++, 1, e -> costBtnAction(e));

//		moveBtn = new Button("MOVE");
		initBtn(new Button("MOVE"), ctrlCnt++, 1, e -> costBtnAction(e));

		
//			teamLbl = new Label("teamX");
//			initLabel(new Label("teamX"), ctrlCnt);

	}
	
	/**
	* ラベルの簡易生成
	* @param ctrl ラベルオブジェクト
	* @param cnt オブジェクト配置のためのカウンタ
	*/
	private void initLabel(Control ctrl, int cnt) {
		ctrl.setTranslateY(SZ * cnt);
		ctrl.setTranslateX(SZ * 4);
		pane.getChildren().add(ctrl);
	}

	/**
	* ボタンの簡易生成
	* @param btn ボタンオブジェクト
	* @param cnt オブジェクト配置のためのカウンタ
	* @param x 横配置
	* @param cons Consumer
	*/
	private void initBtn(ButtonBase btn, int cnt, int x, Consumer<ActionEvent> cons) {
		btn.setTranslateY(SZ * cnt);
		btn.setTranslateX(SZ * x);
		btn.setOnAction(e -> cons.accept(e));
		pane.getChildren().add(btn);
	}

//ボタン
	/** * スカウト * @param ev アクションイベント */
	private void scutBtnAction(ActionEvent ev) {
		print("SCOUT BTN ACTION");
		gm.scoutingView();
	};
	/** * リセット * @param ev アクションイベント */
	private void resetBtnAction(ActionEvent ev) {
		print("RESET BTN ACTION");
		gm.reset();
		gm.moverCreate();
	};
	/** * リニューアル * @param ev アクションイベント */
	private void renewalBtnAction(ActionEvent ev) {
		print("RENEWAL BTN ACTION");
		gm.setting();
		gm.moverCreate();
	};
	/** * コストスカウト * @param ev アクションイベント */
	private void costBtnAction(ActionEvent ev) {
		print("COST BTN ACTION");
//		gm.costRoutingView();
//		gm.sumCostMapping(gm.getHere());
	};
	/** * ムーブ * @param ev アクションイベント */
	private void moveBtnAction(ActionEvent ev) {
		print("MOVE BTN ACTION");
		gm.moving();
	};


	/** * マスゲームの舞台 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}

}