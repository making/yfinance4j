package am.ik.yfinance4j;

import org.jspecify.annotations.Nullable;

/**
 * Base exception for yfinance4j errors.
 */
public class YFinanceException extends RuntimeException {

	public YFinanceException(String message) {
		super(message);
	}

	public YFinanceException(String message, @Nullable Throwable cause) {
		super(message, cause);
	}

}
