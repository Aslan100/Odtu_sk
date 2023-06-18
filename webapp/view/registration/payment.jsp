<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
		
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/jquery-3.6.1.min.js"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/tools.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_ajax.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/add-ons/bordomor_dialogue.js?ver=${verStr}"></script>
		
		<script type="text/javascript" src="${linkPrefix}res/js/common.js?ver=${verStr}"></script>
		<script type="text/javascript" src="${linkPrefix}res/js/registration/payment.js?ver=${verStr}"></script>
	</head>
	<body data-last_completed_step="${newReg.getLastCompletedStep().ordinal()}">
		<div class="main_container">
			<img src="${linkPrefix}res/visual/logo.png"/>
			<figure></figure>
			<section class="centerer">
				<div>
					<h1>ODTÜ Spor Kulübü<br>Kesin Kayıt</h1>
					<div class="options_container">
						<!-- <form id="credit_card_form" class="form_unit" method="post" action="${linkPrefix}/payment/receive.jsp" style="width: auto;">  -->
						<form id="credit_card_form" class="form_unit" method="post" action="https://entegrasyon.asseco-see.com.tr/fim/est3dgate" style="width: auto;">
							<div class="sections" style="flex: none; margin-bottom: 15px;">
								<section style="max-height: none; min-height: 0px; margin-right: 30px;">
									<div style="display: flex; flex-flow: row nowrap; margin-bottom: 30px;">
										<img src="${linkPrefix}res/visual/icon/navigation_panel/payment.png" style="display: inline-block; height: 53px; margin-right: 15px"/>
										<p><span style="font: inherit; font-size: 2.5em; font-weight: 600; color: inherit; line-height: 1.1em;">${amount}</span> TL<br>TOPLAM</p>
									</div>
									<div class="input_container">
										<input type="text" id="card_no_input" name="pan" placeholder=" " required/>
										<label for="card_no_input">Kart Numarası</label>
									</div>
									<div class="input_container">
										<input type="text" id="card_holder_input" name="card_holder" placeholder=" " required/>
										<label for="card_holder_input">Kart Üzerindeki İsim</label>
									</div>
									<div class="inline_input_row" style="position:relative;">
										<div class="input_container select_container">
											<!-- <select id="month_select" name="expiry_month"> -->
											<select id="month_select" name="Ecom_Payment_Card_ExpDate_Month">
												<option value="-1" disabled="disabled" selected="selected"></option>
												<c:forEach items="${months}" var="nextMonth">
													<option value="${nextMonth}">${nextMonth}</option>
												</c:forEach>
											</select>
											<label for="month_select">Ay</label>
										</div>
										<div class="input_container select_container">
											<!-- <select id="year_select" name="expiry_year">  -->
											<select id="month_select" name="Ecom_Payment_Card_ExpDate_Year">
												<option value="-1" disabled="disabled" selected="selected"></option>
												<c:forEach items="${years}" var="nextYear">
													<option value="${nextYear}">${nextYear}</option>
												</c:forEach>
											</select>
											<label for="year_select">Yıl</label>
										</div>
										<div class="input_container select_container">
											<select name="cardType">
												<option value="1">Visa</option>
												<option value="2">MasterCard</option>
											</select>
										</div>
										<div class="input_container" style="position: absolute; top: 0px; right: 0px;">
											<input type="text" id="cv2_input" name="cv2" maxlength="4" size="4" placeholder=" " required/>
											<label for="cvv2_input">CVV2</label>
										</div>
									</div>
									<div class="input_container checkbox_container">
										<input id="aggreement_acceptance_checkbox" type="checkbox"/>
										<label for="aggreement_acceptance_checkbox">
											<a href="javascript:void(0)">Ön Bilgilendirme Koşulları'nı</a> ve <a href="javascript:void(0)">Mesafeli Satış Sözleşmesi'ni</a> okudum, onaylıyorum. 
										</label>
									</div>
									<div>
										<img style="display: inline-block; height: 18px; margin-right: 10px; vertical-align: middle;" src="data:image/png;base64,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"/>
										<p style="display: inline-block; font-size: 12px; vertical-align: middle; line-height: 12px; margin-bottom: 0px;"><strong>3D Secure</strong> ile güvenli alışveriş</p>
									</div>
								</section>
								<section id="payment_details_container" style="max-height: none; min-height: 0px; flex: none; align-self: center; margin-top: 75px; margin-left: 15px;">
									<style>
										div#card_visual
										{
											posiiton: relative;
											height: 230px; width: 370px; 
											 
											border-radius: 10px;
											overflow: hidden;
										}
										
										div#card_visual > button#flip_button
										{
											position: absolute;
											top: 15px; right: 15px;
											z-index: 1;
										}
										
										div#card_visual > button#flip_button > img
										{
											height: 18px;
											opacity: 0.5;	
										}
										
										div#card_visual > div
										{
											position: relative;
											top: 0px;
											
											display: flex;
											flex-flow: column nowrap;
											justify-content: space-between;
											
											height: 170px; width: 310px;
											margin-bottom: 10px;
											padding: 30px;
											background-color: var(--system_dark_gray);
											border-radius: 10px;
											transition: 0.75s;
										}
										
										div#card_visual.flipped > div
										{
											top: -240px;
										}
										
										div#card_visual p
										{
											margin-bottom: 0px;
											font-size: 14px; 
											color: var(--system_light_gray);
											line-height: 1em;
										}
										
										div#card_visual p > span
										{
											display: inline-block;
											font: inherit;
											color: inherit;
											line-height: inherit;
										}
										
										div#card_visual > div.front > p.bank_name
										{
											font-size: 20px;
											font-weight: 600;
											color: var(--system_light_gray);
											opacity: 0.30;
										}
										
										div#card_visual > div.front > div.pan
										{
											margin: 0px 8px;
											margin-top: 20px;
										}
										
										div#card_visual > div.front > div.pan > img.chip
										{
											display: inline-block;
											height: 38px; width: auto;
											margin-bottom: 5px;
										}
										
										div#card_visual > div.front > div.pan > p
										{
											display: flex;
											flex-flow: row nowrap;
											align-items: center;
											justify-content: space-between;
											margin-top: 5px;
										}
										
										div#card_visual > div.front > div.pan > p > span
										{
											margin-bottom: 5px;
											font-size: 20px;
											letter-spacing: 2px;
										}
										
										div#card_visual > div.front > p.expiry_date
										{
											text-align: right;
											margin-right: 12px;
										}
										
										div#card_visual > div.front > p.expiry_date > span
										{
											display: inline-block;
											margin-right: 5px;
											font-size: 7px;
											line-height: 7px;
										}
										
										div#card_visual > div.back:before
										{
											content: "";
											
											position: absolute;
											top: 15px; left: 0px;
											height: 35px; width: 100%;
											background-color: var(--system_main_color);
										}
										
										div#card_visual > div.back:after
										{
											content: "İmzalanmadığında geçersizdir, - Not valid unless authorised signature";
											
											position: absolute;
											top: 65px; left: 30px;
											display: inline-block;
											font: var(--system_font);
    										font-size: 7px;
    										color: var(--system_light_gray);
										}
										
										div#card_visual > div.back > div.cvv
										{
											margin-top: 65px;
											background-color: var(--system_light_gray);
											height: 35px; width: 75%;
										}
										
										div#card_visual > div.back > div.cvv > p
										{
											color: var(--system_main_color);
											text-align: right;
											margin-right: 15px;
											margin-top: 10px;
										}
									</style>
									<div id="card_visual">
										<button type="button" id="flip_button" class="clear_style"><img src="data:image/png;base64,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28DFdUtT9oDLHWDgyZBN2ZWCNIV8Hng/o2/KAv23QPea11tzH6cPQJq6St4AB4fAaIGy133e3dXe279nmv39AD78cpKlK39vAAANGmlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4KPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iWE1QIENvcmUgNC40LjAtRXhpdjIiPgogPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iCiAgICB4bWxuczpzdEV2dD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL3NUeXBlL1Jlc291cmNlRXZlbnQjIgogICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICAgeG1sbnM6R0lNUD0iaHR0cDovL3d3dy5naW1wLm9yZy94bXAvIgogICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iCiAgICB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iCiAgIHhtcE1NOkRvY3VtZW50SUQ9ImdpbXA6ZG9jaWQ6Z2ltcDpiNGI2ZTVmNy1hNGNjLTQwNmMtYTI4ZC1hMjQ5Y2E4OThlMDkiCiAgIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6ZmZkZWYxY2EtZDI4Yy00NGY0LWExYzYtNzlkZjZiMGVlZjgzIgogICB4bXBNTTpPcmlnaW5hbERvY3VtZW50SUQ9InhtcC5kaWQ6ZTE1ZmFmZGQtYTRmMS00YmQ4LWE3YjUtNzU0NmQ5YWIxN2E0IgogICBkYzpGb3JtYXQ9ImltYWdlL3BuZyIKICAgR0lNUDpBUEk9IjIuMCIKICAgR0lNUDpQbGF0Zm9ybT0iTGludXgiCiAgIEdJTVA6VGltZVN0YW1wPSIxNjgyNDQwNTg5NTk5NDgxIgogICBHSU1QOlZlcnNpb249IjIuMTAuMzAiCiAgIHRpZmY6T3JpZW50YXRpb249IjEiCiAgIHhtcDpDcmVhdG9yVG9vbD0iR0lNUCAyLjEwIj4KICAgPHhtcE1NOkhpc3Rvcnk+CiAgICA8cmRmOlNlcT4KICAgICA8cmRmOmxpCiAgICAgIHN0RXZ0OmFjdGlvbj0ic2F2ZWQiCiAgICAgIHN0RXZ0OmNoYW5nZWQ9Ii8iCiAgICAgIHN0RXZ0Omluc3RhbmNlSUQ9InhtcC5paWQ6YzkzNmM0OGItYjVkMy00ZTQ4LTk2ZWMtMDAzNmE0YjY2ZmM3IgogICAgICBzdEV2dDpzb2Z0d2FyZUFnZW50PSJHaW1wIDIuMTAgKExpbnV4KSIKICAgICAgc3RFdnQ6d2hlbj0iMjAyMy0wNC0yNVQxOTozNjoyOSswMzowMCIvPgogICAgPC9yZGY6U2VxPgogICA8L3htcE1NOkhpc3Rvcnk+CiAgPC9yZGY6RGVzY3JpcHRpb24+CiA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgCjw/eHBhY2tldCBlbmQ9InciPz72Xtz0AAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH5wQZECQdkcwRQAAABVBJREFUaN7tm1uIVVUYx3/fNNqImqVRpmVZdFExKuxiWHShdDKIKK2XeohEsjKCMAmkoMCyh6DowSK62EOFPRgYgpciKwxKLcMQSotUzLLwNlOOM78ezjIkZs+cc2bvM+eofxgG9tmXtf57rW/91//7dlAQ1AHApcDNwHhgKBDUFgL7gO+AlcDmiOgq/ql6pbpa7bB+0K4uVc8vsuOh3q8esH6xQ732SJsjZwLuAN4DWqhv7AZuiIgfIsfOnwFsAEbRGFgN3NaU4w0faqDOA9wIXNec09s/Gbizh1P+AH5PUbmWaAFGAyd381sTcE9ew/8cdW9G0Hk/TY+aIwXlqeqejLZtyutBF2c8YJ96dn+PdfXZjPbtySsGZN2nPSK218F835xxfEATxwes9M0dNzhBwAkCThDQ5yVmKDClUQlorrLTAZwFzAbuA847bghQLwEeBu4HTmn0KdBcwRsfAywAZiZ355hAcxmdHw7MBR7v4Y1L7e2uYglQm4A7gBeACzNOOwC8CewHnjpmVgH1dOB1YGlG5w8CbwATgceAbcfMFFAvB5YAEzKuWQPMi4hvjrrm2NAB6lRgVUbnDwJPAq1Hd76IPXy/EJA6/z4wvJvz9gIzI2JRRBwqsPMjgU/VF9TBtTQMJqh/ZJgGneqsXq6/VV2jblS3Juv5d/XHCtsxST2kdqV7XZVT/+7NMmxQW9RPevDR/6rkbahN6lB1pHpelQQcwX51gdpSJAHTE+NZaFMn1Ggk/p8AU9vWquOLIKAJmNGLiBkEvKtO6qdAHWmz9bk6N+Uccw2C48o47zLgM/WD5LKemoRSLXEa8BKwTB2bpw5oK/PcQWm0zAB+A9arXwAbKaWa9gC7I+JAwatWK/ClOh9Y0tdsbzOwFrihwuvOTA1pBbqAw0AH0KHuA3YA24Ffk0rclXTEYeCfJKHbgHbg73QcYFiZzx+ZlOh0dW5E7OpL4LlA/bMOsrYdVabTf1bv7ElA9RgEI+In4Gmgsw5keTUGzblJwC1WT61WCb4KLKwDEqrFAGBWig03VkxACiTPJKfnYAP7G+OAj9WF5Yq3/5ayiOiMiMUpIK5tYBJagPnAavWKiv2AiPgamAo8AGyl9intvHA1sKI3zdCtmImI9oh4MwmgxyhVWTUiTMtzZQQcRcT+iHgFmAzcDixLa3cj4GtgWm/Z6bLkbES0RcRySlUglyRjZF0FKrKWaAdepFQEtaHIndtJ6kXqHHV5qhDp7GcxtUW9qaLtcF42ljpEvUV9Xl3Xg8FSlIp8Qz2tUiWYS5FURJj0/UpgZZKloyg5yhelaTOFUunsQPLNIeymlLdYGhEVC7nmIqZHImRH+vv0yJShlFG6DLg8/Z+YNjYjEjGVRvhVwOyIaFhbHnWEeql6t/pcN45QdziozivXKqtFDBiujkr1gn29V291xpvUayqxz3uzxPLAXcAWYJ36jvqIOlkdkuNgOQy8DFwfEevSNKubYfxgRmTeltMI2KpOq9aGq8UIyAqwfU1udAFvA5MjYkURHzs013F83JX2IR9Ws7w1MgFfUSqunhcRv9TChqo3tAKdRb71uiagoORr9Gk3WOYS1a1XV41RWQDOyjjekRcBOzOODwOeSDK4v5boscCcrHZHTg8ZA2yi+yKqTmA98H1a1mqJIZQ+jcn6YOO1vAhoAj4CpjfQNqQTuCWXKZAEykLgUAMRsBr4PDclGBFfJBIawUXeCTwaER15B5yB6qI6+2S2u1xicbUOyR67S/22l8qTWmOf+pY6uiyBkAMRg4ErKWWaLqBUX1BrdFGqW1hPqb5x6/+30f8CDni5jwPw54kAAAAASUVORK5CYII="></button>
										<div class="front">
											<p class="bank_name">KART BİLGİLERİNİZ</p>
											<div class="pan">
												<img class="chip" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAHYAAAB2AH6XKZyAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAA7lJREFUeJztm1trXFUYhp912JPMTCadpKR44Y0SL6pSFILUgiFVoZ0IIiL9M/bSXyOIRTQRq22Q2katqIUSIbFRKJpzZzKnJHuvvbwYR4O409B9WAnZz/We73u/d8067G/NQE5OTk5OzolFANhvXzmNFVexYhoYyyr5TzPV4T8f1lXtvY7NKidC+Gh5D+tdEZe+W9b269fGCP3bwHhmIv4mNJLQALY3EJlgbYE9M4EMf7FfvPSiRAcf4KB454S2gBUfSYR9x7UWZ5jweUmGc/7IEVolIcP5dwSRrgW4JjfAtQDX6Huzo+y2Vawgw2d8xi80UDqb84wxglvXyzTqAXEyep5CKy9kYOjJw4SBYOthgYWbVc5erKdugjGCuZkyzaaP1goVY+ykAP3Cm/V4iiws3x1iZbHEwo0qZ19Pz4T9xZfLHhen22gvXq74a4CAZyZaPPVch+aGx8KNKiZIfmdNo3hIahFM2YRe8aXEi4ckd4GUTPi3+CDx4gGEvXM+2Qlr4cHdCquLRUqnDMVTQeSjj/7wWF1fozqiI5/ptqG741OpeExNt1EqWbnRmZ8UAc9ONNlpKhorBTqN6GXaYglDy9amf2DIwUGdSvGQhgEAAgrFEICX395EF8L/f86Cvxr9DWm3FLe+EhQHRSrFQ1oG7E9QsOhChHgLumwiP2sMpC3xxB+FcwNcC3BNboBrAa7RP35yOpXA/k5y3tYbAZ9+WEosXh8BaBW1R8ck2BMch3ajPnf5USqBl+4Ms748mEiskapmstZKJNZ/OfFrQG6AawGuyQ1wLcA1qb8NHrgdWvAPaMnvdOO16w9DKgZYC7utnviDDloWy9rmymPjdbu91tjxaIhYwa/zFbbXPcqjAUOj0d2ejd8HkFJSHYke6W7H0u36zM2Uj0FLzAqW5iusLw9SGfMfe1HSWPNQUjB5KfqQYwzMzZZpbvvc/KzEVK2TaFM0uUXQwoPvhw5d/GFRCqZqbSrDmlYrYG62ROAnd8ROxoB+J3ipmGjxfdI0Ib4B+9rgaRTfJy0T9P0v411ghIGku61SLb5Pz4TOPxcln39cjHU5KoRAG1/Gvh4ffXqX8QvbmVyPK2WZmu7wzfUS9brBxHib97RCn6ttJacuI5SyTF5uJxLrxB+FcwNcC3CNhFg/szn2SGDNtQhnSGEkwl5zrcMZSt6X6OAqsOhaS+ZIsYeW72ox8cOGvf3qeYR9H2HfwoozmWnQYUUqNCLDdaj3h4mfMQNXxBvzv2WWNycnJycn5+jxF9zwizfSWHPuAAAAAElFTkSuQmCC">
												<p><span>0000</span><span>0000</span><span>0000</span><span>0000</span></p>
											</div>
											<p class="expiry_date"><span>VALID<br>THRU</span>00/00</p>
											<p class="card_holder">KART ÜZERİNDEKİ İSİM</p>
										</div>
										<div class="back">
											<div class="cvv">
												<p>000</p>
											</div>
										</div>
									</div>
								</section>
							</div>
							<div class="controls">
								<input type="hidden" name="reg_code" value="${reg.getCode()}">
								
								<input type="hidden" name="clientid" value="${clientId}">
								<input type="hidden" name="storetype" value="${model}">
								<input type="hidden" name="islemtipi" value="${type}"/>
								
								<input type="hidden" name="amount" value="${amount}"/>
								<input type="hidden" name="currency" value="949"/>
								<input type="hidden" name="oid" value="${orderId}">
								
								<input type="hidden" name="okUrl" value="${okURL}">
								<input type="hidden" name="failUrl" value="${failURL}">
								<input type="hidden" name="lang" value="tr">
								<input type="hidden" name="rnd" value="${rnd}" > 
								<input type="hidden" name="hash" value="${hash}">
								
								<div class="extra_options">
									<button type="button" class="cancel">Kaydı İptal Et</button>
									<button type="button" class="cancel">Geri Dön</button>
								</div>
								<button type="submit" id="submit_button">Ödemeyi Tamamla</button>
							</div>
						</form>
					</div>
				</div>
			</section>
		</div>
	</body>
</html>