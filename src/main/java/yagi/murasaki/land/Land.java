package yagi.murasaki.land;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

//セルフ外部
import yagi.murasaki.utilCompo.quick.QuickUtil;

//セルフ
import yagi.murasaki.land.ground.GroundManager;
import yagi.murasaki.land.ground.GroundModel;
import yagi.murasaki.land.ground.GroundView;
import yagi.murasaki.land.event.SetOnMona;
import yagi.murasaki.land.subStage.ControlerStage;

//import yagi.murasaki.land.ground.masu.Masu;
//import yagi.murasaki.land.ground.masu.MasuA;
//import yagi.murasaki.land.ground.masu.MasuB;
////import yagi.murasaki.land.ground.masu.MasuBC;
//import yagi.murasaki.land.ground.masu.MasuColony;
import yagi.murasaki.utilCompo.quick.Att;
import yagi.murasaki.utilCompo.quick.AttA;
import java.awt.Point;
import yagi.murasaki.utilCompo.geometry.PointCustom;


public class Land extends Application {

	static Land KARI = new Land();

	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage stage) throws Exception {
//		GroundModel model = new GroundModel();
		GroundManager gm = new GroundManager();
		gm.init();
		gm.moverCreate();

//		GroundView gv = new GroundView(model);
//		gv.masuNarabeRect(model.getField());
		
		
		Pane pane = new Pane();
		Scene scene = new Scene(pane, 700, 500);

			SetOnMona som = new SetOnMona(scene, gm);

		stage.setScene(scene);
		stage.show();
		
		new ControlerStage(stage, gm);
		
		pane.getChildren().add(gm.getPane());
		
		
			jikken();
	}
	
	static void jikken() {
//		KARI.print("\n--- JIKKEN ---\n");
//		int[][] ground = {
//			{1,0,0},
//			{0,1,0},
//			{0,0,1}
//		};
//		MasuColony<MasuA> mma = new MasuColony<>();
//		MasuA.create(ground, mma);
//		KARI.print(mma.getMasu(new Point2D(0,0)));
//		KARI.print(mma.getAtt(new Point2D(0,0)).getNum());
//		KARI.print(mma.getAtt(new Point2D(0,1)).getNum());
//		KARI.print(mma.getAtt(new Point2D(1,1)).getNum());
	}
	
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	protected void print(Object... objs) { qu.print(objs); }

}
