package am.ik.yfinance4j;

import am.ik.yfinance4j.quote.StockInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TickerInfoTest {

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
	void shouldReturnInfoForApple() {
		Ticker ticker = yf.ticker("AAPL");
		StockInfo info = ticker.info();
		assertThat(info.shortName()).isPresent();
		assertThat(info.symbol()).isPresent().hasValue("AAPL");
		assertThat(info.currency()).isPresent().hasValue("USD");
		assertThat(info.currentPrice()).isPresent();
		assertThat(info.raw()).isNotEmpty();
	}

	@Test
	void shouldReturnInfoForJapaneseStock() {
		Ticker ticker = yf.ticker("7203.T");
		StockInfo info = ticker.info();
		assertThat(info.shortName()).isPresent();
		assertThat(info.currency()).isPresent().hasValue("JPY");
	}

	@Test
	void shouldReturnSectorAndIndustry() {
		Ticker ticker = yf.ticker("AAPL");
		StockInfo info = ticker.info();
		assertThat(info.sector()).isPresent();
		assertThat(info.industry()).isPresent();
	}

	@Test
	void shouldThrowForInvalidTicker() {
		Ticker ticker = yf.ticker("INVALIDTICKER12345");
		assertThatThrownBy(ticker::info).isInstanceOf(Exception.class);
	}

}
