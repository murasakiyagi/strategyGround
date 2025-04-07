package yagi.murasaki.land.event;

import java.io.*;
import java.util.*;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.event.*;
import javafx.scene.Camera;

import yagi.murasaki.utilCompo.event.SceneSetOn;
import yagi.murasaki.utilCompo.event.Keys;

//import event.KeyHandler;
//import event.MouseHandler;
//import event.ScrollHandler;
import yagi.murasaki.land.ground.GroundManager;

/**
* Sceneにイベントを紐付ける作業を一括する
*/
public class SetOnMona extends SceneSetOn {
	
	/**
	* コンストラクタ
	* @param scene イベントを紐付けるシーン
	* @param gm 舞台設定などの窓口
	*/
	public SetOnMona(Scene scene, GroundManager gm) {
		super(
			scene, 
			new Keys(),
			new KeyHandler(),
			new MouseHandler(gm),
			new ScrollHandler()
		);
// 		super.setOn();
		System.out.println("SET ON MONA");
	}



}




