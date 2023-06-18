<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>ODTÜ Spor Kulübü Demo Sayfası'na Hoşgeldiniz</title>
		<link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,900&subset=latin,latin-ext" rel="stylesheet" type="text/css">
		<link type="text/css" rel="stylesheet" href="res/css/fonts.css">
		<link type="text/css" rel="stylesheet" href="res/css/ui_elements.css">
		<link type="text/css" rel="stylesheet" href="res/css/global.css">
		<link type="text/css" rel="stylesheet" href="res/css/index.css">
	</head>
	<body>
		<div class="main_container">
			<img src="res/visual/logo.png"/>
			<figure></figure>
			<section class="centerer">
				<form autocomplete="off" method="post" action="auth/web_login.jsp">
					<h1>ODTÜ Spor Kulübü<br>Kullanıcı Portalı</h1>
					<p>Portal'a giriş yaparak hesabınıza tanımlı yetkiler ile <br>sistemi kullanmaya başlayabilirsiniz.</p>
					<div class="page_options_container">
						<button type="button" class="clear_style">Giriş</button>
						<a href="registration/index.jsp">Yeni Kayıt</a>
					</div>
					<c:choose>
						<c:when test="${error == 'bad_parameters'}">
							<p id="error_msg">Lütfen geçerli bir e-posta adresi ve 8 ile 16 karakter aralığında bir şifre girdiğinizden emin olun ve tekrar deneyin.</p>
						</c:when>
						<c:when test="${error == 'no_user'}">
							<p id="error_msg">Hatalı bir e-posta adresi ya da şifre girdiniz. Lütfen bilgilerinizi gözden geçirip tekrar deneyin.</p>
						</c:when>
						<c:when test="${error == 'unauthorized_user'}">
							<p id="error_msg">Sistem kullanımınız açık değil ya da hesabınız askıya alınmış durumda. Hesabınız etkinleştirilene kadar sisteme giriş yapamazsınız.</p>
						</c:when>
						<c:when test="${error == 'unconfirmed_user'}">
							<p id="error_msg">Hesap kurulumunuz devam ediyor. Hesap bilgileriniz kayıt olduğunuz kurum tarafından onaylandıktan sonra sisteme giriş yapabilirsiniz.</p>
						</c:when>
					</c:choose>
					<div class="input_container">
						<input type="text" id="user_name_input" name="user_name" placeholder=" " required/>
						<label for="user_name_input">Kullanıcı Adı</label>
					</div>
					<div class="input_container">
						<input type="password" id="pwd_input" name="pwd" placeholder=" " required/>
						<label for="pwd_input">Şifre</label>
					</div>
					<div class="checkbox_container">
						<input type="checkbox" id="session_checkbox"/>
						<label for="session_checkbox">Oturumu Açık Tut</label>
					</div>
					<div class="controls_container">
						<button type="submit">Giriş Yap</button>
						<a href="javascript:void(0)">Şifrenizi mi Unuttunuz?</a>
					</div>
				</form>
			</section>
		</div>
	</body>
</html>