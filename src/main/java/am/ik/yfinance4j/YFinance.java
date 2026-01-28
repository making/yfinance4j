package am.ik.yfinance4j;

import org.springframework.web.client.RestClient;

/**
 * Entry point for the yfinance4j library. Create an instance with a {@link RestClient}
 * and use it to create {@link Ticker} instances.
 *
 * <pre>{@code
 * RestClient restClient = RestClient.builder()
 *     .defaultHeader("User-Agent", "Mozilla/5.0")
 *     .build();
 * YFinance yf = new YFinance(restClient);
 * Ticker ticker = yf.ticker("AAPL");
 * List<HistoryRecord> history = ticker.history();
 * }</pre>
 */
public class YFinance {

	private final RestClient restClient;

	private final CrumbManager crumbManager;

	/**
	 * Creates a new YFinance instance with the given RestClient.
	 * @param restClient the RestClient to use for HTTP calls (must have appropriate
	 * User-Agent and message converters configured)
	 */
	public YFinance(RestClient restClient) {
		this(restClient, new CrumbManager(restClient));
	}

	/**
	 * Creates a new YFinance instance with the given RestClient and CrumbManager.
	 * @param restClient the RestClient to use for HTTP calls
	 * @param crumbManager the CrumbManager to use for authentication
	 */
	public YFinance(RestClient restClient, CrumbManager crumbManager) {
		this.restClient = restClient;
		this.crumbManager = crumbManager;
	}

	/**
	 * Creates a {@link Ticker} for the given symbol.
	 * @param symbol the ticker symbol (e.g. "AAPL", "MSFT", "7203.T")
	 * @return the ticker instance
	 */
	public Ticker ticker(String symbol) {
		return new Ticker(symbol, this.restClient, this.crumbManager);
	}

}
