package strategyGround.subStage;

import java.io.*;
import java.util.*;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;

import utilCompo.quick.QuickUtil;
import utilCompo.gui.SatelliteStage;;
import strategyGround.ground.GroundManager;

/**
* デバッグ用なので、デザインとか気にしない
*/
public class ControlerStage extends SatelliteStage {

	GroundManager gm;
	Button scutBtn;
	Button resetBtn;
	Button renewalBtn;
	Button costBtn;
	TextField txf;
	int txfNum;
	
	//インスタンスするだけで出る
	public ControlerStage(Stage pare, GroundManager gm) {
		super(pare, 300, 300, "ld");
		setTitle("SystemControlerStage");
		init();
		this.gm = gm;
	}

	private void init() {
		scutBtn = new Button("SCOUT");
		scutBtn.setTranslateY(30 * 4);
		scutBtn.setTranslateX(30);
		scutBtn.setOnAction(e -> scutBtnAction(e));

		resetBtn = new Button("RESET");
		resetBtn.setTranslateY(30 * 5);
		resetBtn.setTranslateX(30);
		resetBtn.setOnAction(e -> resetBtnAction(e));

		renewalBtn = new Button("RENEWAL");
		renewalBtn.setTranslateY(30 * 6);
		renewalBtn.setTranslateX(30);
		renewalBtn.setOnAction(e -> renewalBtnAction(e));

		costBtn = new Button("COST");
		costBtn.setTranslateY(30 * 7);
		costBtn.setTranslateX(30);
		costBtn.setOnAction(e -> costBtnAction(e));

		txf = new TextField();
		txf.setTranslateY(30 * 3);
		txf.setTranslateX(30);
		txf.setOnAction(e -> txfAction(e));

		pane.getChildren().add(scutBtn);
		pane.getChildren().add(resetBtn);
		pane.getChildren().add(renewalBtn);
		pane.getChildren().add(costBtn);
		pane.getChildren().add(txf);
	}

//テキストフィールド
	private void txfAction(ActionEvent ev) {
		String tx = txf.getText();
		print(tx);
		try {
			txfNum = Integer.parseInt(tx);
			gm.setTxfNum(txfNum);
		} catch(Exception e) {
			e.getStackTrace();
		}
	}



//テキスト


//ボタン
	private void scutBtnAction(ActionEvent ev) {
		print("SCOUT BTN ACTION");
		gm.scouting();
	};

	private void resetBtnAction(ActionEvent ev) {
		print("RESET BTN ACTION");
		gm.reset();
	};

	private void renewalBtnAction(ActionEvent ev) {
		print("RENEWAL BTN ACTION");
		gm.action();
	};

	private void costBtnAction(ActionEvent ev) {
		print("COST BTN ACTION");
		gm.costMapping();
	};


	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}

}