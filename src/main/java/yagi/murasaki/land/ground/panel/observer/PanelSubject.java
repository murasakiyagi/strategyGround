package yagi.murasaki.land.ground.panel.observer;

import java.io.*;
import java.util.*;
import java.awt.Point;

/**
* 通知を発する
* @author 作者：俺
* @version 1.00
*/
public interface PanelSubject {

	/**
	* 受け取るクラスの追加
	* @param obs 登録するオブザーバー
	*/
	public void registerObserver(PanelObserver obs);
	
	/**
	* 受け取るクラスの削除
	* @param obs 登録中のオブザーバー
	*/
	public void removeObserver(PanelObserver obs);
	
	/**
	* オブザーバーに通知
	*/
	public void notifyObservers();

}