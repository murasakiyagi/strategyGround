package strategyGround.ground.scout;

import java.io.*;
import java.util.*;

import javafx.geometry.Point2D;

//オリジナル
import utilCompo.quick.QuickUtil;
import utilCompo.geometry.P2Dcustom;


/**
* 自動追尾、最短距離アルゴリズムほぼ完成品
* 
* 処理行程
	ある点から四方を見ることができる。
	スタート地点からゴール地点が見えれば終わり(壁の向こうは見えない)。
	スタートからの視線とゴールからの視線の交点を通過点として結んで終わり。
	スタートとゴールの交点できない場合、スタートから幽体がランダムに移動する。
		幽体が歩いた点は一時的に封鎖される。幽体も四方を見る。
		幽体とゴールからの交点を通過点として結ぶ。無ければ移動する。
		どこにも移動できなければ、その点はほぼ永続的に封鎖。幽体はスタートに戻る。
	スタートから通過点が見えれば終わり。

* 気をつけないと後々めんどい
* （上から下に数値が大きくなる　縦, y, i, row）
* （左から右に数値が大きくなる　横, x, j, col）
	一般的に２重for文の並びはiが先でjが後
	int[] ints = new int[n][m];
	for(int i = 0; i < n; i++) {
		for(int j = 0; j < m; j++) {
			y = i;
			x = j;
			col = x;
			row = y;
			//iが左でyが右にあって紛らわしい
			ints[i][j] = pd.getXY(x, y);
		}
	}
* 
* 
* 
* 
* 
* 
*/
public class Scouter {

	private Point2D sttPd;//移動する者の初期位置。固定だけどfinalにするのは後で
	private Point2D goalPd;//目的地。固定だけどfinalにするのは後で
	private Point2D gstPd;//幽体
	private Point2D ripPd;//波紋
	private Point2D tranPd;//通過点
	
	/**
	* 座標と歩行難度
	*/
	Map<Point2D, Integer> diffMap = new HashMap<>();

	//
	/**
	* 歩道。このリストの中身を詰めるのが、このクラスの目的。
	* guide()によって生成し返す
	* ユニットの現在地slfRow,slfColは含まない目的地までの最短ルートリスト。
	*/
 	List<Point2D> routeList = new ArrayList<>();//
 	List<Point2D> routeListNow = new ArrayList<>();//
 	List<Point2D> routeListFin = new ArrayList<>();//最終決定
	List<List<Point2D>> routeListList = new ArrayList<>();

	//行ける
 	List<Point2D> walkList = new ArrayList<>();//zeroListじゃない方でやる場合
 	List<Point2D> walkListOrigin = new ArrayList<>();//

	//行けない点
 	List<Point2D> ftprList = new ArrayList<>();//踏んだところ。一時的に壁になる
	List<Point2D> stopList = new ArrayList<>();//立ち止まって（次に進めなくて）stopcnt >= 4になった所。

	//行った十字線
	List<Point2D> crosListG = new ArrayList<>();//ゴール地点から十字線
	List<Point2D> crosListS = new ArrayList<>();//スタート地点から
	List<Point2D> crosListT = new ArrayList<>();//一時。transitから
	List<Point2D> crosListR = new ArrayList<>();//一時。探索中(gstPd)のマスから
	
	/**
	* sttPd, goalPd, tranListは道標
	*/
 	List<Point2D> tranList = new ArrayList<>();//通過点（曲がり角）
 	List<Point2D> riplList = new ArrayList<>();//一時。発見点
 	
 	/**
	* sttPd, goalPd, tranListなど網羅
 	*/
 	List<Point2D> markList = new ArrayList<>();//目印。使ってない
 	List<Point2D> logList = new ArrayList<>();//汎用。今はgstPdの足跡
	
	P2Dcustom pdc = new P2Dcustom();
	private List<Point2D> zeroList;
	private int[][] ground;

	//コンストラクタ=========================================
	public Scouter(List<Point2D> zeroList, List<Point2D> walkListOrigin, int[][] ground) {
		this.zeroList = zeroList;
		this.walkListOrigin = walkListOrigin;
		this.walkList = new ArrayList<>(walkListOrigin);
		this.ground = ground;
		diffMapping(ground);
	}
	//=======================================================


