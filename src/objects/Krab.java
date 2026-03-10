package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Krab extends GameCharacter implements Fallable {

	//Direita 1 Esquerda -1
	private int dx = 1;

	public Krab(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "krab";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public Weight getWeight() {
		return Weight.HEAVY;
	}

	@Override
	public boolean blocksSmall() {
		return true;
	}

	@Override
	public boolean blocksBig() {
		return true;
	}

	public void tickMove() {
		Room room = getRoom();
		if (room == null)
			return;

		Point2D current = getPosition();
		krabStep(room, current, dx);
	}

    //Tenta andar na direção dx se não conseguir muda de direção e tenta andar
	private void krabStep(Room room, Point2D current, int stepDx) {
		for (int attempt = 0; attempt < 2; attempt++) {
			Point2D target = new Point2D(current.getX() + stepDx, current.getY());
			if (handleKrabCollision(room, target))
				return;
			if (room.canMove(this, target)) {
				setPosition(target);
				return;
			}
			//Inverter direção e tentar de novo
			stepDx = -stepDx;
			dx = stepDx;
		}
	}

    //Trata as colisões com outros objetos
	private boolean handleKrabCollision(Room room, Point2D target) {
		for (GameObject o : room.getObjectsAt(target)) {
			if (o instanceof Trap || o instanceof BigFish) {
				room.removeObject(this);
				return true;
			}
			if (o instanceof SmallFish) {
				room.killFish((SmallFish) o);
				setPosition(target);
				return true;
			}
		}
		return false;
	}
}
