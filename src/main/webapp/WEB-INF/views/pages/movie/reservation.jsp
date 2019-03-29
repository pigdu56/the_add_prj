<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<meta charset="utf-8">
<style>
ul>li {
	list-style: none;
}

#reser_table {
	text-align: center;
	min-height:502px;
	margin:0;	
}

.re_title {
	background-color: #1D1D1C;
	color: #CBCBCB;
	min-height:52px;
}

.re_title>td {
	border-right: 2px darkgray solid;
}

.re_main {
	background-color: #F2F0E5;
	min-height:450px;
}

.re_main>td {
	border-right: 2px darkgray solid;
}

.re_select {
	padding: 0;
	cursor: pointer;
}
#select_box{
	width:100%;
	height:105px;
	background-color:#1D1D1C;
	color:white;
}
#selects{	
	padding:10px;
	color:white;
}
#go_seat{
	border:0;
	background-color:#1D1D1C;
	padding:0;	
}
.box_th{
	border-right:1px #5B5B5B solid;
}
.box_title{
	padding:0;
	text-align:center;	
	color:#5B5B5B;
}
#go_btn{	
	padding:0;
	text-align:center;	
}
.check_choice{
	background-color:#1D1D1C;
	color:white;
}
</style>
	<div class="container">
		<div class="row">
			<div class="col-sm-1"></div>
			<div class="col-sm-10">
				<h1 style="text-align: center;">영화 예매</h1>
				<table class="table" id="reser_table">
					<tr class="row re_title">
						<td class="col-sm-4"><h4>영화</h4></td>
						<td class="col-sm-3"><h4>극장</h4></td>
						<td class="col-sm-1"><h4>날짜</h4></td>
						<td class="col-sm-4"><h4>시간</h4></td>
					</tr>
					<tr class="row re_main">
						<td class="col-sm-4">
							<ul class="re_select" id="mv_choice">
								<li>캡틴마블</li>
								<li>돈</li>		
							</ul>
						</td>
						<td class="col-sm-3">
							<ul class="re_select" id="c_choice">
								<li>천안터미널CGV</li>
								<li>천안역 CGV</li>
								<li>천안펜타포트 CGV</li>
							</ul>
						</td>
						<td class="col-sm-1">
							<ul class="re_select" id="sd_choice">
								<li style="font-size: 13px;">
								<c:set var="now" value="<%=new java.util.Date()%>" /> 
								<fmt:formatDate value="${now}" type="date" pattern="YYYY" var="years" />${years}</li>
								<li style="font-size: 30px;">
								<fmt:formatDate value="${now}" type="date" pattern="MM" var="mon" />${mon}</li>
								<c:forEach var="i" items="${daylist}">
									<c:choose>
										<c:when test="${i.dates eq 01}">
											<li>
												<c:choose>
													<c:when test="${mon eq 01}">
														<fmt:parseDate value="${years+1}" pattern="YYYY" var="ye"/>
														<fmt:formatDate value="${ye}" type="date" pattern="YYYY" var="years2"/>
														${years2} 
													</c:when>
													<c:otherwise>
														${years}
													</c:otherwise>
												</c:choose>
											</li>
											<li style="font-size: 30px;">
												<fmt:parseDate value="${mon+1}" pattern="MM" var="months"/>
												<fmt:formatDate value="${months}" type="date" pattern="MM" var='mon2'/> ${mon2}												
											</li>
											<li class="sd_dates">
												<b>${i.days}</b>&nbsp;<b>${i.dates}</b>
												<c:choose>
													<c:when test="${mon eq 01}">
														<input type="hidden" name='s_dates' value="${years2}${mon2}${i.dates}">
													</c:when>
													<c:otherwise>
														<input type="hidden" name='s_dates' value="${years}${mon2}${i.dates}">
													</c:otherwise>
												</c:choose>
											</li>
										</c:when>
										<c:otherwise>
											<li class="sd_dates">
												<b>${i.days}</b>&nbsp;<b>${i.dates}</b>
													<c:choose>
														<c:when test="${empty mon2}">
															<input type="hidden" name='s_dates' value="${years}${mon}${i.dates}">
														</c:when>
														<c:otherwise>
															<input type="hidden" name='s_dates' value="${years}${mon2}${i.dates}">
														</c:otherwise>
													</c:choose>
											</li>
										</c:otherwise>
									</c:choose>								
								</c:forEach>

							</ul>
						</td>
						<td class="col-sm-4"></td>
					</tr>
				</table>
			</div>
			<div class="col-sm-1"></div>
		</div>
		<div class='row'>
			<div class="col-sm-1"></div>
			<div class="col-sm-10" style="padding:0;">
				<c:url var="go_seat" value="/movie/reservation_seat" />
				<form method="get" action="${go_seat}">
				<table id="select_box">
					<tr class="row" id="selects">
						<th class="col-sm-2 box_th">
							<h3 class="box_title">영화선택</h3>
						</th>
						<th class="col-sm-2 box_th">
							<h3 class="box_title">극장선택</h3>
						</th>
						<th class="col-sm-2 box_th">
							<h3 class="box_title">좌석선택</h3>
						</th>
						<th class="col-sm-2 box_th">
							<h3 class="box_title">결제</h3>
						</th>
						<th class="col-sm-2"></th>
						<th class="col-sm-2" id="go_btn">
							<button class="btn" id="go_seat" type="submit">
								<img class="btn-img" src="${pageContext.request.contextPath}/static/img/movie/right_seat.png">
							</button>
						</th>
					</tr>
				</table>
				</form>
			</div>
			<div class="col-sm-1"></div>
		</div>
	</div>
