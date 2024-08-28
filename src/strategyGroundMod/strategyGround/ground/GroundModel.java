package strategyGround.ground;

import java.io.*;
import java.util.*;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.geometry.Point2D;

//セルフ外部
import utilCompo.quick.QuickUtil;


//マスゲームの舞台を設定するクラス
//および各クラス各メソッドで使う面倒な処理をここに一括する
//重要。rowとcolumnの関係。row=y,i,縦,行　column=x,j,横,列
/*処理の順番
	舞台の広さをランダムで決定。autoField()
	０マス、１マスを決める。autoField()
		マスの属性を示すpanel[][]インスタンスのみ。autoField()
	zeroListの生成、panel[][]に０マス、１マスを決定。zeromasu(),syokiiti()
	初期配置を決定。walkSetに追加。syokiiti()
	初期配置(fstPds[fs])から別の初期配置(fstPds[fs+1])までランダムに歩き回り、walkSetに追加していく。nextPos(),syokiiti()
	上記の処理が出来ているかチェック。syokiiti()
	歩けるマスと０マスの比率、初期配置間の連絡が不合格なら始めからやり直し。connect()

*/

public class GroundModel {

	static Pane p = new Pane();
	static int vidcnt = 0;//pに含まれるNodeの通し番号

	//プロパティ
		//fieldは一応ここに参考に書くが、メインで作成して、コンストラクタで上書きする
	static int[][] field = {//int[9][10],0の数15+34=49
			{0,0,0,0,0,0,0,0,0,0},
			{0,1,1,1,1,1,1,1,1,0},
			{0,1,1,0,1,1,1,1,1,0},
			{0,1,1,2,0,1,2,1,1,0},
			{0,1,1,1,1,0,1,1,1,0},
			{0,1,1,0,1,1,1,1,1,0},
			{0,1,1,1,1,1,1,1,1,0},
			{0,1,1,1,1,1,1,1,1,0},
			{0,0,0,0,0,0,0,0,0,0}
	};

	//重要。rowとcolumnの関係。row=y,i,縦　column=x,j,横
	static int flRow = field.length;	//行数。横並びが何個あるか
	static int flCol = field[ field.length-1 ].length;	//列数。縦並びが何個あるか

	/**
	* fieldサイズを動的に決めるときの要素
	* autoField()
	*/
	final private int minimum = 7;//最小限サイズ。ランダム値が０の場合もある。
	final private int tate = 6;//係数
	final private int yoko = 6;//係数

	int row = 0;//field[row][col]
	int col = 0;
	private static ArrayList<Point2D> zeroList = new ArrayList<Point2D>();//fieldの0のマスだけ集める
	private int zeroSize;//zeroListの長さ
	HashSet<Point2D> walkSet = new HashSet<Point2D>();//歩ける場所。nextPos(),syokiiti()。初期位置から別の初期位置を探し回り歩いた場所を追加
	private static Map<Point2D, Integer> ftcntMap;
//	FtcntMapping ft;

	//パネルの状態
	private static int[][] panel = new int[flRow][flCol];//fieldの中の数値をいじるため
//	private Paneler paneler;//staticインスタンス生成のみ

	/**
	* syokiiti()で使うメンバー
	*/
	Point2D fstPds[] = new Point2D[4];
	Point2D sndPds[] = new Point2D[fstPds.length];
	Boolean[] fstbo = new Boolean[fstPds.length];//始点がすべて通じているか。
	int fs;//fstPds[fs]
	Point2D beginPd;//ftに与える値。walkListの始めのPoint。Panelerとftがうまくいったら消す。
	Point2D endPd;//walkListの最後のPoint。
//	Point2D keepBeginPd;//beginは用が済んだらnullにしたい。けど残したい
	//Point2D fstPd, sndPd, serPd, forPd;//キャラ初期位置。ここから進入できないマスは0にする

	
	//コンストラクタ=========================================

	public GroundModel() {
//		this.ft = new FtcntMapping(this);
	}; //スーパークラスを(があれば)そのままインスタンスするやつ


