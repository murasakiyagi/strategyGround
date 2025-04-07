package yagi.murasaki.land.ground;

import java.io.*;
import java.util.*;

import javafx.geometry.Point2D;

//セルフ外部
import yagi.murasaki.utilCompo.quick.QuickUtil;
import yagi.murasaki.utilCompo.geometry.P2Dcustom;


//重要。rowとcolumnの関係。row=y,i,縦,行　column=x,j,横,列

/**
* マスゲームの舞台を設定するクラス。
* 	groundの属性値変更は別のクラスでやること。
	このクラスはあくまで基盤を作ること
*/
public class GroundModel {

	/** * マスゲームの舞台 */
	private int[][] ground;

	//重要。rowとcolumnの関係。row=y,i,縦　column=x,j,横
	/** * groundの行数と列数 */
	private int height, width;

	//groundサイズを動的に決めるときの要素
	/** * 最小限サイズ。ランダム値が０の場合もある */
	private int minimum;
	/** * 縦係数と横係数 */
	private int tate, yoko;
	
	/** * マス比率。 (全マス/masuhi) > walkSet */
	private int masuhi;
	
	/** * groundの座標 ground[row][col] */
	private int row, col;
	/** * 立ち入り禁止 */
	private Set<Point2D> zeroSet;
	/** * 立ち入り可能 */
	private Set<Point2D> walkSet;
	/** * カスタムPoint2D */
	private P2Dcustom pdc;
	
	//コンストラクタ=========================================
	public GroundModel() {
		init(7,6,6,6);
	}
	/**
	* コンストラクタ
	* @param min 舞台の縦横の辺の最小値
	* @param tate 伸縮幅。min ~ (min + tate - 1)
	* @param yoko 伸縮幅。min ~ (min + yoko - 1)
	* @param masuhi 歩けるマスと、歩けないマスの比。1 ~ 9
	*/
	public GroundModel(int min, int tate, int yoko, int masuhi) {
		init(min, yoko, tate, masuhi);
	}
	//=======================================================
	
	/**
	* 初期化。コンストラクタ補完
	* @param min 舞台の縦横の辺の最小値
	* @param tate 伸縮幅。min ~ (min + tate - 1)
	* @param yoko 伸縮幅。min ~ (min + yoko - 1)
	* @param masuhi 歩けるマスと、歩けないマスの比。1 ~ 9
	*/
	private void init(int min, int yoko, int tate, int masuhi) {
		this.minimum = qu.limitRegu(min, 2, 100);
		this.yoko = qu.limitRegu(yoko, 0, 10);
		this.tate = qu.limitRegu(tate, 0, 10);
		this.masuhi = qu.limitRegu(masuhi, 1, 9);
		this.zeroSet = new HashSet<>();
		this.walkSet = new HashSet<>();
		this.pdc = new P2Dcustom();
	}
		
	//メインアクション---------
	/**
	* groundを生成スタート
	*/
	public void action() {
			print("ACTION");
		lastCheck();
	}
	//-------------------------

	/**
	* 最終チェック。生成中のgroundのチェック
	* 全マス = 歩けるマス + 歩けないマス
		および、マス比率のチェック
	*/
	private boolean lastCheck() {//最終チェック。action
			print("LAST CHECK");
		autoGround();//とりあえず一回
		int brk = 0;
		while(
			(height * width) != (zeroSet.size() + walkSet.size()) && //全マス数 != ０マス＋歩けるマス、であればやり直し
//			(width * height / masuhi) >= walkSet.size()//reclamation()の条件でもある
			(width * height * (masuhi / 10.0)) >= walkSet.size()
		) {
			autoGround();
			if(brk++ >= 100) {
				print("LAST CHECK  BREAK!!");
				return false;
			}
		}
		return true;
	}

	/**
	* groundの生成
	*/
	private void autoGround() {//connect
			print("AUTO GROUND");
		walkSet.clear();//zeroは別にあるからいい
		//groundの基礎。ランダム生成
		vacant();//更地
		reclamation();//開拓
		setZero();
	}//autoGround.end--------------------------

	/**
	* 更(地)。groundのサイズをほぼランダム決定
	*/
	private void vacant() {
		height = (int)(Math.random() * tate + minimum);	//行数。縦。iの長さ。横線が何個あるか
		width = (int)(Math.random() * yoko + minimum);	//列数。横。jの長さ。縦線が何個あるか
		ground = new int[height][width];
	}

