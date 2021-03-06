<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/trip/css/css.css" />
	<section>
		<c:url value="/trip/timg" var="timg" />
		<form action="${timg}" method="get">
			<table>
				<tr>
					<td><select id="s_thema_lest"  id="theme">
							<option>테마</option>
							<option>여행지</option>
							<option>축제</option>
					</select></td>
					<td><input placeholder="지역을 입력하세요 " type="text" id="s_msearch" name="title"></input></td>
					<td><input type="submit" value="검색" id="s_bt"></td>
				</tr>
			</table>
		</form>
	</section>


	<br><hr id="f_hr"><br>


	<ul id="c_ul">
		<li class="bdr_nor" id="contentlist">
			<c:forEach var="i" items="${list}">
			<div id="c_photo">
				<a href="javascript:"
					onclick="goDetail(&quot;304&quot;,&quot;456e69d1-4fa2-413d-bd58-5bf85af50675&quot;)">
					<img src="${i.galWebImageUrl}"
					id="c_img_main">
				</a>
			</div>
			<div id="c_area_txt">
				<div id="c_tit">${i.galTitle}</div>
				<p>${i.galPhotographyLocation}</p>

				<p id="c_tag">
					<span>#${i.galSearchKeyword}</span>
				</p>
				<p></p>
			</div>
			</c:forEach>
		</li>
	</ul>
