package am.ik.yfinance4j.quote;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.Nullable;

/**
 * JSON mapping for the Yahoo Finance v10 quoteSummary API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuoteSummaryResponse(QuoteSummary quoteSummary) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record QuoteSummary(@Nullable List<Map<String, Object>> result, @Nullable QuoteSummaryError error) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record QuoteSummaryError(String code, String description) {
	}

}
