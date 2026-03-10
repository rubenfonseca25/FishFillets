package pt.iscte.poo.game;

import objects.SteelVertical;
import objects.Cup;
import objects.Rock;
import objects.Anchor;
import objects.Bomb;
import objects.Explosion;
import objects.Trap;
import objects.Log;
import objects.CrackedWall;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import objects.Water;
import objects.BigFish;
import objects.GameObject;
import objects.SmallFish;
import objects.Wall;
import objects.SteelHorizontal;
import objects.Krab;
import pt.iscte.poo.utils.Point2D;
import objects.Fish;

public class Room {
	
	private List<GameObject> objects;
	private String roomName;
	private GameEngine engine;
    private Point2D smallFishStartingPosition;
    private Point2D bigFishStartingPosition;
    private final RoomMovement movement;
    private final RoomDeath death;
	private boolean smallFishExited = false;
	private boolean bigFishExited = false;
	
    public Room() {
        objects = new ArrayList<GameObject>();
        movement = new RoomMovement(this);
        death = new RoomDeath(this);
    }

	private void setName(String name) {
		roomName = name;
	}
	
	public String getName() {
		return roomName;
	}

	public GameEngine getEngine() {
		return engine;
	}
	
	private void setEngine(GameEngine engine) {
		this.engine = engine;
	}

	public void addObject(GameObject obj) {
		objects.add(obj);
		engine.updateGUI();
	}
	
	public void removeObject(GameObject obj) {
		objects.remove(obj);
		engine.updateGUI();
	}
	
	public List<GameObject> getObjects() {
		return objects;
	}

	public void setSmallFishStartingPosition(Point2D heroStartingPosition) {
		this.smallFishStartingPosition = heroStartingPosition;
	}
	
	public Point2D getSmallFishStartingPosition() {
		return smallFishStartingPosition;
	}
	
	public void setBigFishStartingPosition(Point2D heroStartingPosition) {
		this.bigFishStartingPosition = heroStartingPosition;
	}
	
	public Point2D getBigFishStartingPosition() {
		return bigFishStartingPosition;
	}

	//Aplica flutuação a todos os floatables
	public void applyFloating() {
		movement.applyFloating();
	}

	//Aplica a gravidade a todos os objectos que podem cair
	public void applyGravity() {
		movement.applyGravity();
    }

	//Processa mortes pendentes por esmagamento
	public void processPendingDeaths() {
		death.processPendingDeaths();
	}

	//Move NPCs como o caranguejo
	public void moveKrabs() {
		List<GameObject> krabs = new ArrayList<>();

		for (GameObject o : objects) {
			if (o instanceof Krab)
				krabs.add(o);
		}
		for (GameObject k : krabs)
			((Krab) k).tickMove();
	}

	//Verifica se a posição de um determinado objeto está dentro dos limites do mapa
	public boolean isInside(Point2D p) {
		int x = p.getX();
		int y = p.getY();
		return x >= 0 && x < 10 && y >= 0 && y < 10;
	}

	//Devolve uma lista de objetos que estão na posição p
	public List<GameObject> getObjectsAt(Point2D p) {
		List<GameObject> result = new ArrayList<>();
		for (GameObject o : objects) {
			if (o.getPosition().equals(p)) {
				result.add(o);
			}
		}
		return result;
	}

	//Função que verifica se um objecto pode mover-se para uma posição de destino
	public boolean canMove(GameObject mover, Point2D dest) {
        return movement.canMove(mover, dest);
    }

	//Verifica se um peixe já saiu do mapa
	public boolean hasExited(Fish fish) {
		if (fish instanceof SmallFish)
			return smallFishExited;
		if (fish instanceof BigFish)
			return bigFishExited;
		return false;
	}

	//Notifica a sala que um peixe saiu do mapa
	public void fishExited(Fish fish) {
		if (fish instanceof SmallFish)
			smallFishExited = true;
		else if (fish instanceof BigFish)
			bigFishExited = true;
		objects.remove(fish);
		engine.updateGUI();
		checkWin();
	}

	//Verifica se o jogador ganhou
	private void checkWin() {
		if (smallFishExited && bigFishExited)
			engine.onWin();
	}

	//Leitura do mapa a partir do ficheiro
	public static Room readRoom(File f, GameEngine engine) {
		Room r = new Room();
		r.setEngine(engine);
		r.setName(f.getName());

		//Ler o ficheiro da sala.
		List<String> lines = new ArrayList<String>();
		int maxCols = 0;

		try (Scanner sc = new Scanner(f)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				lines.add(line);
				if (line.length() > maxCols)
					maxCols = line.length();
			}
		} catch (Exception e) {
			System.err.println("Error reading room file " + f + ": " + e.getMessage());
			return r;
		}

