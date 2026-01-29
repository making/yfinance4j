package am.ik.yfinance4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import am.ik.yfinance4j.chart.ChartRequest;
import am.ik.yfinance4j.chart.HistoryRecord;
import am.ik.yfinance4j.quote.QuoteSummaryModule;
import am.ik.yfinance4j.quote.StockInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests that verify the code examples in README.md actually work.
 */
class ReadmeExampleTest {

	private static YFinance yf;

	@BeforeAll
	static void setUp() {
		RestClient restClient = RestClient.builder()
			.requestFactory(new JdkClientHttpRequestFactory())
			.defaultHeader("User-Agent", "Mozilla/5.0")
			.build();
		yf = new YFinance(restClient);
	}

	@Test
	void quickStartExample() {
		Ticker ticker = yf.ticker("AAPL");
		List<HistoryRecord> history = ticker.history();
		assertThat(history).isNotEmpty();
		HistoryRecord record = history.get(0);
		// Verify the printf pattern from README compiles and works
		String formatted = String.format("%s  O:%.2f  H:%.2f  L:%.2f  C:%.2f  V:%d", record.timestamp(), record.open(),
				record.high(), record.low(), record.close(), record.volume());
		assertThat(formatted).isNotEmpty();
	}

	@Test
	void chartRequestBuilderExamples() {
		Ticker ticker = yf.ticker("MSFT");

		// 1 year of weekly data
		List<HistoryRecord> weekly = ticker
			.history(ChartRequest.builder().period(Period.ONE_YEAR).interval(Interval.ONE_WEEK).build());
		assertThat(weekly).isNotEmpty();

		// pre/post market, no actions
		List<HistoryRecord> prePost = ticker.history(ChartRequest.builder()
			.period(Period.FIVE_DAYS)
			.interval(Interval.FIVE_MINUTES)
			.prePost(true)
			.actions(false)
			.build());
		assertThat(prePost).isNotEmpty();
	}

	@Test
	void stockInfoExample() {
		StockInfo info = yf.ticker("AAPL").info();

		// Optional-returning methods
		assertThat(info.shortName()).isPresent();
		assertThat(info.currentPrice()).isPresent();
		assertThat(info.sector()).isPresent();
		assertThat(info.industry()).isPresent();
		assertThat(info.marketCap()).isPresent();
		assertThat(info.regularMarketPrice()).isPresent();

		// Nullable-returning methods
		String name = info.shortNameNullable();
		assertThat(name).isNotNull();
		BigDecimal price = info.currentPriceNullable();
		assertThat(price).isNotNull();
		BigDecimal regularMarketPrice = info.regularMarketPriceNullable();
		assertThat(regularMarketPrice).isNotNull();

		// Access by key (returns Optional<Object>)
		assertThat(info.get("trailingPE")).isPresent();

		// Access with type-safe methods
		assertThat(info.getString("shortName")).isPresent();
		assertThat(info.getNumber("trailingPE")).isPresent();

		// Raw map
		Map<String, Object> raw = info.raw();
		assertThat(raw).isNotEmpty();
	}

	@Test
	void specificModulesExample() {
		Ticker ticker = yf.ticker("AAPL");
		StockInfo info = ticker.info(QuoteSummaryModule.PRICE, QuoteSummaryModule.FINANCIAL_DATA);
		assertThat(info.raw()).isNotEmpty();
	}

	@Test
	void japaneseStockExample() {
		Ticker toyota = yf.ticker("7203.T");
		List<HistoryRecord> history = toyota.history();
		assertThat(history).isNotEmpty();
		StockInfo info = toyota.info();
		assertThat(info.currency()).hasValue("JPY");
	}

	@Test
	void crumbManagerExample() {
		RestClient restClient = RestClient.builder()
			.requestFactory(new JdkClientHttpRequestFactory())
			.defaultHeader("User-Agent", "Mozilla/5.0")
			.build();
		CrumbManager crumbManager = new CrumbManager(restClient);
		crumbManager.refresh();

		YFinance yf = new YFinance(restClient, crumbManager);
		assertThat(yf.ticker("AAPL").history()).isNotEmpty();
	}

	@Test
	void customUrlsExampleCompiles() {
		// This test only verifies the README example compiles correctly.
		// Actual URL substitution is tested in YFinanceUrlsTest with MockServer.
		YFinanceUrls urls = YFinanceUrls.builder()
			.cookieUrl("https://my-proxy.example.com/cookie")
			.crumbUrl("https://my-proxy.example.com/crumb")
			.chartUrl("https://my-proxy.example.com/v8/finance/chart/{ticker}")
			.quoteSummaryUrl("https://my-proxy.example.com/v10/finance/quoteSummary/{ticker}")
			.build();
		assertThat(urls.cookieUrl()).isEqualTo("https://my-proxy.example.com/cookie");

		// Partial override example
		YFinanceUrls partial = YFinanceUrls.builder().cookieUrl("https://my-proxy.example.com/cookie").build();
		assertThat(partial.cookieUrl()).isEqualTo("https://my-proxy.example.com/cookie");
		assertThat(partial.crumbUrl()).isEqualTo(YFinanceUrls.DEFAULT.crumbUrl());
	}

	@Test
	void errorHandlingExample() {
		assertThatThrownBy(() -> yf.ticker("INVALIDTICKER12345").info()).isInstanceOf(Exception.class);
	}

}
