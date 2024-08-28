package strategyGround.event;

import java.io.*;
import java.util.*;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;


import utilCompo.event.Keys;
import utilCompo.event.HandlerFace;

public class KeyHandler implements HandlerFace<KeyEvent> {

	Keys keys;
	
	boolean bool = true;

	public KeyHandler() {
		this.keys = new Keys();
	}

	public void handle(KeyEvent e) {
		keys.keyP(e);
	}
	
	public void handle2(KeyEvent e) {
		keys.keyR(e);
	}
	
	public void handle3(KeyEvent e) {}//null
	public void handle4(KeyEvent e) {}//null
	public void handle5(KeyEvent e) {}//null
}