	//=======================================================
	//メインアクション---------
	public void action() {
		p.getChildren().clear();
		vidcnt = 0;
		lastCheck();
		
// 		this.ft = new FtcntMapping(this);
//		ft.initialize(this);
//		ftcntMap = ft.createMap(beginPd);//beginPdはlastCheck()内zeromasu()（最後の最後）で確定する
//		ft.panelPrint(ftcntMap);
//			print("BEGIN PD ", beginPd);
//		paneler = new Paneler(ft, walkSet);//インスタンス生成のみ
//			jikken();
	}
	//-------------------------

// 		jikken
//		private void jikken() {
//			for(Point2D pd : walkSet) {
//				Paneler pane2 = Paneler.getPaneler(pd);
//					print("PD", pd, pane2);
//				pane2.panelPrint(pane2);
//				
//				
//			}
//			
//		}


	private void lastCheck() {//最終チェック。action
		int fccnt = 0;
		walkSet.clear();//初期化。lastCheck()は全ての処理の始めに来るので、下のwhileには必ず一回引っかかる

		while( (flRow * flCol) != (zeroList.size() + walkSet.size()) ) {//全マス数 != ０マス＋歩けるマス、であればやり直し
			walkSet.clear();
			connect();

			if(fccnt++ >= 100) {//無限ループ防止
				fccnt = 0;
				print(" LAST_CHECK  無限ループ防止");
				break;
			}
		}
	}


// 	autoField()を成功させる
	private void connect() {//lastCheck

		int i = 0;
		int jj = 0;
// 		ここのflRow,flColは内部処理で変更されるので注意
		while( (flRow * flCol / 3) > walkSet.size() ) {//歩けるマスとゼロマスの比率
		
			autoField();//とりあえず作ってみる
			
			while( Arrays.asList(fstbo).contains(false) ) {//初期配置同士の連絡ができるか。fstboにfalseがあれば最初からやり直し
				jj++;
				autoField();
				if(jj >= 1000) { break; }//無限ループ防止保険
			}
		}

// 		autoFieldが全部終わったら、fieldの値を修正
		for(int k=0; k < flRow-1; k++) {
			for(int j=0; j < flCol-1; j++) {
				if(walkSet.contains(new Point2D(j, k))) {//walkSet=通れるマス=１のマス、でないなら０に。２マスも１に変わる
					field[k][j] = 1;
				} else {
					field[k][j] = 0;
				}
			}
		}

		zeromasu();//fieldの値からzeroListを作り直すだけ。最後にもう一度
	}


		private void autoField() {//connect
	
			//fieldの基礎
			//field = new int[8][8];//実験用
			field = new int[ (int)(Math.random() * tate + minimum) ][ (int)(Math.random() * yoko + minimum) ];//ランダム生成
			flRow = field.length;	//行数。縦。iの長さ。横線が何個あるか
			flCol = field[0].length;	//列数。横。jの長さ。縦線が何個あるか
// 			kitenX = (int)( flRow * hakoW/2 + 10 );//描画位置の調整
	
//				print("G", flRow, flCol);
//				groundPrint();
	
			makeBarrier();
	
			panel = new int[flRow][flCol];
	
			syokiiti();
	
		}//autoField.end--------------------------


			private void makeBarrier() {//障害
				//障害物(０マス)生成
				for(int i=0; i < flRow-1; i++) {
					Arrays.fill(field[i], 1);//その行の全要素に 1 を格納
					//周囲を０で囲む
					for(int j=0; j < flCol-1; j++) {
						field[0][j] = 0;//上端の行
						field[ flRow-1 ][j] = 0;//下端の行

						//内部に確率で０を生成。この場合1/5
						field[i][j] = (Math.random() < 0.2) ? 0 : 1;
					}
					field[i][0] = 0;//左端の列
					field[i][ flCol-1 ] = 0;//右端の列
				}
//					print("H");
//					groundPrint();
			}


