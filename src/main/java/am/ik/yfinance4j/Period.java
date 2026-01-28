package am.ik.yfinance4j;

/**
 * Represents the time period for historical data queries.
 */
public enum Period {

	ONE_DAY("1d"), FIVE_DAYS("5d"), ONE_MONTH("1mo"), THREE_MONTHS("3mo"), SIX_MONTHS("6mo"), ONE_YEAR("1y"),
	TWO_YEARS("2y"), FIVE_YEARS("5y"), TEN_YEARS("10y"), YEAR_TO_DATE("ytd"), MAX("max");

	private final String value;

	Period(String value) {
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
