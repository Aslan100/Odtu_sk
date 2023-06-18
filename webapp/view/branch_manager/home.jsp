<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Hoşgeldiniz ${user.getFullName()} | ODTÜ SPOR KULÜBÜ</title>
		<link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,900&subset=latin,latin-ext" rel="stylesheet" type="text/css">
		<link type="text/css" rel="stylesheet" href="../res/css/fonts.css">
		<link type="text/css" rel="stylesheet" href="../res/css/ui_elements.css">
		<link type="text/css" rel="stylesheet" href="../res/css/global.css">
		<link type="text/css" rel="stylesheet" href="../res/css/home.css">
		<script type="text/javascript" src="../res/js/add-ons/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="../res/js/add-ons/tools.js"></script>
		<script type="text/javascript" src="../res/js/home.js"></script>
	</head>
	<body>
		<div class="main_container">
			<ul class="top_menu">
	 			<li class="search_item"><input type="text" placeholder="Ara"/></li>
	 			<li>
	 				<button class="clear_style"><img src="../res/visual/icon/top_panel/notification.png"/></button>
	 				<ul class="placeholder">
	 					 <li>
	 					 	<img src="../res/visual/bildirim.png"/>
	 					 	<h2>Bildirimler</h2>
	 					 	<p>Takımınız, sporcularınız ve antrenmanlarınız ile ilgili güncellemeleri takip edebilirsiniz.</p>
	 					 </li>
	 				</ul>
 				</li>
	 			<li>
	 				<button class="clear_style"><img src="../res/visual/icon/top_panel/email.png"/></button>
	 				<ul class="placeholder">
	 					 <li>
	 					 	<img src="../res/visual/mesaj.png"/>
	 					 	<h2>Mesajlar</h2>
	 					 	<p>Kulüp ve branş yöneticilerinizden gelen mesajları takip edebilirsiniz.</p>
	 					 </li>
	 				</ul>
 				</li>
	 			<li>
	 				<button class="clear_style"><img src="../res/visual/icon/top_panel/calendar.png"/></button>
	 				<ul class="placeholder">
	 					 <li>
	 					 	<img src="../res/visual/etkinlik.png"/>
	 					 	<h2>Etkinlikler</h2>
	 					 	<p>Haftalık antrenman saatlerinizi ve etkinlik takviminizi takip edeblirsiniz.</p>
	 					 </li>
	 				</ul>
 				</li>
	 			<li>
	 				<button class="clear_style no_padding"><img src="../res/visual/icon/top_panel/account.png"/></button>
	 				<ul>
	 					<li class="user_data">
	 						<p>${user.getName()} ${user.getSurname().toUpperCase(locale)}</p>
	 						<p>${user.getEmail()}</p>
	 					</li>
	 					<li><a href="javascript:void(0)">Hesap Ayarlarım</a></li>
	 					<li><a href="javascript:void(0)">Şifre Sıfırlama</a></li>
	 					<li><a href="javascript:void(0)">Gizlilik Politikası</a></li>
	 					<li><a href="javascript:void(0)">Çıkış Yap</a></li>
 					</ul>
 				</li>
 			</ul>
		 	<nav>
		 		<img src="../res/visual/logo.png"/>
		 		<ul>
		 			<li class="selected_item"><img src="../res/visual/icon/navigation_panel/home.png"/><a href="javascript:void(0)">Ana Sayfa</a></li>
		 			<li><img src="../res/visual/icon/navigation_panel/account.png"/><button class="clear_style">Kullanıcı Hesapları</button></li>
		 			<li><img src="../res/visual/icon/navigation_panel/branch.png"/><a href="javascript:void(0)">Branşlar</a></li>
		 			<li><img src="../res/visual/icon/navigation_panel/team.png"/><a href="javascript:void(0)">Takımlar</a></li>
		 			<li><img src="../res/visual/icon/navigation_panel/athlete.png"/><a href="../athletes.jsp">Sporcular</a></li>
		 			<li><img src="../res/visual/icon/navigation_panel/training_ground.png"/><a href="javascript:void(0)">Tesisler</a></li>
		 			<li><img src="../res/visual/icon/navigation_panel/calendar.png"/><button class="clear_style">Antrenman Takvimi</button></li>
		 			<li><img src="../res/visual/icon/navigation_panel/payment.png"/><button class="clear_style">Ödemeler</button></li>
		 		</ul>
		 	</nav>
		 	<section class="content_container">
		 		<nav>
		 			
		 		</nav>
		 		<div>
		 			<button class="clear_style"><img src="../res/visual/icon/photo_placeholder.png"/></button>
		 			<p>${user.getName()} ${user.getSurname().toUpperCase(locale)}<br><span>${user.getBranch().getName()} Branş Yöneticisi</span></p>
		 		</div>
		 	</section>
		</div>
	</body>
</html>