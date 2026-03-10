package pt.iscte.poo.game;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import objects.SmallFish;
import objects.BigFish;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;

public class GameEngine implements Observer {
	
	private Map<String,Room> rooms;
	private Room currentRoom;
	private List<String> roomOrder = new ArrayList<>(); //Inicializo aqui pois se inicializar no construtor perco a referencia
	private int currentRoomIndex = 0;
	private int lastTickProcessed = 0;
	private boolean bigMoves = false;
	private int moveCounter = 0;
	private boolean waitingForRestart = false;
	private boolean pendingDeathPrompt = false;
	private boolean gameWon = false;
	private static GameEngine INSTANCE;
	private boolean skipNextTick = false;
	
	private GameEngine() {
		rooms = new HashMap<String,Room>();
		loadGame();
		if (!roomOrder.isEmpty())
			currentRoom = rooms.get(roomOrder.get(0));
		//Inicializa os peixes na sala atual
		if (currentRoom != null) {
			currentRoom.resetRoom();
			skipNextTick = true;
			lastTickProcessed = ImageGUI.getInstance().getTicks();
		}
		updateGUI();
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
	}

	public static GameEngine getInstance() {
		if (INSTANCE == null)
			INSTANCE = new GameEngine();
		return INSTANCE;
	}

	private void loadGame() {
		File[] files = new File("./rooms").listFiles();
		if (files == null)
			return;
		//Função lambda para ordenar os ficheiros por nome (pedido para ser feito no projeto)
		Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
		for(File f : files) {
			rooms.put(f.getName(), Room.readRoom(f, this));
			roomOrder.add(f.getName());
		}
	}

	@Override
	public void update(Observed source) {

		//Fix para caso um peixe já tenha saído e o outro continue a jogar não poder trocar
		if (currentRoom != null) {
			boolean smallOut = currentRoom.hasExited(SmallFish.getInstance());
			boolean bigOut = currentRoom.hasExited(BigFish.getInstance());
			if (smallOut && !bigOut)
				bigMoves = true; //Fica apenas o peixe grande
			else if (bigOut && !smallOut)
				bigMoves = false; //Fica apenas o peixe pequeno
		}

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();
			if (k == KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
			if (k == KeyEvent.VK_R) {
				restartLevel();
				return;
			}
			if (k == KeyEvent.VK_SPACE) {
				boolean smallOut = currentRoom != null && currentRoom.hasExited(SmallFish.getInstance());
				boolean bigOut = currentRoom != null && currentRoom.hasExited(BigFish.getInstance());
				//Só troca se ambos ainda estiverem no jogo
				if (!(smallOut || bigOut))
					bigMoves = !bigMoves;
			}
			//Move os peixes se for uma key de direção
			if (Direction.isDirection(k)) {
				Direction dir = Direction.directionFor(k);
				if (!bigMoves) {
					SmallFish sf = SmallFish.getInstance();
					updateFacing(sf, dir);
					sf.move(dir.asVector());
				}
				else {
					BigFish bf = BigFish.getInstance();
					updateFacing(bf, dir);
					bf.move(dir.asVector());
				}
				currentRoom.moveKrabs();
				moveCounter++;
				currentRoom.evaluateFishSafety();
			}
		}
		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
		}
		ImageGUI.getInstance().update();
		if (pendingDeathPrompt && !waitingForRestart) {
			showDeathPrompt();
		}
		//Atualiza a mensagem da janela com o numero de movimentos e qual dos peixes se está a mover
		ImageGUI.getInstance().setStatusMessage("Moves: " + moveCounter + (bigMoves ? " (Big Fish)" : " (Small Fish)"));
	}

	private void processTick() {	
		//Salta do tick para resolver o problema de os objetos comecarem a cair antes de a GUI mostrar a sala	
		if (skipNextTick) {
			skipNextTick = false;
			lastTickProcessed = ImageGUI.getInstance().getTicks();
			return;
		}
		//Aplica a gravidade, mortes pendentes, explosões, flotação
		if (currentRoom != null) {
			currentRoom.tickExplosions();
			currentRoom.applyFloating();
			currentRoom.applyGravity();
			currentRoom.evaluateFishSafety();
			currentRoom.processPendingDeaths();
		}
		lastTickProcessed++;
	}

	public void updateGUI() {
		if(currentRoom!=null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}

	//Notifica a engine que um peixe morreu
	public void onFishDeath() {
		pendingDeathPrompt = true;
	}

	//Notifica a engine que o jogador ganhou
	public void onWin() {
		if (gameWon)
			return;
		advanceOrFinish();
	}

	//Avança para a próxima sala ou termina o jogo
	private void advanceOrFinish() {
		if (currentRoomIndex < roomOrder.size() - 1) {
			currentRoomIndex++;
			String nextName = roomOrder.get(currentRoomIndex);
			currentRoom = rooms.get(nextName);
			if (currentRoom != null) {
				currentRoom.resetRoom();
				SmallFish.getInstance().setRoom(currentRoom);
				BigFish.getInstance().setRoom(currentRoom);
				skipNextTick = true;
				lastTickProcessed = ImageGUI.getInstance().getTicks();
				pendingDeathPrompt = false;
				waitingForRestart = false;
				updateGUI();
			}
		}
		else {
			gameWon = true;
			//Não sabia como tratar do nome do jogador pedi ao chatGPT que me arranjou esta solução não sei se é o mais correto
			//mas mais uma vez são funcionalidades da GUI que não sei fazer
			String name = JOptionPane.showInputDialog(null, "Enter your name for the leaderboard:", "Victory", JOptionPane.PLAIN_MESSAGE);
			Leaderboard.saveScore(name, moveCounter);
			ImageGUI.getInstance().showMessage("Victory", "You escaped!!");
			System.exit(0);
		}
	}

	//Reinicia o nível atual
	private void restartLevel() {
		currentRoom.resetRoom();
		updateGUI();
		skipNextTick = true;
		lastTickProcessed = ImageGUI.getInstance().getTicks();
		pendingDeathPrompt = false;
		waitingForRestart = false;
		gameWon = false;
	}

	//ChatGPT, não fazia a menor ideia como fazer isto já que são opções da GUI
	private void showDeathPrompt() {
		waitingForRestart = true;
		pendingDeathPrompt = false;
		int choice = JOptionPane.showConfirmDialog(null,
				"You are going to swim with the fishes, best of luck on the next life",
				"Fish dead",
				JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.YES_OPTION)
			restartLevel();
		waitingForRestart = false;
	}

	//Atualiza a direcção do peixe pequeno (um bom exemplo de polimorfismo caso seja necessário)
	private void updateFacing(SmallFish fish, Direction dir) {
		if (dir == Direction.LEFT)
			fish.setFacingRight(false);
		else if (dir == Direction.RIGHT)
			fish.setFacingRight(true);
	}

	//Atualiza a direcção do peixe grande (um bom exemplo de polimorfismo caso seja necessário)
	private void updateFacing(BigFish fish, Direction dir) {
		if (dir == Direction.LEFT)
			fish.setFacingRight(false);
		else if (dir == Direction.RIGHT)
			fish.setFacingRight(true);
	}
	
}
