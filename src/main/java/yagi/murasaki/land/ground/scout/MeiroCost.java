package yagi.murasaki.land.ground.scout;

import java.util.*;
import java.awt.Point;
import javafx.geometry.Point2D;

//オリジナル
import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;
import yagi.murasaki.utilCompo.geometry.PointCustom;


/**
* 歩行可能なマップから、スタート地点から重み（cost）の低いルートを優先して通る順路マップを新規に作る。
* そのマップから始点〜終点間の最短距離をリスト化する。
* デザイン的に別のクラスにする可能性あり。
*/
public class MeiroCost {

	/** * コストの累積マップ。拡がる方で捜索したマップ */
	private Map<Point, Integer> spreadMap;

//歩ける
	/** * 歩ける座標群 */
    private Set<Point> walkSet;
	/** * 重み付けされたマップ */
	private Map<Point, Integer> costMap; 

//目標
	/** * 始点から終点までの道のり */
	private List<Point> routeList;

	/** * 二次元配列で表した時の縦横大きさ */
	private int h, w;
	/** * FX Point2Dを継承カスタム */
    private P2Dcustom pdc;
	/** * SE Pointを継承カスタム */
    private PointCustom ptc;
    
	/** * ground 表示するためだけ */
	private int[][] costGround;
	/** * デバッグ用ループカウント */
    private int loopCnt = 0;
    
	/** * 空のコンストラクタ */
    public MeiroCost() {}
    
	/**
	* コンストラクタ
	* @param walkSetPd 歩ける座標セット
	* @param ground 舞台
	*/
	public MeiroCost(Set<Point2D> walkSet, int[][] ground) {
		this.walkSet = castSet(walkSet);
		this.costMap = arrToMap(ground);
		this.costGround = ground;
		h = ground.length;
		w = ground[0].length;
		init();
	}
	
	/**
	* コンストラクタ
	* @param walkSetPd 歩ける座標セット
	* @param costMap すでに重み付けされたマップ
	* @param ground 舞台
	*/
	public MeiroCost(Set<Point2D> walkSet, Map<Point2D, Integer> costMap, int[][] ground) {
		this.walkSet = castSet(walkSet);
		this.costMap = castMap(costMap);
		this.costGround = ground;
		h = ground.length;
		w = ground[0].length;
		init();
	}
	
	/** * 初期化 */
    private void init() {
		this.pdc = new P2Dcustom();
		this.ptc = new PointCustom();
    	this.spreadMap = new HashMap<>();
        this.routeList = new ArrayList<>();
        
        for(Point pt : walkSet) {
        	//もっと多くても良い。size * コストの最大値以上を
        	int size = walkSet.size();
        	spreadMap.put(pt, size * 10);
        }
    }
    
    /**
    * コストの変更を受け取る
	* @param pt 座標
	* @param int コスト
    */
	public void setCost(Point pt, int cost) {
		costMap.put(pt, cost);
	}


	/**
	* このクラスの目的メソッド。スタート地点からの全マスに距離を取り、ゴール地点の値を拾う
	* @param sttPt 始点
	* @param goalPt 終点
	* @return 始点から終点までの（たぶん）最短、座標リスト
	*/
	public List<Point> guide(Point sttPt, Point goalPt) {
		spreadMapping(sttPt);
		routing(sttPt, goalPt, spreadMap, routeList);
		return routeList;
	}

		/**
		* このクラスの目的メソッド。スタート地点からの全マスに距離を取り、ゴール地点の値を拾う
		* @param sttPd 始点
		* @param goalPd 終点
		* @return 始点から終点までの（たぶん）最短、座標リスト
		*/
		public List<Point> guide(Point2D sttPd, Point2D goalPd) {
			Point sttPt = pdc.deFxp2d(sttPd);
			Point goalPt = pdc.deFxp2d(goalPd);
			return guide(sttPt, goalPt);
		}


	/**
	* 拡がる方でマッピング
	* @param sttPt スタート地点
	*/
	public boolean spreadMapping(Point2D sttPd) {
		return spreadMapping(pdc.deFxp2d(sttPd));
	}

		/**
		* 拡がる方でマッピング
		* @param sttPt スタート地点
		*/
		public boolean spreadMapping(Point sttPt) {
			if(inArea(sttPt)) {
				loopCnt = 0;
				spreadMap.clear();
				spreadLoop(sttPt, 1);
				
				mapPrint(spreadMap);
				print("LOOP CNT", loopCnt);
				return true;
			} else {
				return false;
			}
		}

	/**
	* mapを構築する。広がるタイプ。
	* @param sttPt 始点
	* @param cnt 始点からの距離
	*/
	public void spreadLoop(Point sttPt, int cnt) {
		loopCnt++;
		cnt += costMap.get(sttPt);
		spreadMap.put(sttPt, cnt);

		Set<Point> nexts = new HashSet<>();
		
		for(int i = -1; i <= 1; i += 2) {
			Point addPt = ptc.add(sttPt, i, 0);
			//次の座標の累積コストが、（今の累積コスト＋次マスの単純コスト）より大きい場合、上書きする。
			if(inArea(addPt) && (spreadMap.get(addPt) == null || spreadMap.get(addPt) > cnt + costMap.get(addPt))) {
				nexts.add(addPt);
			}

			addPt = ptc.add(sttPt, 0, i);
			if(inArea(addPt) && (spreadMap.get(addPt) == null || spreadMap.get(addPt) > cnt + costMap.get(addPt))) {
				nexts.add(addPt);
			}
		}
		
		for(Point pt : nexts) {
			//再帰
			spreadLoop(pt, cnt);
		}

	}


