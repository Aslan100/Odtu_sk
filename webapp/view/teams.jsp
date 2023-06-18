<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Hoşgeldiniz ${loggedUser.getFullName()} | ODTÜ SPOR KULÜBÜ</title>
		<link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:400,600,700&subset=latin,latin-ext">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/fonts.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/ui_elements.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/tables.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/global.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/teams.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/athlete.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/add-ons/bordomor_dialogue.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/add-ons/multiselect.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPRefix}res/js/add-ons/jquery-3.6.1.min.js"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/add-ons/multiselect.js?ver=${verStr}"></script>
				
		<script type="text/javascript" src="${linkPRefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/teams.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/obj/location.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/obj/district.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/obj/city.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/obj/loginable.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/obj/athlete.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/obj/trainer.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/obj/team.js?ver=${verStr}"></script>
	</head>
	<body>
		<div class="main_container">
		 	${loggedUser.generatePortalTopBar()}
		 	${loggedUser.generatePortalLeftMenu()}
		 	<section class="content_container">
		 		<c:choose>
					<c:when test="${dataMode == 0}">
						<div class="dbman_result_container delete">
							<p>TAKIM SİLİNDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 1}">
						<div class="dbman_result_container">
							<p>TAKIM YARATILDI</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 2}">
						<div class="dbman_result_container edit">
							<p>TAKIM GÜNCELLENDİ</p>
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
				 						<th>AÇIKLAMA</th>
					 				</tr>
				 				</thead>
				 				<tbody>
				 					<c:forEach items="${ageGroups}" var="nextGroup">
					 					<tr>
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
		 				<h1>TAKIMLAR</h1>
		 				<div class="controls_container">
		 					<span class="input_container">
		 						<input type="text" class="filter_input" data-for="athletes_table" placeholder="Tabloda Ara"/>
	 							<button id="filters_button" class="clear_style"></button>
	 						</span>
	 						<button id="new_item" class="clear_style">YENİ</button>
		 				</div>
	 				</header>
		 			<div class="body">
		 				<div class="data_card triple"></div>
			 			<table class="data_table">
			 				<thead>
			 					<tr>
			 						<th>ADI</th>
				 					<th class="collapse">YAŞ KATEGORİSİ</th>
				 					<th class="collapse">CİNSİYET KATEGORİSİ</th>
				 					<th class="collapse">BRANŞI</th>
				 				</tr>
			 				</thead>
			 				<tbody>
			 					<c:forEach items="${teams}" var="nextTeam">
			 						<c:set var="genderCategory" value="${nextTeam.getGenderCategory().getName()}"/>
			 						<c:if test="${nextTeam.getGenderCategory().toString() == 'PREFER_NOT_TO_SAY'}">
			 							<c:set var="genderCategory" value="Karma"/>
			 						</c:if>
				 					<tr data-id="${nextTeam.getId()}" ${nextTeam.getId() == itemId ? "data-is_selected_item='true'" : ""}>
				 						<td>${nextTeam.getName()}</td>
				 						<td class="collapse">${nextTeam.getAgeCategory().getLongName()}</td>
				 						<td class="collapse">${nextTeam.getGenderCategory().getName()}</td>
				 						<td class="collapse">${nextTeam.getBranch().getName()}</td>
				 					</tr>
			 					</c:forEach>
			 				</tbody>
			 				<tfoot>
								<tr>
									<td colspan="100" class="controls">
										<div>
											<span id="totals" class="table_data">Toplam <span>##</span> takım</span>
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