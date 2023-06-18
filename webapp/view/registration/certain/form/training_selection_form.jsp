<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="training_selection_form" class="form_unit">
	<div class="sections">
		<section id="training_list" class="detailed_radio_list placeholder training no_data">
			<h3>Antrenman Seçimi</h3>
		</section>
		<section id="map_container" class="placeholder location no_data">
			<h3>Konum</h3>
			<div class="map_frame">
				<div id="map"></div>
			</div>
			<div class="map_controls"></div>
		</section>
	</div>
	<div class="controls">
		<input type="hidden" name="code" value="${newReg.getCode()}">
		<div class="extra_options">
			<button type="button" id="refresh_button" class="refresh">Yenile</button>
			<button type="button" class="cancel">Kaydı İptal Et</button>
		</div>
		<button type="button" class="back">Geri</button>
		<button type="button" id="submit_button" class="forward">İleri</button>
	</div>
</form>