package com.acompany.weddingaffiliate.weddingaffiliate.entity;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity // 이 클래스가 데이터베이스의 테이블과 매핑되는 JPA 엔티티임을 나타냅니다.
@Table(name = "regions") // 이 엔티티가 매핑될 테이블의 이름을 지정합니다.
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer regionId;

	@Column(name = "region_name", unique = true, nullable = false)
	private String regionName;

	@OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
	private Set<WeddingExpo> weddingExpos;

	// Getter와 Setter
	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public Set<WeddingExpo> getWeddingExpos() {
		return weddingExpos;
	}

	public void setWeddingExpos(Set<WeddingExpo> weddingExpos) {
		this.weddingExpos = weddingExpos;
	}

	// equals() 메서드 오버라이드
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Region region = (Region) o;
		return Objects.equals(regionName, region.regionName);
	}

	// hashCode() 메서드 오버라이드
	@Override
	public int hashCode() {
		return Objects.hash(regionName);
	}

}