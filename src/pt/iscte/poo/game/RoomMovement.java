package pt.iscte.poo.game;
import java.util.ArrayList;
import java.util.List;
import objects.BigFish;
import objects.Fallable;
import objects.Fish;
import objects.GameObject;
import objects.MovableObject;
import objects.Pushable;
import objects.SmallFish;
import objects.Water;
import objects.Weight;
import objects.CrackedWall;
import objects.Cup;
import objects.Krab;
import objects.Bomb;
import objects.Floatable;
import objects.Buoy;
import objects.Wall;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

class RoomMovement {

	private final Room room;

	RoomMovement(Room room) {
		this.room = room;
	}

	//Aplica flutuação a todos os floatables
	public void applyFloating() {
		List<GameObject> floating = new ArrayList<>();
		for (GameObject o : room.getObjects()) {
			if (o instanceof Floatable)
				floating.add(o);
		}

		//Para cada um verificar se pode subir ou deve descer
		for (GameObject obj : floating) {
			Point2D pos = obj.getPosition();
			Point2D above = new Point2D(pos.getX(), pos.getY() - 1);
			Point2D below = new Point2D(pos.getX(), pos.getY() + 1);
			boolean blockedAbove = false;
			boolean shouldSink = false;

			if (room.isInside(above)) {
				for (GameObject o : room.getObjectsAt(above)) {
					if (o instanceof Water || o instanceof Wall || o instanceof CrackedWall || o instanceof Fish)
						continue;
					shouldSink = true;
					break;
				}
				blockedAbove = firstSolidIgnoringWater(room.getObjectsAt(above)) != null;
			}

			//Se deve afundar tenta descer
			if (shouldSink) {
				if (room.isInside(below) && firstSolidIgnoringWater(room.getObjectsAt(below)) == null)
					obj.setPosition(below);
				continue;
			}

			if (!blockedAbove && room.isInside(above))
				obj.setPosition(above);
		}
	}

	//Aplica gravidade a todos os Fallable (um passo de cada vez)
	public void applyGravity() {
		//Identificar apenas os que podem cair
		List<GameObject> falling = new ArrayList<>();
		for (GameObject o : room.getObjects()) {
			if (o instanceof Fallable) {
				falling.add(o);
			}
		}
		//Para cada um verificar suporte sólido
		for (GameObject obj : falling) {
			Point2D pos = obj.getPosition();
			Point2D below = new Point2D(pos.getX(), pos.getY() + 1);
			if (!room.isInside(below))
				continue;
			//Suporte sólido é qualquer coisa que nao seja água (excepto taça atravessa parede rachada)
			List<GameObject> belowObjects = room.getObjectsAt(below);
			boolean hasSolidSupport = false;
			for (GameObject o : belowObjects) {
				if (o instanceof Water)
					continue;
				//A taça e o caranguejo atravessam a parede partida
				if ((obj instanceof Cup || obj instanceof Krab) && o instanceof CrackedWall)
					continue;
				hasSolidSupport = true;
				break;
			}
			//Sem suporte cai uma posição
			if (!hasSolidSupport) {
				//Se for a bomba marcar que está a cair
				if (obj instanceof Bomb)
					((Bomb) obj).markFalling();
				obj.setPosition(below);
			}
			//Se for bomba verificar se deve explodir
			else if (obj instanceof Bomb)
				((Bomb) obj).handleLanding(belowObjects);
		}
	}

	//Verifica se um objecto pode mover-se para uma posição de destino
	public boolean canMove(GameObject mover, Point2D dest) {
		if (mover instanceof Fish && room.hasExited((Fish) mover))
			return false; //Peixe saiu do mapa não pode voltar a mexer
		if (!room.isInside(dest) && mover instanceof Fish) {
			//Notificar saída do peixe
			room.fishExited((Fish) mover);
			return false;
		}
		if (!room.isInside(dest))
			return false;

		//Peixes têm lógica própria
		if (mover instanceof Fish)
			return canFishMoveOrPush((Fish) mover, dest);

		//Para os restantes apenas verifico bloqueios
		for (GameObject o : room.getObjects()) {
			if (o == mover)
				continue;
			if (o.getPosition().equals(dest)) {
				if (mover instanceof Cup && o instanceof CrackedWall)
					continue; // taça atravessa parede rachada
				if (o.blocksSmall() || o.blocksBig())
					return false;
			}
		}
		return true;
	}

	private Direction directionFrom(Point2D from, Point2D to) {
		int dx = to.getX() - from.getX();
		int dy = to.getY() - from.getY();

		if (dx == 1 && dy == 0)
			return Direction.RIGHT;
		if (dx == -1 && dy == 0)
			return Direction.LEFT;
		if (dx == 0 && dy == 1)
			return Direction.DOWN;
		if (dx == 0 && dy == -1)
			return Direction.UP;

		return null;
	}

	//Movimento dos peixes valida direcção, constrói cadeia a empurrar e move se possível
	private boolean canFishMoveOrPush(Fish fish, Point2D dest) {
		Direction dir = directionFrom(fish.getPosition(), dest);

		if (dir == null)
			return false;
		if (!room.isInside(dest))
			return true;

		int dx = dir.asVector().getX();
		int dy = dir.asVector().getY();

		List<GameObject> chainOfObjects = buildPushChain(fish, dest, dir, dx, dy);
		if (chainOfObjects == null)
			return false;

		if (!canFishHandleChain(fish, dir, chainOfObjects))
			return false;

		pushChain(chainOfObjects, dx, dy);
		return true;
	}

