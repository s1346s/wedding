package com.acompany.weddingaffiliate.weddingaffiliate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.acompany.weddingaffiliate.weddingaffiliate.entity.Region;
import com.acompany.weddingaffiliate.weddingaffiliate.entity.WeddingExpo;
import com.acompany.weddingaffiliate.weddingaffiliate.repository.RegionRepository;
import com.acompany.weddingaffiliate.weddingaffiliate.repository.WeddingExpoRepository;

@Component
public class WeddingCrawler {
	// 로거 생성
	private static final Logger logger = LoggerFactory.getLogger(WeddingCrawler.class);

	// 리포지토리 주입
	@Autowired
	private WeddingExpoRepository weddingExpoRepository;

	@Autowired
	private RegionRepository regionRepository;

	// 크롤링을 수행하는 메서드
	public void crawl() {

		// 날짜 포맷터 생성 (yyyyMMdd 형식)
		SimpleDateFormat sdfWithYear = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);

		// WebDriver 경로 설정 (로컬 환경에 맞게 수정 필요)
		System.setProperty("webdriver.chrome.driver", "C:\\SHJ Java\\설치파일\\chromedriver-win64\\chromedriver.exe");

		// ChromeDriver 생성
		WebDriver driver = new ChromeDriver();
		logger.info("WebDriver 생성 완료");

		// 대기시간 설정 (암시적 대기)
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