	/**
	* 作られたマップのある点から始点までを逆順でリスト化する。始点を含むことに注意
	* @param goalPt 終点。処理はここから始まり、リスト化された時lastIndexになる。
	* @param map 探索済みの累積コストマップ
	* @param list 空の座標リスト
	* @return 始点から終点までの（たぶん）最短、座標リスト
	*/
	public void routing(Point sttPt, Point goalPt, Map<Point, Integer> map, List<Point> list) {
		loopCnt++;
		list.add(0, goalPt);
		int cnt = map.get(goalPt);
		if(cnt == costMap.get(sttPt)) {
			return;
		}
		
		Point nextPt = null;
		//最小値比較
		int nextNum = Integer.MAX_VALUE;
		
		for(int i = -1; i <= 1; i += 2) {
			Point addPt = ptc.add(goalPt, i, 0);
			//次の座標値が、今より小さいなら、実際進む候補になる
			if(inArea(addPt) && map.get(addPt) < cnt) {
				int addNum = map.get(addPt);
				//候補の更新
				if(addNum < nextNum) {
					nextNum = addNum;
					nextPt = addPt;
				}
			}

			addPt = ptc.add(goalPt, 0, i);
			if(inArea(addPt) && map.get(addPt) < cnt) {
				int addNum = map.get(addPt);
				if(addNum < nextNum) {
					nextNum = addNum;
					nextPt = addPt;
				}
			}
		}
		
		if(nextPt == null) {
			return;
		} else {
			//再帰
			routing(sttPt, nextPt, map, list);
		}
		
	}

	/**
	* 範囲指定
	* @param pt 立ち入ろうとする座標
	* @return 立ち入れるならtrue
	*/
	private boolean inArea(Point pt) {
//		if(traceSet.contains(pt)) { return false; }
		if(!walkSet.contains(pt)) { return false; }
		return true;
	}

	/**
	* FX Point2DのSetをSE PointのSetに変換
	* @param pdSet FX Point2DのSet
	* @return SE PointのSet
	*/
    private Set<Point> castSet(Set<Point2D> pdSet) {
        Set<Point> ptSet = new HashSet<>();
        for(var val : pdSet) {
            ptSet.add( new Point(pdc.intX(val), pdc.intY(val)) );
        }
        return ptSet;
    }
    
	/**
	* FX Point2DのMapをSE PointのMapに変換
	* @param pdMap FX Point2DのMap
	* @return SE PointのMap
	*/
    private Map<Point, Integer> castMap(Map<Point2D, Integer> pdMap) {
    	Map<Point, Integer> ptMap = new HashMap<>();
    	for(Point2D pd : pdMap.keySet()) {
    		ptMap.put(pdc.deFxp2d(pd), pdMap.get(pd));
    	}
    	return ptMap;
    }
    
    /**
    * デバッグ用。捜索ずみのマップを２次元配列に変換
	* @param map 捜索ずみのマップ
	* @return 捜索ずみのマップの値を受け取った２次元配列
    */
    public int[][] mapToArr(Map<Point, Integer> map) {
		int[][] intss = new int[h][w];
		
		for(Point pt : map.keySet()) {
			int r = (int)pt.getY();
			int c = (int)pt.getX();
			intss[r][c] = map.get(pt);
		}
		return intss;
    }
    
    /**
    * 配列をマップに変換
	* @param map 捜索ずみのマップ
	* @return 捜索ずみのマップの値を受け取った２次元配列
    */
    public Map<Point, Integer> arrToMap(int[][] intss) {
		Map<Point, Integer> kariMap = new HashMap<>();
		for(int i = 0; i < intss.length; i++) {
			for(int j = 0; j < intss[0].length; j++) {
				//i,jの並び注意
				kariMap.put(new Point(j, i), intss[i][j]);
			}
		}
		return kariMap;
    }
    
	/**
	* Mapを二次元配列として表示
	* @param map keyがSE Pointのマップ
	*/
	public void mapPrint(Map<Point, Integer> map) {
		print("mapPrint START");

		int[][] intss = mapToArr(map);

		for(int i = 0; i < h; i++) {
			for(int j = 0; j < w; j++) {
				if(intss[i][j] >= 10) {
					System.out.print(intss[i][j] + "  ");
				} else {
					System.out.print(intss[i][j] + "   ");
				}
				if(j == w - 1) {
					System.out.println();
				}
			}
		}
		print("mapPrint END");
	}

	/**
	* 二次元配列を並べて表示
	* @param intss int[][]
	*/
	public void arrPrint(int[][] intss) {
		for(int i = 0; i < h; i++) {
			for(int j = 0; j < w; j++) {
				if(intss[i][j] >= 10) {
					System.out.print(intss[i][j] + "  ");
				} else {
					System.out.print(intss[i][j] + "   ");
				}
				if(j == w - 1) {
					System.out.println();
				}
			}
		}
	}


	/**
	* 動作実験。expandLoopとspreadの比較
	* @param goalPt 終点。処理はここから始まり、リスト化された時lastIndexになる。
	* @param map 探索済みのマップ
	* @param list 空の座標リスト
	* @return 始点から終点までの（たぶん）最短、座標リスト
	*/
	public void jikken(Point2D sttPd, Point2D goalPd) {
		Point sttPt = pdc.deFxp2d(sttPd);
		Point goalPt = pdc.deFxp2d(goalPd);
		
		spreadMapping(sttPd);
		int[][] aaa = mapToArr(spreadMap);
		boolean bool = true;
		
		print("JIKKEN START");
		
		routing(sttPt, goalPt, spreadMap, routeList);
		print("ROUTE", routeList);
		print("LOOP CNT2", loopCnt);
		
		print("JIKKEN END");
	}


	/** * マスゲームの舞台 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}

}