package am.ik.yfinance4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import am.ik.yfinance4j.chart.ChartRequest;
import am.ik.yfinance4j.chart.HistoryRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class TickerHistoryTest {

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
	void shouldReturnHistoryWithDefaultParameters() {
		Ticker ticker = yf.ticker("AAPL");
		List<HistoryRecord> history = ticker.history();
		assertThat(history).isNotEmpty();
		HistoryRecord record = history.get(0);
		assertThat(record.open()).isGreaterThan(BigDecimal.ZERO);
		assertThat(record.high()).isGreaterThan(BigDecimal.ZERO);
		assertThat(record.low()).isGreaterThan(BigDecimal.ZERO);
		assertThat(record.close()).isGreaterThan(BigDecimal.ZERO);
		assertThat(record.volume()).isGreaterThan(0L);
		assertThat(record.timestamp()).isNotNull();
	}

	@Test
	void shouldReturnHistoryForFiveDayPeriod() {
		Ticker ticker = yf.ticker("MSFT");
		List<HistoryRecord> history = ticker
			.history(ChartRequest.builder().period(Period.FIVE_DAYS).interval(Interval.ONE_DAY).build());
		assertThat(history).isNotEmpty();
		assertThat(history.size()).isLessThanOrEqualTo(6);
	}

	@Test
	void shouldReturnHistoryForDateRange() {
		Ticker ticker = yf.ticker("AAPL");
		Instant end = Instant.now();
		Instant start = end.minus(7, ChronoUnit.DAYS);
		List<HistoryRecord> history = ticker
			.history(ChartRequest.builder().start(start).end(end).interval(Interval.ONE_DAY).build());
		assertThat(history).isNotEmpty();
	}

	@Test
	void shouldReturnHistoryForOneYearPeriodWeeklyInterval() {
		Ticker ticker = yf.ticker("AAPL");
		List<HistoryRecord> history = ticker
			.history(ChartRequest.builder().period(Period.ONE_YEAR).interval(Interval.ONE_WEEK).build());
		assertThat(history).isNotEmpty();
		assertThat(history.size()).isGreaterThan(40);
	}

	@Test
	void shouldReturnHistoryForJapaneseStock() {
		Ticker ticker = yf.ticker("7203.T");
		List<HistoryRecord> history = ticker.history();
		assertThat(history).isNotEmpty();
		HistoryRecord record = history.get(0);
		assertThat(record.open()).isGreaterThan(BigDecimal.ZERO);
	}

	@Test
	void shouldReturnAdjustedClose() {
		Ticker ticker = yf.ticker("AAPL");
		List<HistoryRecord> history = ticker.history();
		assertThat(history).isNotEmpty();
		HistoryRecord record = history.get(0);
		assertThat(record.adjClose()).isNotNull();
		assertThat(record.adjClose()).isGreaterThan(BigDecimal.ZERO);
	}

}