	/**
	* 最初の地点に降臨する
	* @return はじめの一歩
	*/
	private Point2D descent() {
		Point2D walkPd = Point2D.ZERO;
		boolean bool = true;
		while(bool) {
			int kariH = (int)(Math.random() * height);
			int kariW = (int)(Math.random() * width);
			if(inArea(kariH, kariW, height, width)) {
				walkSet.add(new Point2D(kariW, kariH));
				ground[kariH][kariW] = 1;
				walkPd = walkPd.add(kariW, kariH);
				bool = false;
			}
		}
			print("H W", height, width);
			print("WALKPD", walkPd);
			
		return walkPd;
	}

	/**
	* ランダムウォーク開拓(レクラメーション)
	* 適当に歩き回って歩ける場所を作っていく
	*/
	private void reclamation() {
		//始めの一歩
		Point2D walkPd = descent();
		
		//歩き回る
		int brk = 0;
		while((width * height * (masuhi / 10.0)) >= walkSet.size()) {
			//上下左右のマスをランダム取得。連続して同じ物が出ない
			Point2D addPd = pdc.nextFour();
			Point2D pd = walkPd.add(addPd);
			
			if(inArea((int)pd.getY(), (int)pd.getX(), height, width)) {
				walkSet.add(pd);
				ground[(int)pd.getY()][(int)pd.getX()] = 1;
				walkPd = walkPd.add(addPd);
				pdc.resetFourList();
			}
			if(brk++ > (width * height) * 10) { print("reclamation.BREAK"); break; }
		}
//		print(width * height, walkSet.size());
//		print((width * height / masuhi), (width * height * (masuhi / 10.0)), masuhi);
	}

		/**
		* この処理は配列に対して行うので、[h, w], [r, c]の並び
		*/
	    private boolean inArea(int r, int c, int h, int w) {
	        if(0 > r || r >= h) { return false; }
	        if(0 > c || c >= w) { return false; }
	        return true;
	    }

	/**
	* ground[][]のゼロのマスをSetに格納
	*/
	private void setZero() {
		zeroSet.clear();
		for(int i=0; i < height; i++) {
			for(int j=0; j < width; j++) {
				if(ground[i][j] == 0) {
					zeroSet.add( new Point2D(j, i) );
				}
			}
		}
	}

// ==================================
// ゲッター
	/** * マスゲームの舞台 */
	public int[][] getGround() { return ground; }
	/** * 歩けないマス */
	public Set<Point2D> getZeroSet() { return zeroSet; }
	/** * 歩けるマス */
	public Set<Point2D> getWalkSet() { return walkSet; }
	
	/**
	* 指定座標の値
	* @param i 指定座標
	* @param j 指定座標
	* @return 指定座標の値
	*/
	public int getGdValue(int i, int j) { return ground[i][j]; }
	
	/**
	* 指定座標の値
	* @param pd 指定座標
	* @return 指定座標の値
	*/
	public int getGdValue(Point2D pd) { return ground[(int)pd.getY()][(int)pd.getX()]; }
	/** * 縦サイズ */
	public int getRow() { return height; }
	/** * 横サイズ */
	public int getCol() { return width; }
	
//その他
	
	/**
	* groundを表示
	*/
	public void groundPrint() {
		System.out.println("=== FL.PANEL by GroundMasu ===");
		for(int i=0; i < ground.length; i++) {
			for(int j=0; j < ground[0].length; j++) {
				//桁数によるずれちょうせい
				if(ground[i][j] < 10 ) {//一桁
					System.out.print("  " + ground[i][j]);
				} else {
					System.out.print(" " + ground[i][j]);
				}
			}
			System.out.println();
		}
		System.out.println("===== FL.PANEL END =====");
	}

	/**
	* 指定の座標をint値で文字列化
	* @param pd 指定座標
	* @return int値で文字列表現
	*/
	private String xy(Point2D pd) {
		return "["+ (int)pd.getX() +","+ (int)pd.getY() +"]";
	}

	/** * マスゲームの舞台 */
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	/** * 「 +" "+ 」いらず * @param objs 可変長Object */
	public void print(Object... objs) {
		qu.print(objs);
	}
	

	//=============================================


}




