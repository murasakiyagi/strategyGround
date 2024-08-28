package strategyGround.event;

import java.io.*;
import java.util.*;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.event.*;
import javafx.scene.Camera;

import utilCompo.event.SceneSetOn;
import utilCompo.event.Keys;

//import event.KeyHandler;
//import event.MouseHandler;
//import event.ScrollHandler;
import strategyGround.ground.GroundManager;

public class SetOnMona extends SceneSetOn {
	
	public SetOnMona(Scene scene, GroundManager gm) {
		super(
			scene, 
			new Keys(),
			new KeyHandler(),
			new MouseHandler(gm),
			new ScrollHandler()
		);
// 		super.setOn();
		System.out.println("SET ON MONA");
	}



}




