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
					<h1>KULLANICI HESAPLARI</h1>
					<table>
						<thead>
							<tr>
								<th>AD SOYAD</th>
								<th>BRANŞ</th>
								<th>GÖREV</th>
								<th>YETKİ GRUBU</th>
								<!-- <th>YARATILMA</th>
								<th>SON DEĞİŞİKLİK</th>  -->
								<th>DURUM</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${users}" var="nextUser">
								<tr>
									<td>${nextUser.getFullName()}</td>
									<td>${(nextUser['class'].simpleName == 'BranchManager' || nextUser['class'].simpleName == 'Trainer') ? nextUser.getBranch().getName() : '-'}</td>
									<td>
										<c:choose>
											<c:when test="${nextUser['class'].simpleName == 'ClubManager'}">
												Kulüp Yöneticisi
											</c:when>
											<c:when test="${nextUser['class'].simpleName == 'BranchManager'}">
												Branş Yöneticisi
											</c:when>
											<c:when test="${nextUser['class'].simpleName == 'Trainer'}">
												Antrenör
											</c:when>
										</c:choose>
									</td>
									<td>${empty nextUser.getRole() ? '-' : nextUser.getRole().getTitle()}</td>
									<!-- <td>${dtf.format(nextUser.getCreationTime())}</td>
									<td>${dtf.format(nextUser.getLastModifiedTime())}</td>  -->
									<td>${nextUser.getState().getDesc()}</td>
								</tr>
							</c:forEach>	
						</tbody>
					</table>
				</section>
			</article>
		</div>
	</body>
</html>