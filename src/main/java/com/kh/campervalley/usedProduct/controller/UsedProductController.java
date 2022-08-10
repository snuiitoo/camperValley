package com.kh.campervalley.usedProduct.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kh.campervalley.usedProduct.model.dto.UsedProduct;
import com.kh.campervalley.usedProduct.model.service.UsedProductService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping(value = "/usedProduct")
public class UsedProductController  {
	
	@Autowired
	private UsedProductService usedProductService;

	/* 상품 등록 */
	@GetMapping("/product/productEnroll")
	public void productEnrollPage() {};
	
	@PostMapping("/product/productEnroll")
	@ResponseBody // json
	public ModelAndView productEnroll(@ModelAttribute UsedProduct usedProduct, 
											@RequestParam MultipartFile[] img, Principal principal) {
		// 이미지 파일 복사
		String filePath = "C:/Users/haneu/git/camperValley/src/main/webapp/resources/images/usedProduct";
		File file;
		
		for(int i = 0; i <= img.length - 1; i++) {
			String fileName = img[i].getOriginalFilename();
			file = new File(filePath, fileName);
			
			try {
				FileCopyUtils.copy(img[i].getInputStream(), new FileOutputStream(file));
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(i == 0) usedProduct.setProductImg1(fileName);
			if(i == 1) usedProduct.setProductImg2(fileName);
			if(i == 2) usedProduct.setProductImg3(fileName);
			if(i == 3) usedProduct.setProductImg4(fileName);
			if(i == 4) usedProduct.setProductImg5(fileName);
		}
		
		// 로그인한 회원의 아이디
		usedProduct.setSellerId(principal.getName()); 
		
		// DB 
		usedProductService.productInsert(usedProduct);
		
		// 최근 거래지역
		String sellerId = principal.getName();
		String recentLocation = usedProduct.getProductLocation();
		Map<String, String> map = new HashMap<String, String>();
		map.put("sellerId", sellerId);
		map.put("recentLocation", recentLocation);
		
		// 등록한 상품 no 가져오기
		int no = usedProductService.getProductNo();
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("no", no);
		mav.setViewName("jsonView");
		return mav;
	};
	
	/* 메인페이지 */
	@GetMapping("/main/mainPage")
	public String mainPage() {
		return "/usedProduct/main/mainPage";
		
	};
	
	@PostMapping("/main/getProductList") 
	@ResponseBody
	public ModelAndView getProductList(@RequestParam(name = "page") int page) {
		List<UsedProduct> list = usedProductService.getProductList(page);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("list", list);
		mav.setViewName("jsonView");
		return mav;
	}
	
	@GetMapping("/main/searchDisplay")
	public void searchDisplay() {};
	
	@GetMapping("/main/cateDisplay")
	public void cateDisplay() {};
	
	@GetMapping("/product/productDetail")
	public void productDetail() {};
	
	@GetMapping("/product/productUpdate")
	public void productUpdate() {};
	
	@PostMapping("/chat/chat")
	public void chat() {};
	
	@PostMapping("/chat/chatList")
	public void chatList() {};
}
