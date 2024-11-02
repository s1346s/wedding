package com.acompany.weddingaffiliate.weddingaffiliate.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acompany.weddingaffiliate.weddingaffiliate.entity.WeddingExpo;

// 인터페이스 선언 부분을 수정합니다.
public interface WeddingExpoRepository extends JpaRepository<WeddingExpo, Integer> {
	Optional<WeddingExpo> findByExpoNameAndLocationAndStartDate(String expoName, String location, Date startDate);
}