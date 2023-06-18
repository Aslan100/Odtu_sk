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
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/pie_chart.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/datepicker.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/home.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/athletes.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/district.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/city.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/address.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/location.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/loginable.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/athlete.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/parent.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/payment_schema.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/payment_plan.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/payment.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/promotion.js?ver=${verStr}"></script>
	</head>
	<body>
		<div class="main_container_overlay"></div>
		<div class="main_container">
		 	${loggedUser.generatePortalTopBar()}
		 	${loggedUser.generatePortalLeftMenu()}
		 	<section class="content_container">
		 		<c:choose>
					<c:when test="${dataMode == 0}">
						<div class="dbman_result_container delete">
							<p>SPORCU HESABI SİLİNDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 1}">
						<div class="dbman_result_container">
							<p>SPORCU HESABI YARATILDI</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 2}">
						<div class="dbman_result_container edit">
							<p>SPORCU HESABI GÜNCELLENDİ</p>
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
					 						<td class="collapse">${nextBranch.getTitle()}</td>
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
		 			<div class="table_container">
		 				<header class="dark">
		 					<h1>TAKIMLAR</h1>
		 				</header>
		 				<div class="body">
			 				<table class="data_table">
				 				<thead>
				 					<tr>
				 						<th>TAKIM</th>
				 						<th>ANTRENÖR</th>
					 				</tr>
				 				</thead>
				 				<tbody>
				 					<c:forEach items="${teams}" var="nextTeam">
					 					<tr>
					 						<td class="collapse">${nextTeam.getName()}</td>
					 						<td>-</td>
					 					</tr>
				 					</c:forEach>
				 				</tbody>
				 			</table>
			 			</div>	
		 			</div>
		 		</div>
		 		<div class="table_container">
		 			<header>
		 				<h1>SPORCULAR</h1>
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
			 			<table class="data_table" id="athletes_table">
			 				<thead>
			 					<tr>
			 						<th class="state_header"></th>
			 						<th class="collapse">KOD</th>
				 					<th>ADI</th>
				 					<th>SOYADI</th>
				 					<th>DOĞUM TARİHİ</th>
				 					<th>YAŞ GRUBU</th>
				 					<th class="collapse">BRANŞI</th>
				 					<th class="collapse">CİNSİYETİ</th>
				 					<th>TAKIMI</th>
				 					<th>ANTRENÖRÜ</th>
				 				</tr>
			 				</thead>
			 				<tbody>
			 					<c:forEach items="${athletes}" var="nextAthlete">
				 					<tr data-code="${nextAthlete.getCode()}">
				 						<td class="state_cell ${nextAthlete.getState().toString().toLowerCase()}"></td>
				 						<td class="collapse">${nextAthlete.getCode()}</td>
				 						<td>${nextAthlete.getName()}</td>
				 						<td>${nextAthlete.getSurname()}</td>
				 						<td>${dateFormat.format(nextAthlete.getBirthDate())}</td>
				 						<td>${nextAthlete.getAgeGroup().getName()}</td>
				 						<td class="collapse">${nextAthlete.getPrimaryBranch().getTitle()}</td>
				 						<td class="collapse">${nextAthlete.getGender().getName()}</td>
				 						<td>${not empty nextAthlete.getPrimaryTeam() ? nextAthlete.getPrimaryTeam().getName() : "-"}</td>
				 						<td>-</td>
				 					</tr>
			 					</c:forEach>
			 				</tbody>
			 				<tfoot>
								<tr>
									<td colspan="100" class="controls">
										<div>
											<span id="totals" class="table_data">Toplam <span>##</span> sporcu</span>
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