		//Carregar o mapa a partir das linhas lidas do ficheiro
		int rows = lines.size();
		int cols = maxCols;
		for (int y = 0; y < rows; y++) {
			String line = lines.get(y);
			for (int x = 0; x < cols; x++) {

				char ch = ' ';
				if (x < line.length())
					ch = line.charAt(x);

				//Adicinnar agua a todas as tiles
				GameObject water = new Water(r);
				water.setPosition(new Point2D(x, y));
				r.addObject(water);

				switch (ch) {
				case 'B': //Peixe grande
					GameObject bf = BigFish.getInstance();
					bf.setRoom(r);
					bf.setPosition(new Point2D(x, y));
					r.addObject(bf);
					r.setBigFishStartingPosition(new Point2D(x, y));
					break;
				case 'S': //Peixe pequeno
					GameObject sf = SmallFish.getInstance();
					sf.setRoom(r);
					sf.setPosition(new Point2D(x, y));
					r.addObject(sf);
					r.setSmallFishStartingPosition(new Point2D(x, y));
					break;
				case 'W': //Paredes
					GameObject wall = new Wall(r);
					wall.setPosition(new Point2D(x, y));
					r.addObject(wall);
					break;
				case 'H': //Pipe horizontal
					GameObject sh = new SteelHorizontal(r);
					sh.setPosition(new Point2D(x, y));
					r.addObject(sh);
					break;
				case 'V': //Pipe vertical
					GameObject sv = new SteelVertical(r);
					sv.setPosition(new Point2D(x, y));
					r.addObject(sv);
					break;
				case 'C': //Taça
					GameObject cup = new Cup(r);
					cup.setPosition(new Point2D(x, y));
					r.addObject(cup);
					break;
				case 'R': //Pedra
					GameObject rock = new Rock(r);
					rock.setPosition(new Point2D(x, y));
					r.addObject(rock);
					break;
				case 'A': //Ancora
					GameObject anchor = new Anchor(r);
					anchor.setPosition(new Point2D(x, y));
					r.addObject(anchor);
					break;
				case 'b': //bomba
					GameObject bomb = new Bomb(r);
					bomb.setPosition(new Point2D(x, y));
					r.addObject(bomb);
					break;
				case 'T': //Armadilha
					GameObject trap = new Trap(r);
					trap.setPosition(new Point2D(x, y));
					r.addObject(trap);
					break;
				case 'Y': //Tronco
					GameObject log = new Log(r);
					log.setPosition(new Point2D(x, y));
					r.addObject(log);
					break;
				case 'X': //Parede partida
					GameObject crackedWall = new CrackedWall(r);
					crackedWall.setPosition(new Point2D(x, y));
					r.addObject(crackedWall);
					break;
				case 'K': //Krab
					GameObject krab = new Krab(r);
					krab.setPosition(new Point2D(x, y));
					r.addObject(krab);
					break;
				case 'F': //Boia
					GameObject buoy = new objects.Buoy(r);
					buoy.setPosition(new Point2D(x, y));
					r.addObject(buoy);
					break;
				default: //resto
					break;
				}
			}
		}

		return r;
	}

	//Reinicia a sala para o estado inicial
	public void resetRoom() {
		objects.clear();
		Room newRoom = readRoom(new File("./rooms/" + roomName), engine);
		this.objects = newRoom.getObjects();
		this.smallFishStartingPosition = newRoom.getSmallFishStartingPosition();
		this.bigFishStartingPosition = newRoom.getBigFishStartingPosition();
		SmallFish.getInstance().setRoom(this);
		BigFish.getInstance().setRoom(this);
		death.resetTimers();
		//Novas flags de saída
		smallFishExited = false;
		bigFishExited = false;
	}

	//Avança o tempo das explosões e remove-as quando expirarem
	public void tickExplosions() {
		List<GameObject> expired = new ArrayList<>();
		for (GameObject o : new ArrayList<>(objects)) {
			if (o instanceof Explosion)
				expired.add(o);
		}
		//Remover explosões que "expiraram"
		for (GameObject o : expired)
			removeObject(o);
	}

	//Avalia se o peixe vai ou não morrer
	public void evaluateFishSafety() {
		death.evaluateFishSafety();
	}

	//Mata um peixe
	public void killFish(Fish fish) {
		death.killFish(fish);
	}
	
}
