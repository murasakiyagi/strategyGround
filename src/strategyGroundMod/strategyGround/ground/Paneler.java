package strategyGround.ground;

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
import javafx.geometry.Point2D;

import utilCompo.quick.QuickUtil;

/**
* １マスの情報
* 
*/
public class Paneler {

	private static Map<Point2D, Paneler> pdPnlMap = new HashMap<>();
	private static Map<Rectangle, Paneler> rectPnlMap = new HashMap<>();
	private static int[][] attNumArr;
	private static Map<Point2D, Integer> attNumMap = new HashMap<>();
	private static int width, height;
	
	private int col, row;//col,x,j  row,y,i
	private int attNum;//属性番号
	private Rectangle rect;
	private Paint clr;
	
	public Paneler(int col, int row, int attNum, Rectangle rect) {
		setPanel(col, row, attNum, rect);
	}

	public void setPanel(int col, int row, int attNum, Rectangle rect) {
		this.col = col;
		this.row = row;
		this.rect = rect;
//		this.attNum = attNum;
		setAttNum(attNum);
//		this.clr = rect.getFill();
		pdPnlMap.put(new Point2D(col, row), this);
		rectPnlMap.put(rect, this);
		height = Math.max(height - 1, row) + 1;
		width = Math.max(width - 1, col) + 1;
	}

	public static void newPanel(int col, int row, int attNum, Rectangle rect) {
		new Paneler(col, row, attNum, rect);
	}

	
	public void setAttNum(int num) {
		attNum = num;
		attNumMap.put(getPd(), num);
		rectColor();
	}

	public void setColor(Color clr) {
		this.clr = clr;
		rect.setFill(clr);
	}

	private void rectColor() {
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
	
	public void info() {
		print("----- PANELER INFO -----");
		print("col : ", col, "   ROW : ", row);
		print("ATTNUM : ", attNum);
		print("RECT :", rect.toString());
		print("COLOR : ", clr.toString());
		print("--- PANELER INFO  END ---");
	}

//ゲッター
	public static Paneler pnl(Point2D pd) {
		return pdPnlMap.get(pd);
	}
	public static Paneler pnl(int col, int row) {
		return pdPnlMap.get(new Point2D(col, row));
	}
	public static Paneler pnl(Rectangle rect) {
		return rectPnlMap.get(rect);
	}
	public static Map<Point2D, Integer> getAttNumMap() {
		return attNumMap;
	}
	public static Collection<Paneler> getPnlSet() {
		return pdPnlMap.values();
	}
	
	public static int[][] getAttNumArr() {
		attNumArr = new int[height][width];
		for(Point2D pd : attNumMap.keySet()) {
			attNumArr[(int)pd.getY()][(int)pd.getX()] = attNumMap.get(pd);
		}
		return attNumArr;
	}

	public int getAttNum() { return attNum; }
	public int getRow() { return row; }
	public int getCol() { return col; }
	public Point2D getPd() { return new Point2D(col, row); }

	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	
}



