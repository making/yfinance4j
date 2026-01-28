package am.ik.yfinance4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

import am.ik.yfinance4j.chart.ChartRequest;
import am.ik.yfinance4j.chart.ChartResponse;
import am.ik.yfinance4j.chart.ChartResponse.AdjClose;
import am.ik.yfinance4j.chart.ChartResponse.Events;
import am.ik.yfinance4j.chart.ChartResponse.Indicators;
import am.ik.yfinance4j.chart.ChartResponse.Quote;
import am.ik.yfinance4j.chart.ChartResponse.Result;
import am.ik.yfinance4j.chart.HistoryRecord;
import am.ik.yfinance4j.quote.QuoteSummaryModule;
import am.ik.yfinance4j.quote.QuoteSummaryResponse;
import am.ik.yfinance4j.quote.StockInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

/**
 * Represents a single ticker symbol and provides methods to retrieve its historical data
 * and summary information.
 */
public class Ticker {

	private static final QuoteSummaryModule[] DEFAULT_MODULES = { QuoteSummaryModule.SUMMARY_DETAIL,
			QuoteSummaryModule.SUMMARY_PROFILE, QuoteSummaryModule.FINANCIAL_DATA,
			QuoteSummaryModule.DEFAULT_KEY_STATISTICS, QuoteSummaryModule.PRICE, QuoteSummaryModule.ASSET_PROFILE };

	private final String symbol;

	private final RestClient restClient;

	private final CrumbManager crumbManager;

	private final YFinanceUrls urls;

	Ticker(String symbol, RestClient restClient, CrumbManager crumbManager, YFinanceUrls urls) {
		this.symbol = symbol;
		this.restClient = restClient;
		this.crumbManager = crumbManager;
		this.urls = urls;
	}

	/**
	 * Returns the ticker symbol.
	 * @return the symbol
	 */
	public String symbol() {
		return this.symbol;
	}

	/**
	 * Retrieves historical price data with default settings (period=1mo, interval=1d).
	 * @return list of historical records
	 */
	public List<HistoryRecord> history() {
		return history(ChartRequest.builder().build());
	}

	/**
	 * Retrieves historical price data with the given request parameters.
	 * @param request the chart request parameters
	 * @return list of historical records
	 */
	public List<HistoryRecord> history(ChartRequest request) {
		ChartResponse response = fetchChart(request);
		return toHistoryRecords(response, request.actions());
	}

	/**
	 * Retrieves stock information using default modules.
	 * @return the stock info
	 */
	public StockInfo info() {
		return info(DEFAULT_MODULES);
	}

	/**
	 * Retrieves stock information for the specified modules.
	 * @param modules the modules to query
	 * @return the stock info
	 */
	public StockInfo info(QuoteSummaryModule... modules) {
		QuoteSummaryResponse response = fetchQuoteSummary(modules);
		return toStockInfo(response);
	}

	private ChartResponse fetchChart(ChartRequest request) {
		ChartResponse response = this.restClient.get().uri(this.urls.chartUrl(), uriBuilder -> {
			uriBuilder.queryParam("interval", request.interval().value());
			uriBuilder.queryParam("includePrePost", request.prePost());
			uriBuilder.queryParam("events", request.actions() ? "div,splits" : "");
			Instant start = request.start();
			Instant end = request.end();
			if (start != null && end != null) {
				uriBuilder.queryParam("period1", start.getEpochSecond());
				uriBuilder.queryParam("period2", end.getEpochSecond());
			}
			else {
				uriBuilder.queryParam("range", request.period().value());
			}
			uriBuilder.queryParam("crumb", this.crumbManager.crumb());
			return uriBuilder.build(this.symbol);
		}).header(HttpHeaders.COOKIE, this.crumbManager.cookie()).retrieve().body(ChartResponse.class);
		if (response == null) {
			throw new YFinanceException("Empty chart response for " + this.symbol);
		}
		return response;
	}

	private QuoteSummaryResponse fetchQuoteSummary(QuoteSummaryModule... modules) {
		String moduleList = Arrays.stream(modules).map(QuoteSummaryModule::value).collect(Collectors.joining(","));
		QuoteSummaryResponse response = this.restClient.get().uri(this.urls.quoteSummaryUrl(), uriBuilder -> {
			uriBuilder.queryParam("modules", moduleList);
			uriBuilder.queryParam("crumb", this.crumbManager.crumb());
			return uriBuilder.build(this.symbol);
		}).header(HttpHeaders.COOKIE, this.crumbManager.cookie()).retrieve().body(QuoteSummaryResponse.class);
		if (response == null) {
			throw new YFinanceException("Empty quoteSummary response for " + this.symbol);
		}
		return response;
	}

