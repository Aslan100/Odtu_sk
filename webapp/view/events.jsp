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
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/events.css?ver=${verStr}">
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
		<script type="text/javascript" src="${linkPrefix}res/js/events.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/obj/city.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/district.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/address.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/location.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/athlete.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/team.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/loginable.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/trainer.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/event.js?ver=${verStr}"></script>
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
							<p>ETKİNLİK SİLİNDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 1}">
						<div class="dbman_result_container">
							<p>ETKİNLİK YARATILDI</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
					<c:when test="${dataMode == 2}">
						<div class="dbman_result_container edit">
							<p>ETKİNLİK GÜNCELLENDİ</p>
							<button id="close_dbman_result_button" class="clear_style">&#x274C;</button>
						</div>
					</c:when>
				</c:choose>
		 		<div class="table_container">
		 			<header>
		 				<h1>AYLIK ETKİNLİK TAKVİMİ</h1>
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
		 						<a href="events.jsp?location=${location.getId()}&year=${year}&month=${monthNumber}&direction=prev" class="calendar_nav prev_week"></a>
				 				<a href="events.jsp?location=${location.getId()}&year=${year}&month=${monthNumber}&direction=next" class="calendar_nav next_week"></a>
		 					</c:when>
		 					<c:otherwise>
				 				<a href="events.jsp?year=${year}&month=${monthNumber}&direction=prev" class="calendar_nav prev_week"></a>
				 				<a href="events.jsp?year=${year}&month=${monthNumber}&direction=next" class="calendar_nav next_week"></a>
				 			</c:otherwise>
				 		</c:choose>	
		 				<p>${month} ${year}</p>
		 				<div>
		 					<c:forEach items="${locations}" var="nextLoc">
		 						<a href="events.jsp?location=${nextLoc.getId()}" title="${nextLoc.getName()}"><span class="colour_bullet" style="background-color: ${nextLoc.getRepresentingColourHex()}"></span>${nextLoc.getName()}</a>
		 					</c:forEach>
		 					<c:if test="${not empty location}">
		 						<a href="events.jsp" style="min-width: 0px"><span class="colour_bullet"></span>Tüm Tesisler</a>
		 					</c:if>
		 				</div>
		 			</div>
		 			<div class="body">
			 			<div class="calendar_navigator"></div>
			 			<table id="calendar_table" class="data_table unpaged">
			 				<thead>
			 					<tr>
			 						<c:forEach items="${weekDays}" var="nazli">
					 					<th class="collapse">
					 						<span>${nazli}</span>
					 					</th>
					 				</c:forEach>
					 			</tr>
					 		</thead>
			 				<tbody>
			 					<c:forEach begin="1" end="${weekCount}" varStatus="weekLoop">
			 						<c:forEach begin="1" end="5" varStatus="loop">
					 					<tr id="week_${weekLoop.index}_row_${loop.index}" class="${loop.index == 1 ? 'new_day' : ''}">
					 						<c:forEach items="${monthDates}" var="nextDate" begin="${(weekLoop.index - 1)*7}" end="${(weekLoop.index)*7 - 1}" varStatus="dayLoop">
						 						<c:set var="nextDayNumber" value="${fn:substring(nextDate, 0, 2)}"/>
						 						<c:set var="nextMonthNumber" value="${fn:substring(nextDate, 3, 5)}"/>
						 						<c:set var="nextMonthName" value="${fn:substring(nextDate, 3, 5)}"/>
						 						<c:choose>
						 							<c:when test="${loop.index == 1}">
						 								<td id="${nextDayNumber}_${nextMonthNumber}_row_${loop.index}" class="day_cell ${today == nextDate ? 'today' : ''}" data-date="${monthDates[dayLoop.index]}">
											 				<span>${nextDayNumber}</span>
											 				<c:if test="${nextDayNumber == '01'}">
											 					<c:forEach items="${monthNumbers}" var="nextNumber" varStatus="monthNumbersLoop">
											 						<c:if test="${monthNumbers[monthNumbersLoop.index] == nextMonthNumber}">
											 							<span>${monthNames[monthNumbersLoop.index]}</span>
											 						</c:if>
											 					</c:forEach>
											 				</c:if>
											 			</td>
						 							</c:when>
						 							<c:otherwise>
						 								<td id="${nextDayNumber}_${nextMonthNumber}_row_${loop.index}" class="time_cell" data-date="${monthDates[dayLoop.index]}">
											 				<span><span class="colour_indicator"></span><span></span></span>
											 			</td>
						 							</c:otherwise>
							 					</c:choose>
								 			</c:forEach>
					 					</tr>
					 				</c:forEach>
					 			</c:forEach>
			 				</tbody>
			 			</table>
			 			<c:forEach items="${events}" var="nextEvent">
			 				<script type="text/javascript">
			 					var nextStartTime = ${nextEvent.getStartTime().getTime()};
			 					var nextEndTime = ${nextEvent.getEndTime().getTime()};
			 					var nextLocation = new Location(${nextEvent.getLocation().getId()}, "${nextEvent.getLocation().getName()}", null, "${nextEvent.getLocation().getRepresentingColourHex()}"); 
			 					
			 					events.push(new Event(${nextEvent.getId()}, "${nextEvent.getName()}", "${nextEvent.getType().toString()}", "${nextEvent.getBranch().toString()}", nextLocation, nextStartTime, nextEndTime));
			 				</script>
			 			</c:forEach>
			 		</div>
			 	</div>
			</section>
		</div>
	</body>
</html>