<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>ODTÜ Spor Kulübü Demo Sayfası'na Hoşgeldiniz</title>
		<link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Montserrat:400,700,900&subset=latin,latin-ext">
		
		<script type="text/javascript" src="../../res/js/add-ons/jquery-3.6.1.min.js"></script>
		<script type="text/javascript" src="../../res/js/common.js"></script>
		<script type="text/javascript" src="../../res/js/register.js"></script>
		
		<link type="text/css" rel="stylesheet" href="../../res/css/fonts.css">
		<link type="text/css" rel="stylesheet" href="../../res/css/ui_elements.css">
		<link type="text/css" rel="stylesheet" href="../../res/css/global.css">
		<link type="text/css" rel="stylesheet" href="../../res/css/register/common.css">
		<link type="text/css" rel="stylesheet" href="../../res/css/register/index.css">
	</head>
	<body>
		<div class="main_container">
			<img src="../../res/visual/logo.png"/>
			<figure></figure>
			<section class="centerer">
				<div>
					<h1>ODTÜ Spor Kulübü<br>Deneme Kaydı</h1>
					<h2 id="step_data">Adım 1/3</h2>
					<div class="options_container">
						<form id="player_data_form" class="form_unit">
							<section>
								<h3>Sporcu Temel Bilgileri</h3>
								<div class="input_container select_container">
									<select id="branch_select"></select>
									<label for="branch_select">Branşı</label>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" placeholder=" " required/>
									<label for="user_name_input">Adı</label>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" placeholder=" " required/>
									<label for="user_name_input">Soyadı</label>
								</div>
								<div class="inline_input_row">
									<div class="input_container select_container" style="flex: 1;">
										<select id="gender_select"></select>
										<label for="gender_select">Cinsiyet</label>
									</div>
									<div class="input_container">
										<input type="text" id="user_name_input" name="user_name" mode="datepicker" placeholder=" " required/>
										<label for="user_name_input">Doğum Tarihi</label>
									</div>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" mode="phonenumber" placeholder=" " required disabled="disabled"/>
									<label for="user_name_input">Telefon</label>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" mode="phonenumber" placeholder=" " required disabled="disabled"/>
									<label for="user_name_input">E-posta</label>
								</div>
							</section>
							<div style="flex: none; width: calc(100% - 40px); margin: 0px 20px 0px 20px; text-align: right">
								<button type="button" class="forward" style="min-width: 75px;">İleri</button>
							</div>
						</form>
						<form id="parent_data_form" class="form_unit">
							<section style="height: 385px;">
								<h3>Veli Bilgileri</h3>
								<div class="inline_input_row">
									<div class="input_container">
										<input type="radio" id="mother_select" name="parenthood_select" value="0" checked="checked"/>
										<label for="mother_select">Anne</label>
									</div>
									<div class="input_container">
										<input type="radio" id="father_select" name="parenthood_select" value="1"/>
										<label for="father_select">Baba</label>
									</div>
									<div class="input_container">
										<input type="radio" id="father_select" name="parenthood_select" value="1"/>
										<label for="father_select">Diğer</label>
									</div>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" placeholder=" " required/>
									<label for="user_name_input">Adı</label>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" placeholder=" " required/>
									<label for="user_name_input">Soyadı</label>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" mode="phonenumber" placeholder=" " required/>
									<label for="user_name_input">Telefon</label>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" mode="phonenumber" placeholder=" " required/>
									<label for="user_name_input">E-posta</label>
								</div>
								<div class="input_container">
									<input type="text" id="user_name_input" name="user_name" mode="phonenumber" placeholder=" " required/>
									<label for="user_name_input">Araç Plakası</label>
								</div>	
							</section>
							<div style="flex: none; width: calc(100% - 40px); margin: 0px 20px 0px 20px; text-align: right">
								<button type="button" class="back" style="min-width: 75px;">Geri</button>
								<button type="button" class="forward" style="min-width: 75px;">İleri</button>
							</div>
						</form>
						<form id="training_data_form" class="form_unit">
							<section>
								<h3>Antrenman Seçimi</h3>
								<div class="input_container">
									<input type="radio" id="training_input_1" name="training" required/>
									<label for="training_input_1">
										<strong>ODTÜ SUTOPU U-16</strong><br>
										12/01/2023 | 15:00 - 17:00 | Test Lokasyon - 1<br>
										<span>Güzeltepe Mah. Ahmet Rasim Sokak No: 35/1<br>
										Çankaya/Ankara</span>
									</label>
								</div>
								<div class="input_container">
									<input type="radio" id="training_input_2" name="training" required/>
									<label for="training_input_2">Antrenman 2</label>
								</div>
								<div class="input_container">
									<input type="radio" id="training_input_3" name="training" required/>
									<label for="training_input_3">Antrenman 3</label>
								</div>
								<div class="input_container">
									<input type="radio" id="training_input_4" name="training" required/>
									<label for="training_input_4">Antrenman 4</label>
								</div>
							</section>
							<section id="map_container">
								<h3>Konum</h3>
								<div class="map_frame"></div>
								<div class="map_controls"></div>
							</section>
							<div style="flex: none; width: calc(100% - 40px); margin: 0px 20px 0px 20px; text-align: right">
								<button type="button" class="back" style="min-width: 75px;">Geri</button>
								<button type="submit" style="min-width: 75px;">Kaydı Tamamla</button>
							</div>
						</form>
					</div>
				</div>
			</section>
		</div>
	</body>
</html>