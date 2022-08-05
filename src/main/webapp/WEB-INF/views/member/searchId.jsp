<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/views/common/header.jsp"></jsp:include>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/member/searchInfo.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/member/memberCommon.css" />
	<div class="mx-auto mt-5 container p-5 shadow-sm border"  id="search-my-info">
		<div  class="d-flex justify-content-center" >
			<a href="${pageContext.request.contextPath}/member/searchId" class="btn btn-block font-weight-bold pb-3 camper-color">아이디 찾기</a>
			<a href="${pageContext.request.contextPath}/member/searchPassword" class="btn btn-block m-0 font-weight-bold pb-3">비밀번호 찾기</a>
		</div>
		<form:form method="post" >
			<div class="mt-4 pt-4 input-group-lg">
				<input 
					type="text" class="form-control mb-3" name="name"
					placeholder="이름" autocomplete="off" required>
				<input
					type="email" class="form-control my-3 input-lg" name="email"
					placeholder="이메일" autocomplete="off" required>
			</div>
		    <button class="btn btn-camper-color btn-block btn-lg font-weight-bold">확인</button>
			<p class="pt-4">&nbsp;-&nbsp;&nbsp;&nbsp;가입시 등록한 이름과 이메일을 입력해주세요.</p>					
		</form:form>
	</div>
	<script>
	
	</script>
<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>