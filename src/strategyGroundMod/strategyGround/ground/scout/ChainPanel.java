package strategyGround.ground.scout;

import java.io.*;
import java.util.*;

import javafx.scene.Node;
import javafx.geometry.Point2D;

import utilCompo.quick.QuickUtil;
import utilCompo.geometry.P2Dcustom;


/**
* 	fullCreate();//１回
	setCost()//costの変更があれば
	fullCourse();//都度
*/
public class ChainPanel {

	public static ChainPanel KARI = new ChainPanel();
	private static Map<Point2D, ChainPanel> pdPnlMap = new HashMap<>();
	private static int[][] costArr;
	private static Map<Point2D, Integer> costMap = new HashMap<>();//attNum
	private static int width, height;
	private static Set<Point2D> walkSet;
	private static List<Point2D> dondenList = new ArrayList<>();
	private static List<Point2D> dondenListMoto = new ArrayList<>();//呼び出しもと
	private static List<Integer> doncosList = new ArrayList<>();//
	final private static int sumCostDef = 1000;
	
	private int col, row;//col,x,j  row,y,i
	private int cost;//属性番号
	private int sumCost;//属性番号累積値
	private Map<Point2D, Integer> sumCostMap = new HashMap<>();//costの累計
	private ChainPanel[] nexts = new ChainPanel[4];
	private boolean[] nextBools = new boolean[4];
	private int nextsSize;//nullでないnextの数
	private List<Point2D> routeList = new ArrayList<>();

//	連鎖処理。nextsを使った処理に関するフィールド。どんでん返し
	private static int dondenCntHeap;
	private int dondenCnt;
	private int nextCallCnt;//nextとして呼び出された回数

	private ChainPanel() {}
	public ChainPanel(int col, int row, int cost) {
		init(col, row, cost);
	}

//準備 ------
	private void init(int col, int row, int cost) {
		this.col = col;
		this.row = row;
		this.cost = cost;
		this.setCost(cost);
		this.sumCost = sumCostDef;
		pdPnlMap.put(new Point2D(col, row), this);
		height = Math.max(height - 1, row) + 1;
		width = Math.max(width - 1, col) + 1;
	}

	public static void fullCreate(Map<Point2D, Integer> costMap, Set<Point2D> walkSet) {
		create(costMap);
		setNexts(walkSet);
	}

	public static List<Point2D> fullCourse(Point2D sttPd, Point2D goalPd) {
		ChainPanel.sumCostMappingStart(sttPd);
		ChainPanel.sumCostPrint(sttPd);
		return ChainPanel.routeStart(sttPd, goalPd);
	}

	/**
	* 
	*/
	public static void create(Map<Point2D, Integer> costMap) {
		ChainPanel.costMap = costMap;
		for(Point2D pd : costMap.keySet()) {
			new ChainPanel((int)pd.getX(), (int)pd.getY(), costMap.get(pd));
		}
	}

	/**
	* panelの隣接するマス。最大４マスでwalkSetに準ずる。
	*/
	public static void setNexts(Set<Point2D> walkSet) {
		ChainPanel.walkSet = walkSet;
		for(Point2D pd : walkSet) {
			ChainPanel motoPnl = pnl(pd);
			Point2D[] pds = P2Dcustom.nexts(pd);//[4]
			for(int i = 0; i < 4; i++) {
				if(walkSet.contains(pds[i])) {
					motoPnl.nexts[i] = pnl(pds[i]);//newではない
					motoPnl.nextsSize++;
				}
			}
		}
	}
	
	public void setCost(int num) {
		this.cost = num;
		costMap.put(this.getPd(), num);//ヒープ
	}

