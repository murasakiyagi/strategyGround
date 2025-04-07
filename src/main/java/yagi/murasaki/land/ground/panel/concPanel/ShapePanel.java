package yagi.murasaki.land.ground.panel.concPanel;

import java.io.*;
import java.util.*;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.geometry.Point2D;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;

import yagi.murasaki.land.ground.panel.PanelFace;
import yagi.murasaki.land.ground.panel.PanelColony;
import yagi.murasaki.land.ground.panel.PanelAbst;


/**
* 色付きのパネル
*/
public class ShapePanel extends PanelAbst {

	/** * staticメソッドなどを使うため */
	public static final ShapePanel KARI = new ShapePanel(0,0,new AttA());
	/** * パネル群 */
	private PanelColony<ShapePanel> pc;
	/** * 上下左右隣 */
	private ShapePanel[] nexts;
	/** * nullでないnextの数 */
	private int nextsSize;
	
	/** * シェープ（Node）から、このクラスインスタンスを取得する */
	private static Map<Shape, ShapePanel> shapePnlMap = new HashMap<>();
	/** * シェープ。これが描画される */
	private Shape shape;
	/** * シェープの色 */
	private Paint clr;

	/**
	* コンストラクタ
	* @param col 横
	* @param row 縦
	* @param att 属性
	*/
	public ShapePanel(int col, int row, Att att) {
		super(col, row, att);
		this.nexts = new ShapePanel[4];
	}

	/**
	* コンストラクタ
	* @param col 横
	* @param row 縦
	* @param att 属性
	* @param shape シェープ
	*/
	public ShapePanel(int col, int row, Att att, Shape shape) {
		super(col, row, att);
		this.nexts = new ShapePanel[4];
		setShape(shape);
	}

	/**
	* groundを元にこのパネルを生成し、コロニーに格納する
	* @param ground 舞台
	* @param colony パネル群
	*/
	public static void create(int[][] ground, PanelColony<ShapePanel> colony) {
		for(int i = 0; i < ground.length; i++) {
			for(int j = 0; j < ground[0].length; j++) {
				Att atta = new AttA();
				atta.put("num", ground[i][j]);
				ShapePanel kari = new ShapePanel(j, i, atta);
				colony.putPnl(new Point2D(j,i), kari);
			}
		}
	}

	/**
	* attの変更とともに、shapeがあればshapeの色も変更
	* @param att 属性
	*/
//	@Override
	public void setAtt(Att att) {
		this.att = att;
		if(this.shape != null) {
			shapeColor();
		}
	}
	
	/**
	* attの変更とともに、shapeがあればshapeの色も変更
	* @param num 属性数値
	*/
	public void setAtt(int num) {
		att.put("num", num);
		if(this.shape != null) {
			shapeColor();
		}
	}

	/**
	* 何かするところ。何かを受け取って、何かして、何かを返す
	* @param obj オブジェクト
	*/
	public Object something(Object obj) { return null; }

	/**
	* このパネルの上下左右隣のパネルを探して保持。最大４マスでwalkSetに準ずる。
	* @param walkSet 歩けるところセット
	* @param pnlMap ここから探す。パネルマップ
	*/
	public static void setNexts(Collection<Point2D> walkSet, Map<Point2D, ShapePanel> pnlMap) {
		for(Point2D pd : pnlMap.keySet()) {
			ShapePanel pnl = pnlMap.get(pd);
			Point2D[] pds = P2Dcustom.nexts(pd);//[4]
			for(int i = 0; i < 4; i++) {
				if(walkSet.contains(pds[i])) {
					pnl.nexts[i] = pnlMap.get(pds[i]);//newにしない
					pnl.nextsSize++;
				}
			}
		}
	}
	
	/**
	* コロニーを変更
	* @param pc パネル群
	*/
	public void setColony(PanelColony<ShapePanel> pc) {
		this.pc = pc;
	}
	
	/**
	* シェープを変更
	* @param shape シェープ
	*/
	public void setShape(Shape shape) {
//			print("setShape");
		this.shape = shape;
		shapePnlMap.put(shape, this);
		shapeColor();
	}

	/**
	* シェープの色を変更
	* @param clr 色
	*/
	private void setColor(Color clr) {
//			print("setColor");
		this.clr = clr;
		shape.setFill(clr);//superメソッド
	}

	/** * 属性数値によってシェープの色をかえる */
	private void shapeColor() {
//			print("shapeColor");
		Color c;
		switch((int)att.get("num")) {
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
				c = Color.PINK;
				break;
			case 6 : 
				c = Color.GRAY;
				break;
			case 7 : 
				c = Color.rgb(250, 50, 50, 0.1);
				break;
			default :
				c = Color.BLACK;
				break;
		}
		setColor(c);
	}
	
//ゲッター
	/**
	* このクラスヒープがまとめている、このクラスのインスタンスをシェープを元に返す
	* @param shape シェープ
	*/
	public static ShapePanel spnl(Shape shape) {
		return shapePnlMap.get(shape);
	}
	
}



