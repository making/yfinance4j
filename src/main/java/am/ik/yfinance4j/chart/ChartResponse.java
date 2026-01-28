package am.ik.yfinance4j.chart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

/**
 * JSON mapping for the Yahoo Finance v8 chart API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChartResponse(Chart chart) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Chart(@Nullable List<Result> result, @Nullable ChartError error) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Result(Meta meta, @Nullable List<Long> timestamp, Indicators indicators, @Nullable Events events) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Meta(@Nullable String currency, String symbol, @Nullable String exchangeName,
			@Nullable String fullExchangeName, @Nullable String instrumentType, @Nullable Long firstTradeDate,
			@Nullable Long regularMarketTime, @Nullable Integer gmtoffset, @Nullable String timezone,
			@Nullable String exchangeTimezoneName, @Nullable BigDecimal regularMarketPrice,
			@Nullable BigDecimal previousClose) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Indicators(List<Quote> quote, @JsonProperty("adjclose") @Nullable List<AdjClose> adjclose) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Quote(@Nullable List<@Nullable BigDecimal> open, @Nullable List<@Nullable BigDecimal> high,
			@Nullable List<@Nullable BigDecimal> low, @Nullable List<@Nullable BigDecimal> close,
			@Nullable List<@Nullable Long> volume) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record AdjClose(@JsonProperty("adjclose") @Nullable List<@Nullable BigDecimal> adjclose) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Events(@Nullable Map<String, Dividend> dividends, @Nullable Map<String, Split> splits) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Dividend(BigDecimal amount, long date) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Split(long date, String numerator, String denominator) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ChartError(String code, String description) {
	}

}
