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

	private final RestClient restClient;

	private final YFinanceUrls urls;

	private volatile @Nullable String cookie;

	private volatile @Nullable String crumb;

	/**
	 * Creates a new CrumbManager with default URLs.
	 * @param restClient the RestClient to use for HTTP calls
	 */
	public CrumbManager(RestClient restClient) {
		this(restClient, YFinanceUrls.DEFAULT);
	}

	/**
	 * Creates a new CrumbManager with the given URLs.
	 * @param restClient the RestClient to use for HTTP calls
	 * @param urls the URLs to use for cookie and crumb retrieval
	 */
	public CrumbManager(RestClient restClient, YFinanceUrls urls) {
		this.restClient = restClient;
		this.urls = urls;
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
		this.cookie = this.restClient.get().uri(this.urls.cookieUrl()).exchange((request, response) -> {
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
		this.crumb = this.restClient.get()
			.uri(this.urls.crumbUrl())
			.header(HttpHeaders.COOKIE, c)
			.retrieve()
			.body(String.class);
		if (this.crumb == null || this.crumb.isEmpty()) {
			throw new YFinanceException("Failed to obtain crumb from Yahoo Finance");
		}
	}

}
