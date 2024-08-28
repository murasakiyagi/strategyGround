package strategyGround.ground;

import java.io.*;
import java.util.*;

import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

import utilCompo.quick.QuickUtil;
import utilCompo.geometry.P2Dint;
import strategyGround.ground.scout.Scouter;
import strategyGround.ground.scout.ScouterView;
import strategyGround.ground.scout.ChainPanel;

/**
* 
* 
*/
public class GroundManager {

	GroundModel gModel;
	GroundView gView;
	Paneler pnl;
	Scouter scout;
	ScouterView sView;

	Point2D herePd;
	Point2D therePd;
	List<Point2D> walkList;
	
	int txfNum = 1;//パネル座標値などの変更幅
	
	public GroundManager() {
		this.gModel = new GroundModel();
		this.gView = new GroundView(this.gModel);
		this.sView = new ScouterView(gView.getSz());
	}

	public void action() {
		gModel.action();
		gModel.panelPrint();
		gView.masuNarabeRect(gModel.getField());
		setScout(gModel.getZeroList(), gModel.getWalkSet());
//		for(Point2D pd : gModel.getZeroList()) {
//			print(pd);
//		}
		chainPanelInit();
		ChainPanel.fullCreate(pnl.getAttNumMap(), gModel.getWalkSet());
	}

	public void reset() {
		gView.masuNarabeRect(gModel.getField());
	}

	public void setPnlAttNum(Paneler pnl, int num) {
		//Paneler.attNum == ChainPanel.costで、
		//	PanelerはChainPanelを知らないので、
		//	ChainPanel内でPaneler.attNumを変える
		pnl.setAttNum(num);
		ChainPanel.pnl(pnl.getPd()).setCost(num);
		//問題ありだがとりあえずこのまま。
		//groundにそれぞれ一意。先に3を作り、２を作る
		if(num == 2) {
			herePd = pnl.getPd();
		} else if(num == 3) {
			therePd = pnl.getPd();
		}
	}

		public void crossStart() {
			for(Point2D pd : scout.getCrosListS()) {
				setPnlAttNum(Paneler.pnl(pd), 4);
			}
		}

		public void crossGoal() {
			for(Point2D pd : scout.getCrosListG()) {
				setPnlAttNum(Paneler.pnl(pd), 4);
			}
		}

	private void setScout(List<Point2D> zeroList, Set<Point2D> walkSet) {
		this.scout = new Scouter(zeroList, new ArrayList<>(walkSet), Paneler.getAttNumArr());
	}
	
	/**
	* コントローラー用
	*/
	public void scouting() {
		print("SCOUTING");
		print("ZERO LIST", gModel.getZeroList().size());
		scout.diffMapping(Paneler.getAttNumArr());
		this.walkList = scout.guide(herePd, therePd);
		for(Point2D pd : this.walkList) {
			print("A", pd);
			Paneler.pnl(pd).setAttNum(4);
		}
		
		sView.paneClear();
		sView.pileRect(scout.getTranList(), 5, 17);
		sView.pileRect(scout.getRiplList(), 6, 14);
		gView.paneAdd(sView.getPane());
		for(Point2D pd : scout.getStopList()) {
			print("B", pd);
		}
		print("SCOUT END");
	}
	
	public void costMapping() {
//		print(herePd);
//		chainPanelInit();

		this.walkList = ChainPanel.fullCourse(herePd, therePd);

//		ChainPanel.sumCostMappingStart(herePd);
//		ChainPanel.sumCostPrint(herePd);
//		this.walkList = ChainPanel.routeStart(herePd, therePd);
		for(Point2D pd : this.walkList) {
			print("A", pd);
			Paneler.pnl(pd).setAttNum(4);
		}

		print("costMapping END");
	}
	
	private void chainPanelInit() {
		ChainPanel.create(pnl.getAttNumMap());
		ChainPanel.setNexts(gModel.getWalkSet());
	}
	
	/**
	* ControlerStageからデータ取得
	*/
	public void setTxfNum(int num) {
		txfNum = num;
	}


	public Pane getPane() {
		return gView.getPane();
	}

	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	
}



