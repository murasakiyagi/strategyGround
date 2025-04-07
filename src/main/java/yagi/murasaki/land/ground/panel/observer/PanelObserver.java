package yagi.murasaki.land.ground.panel.observer;

import java.io.*;
import java.util.*;
import java.awt.Point;

/**
* 通知を受け取る
* @author 作者：俺
* @version 1.00
*/
public interface PanelObserver {

	/**
	* イベントの選択物を受け取る
	* @param pt 座標
	* @param att 属性値
	*/
	public void update(Point pt, int att);

}