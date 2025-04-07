package yagi.murasaki.land.event;

import java.io.*;
import java.util.*;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.event.Keys;
import yagi.murasaki.utilCompo.event.HandlerFace;

/**
* キー・イベント対応
*/
public class KeyHandler implements HandlerFace<KeyEvent> {

	/** * キー状態保持クラス */
	Keys keys;
	
	/** * コンストラクタ */
	public KeyHandler() {
		this.keys = new Keys();
	}

	/**
	* キー・プレス・イベント対応
	* @param e キーイベント
	*/
	public void handle(KeyEvent e) {
		keys.keyP(e);
	}
	
	/**
	* キー・リリース・イベント対応
	* @param e キーイベント
	*/
	public void handle2(KeyEvent e) {
		keys.keyR(e);
	}
	
	/**
	* ブランク
	* @param e キーイベント
	*/
	public void handle3(KeyEvent e) {}//null
	/**
	* ブランク
	* @param e キーイベント
	*/
	public void handle4(KeyEvent e) {}//null
	/**
	* ブランク
	* @param e キーイベント
	*/
	public void handle5(KeyEvent e) {}//null

	/** * 便利機能 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}

}