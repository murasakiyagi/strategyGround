package strategyGround.ground.scout;

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

import utilCompo.quick.QuickUtil;


/**
* GroundModelのint[][]を受け取って描画する
* 
* 
* 
*/
public class ScouterView {

	Pane pane;

//	GroundModel model;
	int sz;//GroundViewの値を受け取る
	int[] rowZero;
	int[] colZero;//行のゼロマス、列のゼロマス

	public ScouterView(int sz) {
		this.pane = new Pane();
		this.sz = sz;
	}

	/**
	* GroundViewに重ねるrect集
	*/
	public void pileRect(List<Point2D> list, int attNum, int size) {
		for(Point2D pd : list) {
			double col = pd.getX();
			double row = pd.getY();
			Rectangle rect = new Rectangle(pd.getX() * sz + sz, pd.getY() * sz + sz, size, size);
			rect.setStrokeType(StrokeType.INSIDE);
			rect.setStrokeWidth(1);
			rect.setStroke(Color.SILVER);
			rect.setFill(rectColor(attNum));
			
			pane.getChildren().add(rect);
		}
	}

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
	public void paneClear() { pane.getChildren().clear(); }
	public Pane getPane() { return pane; }


//=============================================


	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	
}



