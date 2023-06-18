<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="medical_data_form" class="form_unit">
	<div class="sections">
		<section>
			<h3>Sağlık Bilgieri</h3>
			<div class="input_container narrow">
				<textarea id="past_therapies_area" name="past_therapies" placeholder=" "></textarea>
				<label for="past_therapies_area">Geçirdiği Hastalık ve/veya Ameliyatlar</label>
			</div>
			<div class="input_container narrow">
				<textarea id="active_issues_area" name="active_issues" placeholder=" "></textarea>
				<label for="active_issues_area">Devam Eden Hastalık, Tedavi ve Alerjileri</label>
			</div>
			<div class="input_container narrow">
				<textarea id="active_medication_area" name="active_medications" placeholder=" "></textarea>
				<label for="active_medication_area">Düzenli Kullandığı İlaçlar</label>
			</div>
			<div class="input_container narrow">
				<textarea id="special_care_needs_area" name="special_care_needs" placeholder=" "></textarea>
				<label for="special_care_needs_area">Özel İlgi İsteyen Durumları</label>
			</div>
		</section>
		<section>
			<h3>&nbsp;</h3>
			<div class="input_container">
				<input type="number" id="height_input" name="height" value="0" min="0" max="250" step="1"/>
				<label for="height_input">Boy</label>
			</div>
			<div class="input_container">
				<input type="number" id="weight_input" name="weight" value="0" min="0" max="150" step="0.5"/>
				<label for="weight_input">Kilo</label>
			</div>
			<div class="input_container select_container">
				<select id="blood_type_select" name="blood_type">
					<c:forEach items="${bloodTypes}" var="nextType">
						<option value="${nextType.toString()}">${nextType.getName()}</option>
					</c:forEach>
				</select>
				<label for="blood_type_select">Kan Grubu</label>
			</div>
			<div class="input_container">
				<input type="checkbox" id="responsibility_acceptance_checkbox"/>
				<label for="responsibility_acceptance_checkbox">İhtiyaç olması durumunda sporcunun sağlık durumunun takibine yardımcı olacak bu bilgilerin doğru ve eksiksiz olduğunu onaylıyorum.</label>
			</div>
			<div class="caution_container" style="margin-top: 40px">
				<img src="${linkPrefix}res/visual/icon/registration/warning.png"/>
				<p>Kulübümüz bu adımda tarafınızca paylaşılmamış ya da eksik ve/veya yanlış olarak paylaşılmış bir bilgi nedeniyle sporcu sağlığında oluşabilecek olumsuzluklardan sorumlu tutulamaz.</p>
			</div>
		</section>
	</div>
	<div class="controls">
		<input type="hidden" name="code" value="${newReg.getCode()}">
		<div class="extra_options">
			<button type="button" class="cancel">Kaydı İptal Et</button>
		</div>
		<button type="button" class="back">Geri</button>
		<button type="button" id="submit_button" class="forward" disabled="disabled">İleri</button>
	</div>
</form>