<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Hoşgeldiniz ${user.getFullName()} | ODTÜ SPOR KULÜBÜ</title>
		<link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:300,400,700&subset=latin,latin-ext">
		<link type="text/css" rel="stylesheet" href="res/css/home.css">
		<link type="text/css" rel="stylesheet" href="res/css/add-ons/bordomor_tam_ekran_stil.css">
		
		<script type="text/javascript" src="res/js/add-ons/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="res/js/add-ons/tools.js"></script>
		<script type="text/javascript" src="res/js/add-ons/bordomor_tam_ekran.js"></script>
		<script type="text/javascript" src="res/js/add-ons/bordomor_ajax.js"></script>
		<script type="text/javascript" src="res/js/obj/login_role.js"></script>
		<script type="text/javascript" src="res/js/login_roles.js"></script>
		<script type="text/javascript" src="res/js/common.js"></script>
	</head>
	<body>
		<div class="main_container">
		 	<article>
			 	<section class="main_data">
					<h1>BEN ${user.getFullName().toUpperCase(locale)}</h1>
					<p class="user_type">Kulüp Yöneticisiyim</p>
					<form autocomplete="off" method="post" action="auth/logout.jsp">
						<button type="button" onclick="window.location = './${user.getHomeURI()}'">Ana Sayfa</button>
						<button type="submit">Çıkış Yap</button>
					</form>
				</section>
				<section class="role_data">
					<h1>Yetki Kümeleri</h1>
					<table id="data_table">
						<thead>
							<tr>
								<th>BAŞLIK</th>
								<th>HEDEF KULLANICI TÜRÜ</th>
								<th>GÖMÜLÜ GRUP</th>
								<th>VERİ KESİNLİĞİ</th>
								<th>YARATILMA</th>
								<th>SON DEĞİŞİKLİK</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${roles}" var="nextRole">
								<tr data-id="${nextRole.getId()}">
									<td>${nextRole.getTitle()}</td>
									<td>
										<c:choose>
											<c:when test="${nextRole.getTargetedType().getSimpleName() == 'ClubManager'}">
												Kulüp Yöneticisi
											</c:when>
											<c:when test="${nextRole.getTargetedType().getSimpleName() == 'BranchManager'}">
												Branş Yöneticisi
											</c:when>
											<c:when test="${nextRole.getTargetedType().getSimpleName() == 'Trainer'}">
												Antrenör
											</c:when>
										</c:choose>
									</td>
									<td>${nextRole.isEmbedded() ? 'Evet' : 'Hayır'}</td>
									<td>${nextRole.isDefinitive() ? 'Kesin Giriş' : 'İzin Gerektirir'}</td>
									<td>${dtf.format(nextRole.getCreationTime())}</td>
									<td>${dtf.format(nextRole.getLastModifiedTime())}</td>
								</tr>
							</c:forEach>	
						</tbody>
					</table>
				</section>
				<section id="data_container" style="display: none; border-top: 1px solid #ccc; padding: 20px 10px;"></section>
			</article>
		</div>
	</body>
</html>