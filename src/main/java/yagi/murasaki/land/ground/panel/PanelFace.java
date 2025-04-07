package yagi.murasaki.land.ground.panel;

import java.io.*;
import java.util.*;

//import javafx.scene.Node;
//import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;


/**
* １マスの情報を持たせる
* 実装について
	実装クラスのインスタンスをstaticフィールドとして持っておく
		final public static Panel KARI = new Panel();
	マスは複数のまとまりがあって機能するので、strategyGround.ground.PanelManagerのコンストラクタに渡し、実装クラスのまとまりができる
		PanelManager pm = new PanelManager(int[][], Collection<Point2D>, Panel.KARI);
*/
public interface PanelFace {

	/**
	* 実装のインスタンス
	* @param col 横座標
	* @param row 縦座標
	* @param att 属性
	* @return 実装のインスタンス
	*/
	public PanelFace create(int col, int row, Att att);
	
	/**
	* attの変更とともに、何か付加するといい
	* @param att 属性
	*/
	public void setAtt(Att att);
	
	/**
	* 属性を返す
	* @return 属性
	*/
	public Att getAtt();
	/**
	* 縦座標を返す
	* @return 縦座標
	*/
	public int getRow();
	/**
	* 横座標を返す
	* @return 横座標
	*/
	public int getCol();
	/**
	* 座標を返す
	* @return 座標
	*/
	public Point2D getPd();
	
	/**
	* このパネルの上下左右に直接するパネルを登録
	* <pre>実装
		//実装クラスの配列を用意 Panel[] nexts = new Panel[4];
		//getPd()の上下左右隣の座標(+-1)のPanelFaceを実装クラスにキャストして入れていく
		nexts[0] = new Panel(getPd(1, 0))
		nexts[1] = new Panel(getPd(-1, 0))
		nexts[2] = new Panel(getPd(0, 1))
		nexts[3] = new Panel(getPd(0, -1))
		<pre>
	* 
	* @param walkSet 歩けるマスのセット
	* @param pnlMap パネル各種のマップ
	*/
	public void setNexts(Collection<Point2D> walkSet, Map<Point2D, PanelFace> pnlMap);

}



