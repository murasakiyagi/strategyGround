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
* パネルを重ねてまとめる
*/
public class PilePanel extends PanelAbst {

	final public static PilePanel KARI = new PilePanel(0,0,new AttA());
	private PanelColony<PilePanel> pc;
	private PilePanel[] nexts;
	private int nextsSize;//nullでないnextの数
	

//コンストラクタ
	public PilePanel(int col, int row, Att att) {
		super(col, row, att);
		this.nexts = new PilePanel[4];
	}

	public static void create(int[][] ground, PanelColony<PilePanel> mana) {
		for(int i = 0; i < ground.length; i++) {
			for(int j = 0; j < ground[0].length; j++) {
				mana.putPnl(new Point2D(j,i), new PilePanel(j, i, new AttA(ground[i][j])));
			}
		}
	}

	public Object something(Object obj) { return null; }

	/**
	* panelの隣接するマス。最大４マスでwalkSetに準ずる。
	*/
	public static void setNexts(Collection<Point2D> walkSet, Map<Point2D, PilePanel> pnlMap) {
		for(Point2D pd : pnlMap.keySet()) {
			PilePanel pnl = pnlMap.get(pd);
			Point2D[] pds = P2Dcustom.nexts(pd);//[4]
			for(int i = 0; i < 4; i++) {
				if(walkSet.contains(pds[i])) {
					pnl.nexts[i] = pnlMap.get(pds[i]);//newにしない
					pnl.nextsSize++;
				}
			}
		}
	}
	
	public void setManager(PanelColony<PilePanel> pc) {
		this.pc = pc;
	}
	

	
}



