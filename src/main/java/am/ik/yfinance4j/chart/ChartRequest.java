package am.ik.yfinance4j.chart;

import java.time.Instant;

import am.ik.yfinance4j.Interval;
import am.ik.yfinance4j.Period;
import org.jspecify.annotations.Nullable;

/**
 * Request parameters for the chart (history) API. Use {@link #builder()} to create an
 * instance.
 *
 * <pre>{@code
 * ChartRequest request = ChartRequest.builder()
 *     .period(Period.THREE_MONTHS)
 *     .interval(Interval.ONE_DAY)
 *     .build();
 * }</pre>
 */
public final class ChartRequest {

	private final Period period;

	private final Interval interval;

	private final @Nullable Instant start;

	private final @Nullable Instant end;

	private final boolean prePost;

	private final boolean actions;

	private ChartRequest(Builder builder) {
		this.period = builder.period;
		this.interval = builder.interval;
		this.start = builder.start;
		this.end = builder.end;
		this.prePost = builder.prePost;
		this.actions = builder.actions;
	}

	public Period period() {
		return this.period;
	}

	public Interval interval() {
		return this.interval;
	}

	public @Nullable Instant start() {
		return this.start;
	}

	public @Nullable Instant end() {
		return this.end;
	}

	public boolean prePost() {
		return this.prePost;
	}

	public boolean actions() {
		return this.actions;
	}

	/**
	 * Creates a new {@link Builder} with default settings (period=1mo, interval=1d).
	 * @return a new builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private Period period = Period.ONE_MONTH;

		private Interval interval = Interval.ONE_DAY;

		private @Nullable Instant start;

		private @Nullable Instant end;

		private boolean prePost = false;

		private boolean actions = true;

		private Builder() {
		}

		/**
		 * Sets the time period. Ignored if start/end are specified.
		 * @param period the period
		 * @return this builder
		 */
		public Builder period(Period period) {
			this.period = period;
			return this;
		}

		/**
		 * Sets the data interval (granularity).
		 * @param interval the interval
		 * @return this builder
		 */
		public Builder interval(Interval interval) {
			this.interval = interval;
			return this;
		}

		/**
		 * Sets the start timestamp. When specified, period is ignored.
		 * @param start the start instant
		 * @return this builder
		 */
		public Builder start(@Nullable Instant start) {
			this.start = start;
			return this;
		}

		/**
		 * Sets the end timestamp. When specified, period is ignored.
		 * @param end the end instant
		 * @return this builder
		 */
		public Builder end(@Nullable Instant end) {
			this.end = end;
			return this;
		}

		/**
		 * Whether to include pre/post market data.
		 * @param prePost true to include
		 * @return this builder
		 */
		public Builder prePost(boolean prePost) {
			this.prePost = prePost;
			return this;
		}

		/**
		 * Whether to include dividends and stock splits.
		 * @param actions true to include
		 * @return this builder
		 */
		public Builder actions(boolean actions) {
			this.actions = actions;
			return this;
		}

		/**
		 * Builds the {@link ChartRequest}.
		 * @return the chart request
		 */
		public ChartRequest build() {
			return new ChartRequest(this);
		}

	}

}
