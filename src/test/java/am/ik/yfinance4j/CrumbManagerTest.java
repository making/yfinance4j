package am.ik.yfinance4j;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class CrumbManagerTest {

	private final RestClient restClient = RestClient.builder()
		.requestFactory(new JdkClientHttpRequestFactory())
		.defaultHeader("User-Agent", "Mozilla/5.0")
		.build();

	@Test
	void shouldObtainCookieAndCrumb() {
		CrumbManager crumbManager = new CrumbManager(this.restClient);
		crumbManager.refresh();
		assertThat(crumbManager.cookie()).isNotNull().isNotEmpty();
		assertThat(crumbManager.crumb()).isNotNull().isNotEmpty();
	}

	@Test
	void shouldRefreshCookieAndCrumb() {
		CrumbManager crumbManager = new CrumbManager(this.restClient);
		crumbManager.refresh();
		String firstCrumb = crumbManager.crumb();
		String firstCookie = crumbManager.cookie();
		crumbManager.refresh();
		assertThat(crumbManager.cookie()).isNotNull().isNotEmpty();
		assertThat(crumbManager.crumb()).isNotNull().isNotEmpty();
	}

}
