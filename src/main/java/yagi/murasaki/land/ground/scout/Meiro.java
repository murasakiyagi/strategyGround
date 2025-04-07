package yagi.murasaki.land.ground.scout;

import java.util.*;
import java.awt.Point;
import javafx.geometry.Point2D;

//オリジナル
import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;
import yagi.murasaki.utilCompo.geometry.PointCustom;

/**
* 最短距離
* 移動コストなし
*/
public class Meiro {

	/** * 伸びる方で捜索したマップ */
	private Map<Point, Integer> expandMap;
	/** * 拡がる方で捜索したマップ */
	private Map<Point, Integer> spreadMap;

//歩ける
	/** * 歩ける座標群 */
    private Set<Point> walkSet;

//歩けない
	/** * 歩いた跡。一時的に歩けない。使ってない */
    private Set<Point> traceSet;

//目標
//    private Set<Point> sttSet;
//    private Set<Point> goalSet;

	/** * 始点から終点までの道のり */
	private List<Point> routeList;

	/** * 二次元配列で表した時の縦横大きさ */
	private int h, w;
	/** * FX Point2Dを継承カスタム */
    private P2Dcustom pdc;
	/** * SE Pointを継承カスタム */
    private PointCustom ptc;
    
    
	/** * 空のコンストラクタ */
    public Meiro() {}

	/**
	* コンストラクタ
	* @param walkSetPd 歩ける座標セット
	* @param ground 舞台
	*/
	public Meiro(Set<Point2D> walkSetPd, int[][] ground) {
		this.walkSet = castSet(walkSetPd);
		this.pdc = new P2Dcustom();
		this.ptc = new PointCustom();
		
		h = ground.length;
		w = ground[0].length;
		
		init();
	}
	
	/** * 初期化 */
    private void init() {
    	expandMap = new HashMap<>();
    	spreadMap = new HashMap<>();
//        sttSet = new HashSet<>();
//        goalSet = new HashSet<>();
        traceSet = new HashSet<>();
        routeList = new ArrayList<>();
        
        for(Point pt : walkSet) {
        	//もっと多くても良い
        	int size = walkSet.size();
        	expandMap.put(pt, size);
        	spreadMap.put(pt, size);
        }
    }
    

	/**
	* このクラスの目的メソッド。スタート地点からの全マスに距離を取り、ゴール地点の値を拾う
	* @param sttPt 始点
	* @param goalPt 終点
	* @return 始点から終点までの（たぶん）最短、座標リスト
	*/
	public List<Point> guide(Point sttPt, Point goalPt) {
		spreadMapping(sttPt);
		return routing(goalPt, spreadMap, routeList);
	}

		/**
		* このクラスの目的メソッド。スタート地点からの全マスに距離を取り、ゴール地点の値を拾う
		* FX Point2Dタイプ。戻りはSE Pointのリスト
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
	* 伸びる方でマッピング
	* @param sttPt スタート地点
	*/
	public void expandMapping(Point2D sttPd) {
		expandMapping(pdc.deFxp2d(sttPd));
	}

		/**
		* 伸びる方でマッピング
		* @param sttPt スタート地点
		*/
		public void expandMapping(Point sttPt) {
			expandMap.clear();
			expandLoop(sttPt, 1);
			mapPrint(expandMap);
		}

	/**
	* 拡がる方でマッピング
	* @param sttPt スタート地点
	*/
	public void spreadMapping(Point2D sttPd) {
		spreadMapping(pdc.deFxp2d(sttPd));
	}

		/**
		* 拡がる方でマッピング
		* @param sttPt スタート地点
		*/
		public void spreadMapping(Point sttPt) {
			spreadMap.clear();
			spreadLoop(sttPt, 1);
			mapPrint(spreadMap);
		}

	/**
	* mapを構築する。広がるタイプ。expandLoopより少し早い
	* @param sttPt 始点
	* @param cnt 始点からの距離
	*/
	public void spreadLoop(Point sttPt, int cnt) {
		spreadMap.put(sttPt, cnt++);
		//どんなに入り組んで遠くとも、歩けるマスの数を超えることはないはず
		if(walkSet.size() < cnt) {
			return;
		}

		Set<Point> nexts = new HashSet<>();
		
		for(int i = -1; i <= 1; i += 2) {
			Point addPt = ptc.add(sttPt, i, 0);
			//次の座標値が今より大きければ、（とても大きい可能性もあるので）最小値に上書きするために移動する
			if(inArea(addPt) && (spreadMap.get(addPt) == null || spreadMap.get(addPt) > cnt)) {
				nexts.add(addPt);
			}

			addPt = ptc.add(sttPt, 0, i);
			if(inArea(addPt) && (spreadMap.get(addPt) == null || spreadMap.get(addPt) > cnt)) {
				nexts.add(addPt);
			}
		}
		
		for(Point pt : nexts) {
			spreadLoop(pt, cnt);
		}

	}


