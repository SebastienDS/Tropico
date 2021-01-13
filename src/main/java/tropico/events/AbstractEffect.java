package tropico.events;

abstract class AbstractEffect implements Effect {
	private final int value;
	private boolean used = false;

	public AbstractEffect(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}

	int use() {
		if (used) {
			throw new IllegalStateException("L'effet a déjà été utilisé.");
		}
		used = true;

		return value;
	}

	@Override
	public String toString() {
		String sign = (value > 0) ? "+" : "";
		return sign + value;
	}

}
