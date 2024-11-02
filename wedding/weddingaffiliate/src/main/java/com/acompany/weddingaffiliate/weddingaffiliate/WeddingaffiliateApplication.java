package com.acompany.weddingaffiliate.weddingaffiliate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication // 이 클래스가 스프링 부트 애플리케이션의 시작점임을 나타내며, 여러 설정을 자동으로 해줍니다.
public class WeddingaffiliateApplication {

    public static void main(String[] args) {
    	
        SpringApplication.run(WeddingaffiliateApplication.class, args);
        
//    	//수동으로 애플리케이션 시작 시 crawl 메소드 실행하도록.
//        ApplicationContext context = SpringApplication.run(WeddingaffiliateApplication.class, args);
//        WeddingCrawler weddingCrawler = context.getBean(WeddingCrawler.class);
//        weddingCrawler.crawl(); // 애플리케이션 시작 시 크롤링 수행
    }
}