			//このメソッドのfor(i,j,k)は入れ子でないので並びは関係ない
			private void syokiiti() {//autoField
				//main舞台設定時、キャラの初期配置
		
				zeromasu();
				//lastCheck引っかかり、やり直しの時のエラー対策初期化----
				for(int i=0; i < fstPds.length; i++) {
					//Point2D[4] fstPds:キャラ初期位置
					fstPds[i] = null;
					sndPds[i] = null;
				}
				fs = 0;
				//----------------------------
		
				int bkcnt = 0;//無限ループ防止
		
				while( Arrays.asList(fstPds).contains(null) ) {//すべてにnullがなくなるまで
					//random()は0.0 ~ 0.9なので、*10すれば0~9, (int)*4すれば0~3が取れる
					//なので最大値はrow < flRow
					row = (int)(Math.random() * flRow);
					col = (int)(Math.random() * flCol);
					
//						print("A", col, row);
					
					if( !zeroList.contains(new Point2D(col, row))
						&& !Arrays.asList(fstPds).contains(new Point2D(col, row))
						&& fstPds[fs] == null
					) {//ゼロマスでない。他と同じ場所でない

//							print("B", zeroList.contains(new Point2D(col, row)), new Point2D(col, row));

						//初期配置
						fstPds[fs] = new Point2D(col, row);
						sndPds[fs] = new Point2D(col, row);
						field[row][col] = 2;
						
						fs++;
						
					} else {}
		
					bkcnt++;
					if(bkcnt >= flRow * flCol * 50) {//適当
						print("SYOKIITI BREAK 1 ");
						break; 
					}//無限ループ防止保険
				}
		
//					printArr(Arrays.asList(fstPds), "C");
		
				//初期配置を歩けるマスリストに格納
				for(int k=0; k < fstPds.length; k++) {
					walkSet.add(fstPds[k]);
					fstbo[k] = false;//初期化
				}
		
		
				//各初期配置からスタートしnextPos()により適当に歩き回り、別の初期位置まで行く
				//その際、歩けるマスリストwalkSetに格納していく
				//０マスによって孤立しないように
				for(int j=0; j < fstPds.length; j++) {
					bkcnt = 0;
					//現在(sndPds == fstPds)である。
					//nextPos(Point2D)によって(sndPds != fstPds)になるが
					//sndPds[0] == fstPds[max]
					//sndPds[1] == fstPds[0]
					//sndPds[2] == fstPds[1]、となるように処理する
					while(fstbo[j] == false) {//すべてtrueになるまで
						bkcnt++;
						fstPds[j] = nextPos(fstPds[j]);
						//fstPdsの各POSが他のPOSまで行けるように
						if(j == fstPds.length-1) {//fstPds[max]
							if( sndPds[0].equals(fstPds[j]) ) {//sndPds[0].equals(fstPds[max])
								fstbo[j] = true;
							}
						} else if( sndPds[j+1].equals(fstPds[j]) ) {//
							fstbo[j] = true; 
						} else {
							fstbo[j] = false; 
						}

						if(bkcnt >= flRow * flCol * 50) {
							print("SYOKIITI BREAK 2 ");
							break; 
						}//無限ループ防止保険。fstboが全てtrueでなければ別メソッドで繰り返される
					}
				}
			}//syokiiti().end


	private Point2D nextPos(Point2D pd) {//syokiiti
		//walkSet移動可能な場所のリストに加え、次の位置ポイントを返す
		int pc = (int)pd.getX();
		int pr = (int)pd.getY();
		int rdm1 = (int)(Math.random() * 3) - 1;//-1,0,1
		int rdm2 = (int)(Math.random() * 3) - 1;

//			Point2D jikken = pd.add(0, 0);
			
		if(rdm1 != 0) {//row移動
			if( field[ pr + rdm1 ][ pc ] >= 1 ) {//マスの属性が０でない
				pd = pd.add(0, rdm1);
				walkSet.add(pd);
//					print("F1", field[ pr + rdm1 ][ pc ], "[", pr + rdm1, pc, "]");
			}
		} else if(rdm2 != 0) {//col移動。rdm1が０である
			if( field[ pr ][ pc + rdm2 ] >= 1 ) {
				pd = pd.add(rdm2, 0);
				walkSet.add(pd);
//					print("F2", field[ pr ][ pc + rdm2 ], "[", pr, pc + rdm2, "]");
			}
		}

//			print("E", zeroList.contains(jikken), jikken, pd);

		return pd;//rdm1,rdm2共に０なら何も変化なしで引数のpdを返す
	}


