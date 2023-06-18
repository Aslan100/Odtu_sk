<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="registrant_form" class="form_unit">
	<div class="sections">
		<section>
			<h3>Veli Bilgileri</h3>
			<div class="input_container has_state">
				<input type="text" id="id_no_input" name="id_no" maxlength="11" data-always_enabled="true" placeholder=" " required/>
				<label for="id_no_input">T.C. Kimlik Numarası</label>
			</div>
			<div class="input_container select_container">
				<select id="parenthood_title_select" name="parenthood_title" data-always_enabled="true">
					<c:forEach items="${parenthoodTitles}" var="nextTitle">
						<option value="${nextTitle.toString()}">${nextTitle.getTitle()}</option>
					</c:forEach>
				</select>
				<label for="parenthood_title_select">Yakınlık Derecesi</label>
			</div>
			<div class="input_container">
				<input type="text" id="name_input" name="name"  data-always_enabled="false" placeholder=" " required/>
				<label for="name_input">Adı</label>
			</div>
			<div class="input_container">
				<input type="text" id="surname_input" name="surname"  data-always_enabled="false" placeholder=" " required/>
				<label for="surname_input">Soyadı</label>
			</div>
			<div class="inline_input_row equal_size_inputs">
				<div class="input_container">
					<input type="text" id="phone_number_input" name="phone_number" mode="phonenumber" data-always_enabled="false" placeholder=" " required/>
					<label for="phone_number_input">Telefon</label>
				</div>
				<div class="input_container has_state">
					<input type="text" id="email_input" name="email" data-always_enabled="false" placeholder=" " required/>
					<label for="email_input">E-posta</label>
				</div>
			</div>
			<div class="input_container">
				<input type="text" id="vehicle_plate_input" name="licence_plate" data-always_enabled="false" placeholder=" " required/>
				<label for="vehicle_plate_input">Araç Plakası</label>
			</div>	
		</section>
		<section id="parent_address_section">
			<h3>Veli Adres Bilgileri</h3>
			<div class="input_container">
				<input type="checkbox" id="address_check_checkbox" name="at_same_address_with_registered" data-always_enabled="false" value="true"/>
				<label for="address_check_checkbox">Veli Adresi Sporcu Adresi İle Aynı</label>
			</div>	
			<div class="input_container select_container">
				<select id="parent_city_select" name="address_city" data-always_enabled="false">
					<c:forEach items="${cities}" var="nextCity" varStatus="cityLoop">
						<option value="${nextCity.getId()}">${nextCity.getName()}</option>
					</c:forEach>
				</select>
				<label for="parent_city_select">İl</label>
			</div>
			<div class="input_container select_container">
				<select id="parent_district_select" name="address_district" data-always_enabled="false"></select>
				<label for="parent_district_select">İlçe</label>
			</div>
			<div class="input_container select_container">
					<select id="parent_neighbourhood_select" name="address_neighbourhood" data-always_enabled="false"></select>
					<label for="parent_neighbourhood_select">Mahalle</label>
				</div>
			<div class="input_container">
				<textarea id="parent_address_area" name="address" data-always_enabled="false" placeholder=" " required></textarea>
				<label for="parent_address_area">Adres</label>
			</div>
		</section>
	</div>
	<div class="controls">
		<input type="hidden" name="code" value="${newReg.getCode()}">
		<input type="hidden" id="registrant_id_input" name="registrant_id" value="">
		<input type="hidden" id="is_sibling_registration_input" name="is_sibling_registration" value="false">
		<div class="extra_options">
			<button type="button" class="cancel">Kaydı İptal Et</button>
		</div>
		<button type="button" class="back">Geri</button>
		<button type="button" id="submit_button" class="forward">İleri</button>
	</div>
</form>