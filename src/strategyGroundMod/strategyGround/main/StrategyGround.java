package strategyGround.main;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

//セルフ外部
import utilCompo.quick.QuickUtil;

//セルフ
import strategyGround.ground.GroundManager;
import strategyGround.ground.GroundModel;
import strategyGround.ground.GroundView;
import strategyGround.event.SetOnMona;
import strategyGround.subStage.ControlerStage;

public class StrategyGround extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage stage) throws Exception {
//		GroundModel model = new GroundModel();
		GroundManager gm = new GroundManager();
		gm.action();
//		GroundView gv = new GroundView(model);
//		gv.masuNarabeRect(model.getField());
		
		
		Pane pane = new Pane();
		Scene scene = new Scene(pane, 700, 500);

			SetOnMona som = new SetOnMona(scene, gm);

		stage.setScene(scene);
		stage.show();
		
		new ControlerStage(stage, gm);
		
		pane.getChildren().add(gm.getPane());
	}
	QuickUtil qu = new QuickUtil(this);//サブクラスも大丈夫
	protected void print(Object... objs) { qu.print(objs); }

}
