package objects;

import pt.iscte.poo.game.Room;

public class Blood extends GameObject {

	public Blood(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "blood";
	}

	@Override
	public int getLayer() {
		return 1;
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
}
