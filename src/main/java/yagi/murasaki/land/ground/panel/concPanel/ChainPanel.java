package yagi.murasaki.land.ground.panel.concPanel;

import java.io.*;
import java.util.*;

import javafx.scene.Node;
import javafx.geometry.Point2D;

import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;

import yagi.murasaki.land.ground.panel.PanelAbst;
//import yagi.murasaki.land.ground.PanelManager;
import yagi.murasaki.land.ground.panel.PanelColony;


/**
* sumCostMapping()などを外部に移そうかと思ったが、ここにあったほうが都合がいい。
	もし分化するとしたらメンバークラスとして作る

* 	fullCreate();//１回
	setCost()//attの変更があれば
	fullCourse();//都度
*/
public class ChainPanel extends PanelAbst {

	public static final ChainPanel KARI = new ChainPanel(0,0,new AttA(),0);
	private static final int sumCostDef = 1000;
	private static int h, w;//groundの縦横
	
	private ChainPanel[] nexts;
	private int nextsSize;//nullでないnextの数

	private PanelColony<ChainPanel> colony;//このクラスは特にマネージャーありき
	/**
	* 属性番号累積値
	* Mappingの際にデフォルト値に直されるので、保持用ではない。
	*/
	private int sumCost;
	private int cost;
	private static Map<Point2D, Integer> costMap = new HashMap<>();//attの累計
	private Map<Point2D, Integer> sumCostMap;//attの累計
	private boolean[] nextBools;
	private List<Point2D> routeList;//保持用

//	連鎖処理。nextsを使った処理に関するフィールド。どんでん返し
	private static int dondenCntHeap;//クリアしない
	private int dondenCnt;//クリアしない
	private int nextCallCnt;//nextとして呼び出された回数
	private List<Point2D> dondenList = new ArrayList<>();
	private List<Point2D> dondenListMoto = new ArrayList<>();//呼び出しもと
	private List<Integer> doncosList = new ArrayList<>();//

	//att.num == 1とした単純な距離のマップ。クリエート直後に作る
	private Map<Point2D, Integer> rangeMap;

	public ChainPanel(int col, int row, Att att, int cost) {
		super(col, row, att);
		this.nexts = new ChainPanel[4];
		this.rangeMap = new HashMap<>();
		setCost(cost);
		init();
	}

	public static void fullCreate(PanelColony<ChainPanel> colony) {
		Map<Point2D, ChainPanel> pnlMap = create(colony.getGround(), colony);
		setNexts(colony.getWalkSet(), pnlMap);
		rangeMapping(colony.getWalkSet(), pnlMap);
	}

	public static Map<Point2D, ChainPanel> create(int[][] ground, PanelColony<ChainPanel> colony) {
		h = ground.length;
		w = ground[0].length;
		KARI.setColony(colony);
		for(int i = 0; i < ground.length; i++) {
			for(int j = 0; j < ground[0].length; j++) {
				ChainPanel cpnl = new ChainPanel(j, i, new AttA(ground[i][j]), ground[i][j]);
				cpnl.setColony(colony);
				colony.putPnl(new Point2D(j,i), cpnl);
			}
		}
		return colony.getPnlMap();
	}

	/**
	* panelの隣接するマス。最大４マスでwalkSetに準ずる。
	*/
	public static void setNexts(Collection<Point2D> walkSet, Map<Point2D, ChainPanel> pnlMap) {
		for(Point2D pd : pnlMap.keySet()) {
			ChainPanel cpnl = pnlMap.get(pd);
			Point2D[] pds = P2Dcustom.nexts(pd);//[4]
			for(int i = 0; i < 4; i++) {
				if(walkSet.contains(pds[i])) {
					cpnl.nexts[i] = pnlMap.get(pds[i]);//newにしない
					cpnl.nextsSize++;
				}
			}
		}
	}

	public static void rangeMapping(Collection<Point2D> walkSet, Map<Point2D, ChainPanel> pnlMap) {
		for(Point2D pd : pnlMap.keySet()) {
			if(walkSet.contains(pd)) {
				ChainPanel cpnl = pnlMap.get(pd);
				cpnl.sumCostMappingStart(cpnl.rangeMap);
				cpnl.sumCostMapCheck();
			}
		}
		KARI.pnlMapPrint(pnlMap);
	}

	public Object something(Object obj) { return null; }

