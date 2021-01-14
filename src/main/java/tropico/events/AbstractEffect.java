package tropico.events;

abstract class AbstractEffect implements Effect {

	private static final long serialVersionUID = 1L;

	private final int value;

	public AbstractEffect(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}

	int use() {
		return value;
	}

	@Override
	public String toString() {
		String sign = (value > 0) ? "+" : "";
		return sign + value;
	}

}
