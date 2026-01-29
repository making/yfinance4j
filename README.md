# yfinance4j

A Java library for accessing Yahoo Finance data, inspired by Python's [yfinance](https://github.com/ranaroussi/yfinance).
Built on Spring Framework's `RestClient`, it provides historical price data and stock information with no direct dependency on a specific Jackson version.

## Requirements

- Java 17+
- Spring Framework 7.0+ (`spring-web`)
- A Jackson implementation (Jackson 2.x or 3.x) on the classpath for JSON deserialization

## Installation

```xml
<dependency>
    <groupId>am.ik.yfinance4j</groupId>
    <artifactId>yfinance4j</artifactId>
    <version>0.1.1</version>
</dependency>
```

## Quick Start

```java
import am.ik.yfinance4j.YFinance;
import am.ik.yfinance4j.Ticker;
import am.ik.yfinance4j.chart.HistoryRecord;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

RestClient restClient = RestClient.builder()
    .requestFactory(new JdkClientHttpRequestFactory())
    .defaultHeader("User-Agent", "Mozilla/5.0")
    .build();

YFinance yf = new YFinance(restClient);
Ticker ticker = yf.ticker("AAPL");

// Get historical price data (default: 1 month, daily)
List<HistoryRecord> history = ticker.history();
for (HistoryRecord record : history) {
    System.out.printf("%s  O:%.2f  H:%.2f  L:%.2f  C:%.2f  V:%d%n",
        record.timestamp(), record.open(), record.high(),
        record.low(), record.close(), record.volume());
}
```

## Usage

### Historical Price Data

`Ticker.history()` returns OHLCV data as a list of `HistoryRecord`.

```java
Ticker ticker = yf.ticker("MSFT");

// Default: 1 month period, daily interval
List<HistoryRecord> history = ticker.history();
```

Use `ChartRequest` to customize the query:

```java
import am.ik.yfinance4j.Period;
import am.ik.yfinance4j.Interval;
import am.ik.yfinance4j.chart.ChartRequest;

// 1 year of weekly data
List<HistoryRecord> weekly = ticker.history(
    ChartRequest.builder()
        .period(Period.ONE_YEAR)
        .interval(Interval.ONE_WEEK)
        .build());

// Specific date range
List<HistoryRecord> range = ticker.history(
    ChartRequest.builder()
        .start(Instant.parse("2024-01-01T00:00:00Z"))
        .end(Instant.parse("2024-06-01T00:00:00Z"))
        .interval(Interval.ONE_DAY)
        .build());

// Include pre/post market data, exclude dividends/splits
List<HistoryRecord> prePost = ticker.history(
    ChartRequest.builder()
        .period(Period.FIVE_DAYS)
        .interval(Interval.FIVE_MINUTES)
        .prePost(true)
        .actions(false)
        .build());
```

`HistoryRecord` fields:

| Field | Type | Description |
|---|---|---|
| `timestamp` | `Instant` | Timestamp of the data point |
| `open` | `BigDecimal` | Opening price |
| `high` | `BigDecimal` | Highest price |
| `low` | `BigDecimal` | Lowest price |
| `close` | `BigDecimal` | Closing price |
| `adjClose` | `BigDecimal` | Adjusted closing price |
| `volume` | `long` | Trading volume |
| `dividends` | `BigDecimal` | Dividend amount (0 if none) |
| `stockSplits` | `BigDecimal` | Stock split ratio (0 if none) |

### Available Periods

| Enum | Value |
|---|---|
| `Period.ONE_DAY` | 1d |
| `Period.FIVE_DAYS` | 5d |
| `Period.ONE_MONTH` | 1mo |
| `Period.THREE_MONTHS` | 3mo |
| `Period.SIX_MONTHS` | 6mo |
| `Period.ONE_YEAR` | 1y |
| `Period.TWO_YEARS` | 2y |
| `Period.FIVE_YEARS` | 5y |
| `Period.TEN_YEARS` | 10y |
| `Period.YEAR_TO_DATE` | ytd |
| `Period.MAX` | max |

### Available Intervals

| Enum | Value |
|---|---|
| `Interval.ONE_MINUTE` | 1m |
| `Interval.TWO_MINUTES` | 2m |
| `Interval.FIVE_MINUTES` | 5m |
| `Interval.FIFTEEN_MINUTES` | 15m |
| `Interval.THIRTY_MINUTES` | 30m |
| `Interval.SIXTY_MINUTES` | 60m |
| `Interval.NINETY_MINUTES` | 90m |
| `Interval.ONE_HOUR` | 1h |
| `Interval.ONE_DAY` | 1d |
| `Interval.FIVE_DAYS` | 5d |
| `Interval.ONE_WEEK` | 1wk |
| `Interval.ONE_MONTH` | 1mo |
| `Interval.THREE_MONTHS` | 3mo |

