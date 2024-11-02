package com.acompany.weddingaffiliate.weddingaffiliate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service // 이 클래스가 서비스 계층을 나타내며, 스프링의 컴포넌트 스캔에 의해 빈으로 등록됨을 나타냅니다.
public class WeddingAffiliateService {

    private final WeddingCrawler weddingCrawler; // WeddingCrawler 객체를 주입받아 사용하기 위해 선언

    @Autowired // 스프링이 WeddingCrawler 객체를 자동으로 주입하도록 지정합니다.
    public WeddingAffiliateService(WeddingCrawler weddingCrawler) {
        this.weddingCrawler = weddingCrawler; // 생성자를 통해 WeddingCrawler 객체를 초기화
    }

    @Scheduled(cron = "0 0 12 * * ?") // 매일 12시에 해당 메서드를 실행하도록 스케줄링합니다.
    public void updateAffiliateData() {
        weddingCrawler.crawl(); // WeddingCrawler 객체의 crawl 메서드를 호출하여 데이터를 크롤링합니다.
    }
}