<script>
$(document).ready(function(){
	var mv_check = false;
	var c_check = false;
	var sd_check = false;
	var mv_li;
	var c_li;
	var sd_li;
	var sd_dates;
	$("#mv_choice li").click(function(){
		$("#mv_choice li").removeClass("check_choice");
		mv_li=$(this);
		$(mv_li).addClass("check_choice");
		mv_check=true;
		if(c_check && sd_check){
			alert("영화 : "+mv_li.text()+" 영화관 : "+c_li.text()+" 날짜 "+ sd_dates);
		}else if(!sd_check || !c_check){
			if(!sd_check && !c_check){
				alert("날짜와 영화관을 선택해주세요.");
			}else if(!c_check){
				alert("영화관을 선택해주세요.");
			}else{
				alert("날짜를 선택해주세요.");
			}
		}
	});
	$("#c_choice li").click(function(){
		$("#c_choice li").removeClass("check_choice");
		c_li=$(this);
		c_check=true;
		$(c_li).addClass("check_choice");
		if(mv_check && sd_check){
			alert("영화 : "+mv_li.text()+" 영화관 : "+c_li.text()+" 날짜 "+ sd_dates);
		}else if(!mv_check || !sd_check){
			if(!mv_check && !sd_check){
				alert("영화와 날짜를 선택해주세요.");
			}else if(!sd_check){
				alert("날짜를 선택해주세요.");
			}else{
				alert("영화를 선택해주세요.");
			}
		}
	});
	$("#sd_choice .sd_dates").click(function(){
		$("#sd_choice .sd_dates").removeClass("check_choice");
		sd_li=$(this);
		sd_dates = $(this).find("input").val();
		sd_check = true;
		$(sd_li).addClass("check_choice");
		if(mv_check && c_check){
			alert("영화 : "+mv_li.text()+" 영화관 : "+c_li.text()+" 날짜 "+ sd_dates);
		}else if(!mv_check || !c_check){
			if(!mv_check && !c_check){
				alert("영화와 영화관을 선택해주세요.");
			}else if(!c_check){
				alert("영화관을 선택해주세요.");
			}else{
				alert("영화를 선택해주세요.");
			}
		}
	});
	
});
	
</script>