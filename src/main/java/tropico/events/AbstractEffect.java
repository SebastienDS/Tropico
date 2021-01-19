package tropico.events;

abstract class AbstractEffect implements Effect {

	/**
	 * Necessary field to avoid warning while implementing Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The value of the effect
	 */
	private final int value;

	/**
	 * <b>AbstractEffect's constructor</b>
	 *
	 * Creates an AbstractEffect object, requiring the effect's value.
	 *
	 * @param value        The value of the effect
	 */
	public AbstractEffect(int value) {
		this.value = value;
	}

	/**
	 * Getter for the field value.
	 *
	 * @return The effect's value.
	 */
	@Override
	public int getValue() {
		return value;
	}

	/**
	 *
	 * @return The effect's value
	 */
	int use() {
		return value;
	}

	@Override
	public String toString() {
		String sign = (value > 0) ? "+" : "";
		return sign + value;
	}

}
