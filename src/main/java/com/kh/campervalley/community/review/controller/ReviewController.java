package com.kh.campervalley.community.review.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.campervalley.common.CamperValleyUtils;
import com.kh.campervalley.community.review.model.dto.CampsiteReview;
import com.kh.campervalley.community.review.model.dto.CampsiteReviewExt;
import com.kh.campervalley.community.review.model.dto.ReviewPhoto;
import com.kh.campervalley.community.review.model.service.ReviewService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/community/review")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	ServletContext application;
	
	@GetMapping("/reviewList")
	public ModelAndView reviewList(
			@RequestParam(defaultValue = "1") int cPage, 
			@RequestParam(defaultValue = "") String searchType, 
			@RequestParam(defaultValue = "") String searchKeyword, 
			ModelAndView mav,
			HttpServletRequest request) {
		try {			
			int numPerPage = ReviewService.REVIEW_NUM_PER_PAGE;
			
			List<CampsiteReview> list = null;
			int totalReview = 0;
			String url = request.getRequestURI();
			String pagebar = "";
			
			Map<String, Object> searchParam = new HashMap<>();
			searchParam.put("searchType", searchType);
			searchParam.put("searchKeyword", searchKeyword);
			
			String qStr = "?searchType=" + searchType + "&searchKeyword=" + searchKeyword;
			
			if(searchParam.isEmpty()) {
				list = reviewService.selectReviewList(cPage, numPerPage);
				totalReview = reviewService.selectTotalReview();
				pagebar = CamperValleyUtils.getPagebar(cPage, numPerPage, totalReview, url);
			}
			else {
				list = reviewService.searchReviewList(searchParam, cPage, numPerPage);
				totalReview = reviewService.searchTotalReview(searchParam);
				pagebar = CamperValleyUtils.getMultiParamPagebar(cPage, numPerPage, totalReview, url + qStr);
			}
			
			mav.addObject("list", list);
			mav.addObject("searchParam", searchParam);
			mav.addObject("pagebar", pagebar);
			
			mav.setViewName("community/review/reviewList");
		} catch (Exception e) {
			log.error("후기 목록 조회 오류", e);
			throw e;
		}
		return mav;
	}
	
	@GetMapping("/reviewEnroll")
	public void reviewEnroll() {}
	
	@PostMapping("/reviewEnroll")
	public String reviewEnroll(
			CampsiteReviewExt review, 
			@RequestParam("upFile") MultipartFile[] upFiles, 
			RedirectAttributes redirectAttr) throws Exception {
		try {
			String saveDirectory = application.getRealPath("/resources/upload/community/review");
			
			for(MultipartFile upFile : upFiles) {
				
				if(upFile.getSize() > 0) {
					String originalFilename = upFile.getOriginalFilename();
					String renamedFilename = CamperValleyUtils.getRenamedFilename(originalFilename);
					
					File destFile = new File(saveDirectory, renamedFilename);
					upFile.transferTo(destFile);
					
					ReviewPhoto photo = new ReviewPhoto();
					photo.setOriginalFilename(originalFilename);
					photo.setRenamedFilename(renamedFilename);
					review.addReviewPhoto(photo);
				}
			}
			
			int result = reviewService.insertReview(review);
			redirectAttr.addFlashAttribute("msg", "후기를 성공적으로 등록했습니다.");
			
		} catch (IOException e) {
			log.error("첨부파일 등록 오류", e);
			throw e;
		} catch (Exception e) {
			log.error("후기 등록 오류", e);
			throw e;
		}
		return "redirect:/community/review/reviewDetail?reviewNo=" + review.getReviewNo();
	}
		
	@PostMapping("/autoComplete")
	public @ResponseBody Map<String, Object> autoComplete(
			@RequestParam Map<String, Object> paramMap) throws Exception {
		try {
			List<Map<String, Object>> resultList = reviewService.autoComplete(paramMap);
			paramMap.put("resultList", resultList);
		} catch (Exception e) {
			log.error("캠핑장 검색 자동완성 오류", e);
			throw e;
		}
		return paramMap;
	}
	
	@GetMapping("/reviewUpdate")
	public void reviewUpdate(@RequestParam int reviewNo, Model model) {
		try {
			CampsiteReviewExt review = reviewService.selectOneReview(reviewNo);
			model.addAttribute("review", review);
		} catch (Exception e) {
			log.error("후기 수정 오류", e);
			throw e;
		}
	}
	
	@PostMapping("/reviewUpdate")
	public String reviewUpdate(
    		@ModelAttribute CampsiteReviewExt review, 
            @RequestParam("upFile") MultipartFile[] upFiles, 
            @RequestParam(value="delFile", required=false) int[] delFiles, 
            RedirectAttributes redirectAttr) throws Exception {
		try {
			String saveDirectory = application.getRealPath("/resources/upload/community/review");
			
			if(delFiles != null) {
				for(int reviewPhotoNo : delFiles) {
					ReviewPhoto photo = reviewService.selectOneReviewPhoto(reviewPhotoNo);

					String renamedFilename = photo.getRenamedFilename();
					File delFile = new File(saveDirectory, renamedFilename);
					if(delFile.exists()) {
						delFile.delete();
					}
					int result = reviewService.deleteReviewPhoto(reviewPhotoNo);
				}
			}
			
			for(MultipartFile upFile : upFiles) {
				if(upFile.getSize() > 0) {					
					ReviewPhoto photo = new ReviewPhoto();
					photo.setOriginalFilename(upFile.getOriginalFilename());
					photo.setRenamedFilename(CamperValleyUtils.getRenamedFilename(upFile.getOriginalFilename()));
					photo.setReviewNo(review.getReviewNo());
					review.addReviewPhoto(photo);
					
					File destFile = new File(saveDirectory, photo.getRenamedFilename());
					upFile.transferTo(destFile);
				}
			}
			
			int result = reviewService.updateReview(review);
			redirectAttr.addFlashAttribute("msg", "후기를 성공적으로 수정했습니다.");
		} catch (Exception e) {
			log.error("후기 수정 오류", e);
			throw e;
		}
		return "redirect:/community/review/reviewDetail?reviewNo=" + review.getReviewNo();
	}
	
	@GetMapping("/reviewDetail")
	public ModelAndView reviewDetail(ModelAndView mav, @RequestParam int reviewNo) {
		try {
			int result = reviewService.updateReadCount(reviewNo);
			CampsiteReviewExt review = reviewService.selectOneReview(reviewNo);
			mav.addObject("review", review);
		} catch (Exception e) {
			log.error("후기 상세 조회 오류", e);
			throw e;
		}
		return mav;
	}
	
	@PostMapping("/reviewDelete")
	public String reviewDelete(
			@RequestParam int reviewNo, 
			RedirectAttributes redirectAttr) {
		try {
			String delDirectory = application.getRealPath("/resources/upload/community/review");
			List<ReviewPhoto> photos = reviewService.selectOneReview(reviewNo).getPhotos();
			if(photos != null && !photos.isEmpty()) {
				for(ReviewPhoto photo : photos) {
					File delFile = new File(delDirectory, photo.getRenamedFilename());
					if(delFile.exists()) {
						delFile.delete();
					}
				}
			}
			
			int result = reviewService.deleteReview(reviewNo);
			redirectAttr.addFlashAttribute("msg", "후기를 성공적으로 삭제했습니다.");
		} catch (Exception e) {
			log.error("후기 삭제 오류", e);
			throw e;
		}
		return "redirect:/community/review/reviewList";
	}
	
}
