<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/trainings.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/bordomor_dialogue.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/datepicker.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPRefix}res/css/add-ons/multiselect.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/datepicker.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPRefix}res/js/add-ons/multiselect.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/trainings.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/obj/city.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/district.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/address.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/location.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/athlete.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/team.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/loginable.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/trainer.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/training.js?ver=${verStr}"></script>
	</head>
	<body>
		<div class="main_container">
		 	${loggedUser.generatePortalTopBar()}
		 	${loggedUser.generatePortalLeftMenu()}
		 	<section class="content_container">
		 		<c:choose>
					<c:when test="${dataMode == 0}">
						<div class="dbman_result_container delete">
							<p>ANTRENMAN SİLİNDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 1}">
						<div class="dbman_result_container">
							<p>ANTRENMAN YARATILDI</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 2}">
						<div class="dbman_result_container edit">
							<p>ANTRENMAN GÜNCELLENDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
				</c:choose>
		 		<div class="table_container">
		 			<header>
		 				<h1>${not empty location ? location.getName().toUpperCase(locale) : ""} HAFTALIK ANTRENMAN PROGRAMI</h1>
		 				<div class="controls_container">
		 					<span class="input_container">
		 						<input type="text" class="filter_input" data-for="locations_table" placeholder="Tabloda Ara"/>
	 							<button id="filters_button" class="clear_style"></button>
	 						</span>
		 					<button id="new_item" class="clear_style">YENİ</button>
		 				</div>
		 			</header>
		 			<div class="calendar_controls">
		 				<button id="today_button" class='dark'>Bugün</button>
		 				<c:choose>
		 					<c:when test="${not empty location}">
		 						<a href="trainings.jsp?location=${location.getId()}&year=${year}&week=${week}&direction=prev" class="calendar_nav prev_week"></a>
				 				<a href="trainings.jsp?location=${location.getId()}&year=${year}&week=${week}&direction=next" class="calendar_nav next_week"></a>
		 					</c:when>
		 					<c:otherwise>
				 				<a href="trainings.jsp?year=${year}&week=${week}&direction=prev" class="calendar_nav prev_week"></a>
				 				<a href="trainings.jsp?year=${year}&week=${week}&direction=next" class="calendar_nav next_week"></a>
				 			</c:otherwise>
				 		</c:choose>	
		 				<p>${month} ${year} | ${week}. Hafta</p>
		 				<div>
		 					<c:forEach items="${locations}" var="nextLoc">
		 						<a href="trainings.jsp?location=${nextLoc.getId()}" title="${nextLoc.getName()}"><span class="colour_bullet" style="background-color: ${nextLoc.getRepresentingColourHex()}"></span>${nextLoc.getName()}</a>
		 					</c:forEach>
		 					<c:if test="${not empty location}">
		 						<a href="trainings.jsp" style="min-width: 0px"><span class="colour_bullet"></span>Tüm Tesisler</a>
		 					</c:if>
		 				</div>
		 			</div>
		 			<div class="body">
			 			<div class="calendar_navigator"></div>
			 			<table id="calendar_table" class="data_table unpaged">
			 				<thead>
			 					<tr>
			 						<th class="collapse"></th>
				 					<c:forEach items="${intervals}" var="nazli">
					 					<c:if test="${fn:indexOf(nazli, ':00') >= 0}">
					 						<th class="collapse" colspan="4">
					 							<span>${nazli}</span>
					 						</th>
					 					</c:if>
					 				</c:forEach>
					 			</tr>
					 		</thead>
			 				<tbody>
			 					<c:forEach items="${weekDates}" var="nextDate" varStatus="weekLoop">
			 						<c:set var="nextDayNumber" value="${fn:substring(nextDate, 0, 2)}"/>
			 						<c:set var="nextDayName" value="${weekDays[weekLoop.index]}"/>
			 						<c:forEach begin="1" end="10" varStatus="loop">
					 					<tr id="${nextDayNumber}_row_${loop.index}" class="${loop.index == 1 ? 'new_day' : ''}">
					 						<c:if test="${loop.index == 1}">
					 							<td rowspan="10" class="day_cell ${today == nextDate ? 'today' : ''} collapse"><span>${nextDayNumber}</span><br>${nextDayName}</td>
					 						</c:if>	
					 						<c:forEach items="${intervals}" var="nazli">
					 							<td id="${nextDayNumber}_row_${loop.index}_${fn:replace(nazli, ':', '_')}" class="time_cell" data-date="${weekDates[weekLoop.index]}">
					 								<span><span class="colour_indicator"></span><span></span></span>
					 							</td>
					 						</c:forEach>
					 					</tr>
				 					</c:forEach>
				 				</c:forEach>
			 				</tbody>
			 			</table>
			 			<c:forEach items="${trainings}" var="nextTraining">
			 				<script type="text/javascript">
			 					var nextStartTime = ${nextTraining.getStartTime().getTime()};
			 					var nextEndTime = ${nextTraining.getEndTime().getTime()};
			 					var nextTeam = new Team(${nextTraining.getTeam().getId()}, "${nextTraining.getTeam().getName()}");
			 					var nextLocation = new Location(${nextTraining.getLocation().getId()}, "${nextTraining.getLocation().getName()}", null, "${nextTraining.getLocation().getRepresentingColourHex()}"); 
			 					
			 					trainings.push(new Training(${nextTraining.getId()}, nextTeam, nextLocation, nextStartTime, nextEndTime));
			 				</script>
			 			</c:forEach>
			 		</div>
			 	</div>
			</section>
		</div>
	</body>
</html>