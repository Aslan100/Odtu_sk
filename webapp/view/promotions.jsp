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
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/promotions.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/bordomor_dialogue.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/datepicker.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/datepicker.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/promotions.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/promotion.js?ver=${verStr}"></script>
	</head>
	<body>
		<div class="main_container">
		 	${loggedUser.generatePortalTopBar()}
		 	${loggedUser.generatePortalLeftMenu()}
		 	<section class="content_container">
		 		<c:choose>
					<c:when test="${dataMode == 0}">
						<div class="dbman_result_container delete">
							<p>PROMOSYON SİLİNDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 1}">
						<div class="dbman_result_container">
							<p>PROMOSYON YARATILDI</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 2}">
						<div class="dbman_result_container edit">
							<p>PROMOSYON GÜNCELLENDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
				</c:choose>
		 		<div class="table_container">
		 			<header>
		 				<h1>PROMOSYON TANIMLARI</h1>
		 				<div class="controls_container">
		 					<span class="input_container">
		 						<input type="text" class="filter_input" data-for="promotions_table" placeholder="Tabloda Ara"/>
	 							<button id="filters_button" class="clear_style"></button>
	 						</span>
		 					<button id="new_item" class="clear_style">YENİ</button>
		 				</div>
		 			</header>
		 			<div class="body">
			 			<table class="data_table" id="promotions_table">
			 				<thead>
			 					<tr>
			 						<th>ADI</th>
				 				</tr>
			 				</thead>
			 				<tbody>
			 					<c:forEach items="${promotions}" var="nextPromotion">
				 					<tr data-id="${nextPromotion.getId()}" ${nextPromotion.getId() == itemId ? "data-is_selected_item='true'" : ""}>
				 						<td class="collapse">
				 							<span class="colour_bullet">&bull;</span> ${nextPromotion.getTitle().toUpperCase(locale)}
				 							<p style="margin-top: 5px">Branş: ${nextPromotion.getBranch().getTitle()}</p>
				 							<p style="margin-top: 5px">İndirim Oranı: ${nextPromotion.getDiscountAmount()} TL + %${nextPromotion.getDiscountRatio()*100}</p>
				 							<div>
				 								<button id="get_code_button" class="clear_style" title="Promosyon kodu al"></button>
				 								<button id="edit_promotion_button" class="clear_style" title="Promosyon tanımını güncelle"></button>
				 								<button id="delete_promotion_button" class="clear_style" title="Promosyon tanımını sil"></button>
				 							</div>
				 						</td>
				 					</tr>
			 					</c:forEach>
			 				</tbody>
			 				<tfoot>
								<tr>
									<td colspan="100" class="controls">
										<div>
											<span id="totals" class="table_data">Toplam <span>##</span> promosyon</span>
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
				 		<header>DETAYLAR</header>
				 		<div>
				 			
				 		</div>
				 		<button id="go_to_location_button" class="dark" disabled="disabled">Planı İncele</button>
				 	</div>
			 	</div>
			 </section>
		</div>
	</body>
</html>