	public static void setCost(Point2D pd, int num) {
		ChainPanel pnl = ChainPanel.pnl(pd);
		pnl.cost = num;
		costMap.put(pd, num);//ヒープ
	}

//準備 END ------

//マッピング ------
	/**
	* sumCostMappingStartでできたsumCostMapのチェック
	* 始点以外はnextsに自身のsumCostより小さい数が必ず１以上ある。
	* 	自身 - next >= 0 && "その最大値が自身のマスのコスト",であれば正解
	*/
	public static void sumCostMapCheck(Point2D sttPd) {
		int falCnt = 1;
		int wilCnt = 1;
		ChainPanel sttPnl = ChainPanel.pnl(sttPd);
		Map<Point2D, Integer> sortedMap = mapSort(sttPnl.sumCostMap);
		while(falCnt > 0) {
			KARI.print("\n------ CHECK", wilCnt++, "------", falCnt, "\n");
			falCnt = 0;
			for(Point2D pd : sortedMap.keySet()) {
				int costA = costMap.get(pd);
				int sumA = sortedMap.get(pd);
				ChainPanel pnl = ChainPanel.pnl(pd);
				int sumSa = Integer.MIN_VALUE;//sumの差
				int sumSaMax = Integer.MIN_VALUE;//sumの差
				for(ChainPanel next : pnl.nexts) {
					if(next != null) {
						int costB = costMap.get(next.getPd());
						int sumB = sortedMap.get(next.getPd());
						sumSa = sumA - sumB;
						sumSaMax = Math.max(sumSaMax, sumSa);//sumSaが0未満だけなのはスタートのみ
					}
				}
				if(sumSaMax >= 0) {
					if(costA == sumSaMax) {
						sttPnl.print("TRUE", costA, sumA, sumSaMax, xy(pd));
					} else {
						sttPnl.print("False", costA, sumA, sumSaMax, xy(pd));
						sortedMap.put(pd, sumA - sumSaMax + costA);
						sttPnl.print("CORRECT", sumA - sumSaMax + costA, ++falCnt);
					}
				} else {
					sttPnl.print("MINUS", costA, sumA, xy(pd));
				}
			}
		}//while
		
		sttPnl.sumCostMap = sortedMap;
		KARI.print("\n------ CHECK  END ------\n");
	}

