package yagi.murasaki.land.ground;

import java.io.*;
import java.util.*;
import java.awt.Point;

import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;
import yagi.murasaki.utilCompo.geometry.P2Dint;
//import yagi.murasaki.land.ground.scout.Scouter;
import yagi.murasaki.land.ground.scout.ScouterView;
import yagi.murasaki.land.ground.scout.Mover;

import yagi.murasaki.land.ground.scout.Meiro;
import yagi.murasaki.land.ground.scout.MeiroCost;

import yagi.murasaki.land.ground.panel.PanelFace;
import yagi.murasaki.land.ground.panel.PanelColony;
import yagi.murasaki.land.ground.panel.PanelAbst;
import yagi.murasaki.land.ground.panel.concPanel.ShapePanel;
//import yagi.murasaki.land.ground.panel.concPanel.ChainPanel;

import yagi.murasaki.utilCompo.geometry.P2Dcustom;
import yagi.murasaki.utilCompo.geometry.PointCustom;

/**
* 
* 
*/
public class GroundManager {

	/** * ground生成 */
	private GroundModel gModel;
	/** * ground描画 */
	private GroundView gView;
	/** * パネルの属性に合わせて色変え */
	private ShapePanel pnl;//staticメソッドはインスタンスせずpnl.method()で使える?
	/** * 斥候 */
//	private Scouter scout;
	/** * 斥候の調査報告 */
	private ScouterView sView;
	/** * ルート上を動く */
	private Mover mv;

	/** * ここ。始点 */
	private Point2D herePd;
	/** * あそこ。終点 */
	private Point2D therePd;
	/** * ここからあそこまでの道のり */
	private List<Point> routeList;

//PanelFace導入後
	/** * パネルの概要 */
	private PanelFace pf;
	/** * 実験。パネル管理 */
//	private PanelManager concPm;
	/** * 実験。パネル群 */
//	private PanelColony<Panel> panelPc;
	/** * 色付けパネル群 */
	private PanelColony<ShapePanel> shapePc;
	/** * 連携パネル群 */
//	private PanelColony<ChainPanel> chainPc;
	/** * 最短ルート探索 */
	MeiroCost meico;

	/** * FX Point2Dを継承カスタム */
    private P2Dcustom pdc;
	/** * SE Pointを継承カスタム */
    private PointCustom ptc;
    

	/**
	* コンストラクタ。GroundModel参照
	*/
	public GroundManager() {
		this.gModel = new GroundModel();
		this.routeList = new ArrayList<>();
	}

	/**
	* コンストラクタ。GroundModel参照
	* @param min 舞台の縦横の辺の最小値
	* @param tate 伸縮幅。min ~ (min + tate - 1)
	* @param yoko 伸縮幅。min ~ (min + yoko - 1)
	* @param masuhi 歩けるマスと、歩けないマスの比。1 ~ 9
	*/
	public GroundManager(int min, int tate, int yoko, int masuhi) {
		this.gModel = new GroundModel(min, tate, yoko, masuhi);
		this.routeList = new ArrayList<>();
	}

	/** * 初期化。GroundViewとScouterViewおよびsetting() */
	public void init() {
		this.gView = new GroundView(this.gModel);
		this.sView = new ScouterView(gView.SZ);
		setting();
	}

	/**
	* 最初とリニューアル時に呼び出す
	*/
	public void setting() {
		modelingViewing();
//		setScout(gModel.getZeroSet(), gModel.getWalkSet());
	}

	/**
	* モデルとビューを完成させる。(パネラーも)
	*/
	public void modelingViewing() {
		gModel.action();
		gModel.groundPrint();
		reset();
	}

	/**
	* ビューだけ元に戻すが、PanelColonyはそれごと作り直す
	*/
	public void reset() {
		//器を作ってから
		this.shapePc = new PanelColony<ShapePanel>(gModel.getGround(), gModel.getWalkSet());
		//クリエートする
		ShapePanel.create(gModel.getGround(), shapePc);
		//各ぱねるにシェープを注入
		gView.masuNarabeRect(gModel.getGround(), shapePc);
		
		meico = new MeiroCost(gModel.getWalkSet(), gModel.getGround());

//		this.chainPc = createChainPc(gModel.getGround(), gModel.getWalkSet());
//		shapePc.colonyBind(chainPc);
	}

//		/**
//		* チェイン・パネルコロニーを作る
//		* @param ground 舞台
//		* @param walkSet 歩けるところ
//		* @return チェイン型・パネルコロニー
//		*/
//		public PanelColony<ChainPanel> createChainPc(int[][] ground, Set<Point2D> walkSet) {
//			PanelColony<ChainPanel> kariPc = new PanelColony<ChainPanel>(ground, walkSet);
//			ChainPanel.fullCreate(kariPc);
//			return kariPc;
//		}

//ATT
	/**
	* シェープパネルの値を変更
	* @param pnl パネル各種
	* @param num 変更値
	*/
	public void setAttShape(PanelAbst pnl, int num) {
		Att atta = new AttA();
		atta.put("num", num);
			print("setAttShape", atta);
		meico.setCost(pdc.deFxp2d(pnl.getPd()), num);
		//ShapePanel.att == ChainPanel.costで、
		//shapePcとchainPcはバインドしてあるのでどれか一つ変えればいい
		shapePc.setAtt(pnl.getPd(), num);
		//しかし、chainのcostはattNumと関係なしにしたので別途変更が必要
//		chainPc.pnl(pnl.getPd()).setCost(num);
		
		//問題ありだがとりあえずこのまま。herePdとtherePdは
		//groundにそれぞれ一意。先に3を作り、２を作る
		if(num == 2) {
			herePd = pnl.getPd();
			mv.setPos(herePd);
		} else if(num == 3) {
			therePd = pnl.getPd();
			mv.setTgtPd(therePd);
		}
		
//		chainPc.pnl(pnl.getPd()).pnlMapPrint(chainPc.getPnlMap());
	}