	//Constrói a cadeia de objtos a ser empurrados devolve null se alguma regra falhar
	private List<GameObject> buildPushChain(Fish fish, Point2D dest, Direction dir, int dx, int dy) {
		List<GameObject> chain = new ArrayList<>();
		Point2D current = dest;

		while (true) {
			if (!room.isInside(current)) {
				return null;
			}

			//Lista de objectos na posição atual excluindo o player 
			List<GameObject> here = room.getObjectsAt(current);
			here.remove(fish);

			//Procurar o primeiro objecto sólido ignorando água
			GameObject solid = firstSolidIgnoringWater(here);

			//Se não há mais objetos sólidos a empurrar a cadeia acaba aqui
			if (solid == null) {
				break;
			}

			//Tanto o peixe pequeno como a taça podem atravessar parede rachada
			if (solid instanceof CrackedWall) {
				if (chain.isEmpty() && fish instanceof SmallFish)
					break;
				if (canCupCrossCrackedWall(chain, dir))
					break;
				return null;
			}

			//Outro peixe bloqueia
			if (solid instanceof Fish)
				return null;

			//Objecto nao empurravel bloqueia a cadeia
			if (!(solid instanceof MovableObject) || !(solid instanceof Pushable)) {
				if (blocksFish(fish, solid))
					return null;
				break;
			}

			//A boia apenas pode ser empurrada para baixo por peixe grande ou se tiver suporte em cima
			if (solid instanceof Buoy && dir == Direction.DOWN && !canPushBuoyDown(fish, (Buoy) solid))
				return null;

			//Regras especificas do peixe para empurrar o objeto
			if (!fish.canPush(solid, dir) || !((Pushable) solid).canBePushed(dir))
				return null;

			//Acrescentar a cadeia e avancar na direccao correta
			chain.add(solid);
			current = new Point2D(current.getX() + dx, current.getY() + dy);
		}
		return chain;
	}

	//Regras do peixe para validar se o peixe tem força para empurrar a cadeia
	private boolean canFishHandleChain(Fish fish, Direction dir, List<GameObject> chainOfObjects) {
		if (chainOfObjects.isEmpty())
			return true;

		if (fish instanceof SmallFish) {
			if (chainOfObjects.size() > 1)
				return false;
			GameObject obj = chainOfObjects.get(0);
			return obj.getWeight() == Weight.LIGHT || obj.getWeight() == Weight.NOWEIGHT;
		}

		//Big fish não pode empurrar mais do que um objecto na vertical
		if (fish instanceof BigFish)
			return !((dir == Direction.UP || dir == Direction.DOWN) && chainOfObjects.size() > 1);

		return true;
	}

	//Verifica se o peixe pode empurrarr a boia para baixo
	private boolean canPushBuoyDown(Fish fish, Buoy buoy) {
		if (fish instanceof BigFish)
			return true;
		return hasSupportAbove(buoy, fish);
	}

	//Verifica se o objecto tem suporte sólido em cima
	private boolean hasSupportAbove(GameObject obj, Fish ignoreFish) {
		Point2D pos = obj.getPosition();
		if (pos == null)
			return false;
		Point2D above = new Point2D(pos.getX(), pos.getY() - 1);
		if (!room.isInside(above))
			return false;
		List<GameObject> objsAbove = room.getObjectsAt(above);
		for (GameObject o : objsAbove) {
			if (o instanceof Water || o instanceof CrackedWall)
				continue;
			if (o == ignoreFish)
				continue;
			return true;
		}
		return false;
	}

	//Empurrar a cadeia em ordem inversa para evitar sobreposição que me estava a ocorrer de outra forma
	private void pushChain(List<GameObject> chainOfObjects, int dx, int dy) {
		for (int i = chainOfObjects.size() - 1; i >= 0; i--) {
			GameObject o = chainOfObjects.get(i);
			Point2D p = o.getPosition();
			Point2D newPos = new Point2D(p.getX() + dx, p.getY() + dy);
			o.setPosition(newPos);
		}
	}

	//Procurar o primeiro objecto sólido numa lista ignorando água
	private GameObject firstSolidIgnoringWater(List<GameObject> here) {
		for (GameObject o : here) {
			if (o instanceof Water)
				continue;
			return o;
		}
		return null;
	}

	//Verifica se este sólido bloqueia o peixe em questão
	private boolean blocksFish(Fish fish, GameObject solid) {
		if (fish instanceof SmallFish)
			return solid.blocksSmall();
		if (fish instanceof BigFish)
			return solid.blocksBig();
		return solid.blocksSmall() || solid.blocksBig();
	}

	//A chávena pode ser empurrada através de parede rachada
	private boolean canCupCrossCrackedWall(List<GameObject> chain, Direction dir) {
		if (!(dir == Direction.LEFT || dir == Direction.RIGHT))
			return false;
		if (chain.isEmpty())
			return false;
		GameObject last = chain.get(chain.size() - 1); //Se foi a última da cadeia é a que está a ser empurrada contra a parede
		return last instanceof Cup;
	}
}
