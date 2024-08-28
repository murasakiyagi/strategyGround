package strategyGround.ground;

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
public class GroundView {

	Pane pane;

	GroundModel model;
	final private int sz = 20;
	int[] rowZero;
	int[] colZero;//行のゼロマス、列のゼロマス


	public GroundView(GroundModel model) {
		this.pane = new Pane();
		this.model = model;
	}


	/**
	* fieldだけ描画
	*/
	public void masuNarabeRect(int[][] field) {//action
		pane.getChildren().clear();
		zeroCnter(field);
		for(int i=0; i < field.length; i++) {
			for(int j=0; j < field[0].length; j++) {
				//rectの引数は(x, y, w, h)なので(j, i,,,)となる
				Rectangle rect = new Rectangle(j * sz + sz, i * sz + sz, sz, sz);
				rect.setStrokeType(StrokeType.INSIDE);
				rect.setStrokeWidth(1);
				rect.setStroke(Color.SILVER);
				if(field[i][j] > 0) {
					rect.setFill(Color.WHITE);
				} else {
					rect.setFill(Color.BLUE);
				}
				//newPanelの引数は(c, r,,,)なので(j, i,,,)となる
				Paneler.newPanel(j, i, field[i][j], rect);
				pane.getChildren().add(rect);
			}
		}
		
		numberRowCol(field);
	}

		/**
		* 番号つけ
		*/
		public void numberRowCol(int[][] field) {
//			int right = (field.length + 1) * sz;
//			int bottom = (field[0].length + 1) * sz;
			
			for(int i=0; i < field.length; i++) {
				Text txI = new Text(0, i * sz + sz * 2, String.valueOf(i));
//				Text txIz = new Text(i * sz + sz * 2, right, String.valueOf(rowZero[i]));
				if(i >= sz) {
					txI.setFont(new Font(sz / 2));
				} else {
					txI.setFont(new Font(sz));
				}
				pane.getChildren().add(txI);
//				pane.getChildren().add(txIz);
				
				for(int j=0; j < field[0].length; j++) {
					Text txJ = new Text(j * sz + sz, sz, String.valueOf(j));
//					Text txJz = new Text(bottom + sz, j * sz + sz, String.valueOf(colZero[j]));
					if(j >= sz) {
						txI.setFont(new Font(sz / 2));
					} else {
						txI.setFont(new Font(sz));
					}
					pane.getChildren().add(txJ);
//					pane.getChildren().add(txJz);
				}
			}
		}
		

	/**
	* ゼロマスの数を表示する
	*/
	private void zeroCnter(int[][] field) {
		int zeroi = 0;
		int zeroj = 0;
		rowZero = new int[field.length];
		colZero = new int[field[0].length];
		
		for(int i=0; i < field.length; i++) {
			for(int j=0; j < field[i].length; j++) {
				if(field[i][j] == 0) {
					zeroj++;
				}
			}
			rowZero[i] = zeroj;
			zeroj = 0;
		}

		for(int i=0; i < field[0].length; i++) {
			for(int j=0; j < field.length; j++) {
				if(field[j][i] == 0) {
					zeroi++;
				}
			}
			colZero[i] = zeroi;
			zeroi = 0;
		}
	}


// ==================================
// ゲッター
	protected void paneAdd(Node nd) {
		pane.getChildren().add(nd);
	}
	public Pane getPane() { return pane; }
	public int getSz() { return sz; }

//=============================================


	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	
}



