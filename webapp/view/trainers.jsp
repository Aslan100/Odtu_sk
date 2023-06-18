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
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/bordomor_dialogue.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/datepicker.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/datepicker.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/trainers.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/district.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/city.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/address.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/location.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/loginable.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/trainer.js?ver=${verStr}"></script>
	</head>
	<body>
		<div class="main_container">
		 	${loggedUser.generatePortalTopBar()}
		 	${loggedUser.generatePortalLeftMenu()}
		 	<section class="content_container">
		 		<c:choose>
					<c:when test="${dataMode == 0}">
						<div class="dbman_result_container delete">
							<p>ANTRENÖR HESABI SİLİNDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 1}">
						<div class="dbman_result_container">
							<p>ANTRENÖR HESABI YARATILDI</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 2}">
						<div class="dbman_result_container edit">
							<p>ANTRENÖR HESABI GÜNCELLENDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
				</c:choose>
		 		<div class="multiple_table_container">
		 			<div class="table_container">
		 				<header class="dark">
		 					<h1>BRANŞLAR</h1>
		 				</header>
		 				<div class="body">
			 				<table class="data_table">
				 				<thead>
				 					<tr>
				 						<th>ADI</th>
					 				</tr>
				 				</thead>
				 				<tbody>
				 					<c:forEach items="${branches}" var="nextBranch">
					 					<tr>
					 						<td class="collapse">${nextBranch.getName()}</td>
					 					</tr>
				 					</c:forEach>
				 				</tbody>
				 			</table>
			 			</div>
		 			</div>
		 			<div class="table_container">
		 				<header class="dark">
		 					<h1>YAŞ GRUPLARI</h1>
		 				</header>
		 				<div class="body">
			 				<table class="data_table">
				 				<thead>
				 					<tr>
				 						<th>KOD</th>
				 						<th>AÇIKLAMA</th>
					 				</tr>
				 				</thead>
				 				<tbody>
				 					<c:forEach items="${ageGroups}" var="nextGroup">
					 					<tr>
					 						<td class="collapse">${nextGroup.getName()}</td>
					 						<td>${nextGroup.getLongName()}</td>
					 					</tr>
				 					</c:forEach>
				 				</tbody>
				 			</table>
			 			</div>
		 			</div>
		 		</div>
		 		<div class="table_container">
		 			<header>
		 				<h1>ANTRENÖRLER</h1>
		 				<div class="controls_container">
		 					<span class="input_container">
		 						<input type="text" class="filter_input" data-for="athletes_table" placeholder="Tabloda Ara"/>
	 							<button id="filters_button" class="clear_style"></button>
	 						</span>
		 					<button id="new_item" class="clear_style">YENİ</button>
		 				</div>
		 			</header>
		 			<div class="body">
			 			<div class="data_card"></div>
			 			<table class="data_table" id="trainers_table">
			 				<thead>
			 					<tr>
			 						<th class="state_header"></th>
			 						<th class="collapse">KOD</th>
				 					<th>ADI</th>
				 					<th>SOYADI</th>
				 					<th>DOĞUM TARİHİ</th>
				 					<th class="collapse">DOĞUM YERİ</th>
				 					<th class="collapse">BRANŞI</th>
				 					<th class="collapse">SEVİYE</th>
				 					<th class="collapse">KIDEM</th>
				 					<th class="collapse">T.C. KİMLİK NO</th>
				 					<th>TAKIMI</th>
				 				</tr>
			 				</thead>
			 				<tbody>
			 					<c:forEach items="${trainers}" var="nextTrainer">
				 					<tr data-code="${nextTrainer.getCode()}">
				 						<td class="state_cell active"></td>
				 						<td class="collapse">${nextTrainer.getCode()}</td>
				 						<td>${nextTrainer.getName()}</td>
				 						<td>${nextTrainer.getSurname()}</td>
				 						<td>${dateFormat.format(nextTrainer.getBirthDate())}</td>
				 						<td class="collapse">${nextTrainer.getPlaceOfBirth().getName()}</td>
				 						<td class="collapse">${nextTrainer.getPrimaryBranch().getName()}</td>
				 						<td class="collapse">${nextTrainer.getLevel()}. Seviye</td>
				 						<td class="collapse">${nextTrainer.getLabel().getName()}</td>
				 						<td class="collapse">${not empty nextTrainer.getIdNo() ? nextTrainer.getIdNo() : "-"}</td>
				 						<td>-</td>
				 					</tr>
			 					</c:forEach>
			 				</tbody>
			 				<tfoot>
								<tr>
									<td colspan="100" class="controls">
										<div>
											<span id="totals" class="table_data">Toplam <span>##</span> antrenör</span>
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
		 		</div>
			 </section>
		</div>
	</body>
</html>