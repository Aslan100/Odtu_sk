<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Hoşgeldiniz ${loggedUser.getFullName()} | ODTÜ SPOR KULÜBÜ</title>
		<link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:400,600,700&subset=latin,latin-ext">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/fonts.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/ui_elements.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/tables.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/global.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/athlete.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/locations.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/bordomor_dialogue.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/datepicker.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/datepicker.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/locations.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/city.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/district.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/address.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/location.js?ver=${verStr}"></script>
		<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBwKkHsWOrKCk-BAeyeWXo898QUXaC5GTM&callback=initMap" async="true" defer></script>
	</head>
	<body>
		<div class="main_container">
		 	${loggedUser.generatePortalTopBar()}
		 	${loggedUser.generatePortalLeftMenu()}
		 	<section class="content_container">
		 		<c:choose>
					<c:when test="${dataMode == 0}">
						<div class="dbman_result_container delete">
							<p>TESİS SİLİNDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 1}">
						<div class="dbman_result_container">
							<p>TESİS YARATILDI</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 2}">
						<div class="dbman_result_container edit">
							<p>TESİS GÜNCELLENDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
				</c:choose>
		 		<div class="table_container">
		 			<header>
		 				<h1>TESİSLER</h1>
		 				<div class="controls_container">
		 					<span class="input_container">
		 						<input type="text" class="filter_input" data-for="locations_table" placeholder="Tabloda Ara"/>
	 							<button id="filters_button" class="clear_style"></button>
	 						</span>
		 					<button id="new_item" class="clear_style">YENİ</button>
		 				</div>
		 			</header>
		 			<div class="body">
			 			<table class="data_table" id="locations_table">
			 				<thead>
			 					<tr>
			 						<th>ADI</th>
				 				</tr>
			 				</thead>
			 				<tbody>
			 					<c:forEach items="${locations}" var="nextLoc">
				 					<tr data-code="${nextLoc.getId()}" ${nextLoc.getId() == itemId ? "data-is_selected_item='true'" : ""}>
				 						<td class="collapse">
				 							<span class="colour_bullet" style="background-color: ${nextLoc.getRepresentingColourHex()}">&bull;</span> ${nextLoc.getName()}
				 							<p>${nextLoc.getAddress().getAddressString()}</p>
				 							<p>${nextLoc.getAddress().getDistrictAndCity()}</p>
				 							<div>
				 								<button id="edit_location_button" class="clear_style" title="Tesis bilgilerini güncelle"></button>
				 								<button id="delete_location_button" class="clear_style" title="Tesisi sil"></button>
			 								</div>
				 						</td>
				 					</tr>
			 					</c:forEach>
			 				</tbody>
			 				<tfoot>
								<tr>
									<td colspan="100" class="controls">
										<div>
											<span id="totals" class="table_data">Toplam <span>##</span> tesis</span>
											<span id="filter_data" class="table_data"></span>
											<div class="navigation">
												<button id="first_page" class="clear_style">&#171;</button>
												<button id="prev_page" class="clear_style">&#8249;</button>
												<span id="page_data">Sayfa: #/#</span>
												<button id="next_page" class="clear_style">&#8250;</button>
												<button id="last_page" class="clear_style">&#187;</button>
											</div>
										</div>
									</td>
								</tr>
							</tfoot>
			 			</table>
			 		</div>
				 	<div class="stats_container">
				 		<header>DOLULUK</header>
				 		<div>
				 			<div class="row">
				 				<span>Pazartesi</span>
				 				<div class="data_bar"><div class="indicator"></div></div>
				 				<span class="percentage"></span>
				 			</div>
				 			<div class="row">
				 				<span>Salı</span>
				 				<div class="data_bar"><div class="indicator"></div></div>
				 				<span class="percentage"></span>
				 			</div>
				 			<div class="row">
				 				<span>Çarşamba</span>
				 				<div class="data_bar"><div class="indicator"></div></div>
				 				<span class="percentage"></span>
				 			</div>
				 			<div class="row">
				 				<span>Perşembe</span>
				 				<div class="data_bar"><div class="indicator"></div></div>
				 				<span class="percentage"></span>
				 			</div>
				 			<div class="row">
				 				<span>Cuma</span>
				 				<div class="data_bar"><div class="indicator"></div></div>
				 				<span class="percentage"></span>
				 			</div>
				 			<div class="row">
				 				<span>Cumartesi</span>
				 				<div class="data_bar"><div class="indicator"></div></div>
				 				<span class="percentage"></span>
				 			</div>
				 			<div class="row">
				 				<span>Pazar</span>
				 				<div class="data_bar"><div class="indicator"></div></div>
				 				<span class="percentage"></span>
				 			</div>
				 		</div>
				 		<button id="go_to_location_button" class="dark" disabled="disabled">Planı İncele</button>
				 	</div>
			 		<div id="map_container">
			 			<header>KONUM</header>
			 			<div class="map_frame">
			 				<div id="map"></div>
			 			</div>
			 			<div class="map_controls">
			 				<button class="clear_style" id="zoom_map_in">[+] Yakınlaştır</button>
			 				<button class="clear_style" id="zoom_map_out">[-] Uzaklaştır</button>
			 				<button class="clear_style" id="open_big_map">[*] Büyük Harita</button>
			 			</div>
		 			</div>
		 		</div>
			 </section>
		</div>
	</body>
</html>