	public List<Point2D> guide(Point2D herePd, Point2D therePd) {
 		print("GUIDE START", herePd, therePd);
		print("ZERO SIZE", zeroList.size());
		routeListFin.clear();

		for(int i = 0; i < 3; i++) {
			//自分の場所から敵などの場所
			this.sttPd = herePd;
			this.goalPd = therePd;
			init();//初期化
	
			zeroCrossAdd(crosListS, gstPd);//スタート地点の十字線
			zeroCrossAdd(crosListG, goalPd);//ゴールから十字
				addAbsent(logList, sttPd);
	
			if(straightGoal()) {
	//			print("straightGoal  END");
				return routeList;
			}
	
			if(startToGoal()) {
	//			print("startToGoal TRUE, to be straightTran");
			}
	
			int brk = 0;
			while(!straightTran()) {
				while(!startToTran()) {
					gstMove();//rippleToGoalのまま
					if(brk++ > 100000) { break; }
				}
					if(brk++ > 100000) { break; }
			}
			print("straightTran  END");

			if(routeListFin.size() == 0 || routeListFin.size() > routeList.size()) {
				routeListFin.clear();
				routeListFin.addAll(routeList);
			}

		}//for.end

		return reverseList(routeListFin);

	}

//ルート処理------------
//ドックにguide内での順番を示す
	/**
	* １番。一回。スタートとゴールが一直線。トランジットは無い
	*/
	private boolean straightGoal() {
//		print("startToGoal");
		if(crosListS.contains(goalPd)) {
				print("straightGoal");
			routing(goalPd, sttPd);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	* ２番。一回。スタートクロスとゴールクロスが交わる
	*/
	private boolean startToGoal() {
		Point2D pd = crossToCross(crosListS, crosListG);

		//ゴールをトランジットととらえる
		setTransit(goalPd);

		if(pd != null) {
			print("I startToGoal", pd, goalPd);
			setTransit(pd);
			return true;
		} else {
			return false;
		}
	}

	/**
	* ３番。巡回。gstPdを歩かせる
	* 進行難度を考慮した方法。
	*/
	private void gstMoveDiff() {
		furidasi();
		int brk = 0;
		while(!rippleToTran()) {
			Point2D next = oneOfNexts(gstPd);
			print("NEXT", next);
			if(next.equals(gstPd)) {
				stopList.add(gstPd);
				furidasi();
			} else {
				gstPd = next.add(0,0);
				zeroCrossAdd(crosListR, gstPd);//
				addAbsent(ftprList, gstPd);//移動後にこの処理をするなら、sttPdを先に入れなければいけない
					addAbsent(logList, gstPd);
			}
			
			if(brk++ > 100000) {
				print("brk", brk);
				break;
			}
		}
//		Point2D pd = crossToCross(crosListR, crosListT);
//		if(pd != null) {
//			print("GST MOVE  TRUE ======");
//			print("PD", gstPd);
//			print("============");
//		} else {
//			print("GST MOVE  FALSE");
//		}
	}

	/**
	* 進行難度の無い方法
	*/
	private void gstMove() {
		furidasi();
		int brk = 0;
		while(!rippleToTran()) {
			Point2D neswPd = pdc.neswFour();
			if(neswPd.equals(Point2D.ZERO)) {
				//ここにきたということは、すでに４回失敗している
					print("C", neswPd);
					addAbsent(stopList, gstPd);
				furidasi();
			} else {
				Point2D kariPd = gstPd.add(neswPd);
				//行けるか？
				if(!tripleBarrier(kariPd)) {
					gstPd = kariPd.add(0,0);
					zeroCrossAdd(crosListR, gstPd);//
					addAbsent(ftprList, gstPd);//移動後にこの処理をするなら、sttPdを先に入れなければいけない
					pdc.resetFourList();
						addAbsent(logList, gstPd);
//						print("B barrier", gstPd, kariPd, ftprList.size());
				}
			}
			
			if(brk++ > 100000) {
				print("brk", brk);
				break;
			}
		}
	}

	private boolean straightTran() {
		if(crosListT.contains(sttPd)) {
//				print("straightTran ROUTING ------");
//				printArr(sttPd, crosListT);
//				print("------------");
			routing(tranPd, sttPd);
			return true;
		} else {
			return false;
		}
	}

	/**
	* スタートクロスとトランジットクロスが交わる
	*/
	private boolean startToTran() {
		Point2D pd = crossToCross(crosListS, crosListT);
		if(pd != null) {
//				print("startToTran()", pd);
			setTransit(pd);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	* リップルクロスとトランジットクロスが交わる
	*/
	private boolean rippleToTran() {
		Point2D pd = crossToCross(crosListR, crosListT);
		if(pd != null) {
//				print("rippleToTran()", pd);
			setTransit(pd);
			addAbsent(riplList, gstPd);
			return true;
		} else {
			return false;
		}
	}

	/**
	* 或る線(list)と線が交われば、その交点を返す。でなければnullを返す
	*/
	private Point2D crossToCross(List<Point2D> crosListA, List<Point2D> crosListB) {
		Point2D kariPd = null;
		for(Point2D pd : crosListA) {
			if( crosListB.contains(pd) ) {
				kariPd = pd;
				break;
			}//if,end
		}//for,end
		return kariPd;
	}

	private void setTransit(Point2D pd) {
		crosListT.clear();
		zeroCrossAdd(crosListT, pd);
		addAbsent(tranList, pd);
		if(tranPd != null) {
			routing(tranPd, pd);
			crosListT.removeAll(routeListNow);
		} else {
			//pd == goalPd
//			print("FIRST SET TRANSIT", pd);
		}
		tranPd = pd.add(0,0);
//			print("G setTransit", tranPd);
	}

	/**
	* routeListにadd
	* 以前はtranListが完成した後にまとめて繋げたが、今回は発見の都度
	*/
	private void routing(Point2D pd1, Point2D pd2) {
		double col = pd1.getX() - pd2.getX();
		double row = pd1.getY() - pd2.getY();
		int max = (int)Math.max(Math.abs(col), Math.abs(row));
		routeListNow.clear();
//			print("E routing", pd1, pd2, col, row);
		if(col == 0.0 || row == 0.0) {
			print("F");
			double colSig = Math.signum(col);
			double rowSig = Math.signum(row);
			
			for(int i = 0; i < max; i++) {
				addAbsent(routeList, pd1.add(colSig * -i, rowSig * -i));
				addAbsent(routeListNow, pd1.add(colSig * -i, rowSig * -i));
			}
		}
//		printArr("H routeListNow", routeListNow);
	}

	/**
	* gstPdの次の行き先を進行難度も考えて決める。ただしランダムではないのでstopListが要る
	*/
	private Point2D oneOfNexts(Point2D pd) {
		Point2D[] nexts = pdc.nexts(pd);
		int min = Integer.MAX_VALUE;
		int index = nexts.length;
		for(int i = 0; i < nexts.length; i++) {
			if(!tripleBarrier(nexts[i])) {
				int diff = walkDiffcalty(nexts[i]);
				if(diff > 0 && diff <= min) {
					index = i;
					min = diff;
					print("oneOfNexts", pd, nexts[i], min);
//					print("          ", ftprList.contains(nexts[i]), ftprList.get(0));
				}
			}
		}
		if(index == nexts.length) {
			return pd;//pd.add()せずそのままなので、注意
		} else {
			return nexts[index];
		}
	}

	/**
	* 進行難度
	*/
	private int walkDiffcalty(Point2D pd) {
		return diffMap.get(pd);
	}

	/**
	* 代入と描画
	*/
	public void diffMapping(int[][] ground) {
		this.ground = ground;
		print("--- diffMapping ------");
		for(int i = 0; i < ground.length; i++) {
			for(int j = 0; j < ground[0].length; j++) {
				//i,y,rowに注意
				diffMap.put(new Point2D(j, i), ground[i][j]);
				
				String spc = ground[i][j] >= 10 ? " " : "  ";
				System.out.print(spc + ground[i][j]);
			}
				System.out.println("");
		}
		print("--- diffMapping end ---");
	}

//ルート処理,end------------

//リスト加入処理------------

	/**
	* 唯一のしか入れない
	*/
	private void addAbsent(List<Point2D> arr, Point2D pd) {
		if( !arr.contains(pd) ) { arr.add(pd); }
	}
	
		/**
		* Listの中から唯一のしか入れない
		*/
		private void addAllAbsent(List<Point2D> arr1, List<Point2D> arr2) {
			for(Point2D pd : arr2) { addAbsent(arr1, pd); }
		}
	
	/**
	* 壁にぶつかるまで進む十字線。超重要メソッド
	* @param arr 探した点を加える
	*/
	private void zeroCrossAdd(List<Point2D> arr, Point2D pd) {
		//初期位置（引数で受け取った位置）も加入
		arr.clear();
		for(int i = -1; i <= 1; i = i+2) {//下左、上右の二回分。i=-1 & i=1の２通り
			loopAdd(arr, pd, i, 0);
			loopAdd(arr, pd, 0, i);
		}
	}
	
		private void loopAdd(List<Point2D> arr, Point2D pd, int x, int y) {//go.add(i, 0);
//			Point2D kari = pd.add(0, 0);
			Point2D kari = pd.add(x, y);
			while( !zeroList.contains(kari) ) {//壁にぶつかるまでループ
				addAbsent(arr, kari.add(0, 0));//代入してから、
				kari = kari.add(x, y);//移動
			}
//			print(kari);
		}
//リスト加入処理,end------------

//座標移動処理------------
	/**
	* 離れた精神を体に戻す
	*/
	private void bodySoul() {
		gstPd = sttPd.add(0, 0);
		print("GST PD", gstPd);
	}
	
	/**
	* 歩数を保持しつつ、振出しに戻る
	*/
	public void furidasi() {
//		ftcntSlf = 0;
			ftprList.clear();
			riplList.clear();
			crosListR.clear();
		addAbsent(ftprList, sttPd);
		bodySoul();
	}


		private boolean tripleBarrier(Point2D pd) {//接触があればtrue
//				print("tripleBarrier", pd);
			if( zeroList.contains(pd) ) { return true; } else 
			if( ftprList.contains(pd) ) { return true; } else 
			if( stopList.contains(pd) ) { return true; } else 
			{ return false; }
		}
//座標移動処理,end------------

//その他１------------
	private void init() {
		//crosListはzeroCrossAddでクリアされる
		bodySoul();
		ftprList.clear();
		riplList.clear();
		tranList.clear();
		routeList.clear();
//		routeListFin.clear();
		stopList.clear();
		logList.clear();
		tranPd = null;
		addAbsent(ftprList, sttPd);
	}

	private List<Point2D> reverseList(List<Point2D> list) {
			print("------ reverseList ------");
			print(list.size());
		List<Point2D> kari = new ArrayList<>();
		for(int i = list.size() - 1; i >= 0; i--) {
			kari.add(list.get(i));
				print(list.get(i));
		}
			print("--- reverseList end ---");
		return kari;
	}

//その他１,end------------

//ゲッター
	public List<Point2D> getZeroList() { return zeroList; }
	public List<Point2D> getCrosListG() { return crosListG; }
	public List<Point2D> getCrosListS() { return crosListS; }
	public List<Point2D> getCrosListT() { return crosListT; }
	public List<Point2D> getCrosListR() { return crosListR; }
	public List<Point2D> getRiplList() { return riplList; }
	public List<Point2D> getTranList() { return tranList; }
	public List<Point2D> getFtprList() { return ftprList; }
	public List<Point2D> getLogList() { return logList; }
	public List<Point2D> getStopList() { return stopList; }

	public Point2D getSttPd() { return sttPd; }
	public Point2D getGoalPd() { return goalPd; }
//ゲッター,end


	private int x(Point2D pd) {
		return (int)pd.getX();
	}
	private int y(Point2D pd) {
		return (int)pd.getY();
	}
	
//--------------------------------------

	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}

	private void printArr(Object str, List<?> arr) {//汎用メソッド
		for(Object obj : arr) {
			print(str, obj);
		}
	}
	
}