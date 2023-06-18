<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>ODTÜ Spor Kulübü Demo Sayfası'na Hoşgeldiniz</title>
		<link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:400,600,700&subset=latin,latin-ext">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/fonts.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/ui_elements.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/global.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/registration/common.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/registration/index.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/bordomor_dialogue.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-3.6.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/settings/branch_settings.js?ver=${verStr}"></script>
	</head>
	<body>
		<div class="main_container">
			<img src="${linkPrefix}res/visual/logo.png"/>
			<figure></figure>
			<section class="centerer">
				<div>
					<h1>ODTÜ Spor Kulübü<br>Branş Ayarları</h1>
					<h2>Branşınızı Seçin</h2>
					<div class="options_container">
						<form id="branch_settings_form" class="form_unit">
							<div class="sections elastic">
								<section style="display: flex; flex-flow: row nowrap; align-items: center; justify-content: space-between;">
									<c:forEach items="${branches}" var="nextBranch">
										<div>
											<img style="display: inline-block; height: 40px; vertical-align: middle" src="${linkPrefix}res/visual/icon/branch/${nextBranch.getTitle().toLowerCase().replace(' ', '_')}.png"/>
											<p style="display: inline-block; vertical-align: middle; margin-left: 15px; font-weight: 600">${nextBranch.getTitle().toUpperCase()}</p>
										</div>
									</c:forEach>
								</section>	
							</div>
							<!-- <div class="sections elastic">
								<section>
									<div class="input_container select_container">
										<select id="branch_select" name="id" placeholder=" ">
											<option value="-1" selected="selected" disabled="disabled"></option>
											<c:forEach items="${branches}" var="nextBranch">
												<option value="${nextBranch.getId()}">${nextBranch.getTitle()}</option>
											</c:forEach>
										</select>
										<label for="branch_select">Branş</label>
									</div>
								</section>
								<section><h3>&nbsp;</h3></section>
							</div> -->
							<div class="sections elastic">
								<section>
									<h3>Temel Özellikler</h3>
									<div class="inline_input_row equal_size_inputs">
										<div class="input_container">
											<input type="text" id="title_input" name="title" placeholder=" "/>
											<label for="title_input">Branş Tanımı</label>
										</div>
										<div class="input_container">
											<input type="number" id="penalty_rate_input" name="penalty_rate" min="0" max="10" step="0.5" placeholder=" "/>
											<label for="penalty_rate_input">Ödeme Gecikme Faizi</label>
										</div>
								</section>
								<section>
									<h3>&nbsp;</h3>
									<!-- <div class="input_container">
										<input type="number" id="sibling_discount_ratio_input" name="sibling_discount_ratio" min="0" max="100" step="1" placeholder=" "/>
										<label for="sibling_discount_ratio_input">Kardeş İndirim Oranı</label>
									</div>  -->
								</section>
							</div>
							<div class="sections elastic">
								<section>	
									<h3>Aidat Bilgileri</h3>
									<div class="input_container">
										<input type="number" id="daily_price_input" name="daily_price" min="0" max="10000" step="1" placeholder=" "/>
										<label for="daily_price_input">Günlük</label>
									</div>
									<div class="input_container">
										<input type="number" id="weekly_price_input" name="weekly_price" min="0" max="70000" step="1" placeholder=" "/>
										<label for="weekly_price_input">Haftalık</label>
									</div>
									<div class="input_container">
										<input type="number" id="monthly_price_input" name="monthly_price" min="0" max="70000" step="1" placeholder=" "/>
										<label for="monthly_price_input">Aylık</label>
									</div>
									<div class="input_container">
										<input type="number" id="annual_price_input" name="annual_price" min="0" max="70000" step="1" placeholder=" "/>
										<label for="annual_price_input">Yıllık</label>
									</div>
								</section>
								<section class="placeholder payment no_data">
									<h3>İndirim Tanımları</h3>
								</section>
							</div>
							<div class="controls">
								<input type="hidden" name="data_mode" value="2">
								<button type="button" class="back">Geri Dön</button>
								<button type="button" id="submit_button" class="forward">Kaydet</button>
							</div>
						</form>
					</div>
				</div>
			</section>
		</div>
	</body>
</html>