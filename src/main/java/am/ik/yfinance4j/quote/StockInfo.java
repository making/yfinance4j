package am.ik.yfinance4j.quote;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

/**
 * Stock information retrieved from the quoteSummary API. Provides typed accessors for
 * common fields and raw access to all fields via {@link #raw()}.
 */
public final class StockInfo {

	private final Map<String, Object> raw;

	public StockInfo(Map<String, Object> raw) {
		this.raw = Collections.unmodifiableMap(new LinkedHashMap<>(raw));
	}

	/**
	 * Returns the raw data map containing all fields from the API response.
	 * @return unmodifiable map of all fields
	 */
	public Map<String, Object> raw() {
		return this.raw;
	}

	/**
	 * Returns the value associated with the given key.
	 * @param key the field key
	 * @return an optional containing the value, or empty if not present
	 */
	public Optional<Object> get(String key) {
		return Optional.ofNullable(this.raw.get(key));
	}

	/**
	 * Returns the value associated with the given key, or {@code null} if not present.
	 * @param key the field key
	 * @return the value, or {@code null}
	 */
	public @Nullable Object getNullable(String key) {
		return this.raw.get(key);
	}

	/**
	 * Returns the short name (display name) of the security.
	 * @return an optional containing the short name
	 */
	public Optional<String> shortName() {
		return getString("shortName");
	}

	/**
	 * Returns the short name (display name) of the security, or {@code null}.
	 * @return the short name, or {@code null}
	 */
	public @Nullable String shortNameNullable() {
		return getStringNullable("shortName");
	}

	/**
	 * Returns the long name of the security.
	 * @return an optional containing the long name
	 */
	public Optional<String> longName() {
		return getString("longName");
	}

	/**
	 * Returns the long name of the security, or {@code null}.
	 * @return the long name, or {@code null}
	 */
	public @Nullable String longNameNullable() {
		return getStringNullable("longName");
	}

	/**
	 * Returns the symbol (ticker).
	 * @return an optional containing the symbol
	 */
	public Optional<String> symbol() {
		return getString("symbol");
	}

	/**
	 * Returns the symbol (ticker), or {@code null}.
	 * @return the symbol, or {@code null}
	 */
	public @Nullable String symbolNullable() {
		return getStringNullable("symbol");
	}

	/**
	 * Returns the currency of the security.
	 * @return an optional containing the currency code
	 */
	public Optional<String> currency() {
		return getString("currency");
	}

	/**
	 * Returns the currency of the security, or {@code null}.
	 * @return the currency code, or {@code null}
	 */
	public @Nullable String currencyNullable() {
		return getStringNullable("currency");
	}

	/**
	 * Returns the exchange on which the security is traded.
	 * @return an optional containing the exchange name
	 */
	public Optional<String> exchange() {
		return getString("exchange");
	}

	/**
	 * Returns the exchange on which the security is traded, or {@code null}.
	 * @return the exchange name, or {@code null}
	 */
	public @Nullable String exchangeNullable() {
		return getStringNullable("exchange");
	}

	/**
	 * Returns the quote type (e.g. EQUITY, ETF).
	 * @return an optional containing the quote type
	 */
	public Optional<String> quoteType() {
		return getString("quoteType");
	}

	/**
	 * Returns the quote type (e.g. EQUITY, ETF), or {@code null}.
	 * @return the quote type, or {@code null}
	 */
	public @Nullable String quoteTypeNullable() {
		return getStringNullable("quoteType");
	}

	/**
	 * Returns the current market price.
	 * @return an optional containing the current price
	 */
	public Optional<BigDecimal> currentPrice() {
		return getNumber("currentPrice");
	}

	/**
	 * Returns the current market price, or {@code null}.
	 * @return the current price, or {@code null}
	 */
	public @Nullable BigDecimal currentPriceNullable() {
		return getNumberNullable("currentPrice");
	}

	/**
	 * Returns the market capitalization.
	 * @return an optional containing the market cap
	 */
	public Optional<BigDecimal> marketCap() {
		return getNumber("marketCap");
	}

	/**
	 * Returns the market capitalization, or {@code null}.
	 * @return the market cap, or {@code null}
	 */
	public @Nullable BigDecimal marketCapNullable() {
		return getNumberNullable("marketCap");
	}

	/**
	 * Returns the sector.
	 * @return an optional containing the sector
	 */
	public Optional<String> sector() {
		return getString("sector");
	}

	/**
	 * Returns the sector, or {@code null}.
	 * @return the sector, or {@code null}
	 */
	public @Nullable String sectorNullable() {
		return getStringNullable("sector");
	}

	/**
	 * Returns the industry.
	 * @return an optional containing the industry
	 */
	public Optional<String> industry() {
		return getString("industry");
	}

	/**
	 * Returns the industry, or {@code null}.
	 * @return the industry, or {@code null}
	 */
	public @Nullable String industryNullable() {
		return getStringNullable("industry");
	}

	private Optional<String> getString(String key) {
		return Optional.ofNullable(getStringNullable(key));
	}

	private @Nullable String getStringNullable(String key) {
		Object value = this.raw.get(key);
		return value instanceof String s ? s : null;
	}

	private Optional<BigDecimal> getNumber(String key) {
		return Optional.ofNullable(getNumberNullable(key));
	}

	private @Nullable BigDecimal getNumberNullable(String key) {
		Object value = this.raw.get(key);
		return value instanceof Number n ? new BigDecimal(n.toString()) : null;
	}

	@Override
	public String toString() {
		return "StockInfo" + this.raw;
	}

}
