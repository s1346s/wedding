package com.acompany.weddingaffiliate.weddingaffiliate.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity // 이 클래스가 데이터베이스의 테이블과 매핑되는 JPA 엔티티임을 나타냅니다.
@Table(name = "wedding_expos") // 이 엔티티가 매핑될 테이블의 이름을 지정합니다.
public class WeddingExpo {

	@Id // 이 필드를 기본 키로 설정합니다.
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키를 데이터베이스에서 자동으로 생성하도록 설정합니다 (자동 증가).
	private int expoId; // 웨딩 엑스포 테이블의 기본 키, 자동으로 생성됨

	@Column(nullable = false, length = 200) // 이 필드를 컬럼으로 매핑하며, null을 허용하지 않고 최대 길이는 200자로 설정합니다.
	private String expoName; // 웨딩 엑스포 이름, null을 허용하지 않으며 최대 길이는 200자

	@Column(nullable = true, length = 1000) // expoInfo 필드 추가
	private String expoInfo;

	@ManyToOne // 이 필드가 다른 엔티티와 다대일 관계임을 나타냅니다.
	@JoinColumn(name = "region_id", referencedColumnName = "regionId") // 이 컬럼이 Region 테이블의 외래 키임을 나타내며, 참조할 컬럼을 지정합니다.
	private Region region; // 웨딩 엑스포가 열리는 지역

	@Column(nullable = false, length = 255) // 이 필드를 컬럼으로 매핑하며, null을 허용하지 않고 최대 길이는 255자로 설정합니다.
	private String location; // 웨딩 엑스포의 위치, null을 허용하지 않으며 최대 길이는 255자

	@Column(nullable = false) // 이 필드를 컬럼으로 매핑하며, null을 허용하지 않습니다.
	@Temporal(TemporalType.DATE) // 이 필드가 날짜 타입임을 나타냅니다.
	private Date startDate; // 웨딩 엑스포의 시작 날짜, null을 허용하지 않음

	@Column(nullable = false) // 이 필드를 컬럼으로 매핑하며, null을 허용하지 않습니다.
	@Temporal(TemporalType.DATE) // 이 필드가 날짜 타입임을 나타냅니다.
	private Date endDate; // 웨딩 엑스포의 종료 날짜, null을 허용하지 않음

	@Column(nullable = false, length = 500) // 이 필드를 컬럼으로 매핑하며, null을 허용하지 않고 최대 길이는 500자로 설정합니다.
	private String expoLink; // 웨딩 엑스포의 링크, null을 허용하지 않으며 최대 길이는 500자

	@Column(nullable = true, length = 500) // Image URL field
	private String imageUrl;

	// Getter와 Setter
	public int getExpoId() {
		return expoId;
	}

	public void setExpoId(int expoId) {
		this.expoId = expoId;
	}

	public String getExpoName() {
		return expoName;
	}

	public void setExpoName(String expoName) {
		this.expoName = expoName;
	}

	public String getExpoInfo() {
		return expoInfo;
	}

	public void setExpoInfo(String expoInfo) {
		this.expoInfo = expoInfo;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getExpoLink() {
		return expoLink;
	}

	public void setExpoLink(String expoLink) {
		this.expoLink = expoLink;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
