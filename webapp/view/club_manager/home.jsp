<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Hoşgeldiniz ${loggedUser.getFullName()} | ODTÜ SPOR KULÜBÜ</title>
		<link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:400,700,900&subset=latin,latin-ext">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/fonts.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/ui_elements.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/global.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/home.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/home.js?ver=${verStr}"></script>
	</head>
	<body>
		<div class="main_container">
			${loggedUser.generatePortalTopBar()}
		 	${loggedUser.generatePortalLeftMenu()}
		 	<section class="content_container">
		 		<div>
		 			<button class="clear_style"><img src="res/visual/icon/photo_placeholder.png"/></button>
		 			<p>${loggedUser.getName()} ${loggedUser.getSurname().toUpperCase(locale)}<br><span>Kulüp Yöneticisi</span></p>
		 		</div>
		 	</section>
		</div>
	</body>
</html>