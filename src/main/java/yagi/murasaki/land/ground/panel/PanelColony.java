package yagi.murasaki.land.ground.panel;

import java.io.*;
import java.util.*;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;

//これのフェイスを作ってみたが、”振る舞いを変えるようなことがない”のでやめた
/**
* パネラの管理。機能追加するかも
*/
public class PanelColony<T extends PanelAbst> extends PanelColonyAbst<T> {

	/**
	* コンストラクタ
	* @param ground 舞台
	* @param walkSet 歩けるところ
	*/
	public PanelColony(int[][] ground, Collection<Point2D> walkSet) {
		super(ground, walkSet);
	}
	
}



