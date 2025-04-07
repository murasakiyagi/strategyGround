package yagi.murasaki.land.ground.scout;

import java.io.*;
import java.util.*;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

import yagi.murasaki.utilCompo.quick.QuickUtil;


/**
* Scouterの結果を描画
*/
public class ScouterView {

	/** * ペイン */
	Pane pane;

//	GroundModel model;
	/** * 座標調整に関するサイズ。GroundViewの値を受け取る */
	int SZ;
	/** * 行のゼロマス、列のゼロマス */
	int[] rowZero, colZero;

	/**
	* コンストラクタ
	* @param SZ 描画スケール
	*/
	public ScouterView(int SZ) {
		this.pane = new Pane();
		this.SZ = SZ;
	}

	/**
	* GroundViewに重ねるrect群
	* @param list 座標リスト
	* @param attNum パネルの属性値
	* @param size Rectangleの縦横の大きさ
	*/
	public void pileRect(List<Point2D> list, int attNum, int size) {
		for(Point2D pd : list) {
//			double col = pd.getX();
//			double row = pd.getY();
			Rectangle rect = new Rectangle(pd.getX() * SZ + SZ, pd.getY() * SZ + SZ, size, size);
			rect.setStrokeType(StrokeType.INSIDE);
			rect.setStrokeWidth(1);
			rect.setStroke(Color.SILVER);
			rect.setFill(rectColor(attNum));
			
			pane.getChildren().add(rect);
		}
	}

	/**
	* 属性値によって色を変える
	* @param attNum パネルの属性値
	* @return Paint:Colorオブジェクト
	*/
	private Color rectColor(int attNum) {
		Color c;
		switch(attNum) {
			case 0 : 
				c = Color.BLUE;
				break;
			case 1 : 
				c = Color.WHITE;
				break;
			case 2 : 
				c = Color.RED;
				break;
			case 3 : 
				c = Color.ORANGE;
				break;
			case 4 : 
				c = Color.YELLOW;
				break;
			case 5 : 
				c = Color.rgb(0, 50, 0, 0.3);
				break;
			case 6 : 
				c = Color.GRAY;
				break;
			case 7 : 
				c = Color.rgb(250, 50, 50, 0.3);
				break;
			case 8 : 
				c = Color.BROWN;
				break;
			default :
				c = Color.BLACK;
				break;
		}
		return c;
	}
	

// ==================================
// ゲッター
	/** * ペインをクリア */
	public void paneClear() { pane.getChildren().clear(); }
	/**
	* ペインを取得
	* @return ペイン
	*/
	public Pane getPane() { return pane; }


//=============================================


	/** * 便利機能 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	
}