	/**
	* mapを構築する。伸びるタイプ。
	* @param sttPt 始点
	* @param cnt 始点からの距離
	*/
	private void expandLoop(Point sttPt, int cnt) {
		expandMap.put(sttPt, cnt++);
		if(walkSet.size() < cnt) {
			return;
		}
		
		for(int i = -1; i <= 1; i += 2) {
			Point addPt = ptc.add(sttPt, i, 0);
			if(inArea(addPt) && (expandMap.get(addPt) == null || expandMap.get(addPt) > cnt)) {
				expandLoop(addPt, cnt);
			}

			addPt = ptc.add(sttPt, 0, i);
			if(inArea(addPt) && (expandMap.get(addPt) == null || expandMap.get(addPt) > cnt)) {
				expandLoop(addPt, cnt);
			}
		}
	}

	/**
	* 範囲指定
	* @param pt 立ち入ろうとする座標
	* @return 立ち入れるならtrue
	*/
	private boolean inArea(Point pt) {
		if(traceSet.contains(pt)) { return false; }
		if(!walkSet.contains(pt)) { return false; }
		return true;
	}

	/**
	* Fx Point2DのセットをSE Pointのセットに変換
	* @param pdSet Fx Point2Dのセット
	* @return SE Pointのセット
	*/
    private Set<Point> castSet(Set<Point2D> pdSet) {
        Set<Point> ptSet = new HashSet<>();
        for(var val : pdSet) {
            ptSet.add( new Point(pdc.intX(val), pdc.intY(val)) );
        }
        return ptSet;
    }
    
    /**
    * デバッグ用。捜索ずみのマップを２次元配列に変換
	* @param map 捜索ずみのマップ
	* @return 捜索ずみのマップの値を受け取った２次元配列
    */
    public int[][] costBiArray(Map<Point, Integer> map) {
		int[][] intss = new int[h][w];
		
		for(Point pt : map.keySet()) {
			int r = (int)pt.getY();
			int c = (int)pt.getX();
			intss[r][c] = map.get(pt);
		}
		return intss;
    }
    
	/**
	* Mapを二次元配列として表示
	* @param map keyがSE Pointのマップ
	*/
	public void mapPrint(Map<Point, Integer> map) {
		print("mapPrint START");

		int[][] intss = costBiArray(map);

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

	//終点は値が大きく、始点は小さい。始点に行くほど、同じ値の存在数は減っていく
	//	それは「map.get(addPt) < cnt」に現れている
	/**
	* 作られたマップのある点から始点までを逆順でリスト化する。始点を含むことに注意
	* @param goalPt 終点。処理はここから始まり、リスト化された時lastIndexになる。
	* @param map 探索済みのマップ
	* @param list 空の座標リスト
	* @return 始点から終点までの（たぶん）最短、座標リスト
	*/
	public List<Point> routing(Point goalPt, Map<Point, Integer> map, List<Point> list) {
		list.add(0, goalPt);
		int cnt = map.get(goalPt);
		//つまり始点
		if(cnt == 1) {
			return list;
		}
		
		for(int i = -1; i <= 1; i += 2) {
			//移動予定の座標
			Point addPt = ptc.add(goalPt, i, 0);
			//移動予定の座標の値が今の座標の値より低いこと
			if(inArea(addPt) && map.get(addPt) < cnt) {
				//再帰
				routing(addPt, map, list);
			}

			addPt = ptc.add(goalPt, 0, i);
			if(inArea(addPt) && map.get(addPt) < cnt) {
				routing(addPt, map, list);
			}
		}
		return list;
	}


	/**
	* 動作実験。expandLoopとspreadの比較
	* @param goalPt 終点。処理はここから始まり、リスト化された時lastIndexになる。
	* @param map 探索済みのマップ
	* @param list 空の座標リスト
	* @return 始点から終点までの（たぶん）最短、座標リスト
	*/
	public void jikken(Point2D sttPd, Point2D goalPd) {
		Point goalPt = pdc.deFxp2d(goalPd);
		
		expandMapping(sttPd);
		spreadMapping(sttPd);
		int[][] aaa = costBiArray(expandMap);
		int[][] bbb = costBiArray(spreadMap);
		boolean bool = true;
		
		print("JIKKEN START");
		for(int i = 0; i < h; i++) {
			for(int j = 0; j < w; j++) {
				if(aaa[i][j] != bbb[i][j]) {
					print("ERROR", i, j, aaa[i][j], bbb[i][j]);
				}
			}
		}
		
		routing(goalPt, expandMap, routeList);
		print(routeList);
		
		print("JIKKEN END");
	}


	/** * 便利機能 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}


}