	private void init() {
		sumCost = sumCostDef;
		sumCostMap = new HashMap<>();
		nextBools = new boolean[4];
		routeList = new ArrayList<>();
	}

	public void setColony(PanelColony<ChainPanel> colony) {
		this.colony = colony;
	}

	public List<Point2D> fullCourse(Point2D goalPd) {
		init();
		sumCostMappingStart();
		sumCostMapCheck();
		routeStart(goalPd);
		sumCostPrint();
		costMapPrint();
		return this.routeList;
	}

//	public void mapping(PanelColony<ChainPanel> colony) {
	public void mapping() {
		init();
//		this.colony = colony;
		sumCostMappingStart();
		sumCostMapCheck();
		sumCostPrint();
		costMapPrint();
	}

//マッピング ------
	/**
	* sumCostMappingStartでできたsumCostMapのチェック
	* 始点以外はnextsに自身のsumCostより小さい数が必ず１以上ある。
	* 	自身 - next >= 0 && "その最大値が自身のマスのコスト",であれば正解
	*/
	public void sumCostMapCheck() {
		int falCnt = 1;
		int wilCnt = 1;
		Map<Point2D, Integer> sortedMap = mapSort(this.sumCostMap);
		while(falCnt > 0) {
//			KARI.print("\n------ CHECK", wilCnt++, "------", falCnt, "\n");
			falCnt = 0;
			for(Point2D pd : sortedMap.keySet()) {
//				int attNum = colony.getNum(pd);
				int attNum = colony.pnl(pd).getCost();
//				int attNum = costMap.get(pd);
//					if(cost != 1) {
//						print("COST",cost, pd);
//					}
				int sumA = sortedMap.get(pd);
				int sumSa = Integer.MIN_VALUE;//sumの差
				int sumSaMax = Integer.MIN_VALUE;//sumの差
				ChainPanel cpnl = (ChainPanel)colony.pnl(pd);
				for(int i = 0; i < 4; i++) {
					if(cpnl.nexts[i] != null) {
//						Att attB = colony.getAtt(cpnl.nexts[i].getPd());
						int sumB = sortedMap.get(cpnl.nexts[i].getPd());
						sumSa = sumA - sumB;
						sumSaMax = Math.max(sumSaMax, sumSa);//sumSaが0未満だけなのはスタートのみ
					}
				}
				if(sumSaMax >= 0) {
					if(attNum == sumSaMax) {
//						this.print("TRUE", attNum, sumA, sumSaMax, xy(pd));
					} else {
//						this.print("False", attNum, sumA, sumSaMax, xy(pd));
						sortedMap.put(pd, sumA - sumSaMax + attNum);
//						this.print("CORRECT", sumA - sumSaMax + attNum, ++falCnt);
					}
				} else {
//					this.print("MINUS", attNum, sumA, xy(pd));
				}
			}
		}//while
		
		this.sumCostMap = sortedMap;
//		print("\n------ CHECK  END ------\n");
	}

	/**
	* mapのvalueの自然順序で並び替える。value重複はkeySet順
	* 値の小さい方から処理したほうが直し残しを減らせる
	* sumCostMapCheck()にて使用
	*/
	private static Map<Point2D, Integer> mapSort(Map<Point2D, Integer> sumAttMap) {
		Map<Point2D, Integer> backMap = new HashMap<>(sumAttMap);
		Map<Point2D, Integer> newMap = new LinkedHashMap<>();;
		
		List<Integer> kariList = new ArrayList<>(backMap.values());
		Collections.sort(kariList);
		int cnt = 0;
		for(Integer num : kariList) {
			if(backMap.containsValue(num)) {
				//valueに対するキーpdの特定
		    	for(Point2D kpd : backMap.keySet()) {
					if(num.equals(backMap.get(kpd))) {
						//特定完了
						newMap.put(kpd, num);
						backMap.remove(kpd);
						cnt++;
						break;//for
					}
				}
			}
		}
//		KARI.print("mapSort", kariList.size(), cnt, backMap.size());
		return newMap;
	}

	/**
	* ある点から全ての点までの累積コストを取得
	*/
	public void sumCostMappingStart() {
		sumCostMappingStart(this.sumCostMap);
	}
	
