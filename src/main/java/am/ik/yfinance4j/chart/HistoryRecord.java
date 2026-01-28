package am.ik.yfinance4j.chart;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A single OHLCV record representing one data point from historical price data.
 *
 * @param timestamp the timestamp of this record
 * @param open the opening price
 * @param high the highest price
 * @param low the lowest price
 * @param close the closing price
 * @param adjClose the adjusted closing price
 * @param volume the trading volume
 * @param dividends the dividend amount (0 if none)
 * @param stockSplits the stock split ratio (0 if none)
 */
public record HistoryRecord(Instant timestamp, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close,
		BigDecimal adjClose, long volume, BigDecimal dividends, BigDecimal stockSplits) {
}
