package yagi.murasaki.land.ground;

import java.io.*;
import java.util.*;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.geometry.Point2D;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;

import yagi.murasaki.land.ground.panel.PanelColony;
import yagi.murasaki.land.ground.panel.concPanel.ShapePanel;

/**
* GroundModelのint[][]を受け取って描画する
* ここでは単純な並べ方をしているが、斜めに描画する場合は描画位置の調整が必要
	imgSize = ImageView.getWidth();
	kitenX = gdRow * imgSize/2 + 10;
	kitenY = 任意;
* 
* 
*/
public class GroundView {

	/** * このクラス固有 */
	private Pane pane;
	/** * ground生成クラス */
	private GroundModel model;
	/** * １マスのサイズ */
	public final int SZ = 20;
	private int[] rowZero;
	private int[] colZero;//行のゼロマス、列のゼロマス

	/**
	* コンストラクタ
	* @param model ground生成クラス
	*/
	public GroundView(GroundModel model) {
		this.pane = new Pane();
		this.model = model;
	}

	/**
	* groundだけ描画。Unitとかはしない
	* @param ground 舞台
	* @param pm マスにあるパネル群
	*/
	public void masuNarabeRect(int[][] ground, PanelColony<ShapePanel> pc) {//action
		pane.getChildren().clear();
		zeroCnter(ground);
		for(int i=0; i < ground.length; i++) {
			for(int j=0; j < ground[0].length; j++) {
				//rectの引数は(x, y, w, h)なので(j, i,,,)となる
//				Rectangle rect = new Rectangle(j * SZ + SZ, i * SZ + SZ, SZ, SZ);
				Shape rect = new Rectangle(j * SZ + SZ, i * SZ + SZ, SZ, SZ);
				rect.setStrokeType(StrokeType.INSIDE);
				rect.setStrokeWidth(1);
				rect.setStroke(Color.SILVER);
				rect.setViewOrder(1000);
				if(ground[i][j] > 0) {
					rect.setFill(Color.WHITE);
				} else {
					rect.setFill(Color.BLUE);
				}
				//newPanelの引数は(c, r,,,)なので(j, i,,,)となる
//				ShapePanel spnl = new ShapePanel(j, i, );
				ShapePanel spnl = pc.pnl(new Point2D(j, i));
				spnl.setShape(rect);
				pc.putPnl(spnl);
				pane.getChildren().add(rect);
			}
		}
		
		numberRowCol(ground);
	}

		/**
		* groundを表示したときに、上と左側に座標番号を打つ
		* @param ground 舞台
		*/
		public void numberRowCol(int[][] ground) {
//			int right = (ground.length + 1) * SZ;
//			int bottom = (ground[0].length + 1) * SZ;
			
			for(int i=0; i < ground.length; i++) {
				Text txI = new Text(0, i * SZ + SZ * 2, String.valueOf(i));
//				Text txIz = new Text(i * SZ + SZ * 2, right, String.valueOf(rowZero[i]));
				if(i >= SZ) {
					txI.setFont(new Font(SZ / 2));
				} else {
					txI.setFont(new Font(SZ));
				}
				pane.getChildren().add(txI);
//				pane.getChildren().add(txIz);
				
				for(int j=0; j < ground[0].length; j++) {
					Text txJ = new Text(j * SZ + SZ, SZ, String.valueOf(j));
//					Text txJz = new Text(bottom + SZ, j * SZ + SZ, String.valueOf(colZero[j]));
					if(j >= SZ) {
						txI.setFont(new Font(SZ / 2));
					} else {
						txI.setFont(new Font(SZ));
					}
					pane.getChildren().add(txJ);
//					pane.getChildren().add(txJz);
				}
			}
		}
		

	/**
	* groundを表示したときに、右側にゼロマスの数をうつ
	* @param ground 舞台
	*/
	private void zeroCnter(int[][] ground) {
		int zeroi = 0;
		int zeroj = 0;
		rowZero = new int[ground.length];
		colZero = new int[ground[0].length];
		
		for(int i=0; i < ground.length; i++) {
			for(int j=0; j < ground[i].length; j++) {
				if(ground[i][j] == 0) {
					zeroj++;
				}
			}
			rowZero[i] = zeroj;
			zeroj = 0;
		}

		for(int i=0; i < ground[0].length; i++) {
			for(int j=0; j < ground.length; j++) {
				if(ground[j][i] == 0) {
					zeroi++;
				}
			}
			colZero[i] = zeroi;
			zeroi = 0;
		}
	}


// ==================================
// ゲッター
	/**
	* pane.getChildren()が長いので
	* @param nd paneに入れるノード
	*/
	protected void paneAdd(Node nd) {
		pane.getChildren().add(nd);
	}
	/**
	* ペイン
	* @return ペイン
	*/
	public Pane getPane() { return pane; }

//=============================================


	/** * マスゲームの舞台 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/**
	* 「 +" "+ 」いらず
	* @param objs 可変長Object
	*/
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	
}