	public void sumCostMappingStart(Map<Point2D, Integer> map) {
		dondenCntHeap++;
		//この下の１行のために、再帰処理内と同じような処理を外に出している
//		this.sumCost = att.getNum();
//		map.put(this.getPd(), att.getNum());
		this.sumCost = this.cost;
		map.put(this.getPd(), this.sumCost);
		P2Dcustom pdc = new P2Dcustom();
		for(int i = 0; i < 4; i++) {
			int f = pdc.fourList(false);//乱数
			if(nexts[f] != null) {
				if(!nextBools[f]) {
					nextBools[f] = true;//済み
					nexts[f].sumCostMapping(map, sumCost, colony);
					dondenListMoto.add(this.getPd());
				}
			}
		}
		attMappingSettle();//後始末
	}

		/**
		* 重要：このメソッドを呼び出しているのはnextなので、this==sttPnlではない
		* 目的：this.sumCostMapの作成
		* sumCostMappingStartで使われる、再帰処理
		* that.sumCost = motoSumCostをいじっていることに注意。目的が達せられたら全部元に戻す
		* that.sumCostをいじる理由：１マスに最大４回呼び出しがあり、sumCostの比較がされる。そのための(代用の)Mapを作るのが面倒
		*/
		public void sumCostMapping(Map<Point2D, Integer> sttSumCostMap, int motoSumCost, PanelColony<ChainPanel> colony) {
			//this != pnl なので誰のフィールドか気を付ける。基本はthisは使わずpnl
			if(dondenCntHeap > dondenCnt) {
				if(nextsSize > nextCallCnt++) {
					
//					sumCost = Math.min(sumCost, colony.getNum(getPd()) + motoSumCost);
					sumCost = Math.min(sumCost, cost + motoSumCost);
					sttSumCostMap.put(getPd(), sumCost);

//						print("C", colony.getNum(getPd()), sumCost, cost);

					dondenList.add(getPd());
//					dondenListMoto.add(moto.getPd());
					doncosList.add(sumCost);
					
					P2Dcustom pdc = new P2Dcustom();
					for(int i = 0; i < 4; i++) {
						int f = pdc.fourList(false);
						if(!nextBools[f]) {
							nextBools[f] = true;//済み
							if(nexts[f] != null) {
//								sumCostMapping(sttSumCostMap, cnext, cpnl, sumCost);
								nexts[f].sumCostMapping(sttSumCostMap, sumCost, colony);
							}
						}
					}
				} else {
					dondenCnt++;//一処理終わってもクリアする必要ない
//					print("dondenCnt", dondenCnt, nextCallCnt);
				}
			} else {
//				print("dondenCntHeap > dondenCnt", dondenCntHeap, dondenCnt);
			}
		}
		
			/**
			* 後始末。初期化
			* この処理はChainPanel固有の処理なのでManaじゃない
			*/
			private void attMappingSettle() {
	//			System.out.println("attMappingSettle");
				for(ChainPanel cpnl : colony.getPnlMap().values()) {
//					ChainPanel cpnl = (ChainPanel)face;
					cpnl.nextBools = new boolean[4];
					cpnl.nextCallCnt = 0;
					cpnl.sumCost = sumCostDef;
				}
			}

	/**
	* 完成したsumCostMapから、始点終点間の最小コストルートを割り出す
	*/
	public List<Point2D> routeStart(Point2D goalPd) {
		print("routeStart");
		ChainPanel goalPnl = (ChainPanel)colony.pnl(goalPd);
		int min = Integer.MAX_VALUE;
		route(this.sumCostMap, this.routeList, goalPnl, min);
		return this.routeList;
	}
	
		/**
		* routeStartで使われる再帰処理
		* nexts.sumCostの値が重複しても、どちらに行っても（たぶん）たどり着くことは着く
		*/
		private void route(Map<Point2D, Integer> sttSumCostMap, List<Point2D> sttRouteList, ChainPanel cpnl, int min) {
			if(cpnl == null) {
				sttRouteList.remove(0);
			} else {
				sttRouteList.add(0, cpnl.getPd());
				ChainPanel nextPnl = null;
				for(int i = 0; i < 4; i++) {
					ChainPanel cnext = cpnl.nexts[i];
					if(cnext != null) {
						if(min > sttSumCostMap.get(cnext.getPd())) {
							min = sttSumCostMap.get(cnext.getPd());
							nextPnl = cnext;
						} else {
	//						KARI.print("ROUTE END");
						}
					}
				}
				
				route(sttSumCostMap, sttRouteList, nextPnl, min);
			}
		}
//マッピング END ------


//データ抽出