	/**
	* 目的地設定
	* @param therePd 目的座標
	*/
	public void setTherePd(Point2D therePd) {
		this.therePd = therePd;
		shapePc.setAtt(therePd, 3);
	}

	/**
	* シェープパネルの値を変更。シンプル版
	* @param pd コロニーの座標
	* @param num 変更値
	*/
	public void setShapeAtt(Point2D pd, int num) {
		shapePc.setAtt(pd, num);
	}

	/**
	* ここからあそこまでの最短ルート
	* @param sttPd ここ
	* @param goalPd あそこ
	* @return ルートリスト
	*/
	public List<Point> scouting(Point2D sttPd, Point2D goalPd) {
		this.routeList.clear();
		//実験
		//	通常
//		Meiro meiro = new Meiro(gModel.getWalkSet(), gModel.getGround());
//		meiro.jikken(sttPd, goalPd);
		//	コストあり
//		meico.jikken(sttPd, goalPd);
		this.routeList = meico.guide(sttPd, goalPd);

		return new ArrayList<Point>(this.routeList);
	}
	
	/** * スカウトの結果を表示 */
	public void scoutingView() {
		print("SCOUTING");
		print("ZERO LIST", gModel.getZeroSet().size() );
		routeList = scouting(herePd, therePd);
		for(Point pt : routeList) {
			print("A", pt);
			shapePc.setAtt(pdc.fxp2d(pt), 4);
		}
		
		sView.paneClear();
		gView.paneAdd(sView.getPane());
		print("SCOUT END");
	}


//チェインパネル
//
//	/**
//	* ChainPanelの一連の処理をする。進行コスト最小ルート
//	* ただし、herePdからのだけ
//	* @param sttPd ここ
//	* @param goalPd あそこ
//	* @return ルートリスト
//	*/
//	public List<Point> costRouting(Point2D sttPd, Point2D goalPd) {
//		this.routeList.clear();
//		ChainPanel cpnl = (ChainPanel)chainPc.pnl(sttPd);
//		this.routeList = pdc.deFxList(cpnl.fullCourse(goalPd));
//		return new ArrayList<Point>(this.routeList);
//	}
//	
//	/** * costMapping()結果を描画 */
//	public void costRoutingView() {
//		shapePc.numMapPrint();
//		chainPc.numMapPrint();
//		for(Point pt : costRouting(herePd, therePd)) {
//			print("A", pt);
//			shapePc.setAtt(pdc.fxp2d(pt), new AttA(4));
//		}
//		print("costMapping END", this.routeList.size());
//			jikken();
//	}
//
//		/**
//		* マップ作ってルート作らず
//		* @param pd 或る座標
//		*/
//		public void sumCostMapping(Point2D pd) {
//			ChainPanel cpnl = (ChainPanel)chainPc.pnl(pd);
////			cpnl.mapping(chainPc);
//			cpnl.mapping();
//		}
//
//	
//	public int range(Point2D here, Point2D there) {
//		ChainPanel cpnl = (ChainPanel)chainPc.pnl(here);
//		return cpnl.range(here, there);
//	}
//	
//	public Set<Point2D> nearAreaCost(Point2D pd, int num) {
//		ChainPanel cpnl = (ChainPanel)chainPc.pnl(pd);
//		return cpnl.nearAreaCost(num);
//	}
//		public Set<Point2D> nearAreaRange(Point2D pd, int num) {
//			ChainPanel cpnl = (ChainPanel)chainPc.pnl(pd);
//			return cpnl.nearAreaRange(num);
//		}

//移動
	public void moverCreate() {
		mv = new Mover(gView.SZ, gView.getPane());
	}
	
	public void moving() {
		if(mv.isRouted()) {
			if(mv.moving()) {
				print("MOVER GOAL");
				mv.setPos(herePd);
			}
		} else {
			mv.setRoute(pdc.fxList(routeList));
				print("MOVER SET ROUTE");
		}
	}


//プリント
//	public void sumCostPrint(Point2D pd) {
//		ChainPanel cpnl = (ChainPanel)chainPc.pnl(pd);
//		cpnl.sumCostPrint();
//	}


//実験
	private void jikken() {
		print("JIKKEN");
//		for(Point2D pd : nearAreaRange(herePd, 2)) {
//				print("jikken", pd);
//			shapePc.setAtt(pd, 3);
//		}
//		if(therePd != null) {
//			//rangeMapはできてるが、sumCostMapはできてない可能性
//			for(Point2D pd : nearAreaCost(therePd, 1)) {
//				print("jikken", pd);
//				shapePc.setAtt(pd, 3);
//			}
//		}
	}


//ゲッター
//	public PanelColony<ChainPanel> getChainPc() { return chainPc; }
	public PanelColony<ShapePanel> getShapePc() { return shapePc; }
	public int[][] getGround() { return gModel.getGround(); }
	public List<Point2D> getRouteList() { return pdc.fxList(routeList); }
	public Set<Point2D> getWalkSet() { return gModel.getWalkSet(); }
	public Pane getPane() { return gView.getPane(); }
	public Point2D getPos(Rectangle rect) { return ShapePanel.spnl(rect).getPd(); }
	public int getSz() { return gView.SZ; }
	public Point2D getHere() { return herePd; }

	/** * 便利機能 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}
	
}



