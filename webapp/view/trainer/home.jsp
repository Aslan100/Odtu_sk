<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Hoşgeldiniz ${user.getFullName()} | ODTÜ SPOR KULÜBÜ</title>
		<link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:300,400,700&subset=latin,latin-ext">
		<link type="text/css" rel="stylesheet" href="../res/css/home.css">
	</head>
	<body>
		<div class="main_container">
		 	<article>
			 	<section class="main_data">
					<h1>BEN ${user.getFullName().toUpperCase(locale)}</h1>
					<p class="user_type">${user.getBranch().getName()} Antrenörüyüm</p>
				</section>
				<section class="role_data">
					<h1>YETKİLERİM</h1>
					<p class="role_title">${user.getRole().getTitle()}</p>
					<table>
						<thead>
							<tr>
								<th>KULLANICILAR</th>
								<th>YETKİLENDİRME</th>
								<th>BRANŞLAR</th>
								<th>TAKIMLAR</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>${user.getRole().getUserMod().getDesc().toUpperCase(locale)}</td>
								<td>${user.getRole().getRoleMod().getDesc().toUpperCase(locale)}</td>
								<td>${user.getRole().getBranchMod().getDesc().toUpperCase(locale)}</td>
								<td>${user.getRole().getTeamMod().getDesc().toUpperCase(locale)}</td>
							</tr>
						</tbody>
					</table>
					<table>
						<thead>
							<tr>
								<th>ANTRENMAN NOKTALARI</th>
								<th>ANTRENMAN VE KAMP PLANLAMA</th>
								<th>ÖDEME ÖZELLİKLERİ</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>${user.getRole().getFacilityMod().getDesc().toUpperCase(locale)}</td>
								<td>${user.getRole().getEventMod().getDesc().toUpperCase(locale)}</td>
								<td>${user.getRole().getPaymentMod().getDesc().toUpperCase(locale)}</td>
							</tr>
						</tbody>
					</table>
				</section>
				<form autocomplete="off" method="post" action="../auth/logout.jsp">
					<button type="submit">Çıkış Yap</button>
				</form>
			</article>
		</div>
	</body>
</html>