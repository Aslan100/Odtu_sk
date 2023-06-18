<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="payment_schema_selection_form" class="form_unit">
	<div class="sections">
		<section id="schema_list" class="detailed_radio_list placeholder no_data">
			<h3>Ödeme Planları</h3>
		</section>
		<section id="payment_plan_container" class="table_section placeholder payment no_data">
			<h3>Plan Detayları</h3>
		</section>
	</div>
	<div class="controls">
		<input type="hidden" name="code" value="${newReg.getCode()}">
		<input type="hidden" id="coupon_code_input" name="coupon_code" value="">
		<div class="extra_options">
			<button type="button" id="refresh_button" class="refresh">Yenile</button>
			<button type="button" class="cancel">Kaydı İptal Et</button>
			<button type="button" id="discount_coupon_button" disabled="disabled">İndirim Kodum Var</button>
		</div>
		<button type="button" class="back">Geri</button>
		<button type="submit" id="submit_button" class="forward" disabled="disabled">İleri</button>
	</div>
</form>