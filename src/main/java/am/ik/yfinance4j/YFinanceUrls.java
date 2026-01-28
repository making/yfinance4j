package am.ik.yfinance4j;

/**
 * Holds the URLs used by yfinance4j to communicate with Yahoo Finance. Use
 * {@link #DEFAULT} for the standard Yahoo Finance endpoints, or create a custom instance
 * via {@link #builder()} to override individual URLs.
 *
 * <pre>{@code
 * YFinanceUrls urls = YFinanceUrls.builder()
 *     .cookieUrl("https://my-proxy.example.com")
 *     .build();
 * YFinance yf = new YFinance(restClient, urls);
 * }</pre>
 */
public class YFinanceUrls {

	/**
	 * Default instance using the standard Yahoo Finance endpoints.
	 */
	public static final YFinanceUrls DEFAULT = new YFinanceUrls("https://fc.yahoo.com",
			"https://query1.finance.yahoo.com/v1/test/getcrumb",
			"https://query2.finance.yahoo.com/v8/finance/chart/{ticker}",
			"https://query2.finance.yahoo.com/v10/finance/quoteSummary/{ticker}");

	private final String cookieUrl;

	private final String crumbUrl;

	private final String chartUrl;

	private final String quoteSummaryUrl;

	private YFinanceUrls(String cookieUrl, String crumbUrl, String chartUrl, String quoteSummaryUrl) {
		this.cookieUrl = cookieUrl;
		this.crumbUrl = crumbUrl;
		this.chartUrl = chartUrl;
		this.quoteSummaryUrl = quoteSummaryUrl;
	}

	/**
	 * Returns the URL used to obtain cookies.
	 * @return the cookie URL
	 */
	public String cookieUrl() {
		return this.cookieUrl;
	}

	/**
	 * Returns the URL used to obtain crumbs.
	 * @return the crumb URL
	 */
	public String crumbUrl() {
		return this.crumbUrl;
	}

	/**
	 * Returns the URL template for the chart API.
	 * @return the chart URL
	 */
	public String chartUrl() {
		return this.chartUrl;
	}

	/**
	 * Returns the URL template for the quote summary API.
	 * @return the quote summary URL
	 */
	public String quoteSummaryUrl() {
		return this.quoteSummaryUrl;
	}

	/**
	 * Creates a new builder initialized with the default URLs.
	 * @return a new builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder for {@link YFinanceUrls}. All fields default to the standard Yahoo Finance
	 * endpoints.
	 */
	public static class Builder {

		private String cookieUrl = DEFAULT.cookieUrl;

		private String crumbUrl = DEFAULT.crumbUrl;

		private String chartUrl = DEFAULT.chartUrl;

		private String quoteSummaryUrl = DEFAULT.quoteSummaryUrl;

		/**
		 * Sets the URL used to obtain cookies.
		 * @param cookieUrl the cookie URL
		 * @return this builder
		 */
		public Builder cookieUrl(String cookieUrl) {
			this.cookieUrl = cookieUrl;
			return this;
		}

		/**
		 * Sets the URL used to obtain crumbs.
		 * @param crumbUrl the crumb URL
		 * @return this builder
		 */
		public Builder crumbUrl(String crumbUrl) {
			this.crumbUrl = crumbUrl;
			return this;
		}

		/**
		 * Sets the URL template for the chart API.
		 * @param chartUrl the chart URL
		 * @return this builder
		 */
		public Builder chartUrl(String chartUrl) {
			this.chartUrl = chartUrl;
			return this;
		}

		/**
		 * Sets the URL template for the quote summary API.
		 * @param quoteSummaryUrl the quote summary URL
		 * @return this builder
		 */
		public Builder quoteSummaryUrl(String quoteSummaryUrl) {
			this.quoteSummaryUrl = quoteSummaryUrl;
			return this;
		}

		/**
		 * Builds the {@link YFinanceUrls} instance.
		 * @return a new YFinanceUrls
		 */
		public YFinanceUrls build() {
			return new YFinanceUrls(this.cookieUrl, this.crumbUrl, this.chartUrl, this.quoteSummaryUrl);
		}

	}

}
