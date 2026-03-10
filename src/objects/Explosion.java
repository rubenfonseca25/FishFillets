package objects;

import pt.iscte.poo.game.Room;

public class Explosion extends GameObject implements Hazard {

	public Explosion(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "explosion";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public Weight getWeight() {
		return Weight.FIXED;
	}

	@Override
	public boolean blocksSmall() {
		return false;
	}

	@Override
	public boolean blocksBig() {
		return false;
	}

	@Override
	public void onCollision(Fish fish) {
		Room room = getRoom();
		if (room != null)
			room.killFish(fish);
	}

}
