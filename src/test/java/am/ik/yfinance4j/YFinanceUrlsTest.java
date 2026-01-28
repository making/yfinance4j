package am.ik.yfinance4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import am.ik.yfinance4j.chart.ChartRequest;
import am.ik.yfinance4j.chart.HistoryRecord;
import am.ik.yfinance4j.mockserver.MockServer;
import am.ik.yfinance4j.mockserver.MockServer.Response;
import am.ik.yfinance4j.quote.QuoteSummaryModule;
import am.ik.yfinance4j.quote.StockInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class YFinanceUrlsTest {

	private static MockServer mockServer;

	private static YFinance yf;

	private static int port;

	@BeforeAll
	static void setUp() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		}
		mockServer = new MockServer(port);
		String baseUrl = "http://localhost:" + port;

		// Cookie endpoint
		mockServer.GET("/cookie",
				request -> Response.builder()
					.status(200)
					.body("")
					.header("Set-Cookie", "test-cookie=abc123; path=/")
					.build());

		// Crumb endpoint
		mockServer.GET("/crumb", request -> Response.ok("test-crumb-value"));

		// Chart endpoint
		mockServer.GET("/v8/finance/chart/AAPL", request -> Response.json("""
				{
				  "chart": {
				    "result": [{
				      "meta": {
				        "currency": "USD",
				        "symbol": "AAPL",
				        "exchangeName": "NMS",
				        "regularMarketPrice": 150.0
				      },
				      "timestamp": [1700000000, 1700086400],
				      "indicators": {
				        "quote": [{
				          "open": [148.0, 149.0],
				          "high": [151.0, 152.0],
				          "low": [147.0, 148.5],
				          "close": [150.0, 151.0],
				          "volume": [1000000, 1200000]
				        }],
				        "adjclose": [{
				          "adjclose": [149.5, 150.5]
				        }]
				      }
				    }],
				    "error": null
				  }
				}
				"""));

		// QuoteSummary endpoint
		mockServer.GET("/v10/finance/quoteSummary/AAPL", request -> Response.json("""
				{
				  "quoteSummary": {
				    "result": [{
				      "price": {
				        "shortName": "Apple Inc.",
				        "currency": "USD",
				        "regularMarketPrice": {"raw": 150.0, "fmt": "150.00"}
				      },
				      "summaryDetail": {
				        "marketCap": {"raw": 2500000000000, "fmt": "2.5T"},
				        "dividendYield": {"raw": 0.005, "fmt": "0.50%"}
				      }
				    }],
				    "error": null
				  }
				}
				"""));

		mockServer.run();

		YFinanceUrls urls = YFinanceUrls.builder()
			.cookieUrl(baseUrl + "/cookie")
			.crumbUrl(baseUrl + "/crumb")
			.chartUrl(baseUrl + "/v8/finance/chart/{ticker}")
			.quoteSummaryUrl(baseUrl + "/v10/finance/quoteSummary/{ticker}")
			.build();

		RestClient restClient = RestClient.builder()
			.requestFactory(new JdkClientHttpRequestFactory())
			.defaultHeader("User-Agent", "Mozilla/5.0")
			.build();

		yf = new YFinance(restClient, urls);
	}

	@AfterAll
	static void tearDown() {
		if (mockServer != null) {
			mockServer.close();
		}
	}

	@Test
	void shouldObtainCookieAndCrumbFromCustomUrls() {
		RestClient restClient = RestClient.builder()
			.requestFactory(new JdkClientHttpRequestFactory())
			.defaultHeader("User-Agent", "Mozilla/5.0")
			.build();

		String baseUrl = "http://localhost:" + port;
		YFinanceUrls urls = YFinanceUrls.builder().cookieUrl(baseUrl + "/cookie").crumbUrl(baseUrl + "/crumb").build();

		CrumbManager crumbManager = new CrumbManager(restClient, urls);
		crumbManager.refresh();

		assertThat(crumbManager.cookie()).contains("test-cookie=abc123");
		assertThat(crumbManager.crumb()).isEqualTo("test-crumb-value");
	}

	@Test
	void shouldFetchChartFromCustomUrl() {
		Ticker ticker = yf.ticker("AAPL");
		List<HistoryRecord> history = ticker.history(ChartRequest.builder().build());

		assertThat(history).hasSize(2);
		assertThat(history.get(0).open()).isEqualByComparingTo("148.0");
		assertThat(history.get(0).close()).isEqualByComparingTo("150.0");
		assertThat(history.get(0).volume()).isEqualTo(1000000L);
		assertThat(history.get(1).open()).isEqualByComparingTo("149.0");
		assertThat(history.get(1).close()).isEqualByComparingTo("151.0");
	}

	@Test
	void shouldFetchQuoteSummaryFromCustomUrl() {
		Ticker ticker = yf.ticker("AAPL");
		StockInfo info = ticker.info(QuoteSummaryModule.PRICE, QuoteSummaryModule.SUMMARY_DETAIL);

		assertThat(info.get("shortName")).hasValue("Apple Inc.");
		assertThat(info.get("currency")).hasValue("USD");
	}

	@Test
	void defaultUrlsShouldMatchExpectedValues() {
		assertThat(YFinanceUrls.DEFAULT.cookieUrl()).isEqualTo("https://fc.yahoo.com");
		assertThat(YFinanceUrls.DEFAULT.crumbUrl()).isEqualTo("https://query1.finance.yahoo.com/v1/test/getcrumb");
		assertThat(YFinanceUrls.DEFAULT.chartUrl())
			.isEqualTo("https://query2.finance.yahoo.com/v8/finance/chart/{ticker}");
		assertThat(YFinanceUrls.DEFAULT.quoteSummaryUrl())
			.isEqualTo("https://query2.finance.yahoo.com/v10/finance/quoteSummary/{ticker}");
	}

	@Test
	void builderShouldOverrideOnlySpecifiedUrls() {
		YFinanceUrls urls = YFinanceUrls.builder().cookieUrl("https://custom.example.com/cookie").build();

		assertThat(urls.cookieUrl()).isEqualTo("https://custom.example.com/cookie");
		assertThat(urls.crumbUrl()).isEqualTo(YFinanceUrls.DEFAULT.crumbUrl());
		assertThat(urls.chartUrl()).isEqualTo(YFinanceUrls.DEFAULT.chartUrl());
		assertThat(urls.quoteSummaryUrl()).isEqualTo(YFinanceUrls.DEFAULT.quoteSummaryUrl());
	}

}
