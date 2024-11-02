package com.acompany.weddingaffiliate.weddingaffiliate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.acompany.weddingaffiliate.weddingaffiliate.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Integer> {
	Region findByRegionName(String regionName); // 지역 이름으로 Region 엔티티를 검색하는 메소드
}