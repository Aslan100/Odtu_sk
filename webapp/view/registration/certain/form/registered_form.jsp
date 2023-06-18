<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="registered_data_form" class="form_unit">
	<div  class="sections">
		<section>
			<h3>Sporcu Temel Bilgileri</h3>
			<div class="input_container select_container">
				<select id="branch_select" name="registration_branch">
					<c:forEach items="${branches}" var="nextBranch" varStatus="branchLoop">
						<option value="${nextBranch.getId()}">${nextBranch.getTitle()}</option>
					</c:forEach>
				</select>
				<label for="branch_select">Branşı</label>
			</div>
			<div class="input_container">
				<input type="text" id="user_name_input" name="name" placeholder=" " required/>
				<label for="user_name_input">Adı</label>
			</div>
			<div class="input_container">
				<input type="text" id="user_name_input" name="surname" placeholder=" " required/>
				<label for="user_name_input">Soyadı</label>
			</div>
			<div class="input_container select_container">
				<select id="gender_select" name="gender">
					<c:forEach items="${genders}" var="nextGender" varStatus="genderLoop">
						<option value="${nextGender.toString()}">${nextGender.getName()}</option>
					</c:forEach>
				</select>
				<label for="gender_select">Cinsiyet</label>
			</div>
			<div class="inline_input_row equal_size_inputs">
				<div class="input_container">
					<input type="text" id="birthdate_input" name="birth_date" mode="datepicker" placeholder=" " required/>
					<label for="birthdate_input">Doğum Tarihi</label>
				</div>
				<div class="input_container select_container">
					<select id="place_of_birth_select" name="place_of_birth">
						<c:forEach items="${cities}" var="nextCity" varStatus="cityLoop">
							<script>cities.push(new City(parseInt("nextCity.getId()"))); cities[${cityLoop.index}].districts = [];</script>
							<c:forEach items="${nextCity.getDistricts()}" var="nextDistrict">
								<script>
									var nextDistrict = new District(parseInt("${nextDistrict.getId()}"), "${nextDistrict.getName()}");
									cities[${cityLoop.index}].districts.push(nextDistrict);
								</script>
							</c:forEach>
							<option value="${nextCity.getId()}">${nextCity.getName()}</option>
						</c:forEach>
					</select>
					<label for="place_of_birth_select">Doğun Yeri</label>
				</div>
			</div>
			<div class="input_container has_state">
				<input type="text" id="id_no_input" name="id_no" maxlength="11" placeholder=" " required/>
				<label for="id_no_input">T.C. Kimlik Numarası</label>
			</div>
			<div class="inline_input_row equal_size_inputs">
				<div class="input_container">
					<input type="text" id="phone_number_input" name="phone_number" mode="phonenumber" placeholder=" " required/>
					<label for="phone_number_input">Telefon</label>
				</div>
				<div class="input_container has_state">
					<input type="text" id="email_input" name="email" placeholder=" " required/>
					<label for="email_input">E-posta</label>
				</div>
			</div>	
		</section>
		<section>
			<h3>Sporcu Adres Bilgileri</h3>
			<div class="input_container select_container">
				<select id="athlete_city_select" name="address_city">
					<c:forEach items="${cities}" var="nextCity" varStatus="cityLoop">
						<option value="${nextCity.getId()}">${nextCity.getName()}</option>
					</c:forEach>
				</select>
				<label for="athlete_city_select">İl</label>
			</div>
			<div class="input_container select_container">
				<select id="athlete_district_select" name="address_district"></select>
				<label for="athlete_district_select">İlçe</label>
			</div>
			<div class="input_container select_container">
				<select id="athlete_neighbourhood_select" name="address_neighhbourhood"></select>
				<label for="athlete_neighbourhood_select">Mahalle</label>
			</div>
			<div class="input_container">
				<textarea id="athlete_address_area" name="address" placeholder=" " required></textarea>
				<label for="athlete_address_area">Adres</label>
			</div>
			<div class="caution_container">
				<img src="${linkPrefix}res/visual/icon/registration/warning.png"/>
				<p>Lütfen bu adımda sporcunun ikamet adres bilgilerini girin. Veli adres bilgileri ilerleyen adımlarda istenecektir</p>
			</div>
		</section>
	</div>
	<div class="controls">
		<input type="hidden" name="code" value="${newReg.getCode()}">
		<div class="extra_options">
			<button type="button" class="cancel">Kaydı İptal Et</button>
		</div>	
		<button type="button" class="back" disabled="disabled">Geri</button>
		<button type="button" id="submit_button" class="forward">İleri</button>
	</div>
</form>