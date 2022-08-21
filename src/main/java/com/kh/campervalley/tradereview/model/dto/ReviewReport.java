package com.kh.campervalley.tradereview.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReport {

	private int reportNo;
	private int reviewNo;
	private String category;
	private String content;
	private LocalDateTime createdAt;
	private ReportStatus statusYn;
}