	private void zeromasu() {//フィールドの０のマス。zeroListとzeroSize,panel[][]の属性を決める
		zeroList.clear();//このメソッドに含むか迷う

		boolean bool = true;
		for(int i=0; i < flRow; i++) {
			for(int j=0; j < flCol; j++) {
				if(field[i][j] == 0) {
					zeroList.add( new Point2D(j, i) );
					panel[i][j] = 0;
				} else {
					panel[i][j] = 1;
					if(bool) {
						bool = false;
						beginPd = new Point2D(j, i);
					}
					endPd = new Point2D(j, i);
				}
			}
		}

//		groundPrint();
//		zeroKaraPrint(flRow, flCol, zeroList);
//		printArr(zeroList, "D");
		
		zeroSize = zeroList.size();
	}


// ==================================
// ゲッター
	public int[][] getField() {	return field; }
//	public FtcntMapping getFt() { return ft; }
	public static List<Point2D> getZeroList() { return zeroList; }
	public Set<Point2D> getWalkSet() { return walkSet; }
	public Map<Point2D, Integer> getFtcntMap() { return ftcntMap; }
	public int getFlValue(int i, int j) { return field[i][j]; }
	public int getRow() { return flRow; }
	public int getCol() { return flCol; }
	public Point2D getBeginPd() { return beginPd; }
	public Point2D getEndPd() { return endPd; }





	public void panelPrint() {
		//fl.panelをわかりやすくプリント
		System.out.println("=== FL.PANEL by FieldMasu ===");
		for(int i=0; i < panel.length; i++) {
			for(int j=0; j < panel[i].length; j++) {
				//桁数によるずれちょうせい
				if(panel[i][j] < 10 ) {//一桁
					System.out.print("  " + panel[i][j]);
				} else {
					System.out.print(" " + panel[i][j]);
				}
			}
			System.out.println();
		}
		System.out.println("===== FL.PANEL END =====");
	}
	
	public void groundPrint() {
		//fl.panelをわかりやすくプリント
		System.out.println("=== FL.PANEL by FieldMasu ===");
		for(int i=0; i < field.length; i++) {
			for(int j=0; j < field[0].length; j++) {
				//桁数によるずれちょうせい
				if(field[i][j] < 10 ) {//一桁
					System.out.print("  " + field[i][j]);
				} else {
					System.out.print(" " + field[i][j]);
				}
			}
			System.out.println();
		}
		System.out.println("===== FL.PANEL END =====");
	}

	public void zeroKaraPrint(int isize, int jsize, List<Point2D> zeros) {
		System.out.println("=== FL.PANEL by FieldMasu ===");
		for(int i=0; i < isize; i++) {
			for(int j=0; j < jsize; j++) {
				if(zeros.contains(new Point2D(j, i))) {//一桁
					System.out.print("  " + 0);
				} else {
					System.out.print("  " + 1);
				}
			}
			System.out.println();
		}
		System.out.println("=== FL.PANEL by FieldMasu ===");
	}


	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	public void print(Object... objs) {
		qu.print(objs);
	}
	
	public void printField() {
		printArr(Arrays.asList(field), "");
		for(int i = 0; i < flRow; i++) {
			for(int j = 0; j < flCol; j++) {
				System.out.print(field[i][j] + " ");
			}
				System.out.println();
		}
	}

	public void printArr(List arr, String str) {//汎用メソッド
		for(int i=0; i < arr.size(); i++) {
			System.out.println( str +" "+ i +" "+ arr.get(i) );
		}
	}
	public void printArr2(List<Node> arr, String str) {//汎用メソッド
		for(int i=0; i < arr.size()-1; i++) {
			System.out.println( str +" "+ i +" "+ arr.get(i).getId() );
		}
	}

	//=============================================


}




