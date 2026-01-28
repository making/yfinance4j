package am.ik.yfinance4j;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

/**
 * Manages Yahoo Finance cookie and crumb authentication. The crumb is required for
 * authenticated API calls.
 */
public class CrumbManager {

	private static final Logger log = LoggerFactory.getLogger(CrumbManager.class);

	private static final String COOKIE_URL = "https://fc.yahoo.com";

	private static final String CRUMB_URL = "https://query1.finance.yahoo.com/v1/test/getcrumb";

	private final RestClient restClient;

	private volatile @Nullable String cookie;

	private volatile @Nullable String crumb;

	public CrumbManager(RestClient restClient) {
		this.restClient = restClient;
	}

	/**
	 * Returns the current cookie, refreshing if necessary.
	 * @return the cookie string
	 */
	public String cookie() {
		String c = this.cookie;
		if (c == null) {
			refresh();
			c = this.cookie;
		}
		if (c == null) {
			throw new YFinanceException("Failed to obtain cookie from Yahoo Finance");
		}
		return c;
	}

	/**
	 * Returns the current crumb, refreshing if necessary.
	 * @return the crumb string
	 */
	public String crumb() {
		String c = this.crumb;
		if (c == null) {
			refresh();
			c = this.crumb;
		}
		if (c == null) {
			throw new YFinanceException("Failed to obtain crumb from Yahoo Finance");
		}
		return c;
	}

	/**
	 * Refreshes the cookie and crumb by making requests to Yahoo Finance.
	 */
	public synchronized void refresh() {
		log.debug("Refreshing cookie and crumb");
		fetchCookie();
		fetchCrumb();
		log.debug("Cookie and crumb refreshed successfully");
	}

	private void fetchCookie() {
		this.cookie = this.restClient.get().uri(COOKIE_URL).exchange((request, response) -> {
			String setCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
			if (setCookie == null || setCookie.isEmpty()) {
				throw new YFinanceException("Failed to obtain cookie from Yahoo Finance");
			}
			return setCookie;
		});
	}

	private void fetchCrumb() {
		String c = this.cookie;
		if (c == null) {
			throw new YFinanceException("Cookie must be obtained before fetching crumb");
		}
		this.crumb = this.restClient.get().uri(CRUMB_URL).header(HttpHeaders.COOKIE, c).retrieve().body(String.class);
		if (this.crumb == null || this.crumb.isEmpty()) {
			throw new YFinanceException("Failed to obtain crumb from Yahoo Finance");
		}
	}

}