### Stock Information

`Ticker.info()` returns a `StockInfo` object with typed accessors for common fields.

```java
import am.ik.yfinance4j.quote.StockInfo;

StockInfo info = yf.ticker("AAPL").info();

// Using Optional-returning methods
info.shortName().ifPresent(name -> System.out.println("Name: " + name));
info.currentPrice().ifPresent(price -> System.out.println("Price: " + price));
info.sector().ifPresent(sector -> System.out.println("Sector: " + sector));
info.industry().ifPresent(industry -> System.out.println("Industry: " + industry));
info.marketCap().ifPresent(cap -> System.out.println("Market Cap: " + cap));

// Using nullable-returning methods (returns null if not available)
String name = info.shortNameNullable();
BigDecimal price = info.currentPriceNullable();

// Access any field by key (returns Optional<Object>)
info.get("trailingPE").ifPresent(pe -> System.out.println("P/E: " + pe));

// Access any field with type-safe methods
info.getString("shortName").ifPresent(name -> System.out.println("Name: " + name));
info.getNumber("trailingPE").ifPresent(pe -> System.out.println("P/E: " + pe));

// Access all raw data
Map<String, Object> raw = info.raw();
```

Available typed accessors (each has both `Optional` and `Nullable` variants):

| Method | Nullable variant | Return type |
|---|---|---|
| `shortName()` | `shortNameNullable()` | `String` |
| `longName()` | `longNameNullable()` | `String` |
| `symbol()` | `symbolNullable()` | `String` |
| `currency()` | `currencyNullable()` | `String` |
| `exchange()` | `exchangeNullable()` | `String` |
| `quoteType()` | `quoteTypeNullable()` | `String` |
| `currentPrice()` | `currentPriceNullable()` | `BigDecimal` |
| `price()` | `priceNullable()` | `BigDecimal` |
| `regularMarketPrice()` | `regularMarketPriceNullable()` | `BigDecimal` |
| `marketCap()` | `marketCapNullable()` | `BigDecimal` |
| `sector()` | `sectorNullable()` | `String` |
| `industry()` | `industryNullable()` | `String` |

For accessing arbitrary fields with type safety:

| Method | Nullable variant | Return type |
|---|---|---|
| `getString(key)` | `getStringNullable(key)` | `String` |
| `getNumber(key)` | `getNumberNullable(key)` | `BigDecimal` |

You can also query specific modules:

```java
import am.ik.yfinance4j.quote.QuoteSummaryModule;

StockInfo info = ticker.info(
    QuoteSummaryModule.PRICE,
    QuoteSummaryModule.FINANCIAL_DATA);
```

### Japanese Stocks

Ticker symbols for non-US markets use the standard Yahoo Finance suffix notation:

```java
// Toyota Motor (Tokyo Stock Exchange)
Ticker toyota = yf.ticker("7203.T");
List<HistoryRecord> history = toyota.history();
StockInfo info = toyota.info();
info.currency(); // Optional["JPY"]
```

### CrumbManager

yfinance4j handles Yahoo Finance's cookie/crumb authentication automatically. If you need to share the authentication state across multiple `YFinance` instances or manage the lifecycle yourself, inject a `CrumbManager`:

```java
CrumbManager crumbManager = new CrumbManager(restClient);
crumbManager.refresh(); // Pre-fetch cookie and crumb

YFinance yf = new YFinance(restClient, crumbManager);
```

### Custom URLs

If you need to use a proxy or override the default Yahoo Finance endpoints, use `YFinanceUrls`:

```java
import am.ik.yfinance4j.YFinanceUrls;

YFinanceUrls urls = YFinanceUrls.builder()
    .cookieUrl("https://my-proxy.example.com/cookie")
    .crumbUrl("https://my-proxy.example.com/crumb")
    .chartUrl("https://my-proxy.example.com/v8/finance/chart/{ticker}")
    .quoteSummaryUrl("https://my-proxy.example.com/v10/finance/quoteSummary/{ticker}")
    .build();

YFinance yf = new YFinance(restClient, urls);
```

You can override only specific URLs; unspecified ones default to Yahoo Finance's standard endpoints:

```java
// Only override the cookie URL
YFinanceUrls urls = YFinanceUrls.builder()
    .cookieUrl("https://my-proxy.example.com/cookie")
    .build();
```

### Error Handling

API errors are thrown as `YFinanceException`:

```java
try {
    yf.ticker("INVALIDTICKER").info();
} catch (YFinanceException e) {
    System.err.println(e.getMessage());
}
```

## Null Safety

All packages are annotated with JSpecify `@NullMarked`. Methods that may return `null` are annotated with `@Nullable`. This provides compile-time null safety when used with tools such as NullAway or IntelliJ IDEA.

## License

Apache License 2.0