	/**
	* mapのvalueの自然順序で並び替える。value重複はkeySet順
	* 値の小さい方から処理したほうが直し残しを減らせる
	* sumCostMapCheck()にて使用
	*/
	private static Map<Point2D, Integer> mapSort(Map<Point2D, Integer> sumcostmap) {
		Map<Point2D, Integer> backMap = new HashMap<>(sumcostmap);
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
	public static void sumCostMappingStart(Point2D pd) {
		dondenCntHeap++;
		ChainPanel sttPnl = pnl(pd);
		sttPnl.sumCost = costMap.get(pd);
		sttPnl.sumCostMap.put(pd, costMap.get(pd));
		P2Dcustom pdc = new P2Dcustom();
		for(int i = 0; i < 4; i++) {
			int f = pdc.fourList();//乱数
			ChainPanel next = sttPnl.nexts[f];
			if(next != null) {
				if(!sttPnl.nextBools[f]) {
					sttPnl.nextBools[f] = true;//済み
					next.sumCostMapping(sttPnl.sumCostMap, next, sttPnl, sttPnl.sumCost);
				}
			}
		}
		costMappingSettle();//後始末
	}

		/**
		* sumCostMappingStartで使われる、再帰処理
		*/
		private void sumCostMapping(Map<Point2D, Integer> sttSumCostMap, ChainPanel pnl, ChainPanel moto, int motoSumCost) {
			//this != pnl なので誰のフィールドか気を付ける。基本はthisは使わずpnl
			if(dondenCntHeap > pnl.dondenCnt) {//つまりisDonding
				if(pnl.nextsSize > pnl.nextCallCnt++) {
					pnl.sumCost = Math.min(pnl.sumCost, costMap.get(pnl.getPd()) + motoSumCost);
					sttSumCostMap.put(pnl.getPd(), pnl.sumCost);

					dondenList.add(pnl.getPd());
					dondenListMoto.add(moto.getPd());
					doncosList.add(pnl.sumCost);
					
					P2Dcustom pdc = new P2Dcustom();
					for(int i = 0; i < 4; i++) {
						int f = pdc.fourList();
						ChainPanel next = pnl.nexts[f];
						if(!pnl.nextBools[f]) {
							pnl.nextBools[f] = true;//済み
							if(next != null) {
								sumCostMapping(sttSumCostMap, next, pnl, pnl.sumCost);
							}
						}
					}
				} else {
					pnl.dondenCnt++;//一処理終わってもクリアする必要ない
					print("dondenCnt", pnl.dondenCnt, pnl.nextsSize, pnl.nextCallCnt);
				}
			} else {
				print("dondenCntHeap > dondenCnt", dondenCntHeap, pnl.dondenCnt);
			}
		}

			/**
			* 後始末。初期化
			*/
			private static void costMappingSettle() {
	//			System.out.println("costMappingSettle");
				for(ChainPanel pnl : pdPnlMap.values()) {
					pnl.nextBools = new boolean[4];
					pnl.nextCallCnt = 0;
					pnl.sumCost = sumCostDef;
				}
			}

	/**
	* 完成したsumCostMapから、始点終点間の最小コストルートを割り出す
	*/
	public static List<Point2D> routeStart(Point2D sttPd, Point2D goalPd) {
		KARI.print("routeStart");
		ChainPanel sttPnl = ChainPanel.pnl(sttPd);
		ChainPanel goalPnl = ChainPanel.pnl(goalPd);
		int min = Integer.MAX_VALUE;
		route(sttPnl.sumCostMap, sttPnl.routeList, goalPnl, min);
		return sttPnl.routeList;
	}
	
		/**
		* routeStartで使われる再帰処理
		* nexts.sumCostの値が重複して、どちらに行っても（たぶん）たどり着く
		*/
		private static void route(Map<Point2D, Integer> sttSumCostMap, List<Point2D> sttRouteList, ChainPanel pnl, int min) {
			if(pnl == null) {
				sttRouteList.remove(0);
//				for(Point2D pd : sttRouteList) {
//					KARI.print(pd);
//				}
			} else {
				sttRouteList.add(0, pnl.getPd());
				ChainPanel nextPnl = null;
				for(ChainPanel next : pnl.nexts) {
					if(next != null) {
						if(min > sttSumCostMap.get(next.getPd())) {
							min = sttSumCostMap.get(next.getPd());
							nextPnl = next;
						} else {
	//						KARI.print("ROUTE END");
						}
					}
				}
				route(sttSumCostMap, sttRouteList, nextPnl, min);
			}
		}
//マッピング END ------

//ゲッター
	public static ChainPanel pnl(Point2D pd) {
		return pdPnlMap.get(pd);
	}
	
	public static ChainPanel pnl(int col, int row) {
		return pdPnlMap.get(new Point2D(col, row));
	}
	
	public static Map<Point2D, Integer> getCostMap() {
		return costMap;
	}
	
	public static int[][] getCostArr() {
		costArr = new int[height][width];
		for(Point2D pd : costMap.keySet()) {
			costArr[(int)pd.getY()][(int)pd.getX()] = costMap.get(pd);
		}
		return costArr;
	}
	
	public Map<Point2D, Integer> getSumCostMap() { return sumCostMap; }
	public int getCost() { return cost; }
	public int getRow() { return row; }
	public int getCol() { return col; }
	public Point2D getPd() { return new Point2D(col, row); }
	public ChainPanel[] getNexts() { return nexts; }
	public ChainPanel getNext(int index) { return nexts[index]; }
	
//その他 ------
	public static void sumCostPrint(Point2D pd) {
		ChainPanel pnl = pnl(pd);
		sumCostMapCheck(pd);
//		pnl.print("DONDEN LIST");
		for(int i = 0; i < dondenList.size(); i++) {
//			pnl.print(i, doncosList.get(i), xy(dondenList.get(i)), xy(dondenListMoto.get(i)));
		}
		
//			pnl.print("sumCostPrint", pd, pnl, pnl.getSumCostMap().size());
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				Point2D kariPd = new Point2D(j, i);
				if(walkSet.contains(kariPd) &&
					pnl.sumCostMap.get(kariPd) != null
				) {
					int kari = pnl.getSumCostMap().get(new Point2D(j, i));
					if(kari >= 10) {
						System.out.print("  " + kari);
					} else {
						System.out.print("   " + kari);
					}
				} else {
					System.out.print("   " + 0);
				}
			}
			System.out.println();
		}
		dondenList.clear();
		dondenListMoto.clear();
		doncosList.clear();
	}

	public void info() {
		print("----- PANELER INFO -----");
		print("col : ", col, "   ROW : ", row);
		print("COST : ", cost);
		print("--- PANELER INFO  END ---");
	}

	private static String xy(Point2D pd) {
		return "[" + (int)pd.getX() +","+ (int)pd.getY() + "]";
	}

	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	
}