	/**
	* [thereまでのコスト] - [hereのコスト]
	* 
	* @param here スタート地点
	* @param there ゴール地点
	*/
	public int range(Point2D here, Point2D there) {
			print(here, there);
		return rangeMap.get(there) - rangeMap.get(here);
	}

	/**
	* 非推奨
	*/
	public Set<Point2D> nearAreaCost(int range) {
		return nearArea(range, sumCostMap, sumCost);
	}
	
	/**
	* 単純な距離範囲
	* @param range 現在地を１とした範囲距離
	@ return 範囲のポイント
	*/
	public Set<Point2D> nearAreaRange(int range) {
		return nearArea(range, rangeMap, 1);
	}
		
		/**
		* 
		* @param range 現在地を１とした範囲距離
		* @param map CostMap
		* @param cost 
		@ return 範囲のポイント
		*/
		private Set<Point2D> nearArea(int range, Map<Point2D, Integer> map, int cost) {
			Set<Point2D> kariSet = new HashSet<>();
			for(Point2D pd : map.keySet()) {
				if(map.get(pd) - cost <= range) {
					kariSet.add(pd);
				}
			}
			return kariSet;
		}


//セッター
	public void setCost(int cost) {
//		print("SET COST", hashCode(), this.cost, "NEW",cost);
		
		this.setAtt("cost", cost);
		this.cost = cost;
		if(costMap == null) {
			costMap = new HashMap<>();
		}
//		costMap.put(new Point2D(col, row), cost);
		costMap.put(getPd(), cost);

//		print(this.cost, "NEW", cost, this.intValue("cost"));
	}

//ゲッター
	public Map<Point2D, Integer> getSumCostMap() { return sumCostMap; }
	public Map<Point2D, Integer> getRangeMap() { return rangeMap; }
	public int getSumCost() { return sumCost; }
	public int getCost() { return cost; }
	public ChainPanel[] getNexts() { return nexts; }

	public static Map<Point2D, Integer> getCostMap() { return costMap; }
	/**
	* costMapはこのクラスに固有なのでPanelColonyでは作らなかった
	* 
	* 
	* 
	*/
	public static int[][] costArrays() {
		int[][] costArr = new int[h][w];
		for(int i = 0; i < h; i++) {
			for(int j = 0; j < w; j++) {
				costArr[i][j] = costMap.get(new Point2D(j, i));
			}
		}
		return costArr;
	}


//その他 ------
	public void mapPrint(Map<Point2D, Integer> map) {
		for(int i = 0; i < colony.getHeight(); i++) {
			for(int j = 0; j < colony.getWidth(); j++) {
				Point2D kariPd = new Point2D(j, i);
				if(colony.getWalkSet().contains(kariPd) &&
					map.get(kariPd) != null
				) {
					int kari = map.get(kariPd);
					if(kari >= 10) {
						System.out.print("  " + kari);
					} else {
						System.out.print("   " + kari);
					}
				} else {
					System.out.print("   " + "*");
				}
			}
			System.out.println();
		}
	}
	public void rangeMapPrint() { mapPrint(rangeMap); }
	
	/**
	* 現在地を考慮しない座標のコストを表示
	*/
	public void costMapPrint() { mapPrint(costMap); }

	/**
	* 現在地から移動可能な全てのポイントまでの加算コストを表示
	*/
	public void sumCostPrint() {
		System.out.print("------ SUM COST ------\n");

		mapPrint(sumCostMap);
		dondenList.clear();
		dondenListMoto.clear();
		doncosList.clear();

		System.out.println("\n--- SUM COST  END ---");
	}
		
	public void pnlMapPrint(Map<Point2D, ChainPanel> pnlMap) {
		print(pnlMap.hashCode());
		for(int i = 0; i < colony.getHeight(); i++) {
			for(int j = 0; j < colony.getWidth(); j++) {
				Point2D kariPd = new Point2D(j, i);
				if(colony.getWalkSet().contains(kariPd) &&
					pnlMap.get(kariPd) != null
				) {
					ChainPanel cpnl = pnlMap.get(kariPd);
					int kari = cpnl.rangeMap.size();
					if(kari >= 10) {
						System.out.print("  " + kari);
					} else {
						System.out.print("   " + kari);
					}
				} else {
					System.out.print("   " + "*");
				}
			}
			System.out.println();
		}
	}

	private String xy(Point2D pd) {
		return "[" + (int)pd.getX() +","+ (int)pd.getY() + "]";
	}

	
}



