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
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/tables.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/global.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/registration/common.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/registration/index.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/bordomor_dialogue.css?ver=${verStr}">
		<link type="text/css" rel="stylesheet" href="${linkPrefix}res/css/add-ons/datepicker.css?ver=${verStr}">
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-3.6.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/datepicker.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/register.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/loginable.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/athlete.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/parent.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/branch.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/team.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/city.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/district.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/address.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/location.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/training.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/promotion.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/payment.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/payment_plan.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/obj/payment_schema.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/registration/certain.js?ver=${verStr}"></script>
		
		<c:if test="${not empty registrationCode}">
			<c:choose>
				<c:when test="${empty newReg.getLastCompletedStep()}">
					<script type="text/javascript" src="${linkPrefix}res/js/registration/certain/registered_step.js?ver=${verStr}"></script>
				</c:when>
				<c:when test="${newReg.getLastCompletedStep().ordinal() == 0}">
					<script type="text/javascript" src="${linkPrefix}res/js/registration/certain/medical_data_step.js?ver=${verStr}"></script>
				</c:when>
				<c:when test="${newReg.getLastCompletedStep().ordinal() == 1}">
					<script type="text/javascript" src="${linkPrefix}res/js/registration/certain/registrant_step.js?ver=${verStr}"></script>
				</c:when>
				<c:when test="${newReg.getLastCompletedStep().ordinal() == 2}">
					<script type="text/javascript" src="${linkPrefix}res/js/registration/certain/training_selection_step.js?ver=${verStr}"></script>
				</c:when>
				<c:when test="${newReg.getLastCompletedStep().ordinal() == 3}">
					<script type="text/javascript" src="${linkPrefix}res/js/registration/certain/payment_schema_selection_step.js?ver=${verStr}"></script>
				</c:when>
				<c:when test="${newReg.getLastCompletedStep().ordinal() == 4}">
					<script type="text/javascript" src="${linkPrefix}res/js/registration/certain/payment_step.js?ver=${verStr}"></script>	
				</c:when>
			</c:choose>
		</c:if>
		<c:if test="${empty registrationCode}">
			<script type="text/javascript" src="${linkPrefix}res/js/registration/certain/registered_step.js?ver=${verStr}"></script>
		</c:if>
	</head>
	<body data-last_completed_step="${newReg.getLastCompletedStep().ordinal()}">
		<div class="main_container">
			<img src="${linkPrefix}res/visual/logo.png"/>
			<figure></figure>
			<section class="centerer">
				<div>
					<h1>ODTÜ Spor Kulübü<br>Kesin Kayıt</h1>
					<h2 id="step_data">Adım 1/5</h2>
					<div class="options_container">
						<jsp:include page="form/registered_form.jsp"/>
						<jsp:include page="form/medical_data_form.jsp"/>
						<jsp:include page="form/registrant_form.jsp"/>
						<jsp:include page="form/training_selection_form.jsp"/>
						<jsp:include page="form/payment_schema_selection_form.jsp"/>
					</div>
				</div>
			</section>
		</div>
	</body>
</html>