		try {
			// 크롤링할 사이트 접근
			driver.get("https://ad.cpaad.co.kr/wedunited03");

			// 지역 정보 크롤링
			List<WebElement> regionElements = driver.findElements(By.cssSelector("div.region_name01"));
			logger.info("지역 정보 크롤링 시작. 총 {}개의 지역 발견", regionElements.size());

			// 현재 크롤링한 박람회의 고유 식별자를 저장할 집합
			Set<String> currentExpoIdentifiers = new HashSet<>();

			// 각 지역에 대해 반복
			for (WebElement regionElement : regionElements) {
				try {
					// 지역 이름 추출
					String regionName = regionElement.findElement(By.tagName("b")).getText().trim();
					logger.info("크롤링한 지역 이름: {}", regionName);

					// 지역 엔티티 가져오기 또는 생성
					Region region = regionRepository.findByRegionName(regionName);
					if (region == null) {
						// 새로운 지역 생성 및 저장
						region = new Region();
						region.setRegionName(regionName);
						regionRepository.save(region);
						logger.info("새로운 지역 저장: {}", regionName);
					} else {
						logger.info("기존 지역 발견: {}", regionName);
					}

					// 해당 지역 아래의 박람회 정보 크롤링
					// 다음 요소로 박람회 리스트가 있다고 가정
					WebElement nextSibling = regionElement.findElement(By.xpath("following-sibling::*[1]"));

					// 박람회 리스트에서 각 박람회 요소 찾기
					List<WebElement> expoElements = nextSibling.findElements(By.cssSelector("div.goods01"));
					logger.info("{} 지역의 박람회 정보 크롤링 시작. 총 {}개의 박람회 발견", regionName, expoElements.size());

					// 각 박람회에 대해 반복
					for (WebElement expoElement : expoElements) {
						try {

							// 이미지 URL 추출
							WebElement imageElement = expoElement.findElement(By.cssSelector("div.thumb img"));
							String imageUrl = imageElement.getAttribute("src");
							logger.info("크롤링한 이미지 URL: {}", imageUrl); // 이미지 URL 로깅 추가

							// 박람회 링크 추출 및 's1346s' 추가
							WebElement linkElement = expoElement.findElement(By.tagName("a"));
							String expoLink = linkElement.getAttribute("href") + "s1346s";

							// 박람회 이름 추출
							String expoName = expoElement.findElement(By.cssSelector("div.ad_title")).getText().trim();

							// 박람회 정보 추출
							String expoInfo = expoElement.findElement(By.cssSelector("div.ad_info")).getText().trim();

							// 박람회 날짜 추출
							String adDate = expoElement.findElement(By.cssSelector("div.ad_date")).getText().trim();

							// 시작 날짜와 종료 날짜 파싱
							Date[] dates = parseDates(adDate);
							Date startDate = dates[0];
							Date endDate = dates[1];

							// 박람회 위치 추출
							String location = expoElement.findElement(By.cssSelector("div.ad_location")).getText()
									.trim();

							// 고유 식별자 생성 (expoName + location + startDate)
							String expoIdentifier = expoName + "_" + location + "_" + sdfWithYear.format(startDate);
							currentExpoIdentifiers.add(expoIdentifier);

							// 데이터베이스에서 해당 박람회 조회
							Optional<WeddingExpo> existingExpoOpt = weddingExpoRepository
									.findByExpoNameAndLocationAndStartDate(expoName, location, startDate);

							if (existingExpoOpt.isPresent()) {
								// 기존 박람회가 존재하는 경우 업데이트
								WeddingExpo existingExpo = existingExpoOpt.get();

								// 변경 사항 확인 및 업데이트
								boolean isUpdated = false;
								StringBuilder updatedFields = new StringBuilder();

								// 종료 날짜 비교 및 업데이트
								if (!existingExpo.getEndDate().equals(endDate)) {
									existingExpo.setEndDate(endDate);
									isUpdated = true;
									updatedFields.append("endDate, ");
								}

								// 박람회 정보 비교 및 업데이트
								if (!existingExpo.getExpoInfo().equals(expoInfo)) {
									existingExpo.setExpoInfo(expoInfo);
									isUpdated = true;
									updatedFields.append("expoInfo, ");
								}

								// 박람회 링크 비교 및 업데이트
								if (!existingExpo.getExpoLink().equals(expoLink)) {
									existingExpo.setExpoLink(expoLink);
									isUpdated = true;
									updatedFields.append("expoLink, ");
								}

								// 지역 비교 및 업데이트
								if (!existingExpo.getRegion().equals(region)) {
									existingExpo.setRegion(region);
									isUpdated = true;
									updatedFields.append("region, ");
								}

								// 변경 사항이 있을 경우 저장
								if (isUpdated) {
									weddingExpoRepository.save(existingExpo);
									logger.info("기존 박람회 업데이트: {} (업데이트된 필드: {})", expoName,
											updatedFields.toString().replaceAll(", $", ""));
								} else {
									logger.info("변경 사항 없음: {}", expoName);
								}

							} else {
								// 기존 박람회가 없으므로 새로운 박람회로 추가
								WeddingExpo expo = new WeddingExpo();
								expo.setExpoName(expoName);
								expo.setExpoLink(expoLink);
								expo.setLocation(location);
								expo.setStartDate(startDate);
								expo.setEndDate(endDate);
								expo.setRegion(region);
								expo.setExpoInfo(expoInfo);
								expo.setImageUrl(imageUrl);

								// 새 박람회 저장
								weddingExpoRepository.save(expo);
								logger.info("새로운 박람회 저장: {}", expoName);
							}

						} catch (NoSuchElementException e) {
							// 요소를 찾을 수 없는 경우 예외 처리
							logger.error("박람회 정보를 찾을 수 없거나 이미지를 찾을 수 없습니다: {}", e.getMessage());
						} catch (ParseException e) {
							// 날짜 파싱 중 오류 발생 시 예외 처리
							logger.error("날짜 파싱 중 오류 발생: {}", e.getMessage());
						}
					}

				} catch (NoSuchElementException e) {
					// 지역 이름을 찾을 수 없는 경우 예외 처리
					logger.error("지역 이름을 찾을 수 없습니다: {}", e.getMessage());
				}
			}

			// 데이터베이스에서 모든 박람회의 고유 식별자 목록 가져오기
			List<WeddingExpo> allExpos = weddingExpoRepository.findAll();
			Set<String> databaseExpoIdentifiers = new HashSet<>();
			for (WeddingExpo expo : allExpos) {
				String expoIdentifier = expo.getExpoName() + "_" + expo.getLocation() + "_"
						+ sdfWithYear.format(expo.getStartDate());
				databaseExpoIdentifiers.add(expoIdentifier);
			}

			// 데이터베이스에 있지만 현재 크롤링한 목록에 없는 박람회 식별자 찾기
			Set<String> exposToDelete = new HashSet<>(databaseExpoIdentifiers);
			exposToDelete.removeAll(currentExpoIdentifiers);

			// 삭제할 박람회 삭제
			for (String expoIdentifier : exposToDelete) {
				String[] parts = expoIdentifier.split("_", 3);
				String expoName = parts[0];
				String location = parts[1];
				Date startDate = sdfWithYear.parse(parts[2]);

				Optional<WeddingExpo> expoOpt = weddingExpoRepository.findByExpoNameAndLocationAndStartDate(expoName,
						location, startDate);
				if (expoOpt.isPresent()) {
					weddingExpoRepository.delete(expoOpt.get());
					logger.info("삭제된 박람회: {}", expoName);
				}
			}

		} catch (Exception e) {
			// 전체 프로세스 중 예외 발생 시 처리
			logger.error("오류 발생: {}", e.getMessage(), e);
		} finally {
			// WebDriver 종료
			logger.info("WebDriver 종료 중...");
			driver.quit();
			logger.info("WebDriver 종료 완료");
		}
	}

	// 날짜 파싱 메서드
	public Date[] parseDates(String dateStr) throws ParseException {
		// 날짜 포맷터 생성 (yyyy년 MM월 dd일 형식)
		SimpleDateFormat sdfWithYear = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN);

		Date startDate = null;
		Date endDate = null;

		// 불필요한 텍스트 제거 및 트림
		dateStr = dateStr.replaceAll("VIP초대전", "").replaceAll("단 하루!", "").trim();
		String[] dateParts = dateStr.split("\n");

		// 마지막 부분에 날짜 정보가 있다고 가정
		String lastDatePart = dateParts[dateParts.length - 1];

		if (lastDatePart.contains("~")) {
			// 시작 날짜와 종료 날짜가 모두 있는 경우
			String[] dates = lastDatePart.split("~");
			String startDateStr = dates[0].trim().replaceAll("\\([^)]+\\)", "").trim();
			String endDateStr = dates[1].trim().replaceAll("\\([^)]+\\)", "").trim();

			// 현재 연도를 추가하여 날짜 파싱
			int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
			startDateStr = currentYear + "년 " + startDateStr;
			endDateStr = currentYear + "년 " + endDateStr;

			startDate = sdfWithYear.parse(startDateStr);
			endDate = sdfWithYear.parse(endDateStr);
		} else {
			// 단일 날짜인 경우
			String dateOnly = lastDatePart.replaceAll("\\([^)]+\\)", "").trim();
			int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
			dateOnly = currentYear + "년 " + dateOnly;
			startDate = sdfWithYear.parse(dateOnly);
			endDate = startDate;
		}

		return new Date[] { startDate, endDate };
	}
}
