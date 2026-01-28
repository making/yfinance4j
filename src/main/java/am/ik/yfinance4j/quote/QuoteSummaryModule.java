package am.ik.yfinance4j.quote;

/**
 * Available modules for the Yahoo Finance v10 quoteSummary API.
 */
public enum QuoteSummaryModule {

	ASSET_PROFILE("assetProfile"), SUMMARY_DETAIL("summaryDetail"), SUMMARY_PROFILE("summaryProfile"),
	FINANCIAL_DATA("financialData"), DEFAULT_KEY_STATISTICS("defaultKeyStatistics"), PRICE("price"),
	EARNINGS("earnings"), CALENDAR_EVENTS("calendarEvents"), INCOME_STATEMENT_HISTORY("incomeStatementHistory"),
	BALANCE_SHEET_HISTORY("balanceSheetHistory"), CASH_FLOW_STATEMENT_HISTORY("cashflowStatementHistory");

	private final String value;

	QuoteSummaryModule(String value) {
		this.value = value;
	}

	/**
	 * Returns the module name used in the API request.
	 * @return the API module name
	 */
	public String value() {
		return this.value;
	}

}
