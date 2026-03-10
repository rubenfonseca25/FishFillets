package pt.iscte.poo.game;

import java.util.List;
import objects.BigFish;
import objects.Blood;
import objects.Fish;
import objects.GameObject;
import objects.Hazard;
import objects.MovableObject;
import objects.SmallFish;
import objects.Water;
import pt.iscte.poo.utils.Point2D;

class RoomDeath {

	private final Room room;
	//Timer para esmagamento do peixe, -1 pois não está a ser esmagado, ainda :)
	private int smallFishCrushTimer = -1;
	private int bigFishCrushTimer = -1;
	//Delay antes de o peixe morrer esmagado caso contrário morria logo (usei static final para ter um exemplo no projeto)
	private static final int DELAY_TICK_MORTE = 1;

	public RoomDeath(Room room) {
		this.room = room;
	}

	//Reinicia os timers de esmagamento quando a room for resetada
	public void resetTimers() {
		smallFishCrushTimer = -1;
		bigFishCrushTimer = -1;
	}

	//Avalia se algum peixe deve morrer
	public void evaluateFishSafety() {
		checkFishSafety(SmallFish.getInstance());
		checkFishSafety(BigFish.getInstance());
	}

	//Processa mortes pendentes por esmagamento
	public void processPendingDeaths() {
		smallFishCrushTimer = tickCrushTimer(smallFishCrushTimer, SmallFish.getInstance());
		bigFishCrushTimer = tickCrushTimer(bigFishCrushTimer, BigFish.getInstance());
	}

	//Mata um peixe
	public void killFish(Fish fish) {
		if (fish == null)
			return;
		clearCrush(fish);
		room.getObjects().remove(fish);
		Blood blood = new Blood(room);
		blood.setPosition(fish.getPosition());
		room.getObjects().add(blood);
		room.getEngine().updateGUI();
		room.getEngine().onFishDeath();
	}

	//Avalia se o peixe vai ou não morrer
	private void checkFishSafety(Fish fish) {
		if (fish == null)
			return;
		Point2D pos = fish.getPosition();
		if (pos == null)
			return;

		handleHazards(fish, pos);
		checkSupportLimits(fish, pos);
	}

	//Verifica se o peixe está em contacto com alguma hazard
	private void handleHazards(Fish fish, Point2D pos) {
		List<GameObject> occupants = room.getObjectsAt(pos);
		for (GameObject o : occupants) {
			if (o == fish)
				continue;
			if (o instanceof Hazard)
				((Hazard) o).onCollision(fish);
		}
	}

	//Verifica se o peixe está a ser esmagado por objectos em cima dele
	private void checkSupportLimits(Fish fish, Point2D pos) {
		fish.clearSupport();

		for (int y = pos.getY() - 1; y >= 0; y--) {
			Point2D above = new Point2D(pos.getX(), y);
			GameObject solidAbove = firstSolidObject(above);
			if (solidAbove == null)
				break;
			if (!(solidAbove instanceof MovableObject))
				break;
			if (!fish.canSupport(solidAbove)) {
				scheduleCrush(fish);
				return;
			} 
			else
				clearCrush(fish);
			fish.addSupportedObject(solidAbove);
		}
	}

	//Devolve o primeiro objecto sólido na posição p
	private GameObject firstSolidObject(Point2D p) {
		List<GameObject> here = room.getObjectsAt(p);
		for (GameObject o : here) {
			if (o instanceof Water)
				continue;
			return o;
		}
		return null;
	}

	//Actualiza o timer de esmagamento
	private int tickCrushTimer(int timer, Fish fish) {
		if (timer < 0)
			return timer;
		int next = timer - 1;
		if (next <= 0) {
			killFish(fish);
			return -1;
		}
		return next;
	}

	//Agenda o esmagamento do peixe para não ser instantaneamente
	private void scheduleCrush(Fish fish) {
		if (fish instanceof SmallFish)
			smallFishCrushTimer = smallFishCrushTimer < 0 ? DELAY_TICK_MORTE : smallFishCrushTimer;
		else if (fish instanceof BigFish)
			bigFishCrushTimer = bigFishCrushTimer < 0 ? DELAY_TICK_MORTE : bigFishCrushTimer;
	}

	//Limpa o timer de esmagamento caso o peixe já não esteja a ser esmagado
	private void clearCrush(Fish fish) {
		if (fish instanceof SmallFish)
			smallFishCrushTimer = -1;
		else if (fish instanceof BigFish)
			bigFishCrushTimer = -1;
	}
}