	private List<HistoryRecord> toHistoryRecords(ChartResponse response, boolean includeActions) {
		if (response == null || response.chart() == null || response.chart().result() == null
				|| response.chart().result().isEmpty()) {
			if (response != null && response.chart() != null && response.chart().error() != null) {
				throw new YFinanceException("Chart API error: " + response.chart().error().code() + " - "
						+ response.chart().error().description());
			}
			return Collections.emptyList();
		}
		Result result = response.chart().result().get(0);
		List<Long> timestamps = result.timestamp();
		if (timestamps == null || timestamps.isEmpty()) {
			return Collections.emptyList();
		}
		Indicators indicators = result.indicators();
		Quote quote = indicators.quote().get(0);
		@Nullable List<@Nullable BigDecimal> adjCloseList = null;
		List<AdjClose> adjcloseIndicators = indicators.adjclose();
		if (adjcloseIndicators != null && !adjcloseIndicators.isEmpty()) {
			AdjClose adjCloseData = adjcloseIndicators.get(0);
			if (adjCloseData != null) {
				adjCloseList = adjCloseData.adjclose();
			}
		}
		Map<Long, ChartResponse.Dividend> dividendMap = Collections.emptyMap();
		Map<Long, ChartResponse.Split> splitMap = Collections.emptyMap();
		if (includeActions) {
			Events events = result.events();
			if (events != null) {
				if (events.dividends() != null) {
					dividendMap = new LinkedHashMap<>();
					for (ChartResponse.Dividend div : events.dividends().values()) {
						dividendMap.put(div.date(), div);
					}
				}
				if (events.splits() != null) {
					splitMap = new LinkedHashMap<>();
					for (ChartResponse.Split split : events.splits().values()) {
						splitMap.put(split.date(), split);
					}
				}
			}
		}
		List<HistoryRecord> records = new ArrayList<>(timestamps.size());
		for (int i = 0; i < timestamps.size(); i++) {
			long ts = timestamps.get(i);
			@Nullable BigDecimal open = safeGet(quote.open(), i);
			@Nullable BigDecimal high = safeGet(quote.high(), i);
			@Nullable BigDecimal low = safeGet(quote.low(), i);
			@Nullable BigDecimal close = safeGet(quote.close(), i);
			@Nullable BigDecimal adjClose = adjCloseList != null ? safeGet(adjCloseList, i) : close;
			@Nullable List<@Nullable Long> volumes = quote.volume();
			long volume = volumes != null && i < volumes.size() && volumes.get(i) != null ? volumes.get(i) : 0L;
			BigDecimal dividends = BigDecimal.ZERO;
			BigDecimal stockSplits = BigDecimal.ZERO;
			if (includeActions) {
				ChartResponse.Dividend div = dividendMap.get(ts);
				if (div != null) {
					dividends = div.amount();
				}
				ChartResponse.Split split = splitMap.get(ts);
				if (split != null) {
					try {
						BigDecimal num = new BigDecimal(split.numerator());
						BigDecimal den = new BigDecimal(split.denominator());
						if (den.compareTo(BigDecimal.ZERO) != 0) {
							stockSplits = num.divide(den, 6, java.math.RoundingMode.HALF_UP);
						}
					}
					catch (NumberFormatException ex) {
						// skip invalid split data
					}
				}
			}
			if (open != null && high != null && low != null && close != null) {
				BigDecimal effectiveAdjClose = adjClose != null ? adjClose : close;
				records.add(new HistoryRecord(Instant.ofEpochSecond(ts), open, high, low, close, effectiveAdjClose,
						volume, dividends, stockSplits));
			}
		}
		return records;
	}

	@SuppressWarnings("unchecked")
	private StockInfo toStockInfo(QuoteSummaryResponse response) {
		if (response == null || response.quoteSummary() == null) {
			throw new YFinanceException("Empty quoteSummary response for " + this.symbol);
		}
		if (response.quoteSummary().error() != null) {
			throw new YFinanceException("QuoteSummary API error: " + response.quoteSummary().error().code() + " - "
					+ response.quoteSummary().error().description());
		}
		if (response.quoteSummary().result() == null || response.quoteSummary().result().isEmpty()) {
			throw new YFinanceException("No quoteSummary result for " + this.symbol);
		}
		Map<String, Object> moduleData = response.quoteSummary().result().get(0);
		Map<String, Object> flat = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : moduleData.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Map<?, ?> nested) {
				for (Map.Entry<?, ?> nestedEntry : nested.entrySet()) {
					Object nestedValue = nestedEntry.getValue();
					if (nestedValue instanceof Map<?, ?> rawValMap && rawValMap.containsKey("raw")) {
						flat.put(String.valueOf(nestedEntry.getKey()), rawValMap.get("raw"));
					}
					else {
						flat.put(String.valueOf(nestedEntry.getKey()), nestedValue);
					}
				}
			}
		}
		return new StockInfo(flat);
	}

	private static @Nullable BigDecimal safeGet(@Nullable List<@Nullable BigDecimal> list, int index) {
		if (list == null || index >= list.size()) {
			return null;
		}
		return list.get(index);
	}

}
