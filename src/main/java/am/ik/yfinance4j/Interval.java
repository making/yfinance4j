package am.ik.yfinance4j;

/**
 * Represents the data interval (granularity) for historical data queries.
 */
public enum Interval {

	ONE_MINUTE("1m"), TWO_MINUTES("2m"), FIVE_MINUTES("5m"), FIFTEEN_MINUTES("15m"), THIRTY_MINUTES("30m"),
	SIXTY_MINUTES("60m"), NINETY_MINUTES("90m"), ONE_HOUR("1h"), ONE_DAY("1d"), FIVE_DAYS("5d"), ONE_WEEK("1wk"),
	ONE_MONTH("1mo"), THREE_MONTHS("3mo");

	private final String value;

	Interval(String value) {
		this.value = value;
	}

	/**
	 * Returns the query parameter value used in Yahoo Finance API.
	 * @return the API parameter value
	 */
	public String value() {
		return this.value;
	}

}
