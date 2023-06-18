var isRemoteServerMode = false;

var portalIndexURL = isRemoteServerMode ? "/" : "/ODTU_SK";
var thumbnailsURI = isRemoteServerMode ? "https://online.tuvakademi.org/local/thumbnail/" : (portalIndexURL + "/local/thumbnail/");

var webLoginURL = portalIndexURL + "/auth/web_login.jsp";
var logoutURL = portalIndexURL + "/auth/logout.jsp";
var parentDataURL = portalIndexURL + "/query/get_parent_data.jsp";
var athleteDataURL = portalIndexURL + "/query/get_athlete_data.jsp";
var teamDataURL = portalIndexURL + "/query/get_team_data.jsp";
var branchDataURL = portalIndexURL + "/query/get_branch_data.jsp";
var trainerDataURL = portalIndexURL + "/query/get_trainer_data.jsp";
var cityDataURL = portalIndexURL + "/query/get_city_data.jsp";
var locationDataURL = portalIndexURL + "/query/get_location_data.jsp";
var loginableLicenceDataURI = portalIndexURL + "/query/get_loginable_licence_data.jsp";
var loginableMedicalDataURI = portalIndexURL + "/query/get_loginable_medical_data.jsp";
var trainingParameterDataURI = portalIndexURL + "/query/get_training_parameter_data.jsp";
var eventParameterDataURI = portalIndexURL + "/query/get_event_parameter_data.jsp";
var trainingDataURI = portalIndexURL + "/query/get_training_data.jsp";
var eventDataURI = portalIndexURL + "/query/get_event_data.jsp";
var eventSquadLoginableDataURI = portalIndexURL + "/query/get_event_squad_loginable_data.jsp";
var eventTeamDataURI = portalIndexURL + "/query/get_event_team_data.jsp";
var suitableTrainingsForRegistrationDataURI = portalIndexURL + "/query/get_suitable_training_data_for_registration.jsp";
var suitablePaymentSchemasForRegistrationDataURI = portalIndexURL + "/query/get_suitable_payment_schema_data_for_registration.jsp";
var paymentPlanForRegistrationDataURI = portalIndexURL + "/query/get_payment_plan_for_registration.jsp";
var paymentPlanDataURI = portalIndexURL + "/query/get_payment_plan_data.jsp";
var promotionDataURI = portalIndexURL + "/query/get_promotion_data.jsp";

var athleteManURI = portalIndexURL + "/dbman/manipulate_athlete.jsp";
var trainerManURI = portalIndexURL + "/dbman/manipulate_trainer.jsp";
var teamManURI = portalIndexURL + "/dbman/manipulate_team.jsp";
var squadManURI = portalIndexURL + "/dbman/manipulate_squad.jsp";
var locationManURI = portalIndexURL + "/dbman/manipulate_location.jsp";
var paymentSchemaManURI = portalIndexURL + "/dbman/manipulate_payment_schema.jsp";
var promotionManURI = portalIndexURL + "/dbman/manipulate_promotion.jsp";
var promotionCodeGeneratorURI = portalIndexURL + "/dbman/generate_promotion_code.jsp";
var paymentPlanManURI = portalIndexURL + "/dbman/manipulate_payment_plan.jsp";
var paymentManURI = portalIndexURL + "/dbman/manipulate_payment.jsp";
var documentManURI = portalIndexURL + "/dbman/manipulate_document.jsp";
var medicalDataManURI = portalIndexURL + "/dbman/manipulate_medical_data.jsp";
var trainingManURI = portalIndexURL + "/dbman/manipulate_training.jsp";
var eventManURI = portalIndexURL + "/dbman/manipulate_event.jsp";
var branchManURI = portalIndexURL + "/dbman/manipulate_branch.jsp";

var certainRegistrationStep1URI = portalIndexURL + "/dbman/manipulate_certain_registration_s1.jsp";
var certainRegistrationStep2URI = portalIndexURL + "/dbman/manipulate_certain_registration_s2.jsp";
var certainRegistrationStep3URI = portalIndexURL + "/dbman/manipulate_certain_registration_s3.jsp";
var certainRegistrationStep4URI = portalIndexURL + "/dbman/manipulate_certain_registration_s4.jsp";
var certainRegistrationStep5URI = portalIndexURL + "/dbman/manipulate_certain_registration_s5.jsp";
var certainRegistrationStep6URI = portalIndexURL + "/dbman/manipulate_certain_registration_s6.jsp";
var certainRegistrationStepBackURI = portalIndexURL + "/dbman/manipulate_certain_registration_step_back.jsp";

var dataManipulationRegExp = "^sonuc:[0-1]{1}$";
var responseErrorRegExp = "^hata:[0-9]{2}$";
var noDataErrorRegExp = "^hata:04$";
var quotaOverflowRegExp = "^hata:09$";
var emailRegExp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.([a-z]{2,})+$";
var nameRegExp = "^([A-Za-zÇĞİÖŞÜçğıöşü][A-Za-zÇĞİÖŞÜçğıöşü]*[.]+?\\s)*(([A-Za-zÇĞİÖŞÜçğıöşü]){2,}\\s?)+$";
var surnameRegExp = "^([A-Za-zÇĞİÖŞÜçğıöşü]{2,}\\s?)+$";
var birthDateRegExp = "^[0-9]{2}\/?[0-9]{2}\/?[0-9]{4}$";
var phoneNumberRegExp = "^([+]|(00))?([0]|(90))\\s?([0-9]{3}|[(][0-9]{3}[)])\\s?[0-9]{3}\\s?[0-9]{2}\\s?[0-9]{2}$";

var manipulationModes = ["FF", "FT", "TF", "TT"];
var manipulationModeStr = ["Yetki Yok", "Yalnızca Değişiklik Yapabilir", "Yalnızca Görüntüleyebilir", "Görüntüleyebilir ve Değişiklik Yapabilir"];

var months = ["Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"];
var monthVals = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
var days = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"];
var dayVals = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"];
var weekDays = ["Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"];
var hourVals = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"];
var hourValsShort = ["07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18"];
var minVals = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"];
var minValsShort = ["00", "10", "15", "20", "30", "40", "45", "50"];
var timeVals = ["06:00", "06:15", "06:30", "06:45", "07:00", "07:15", "07:30", "07:45", "08:00", "08:15", "08:30", "08:45", "09:00", "09:15", "09:30", "09:45", "10:00", "10:15", "10:30", "10:45", "11:00", "11:15", "11:30", "11:45", "12:00", "12:15", "12:30", "12:45", "12:00", "12:15", "12:30", "12:45", "13:00", "13:15", "13:30", "13:45", "14:00", "14:15", "14:30", "14:45", "15:00", "15:15", "15:30", "15:45", "16:00", "16:15", "16:30", "16:45", "17:00", "17:15", "17:30", "17:45", "18:00", "18:15", "18:30", "18:45", "19:00", "19:15", "19:30", "19:45", "19:00", "19:15", "19:30", "19:45", "20:00", "20:15", "20:30", "20:45", "21:00", "21:15", "21:30", "21:45", "22:00", "22:15", "22:30", "22:45", "23:00", "23:15", "23:30", "23:45"];

var loginableStates = ["PENDING", "ACTIVE", "SUSPENDED", "GHOST"];
var loginableStateVals = ["Onay Bekliyor", "Etkin", "Askıda", "Hayalet"];

var membershipStates = ["STANDARD", "FROZEN", "DISMISSED"];
var membershipStateVals = ["Standart", "Kayıt Dondurulmuş", "İlişik Kesilmiş"];

var genders = ["FEMALE", "MALE", "PREFER_NOT_TO_SAY"];
var genderVals = ["Kadın", "Erkek", "Belirtmek İstemiyor"];

var currencyVals = ["TRY", "USD", "EUR", "GBP", "RUB"];
var currencyTRVals = ["Türk Lirası", "Amerikan Doları", "Avro", "İngiliz Sterlini", "Rus Rublesi"];

var branches = ["WATER_POLO", "BASKETBALL", "VOLLEYBALL", "ROWING", "OTHER"];
var branchVals = ["Sutopu", "Basketbol", "Voleybol", "Kürek", "Diğer"];

var eventTypes = ["OFFICIAL_MATCH", "FRIENDLY_MATCH", "TOURNAMENT", "CAMP", "OTHER"];
var eventTypeVals = ["Resmi Müsabaka", "Özel Müsabaka", "Turnuva", "Kamp", "Diğer"];

var bloodTypes = ["A_RH_POS", "B_RH_POS", "ZR_RH_POS", "AB_RH_POS", "A_RH_NEG", "B_RH_NEG", "ZR_RH_NEG", "AB_RH_NEG"];
var bloodTypeVals = ["A RH(+)", "B RH(+)", "0RH(+)", "AB RH(+)", "A RH(-)", "B RH(-)", "0 RH(-)", "AB RH(-)"];

var ageGroups = ["U10", "U11", "U12", "U13", "U14", "U15", "U16", "U17", "U18", "U19", "U20", "U21"];
var ageGroupVals = ["U-10", "U-11", "U-12", "U-13", "U-14", "U-15", "U-16", "U-17", "U-18", "U-19", "U-20", "U-21"];

var trainerLabels = ["MASTER", "SENIOR", "PROFICIENT", "BEGINNER", "TRAINEE"];
var trainerLabelVals = ["Usta Antrenör", "Kıdemli Antrenör", "Yetkin Antrenör", "Kıdemsiz Antrenör", "Çömez Antrenör"];

var memberRoles = ["GROUP_HEAD", "HEAD_TRAINER", "TRAINER", "ATHLETE", "PHSIOTHERAPIST", "MEDICAL_PERSONNEL", "OFFICER", "OTHER"];
var memberRoleVals = ["Kafile Başkanı", "Baş Antrenör", "Antrenör", "Sporcu", "Fizyoterapist", "Sağlık Görevlisi", "Görevli", "Diğer"];

var timeUnits = ["DAYS", "WEEKS", "MONTHS", "YEARS"];
var timeUnitVals = ["Gün", "Hafta", "Ay", "Yıl"];
var periodicTimePhraseVals = ["Günde Bir", "Haftada Bir", "Ayda Bir", "Yılda Bir"];

var schemaTypes = ["INTERVAL_CONSTRAINED_SCHEMA", "EQUIDISTRIBUTED_SCHEMA"];
var schemaTypeVals = ["Dönem Kısıtlı Plan", "Eşit Taksitli Plan"];

var paymentStates = ["PENDING", "NO_PAYMENT", "PARTIAL_PAYMENT", "PAID", "LATE_PAYMENT"];
var paymentStateVals = ["Bekliyor", "Ödenmedi", "Kısmi Ödeme", "Ödendi", "Geç Ödeme"];

var paymentFailureActions = ["NO_ACTION", "REMOVE_FROM_ACTIVITIES", "SUSPEND"];
var paymentFailureActionVals = ["Eylem Yok", "Etkinliklerden Çıkart", "Etkinliklerden Çıkart ve Askıya Al"];

var subscriptionCancellationActions = ["ENFORCE_PLAN", "PACIFY_FUTURE_PAYMENTS", "CANCEL_FUTURE_PAYMENTS"];
var subscriptionCancellationActionVals = ["Plan Sonuna Kadar Ödemeleri Al", "İleri Vade Ödemeleri Pasifize Et", "İleri Vade Ödemeleri İptal Et"];
var subscriptionCancellationActionDescs = ["Geçmiş ve gelecekteki tüm tahakkuk etmiş ve ödenmemiş aidatlar tahsilattan çekilecektir.", "Geçmişten kalan tüm tahakkuk etmiş ve ödenmemiş aidatların tahsilatını takiben, ileriye dönük tüm tahakkuk etmiş ve ödenmemiş aidatlar tahsilattan çekilecektir.", "Ödeme Planı çerçevesinde tahakkuk etmiş ve ödenmemiş tüm aidatların tahsilini takiben ilişik kesilecektir."];

var menuAnimMsec = 200;

var dialogDisplayDurationMsec = 4000;
var ajaxResultDelayMsec = 500;

$(document).ready(function()
{
	$("table.data_table:not(.unpaged)").each(function()
	{
		let table = this;
		setTablePagination(table); 
		
		$(this).find("input#filter_input").eq(0).keyup(function()
		{
			filterTable(table, $(this).val());
			setTablePagination(table);
		});
	});
	
	$("input.filter_input[data-for!='']").keyup(function()
	{
		let table = $(document).find("table#" + $(this).data("for"));
		filterTable(table, $(this).val());
		setTablePagination(table);
	});
	
	$("div.main_container > ul.top_menu > li > button").click(function(event)
	{
		event.preventDefault();
		event.stopPropagation();
		
		$("div.main_container > ul.top_menu > li > ul").removeClass("display_submenu");
		$(this).parent().parent().addClass("bring_to_top");
		$(this).parent().find("ul").eq(0).addClass("display_submenu");
	});
	
	$("body").click(function(event)
	{
		event.stopPropagation();
		$("div.main_container > ul.top_menu").removeClass("bring_to_top");
		$("div.main_container > ul.top_menu > li > ul").removeClass("display_submenu");
	});
	
	$("input[type=text][mode=phonenumber]").on("input", event => 
	{
		$(event.target).val($(event.target).val().replace(/[^0-9\+\(\)_\s]/g, ""));
		let strippedVal = $(event.target).val().replace(/[^0-9]/g, "");
		
		strippedVal = strippedVal.substring(0, 4) == "0090" ? "90" + strippedVal.substring(4) : strippedVal;
		strippedVal = strippedVal.substring(0, 2) == "00" ? "90" + strippedVal.substring(2) : strippedVal;
		strippedVal = strippedVal.substring(0, 1) == "0" ? "9" + strippedVal : strippedVal; 
			
		if(strippedVal.length > 10)
		{
			let part1 = strippedVal.substring(0, 2);
			let part2 = strippedVal.substring(2, 5);
			let part3 = strippedVal.substring(5, 8);
			let part4 = strippedVal.substring(8, 10);
			let part5 = strippedVal.substring(10, 12);
			
			$(event.target).val("+" + part1 + " (" + part2 + ") " + part3 + " " + part4 + " " + part5);
		}
		else if(strippedVal.length > 8)
		{
			let part1 = strippedVal.substring(0, 2);
			let part2 = strippedVal.substring(2, 5);
			let part3 = strippedVal.substring(5, 8);
			let part4 = strippedVal.substring(8);
			
			$(event.target).val("+" + part1 + " (" + part2 + ") " + part3 + " " + part4);
		}
		else if(strippedVal.length > 5)
		{
			let part1 = strippedVal.substring(0, 2);
			let part2 = strippedVal.substring(2, 5);
			let part3 = strippedVal.substring(5);
			
			$(event.target).val("+" + part1 + " (" + part2 + ") " + part3);
		}
		else if(strippedVal.length > 2)
		{
			let part1 = strippedVal.substring(0, 2);
			let part2 = strippedVal.substring(2);
			
			$(event.target).val("+" + part1 + " (" + part2);
		}
		else if(strippedVal.length > 0)
			$(event.target).val("+" + strippedVal);
	});
	
	$("input[type=text][mode=phonenumber]").trigger("input"); 
	
	let posStr = (el, className) => $(el).hasClass(className) ? "top left" : "bottom left";
	let hidePicker = picker => 
	{
		let initialVal = $(picker).val();
		$(picker).datepicker("hide");
		$(picker).blur();
		$(picker).val(initialVal);
	};
	
	$("input[type=text][mode=datepicker]").each(function()
	{
		let thisPicker = this;
		$(this).datepicker({language: "tr", position: posStr(this, "top_aligned")});
		
		$(".io_container").scroll(() => { hidePicker(thisPicker) });
		$(window).resize(() => { hidePicker(thisPicker) }); 
		
		$(this).on("click", event => { $(this).select() });
		$(this).on("input", event => 
		{
			$(event.target).val($(event.target).val().replace(/[^0-9\/]/g, ""));
			let strippedVal = $(event.target).val().replace(/[^0-9]/g, "");
			
			try
			{
				if(strippedVal.length > 4)
				{
					let part1 = strippedVal.substring(0, 2);
					let part2 = strippedVal.substring(2, 4);
					let part3 = strippedVal.substring(4);
					
					$(event.target).val(part1 + "/" + part2 + "/" + part3);
				}
				else if(strippedVal.length > 2)
				{
					let part1 = strippedVal.substring(0, 2);
					let part2 = strippedVal.substring(2);
					
					$(event.target).val(part1 + "/" + part2);
				}
				else if(strippedVal.length > 0)
					$(event.target).val(strippedVal);
			}
			catch(err) {}
		});
	});
	
	$("#close_dbman_result_button").click(function(){ $(".dbman_result_container").remove(); });
	
	let resultTimeout = setTimeout(function()
	{
		$(".dbman_result_container").remove();
		clearTimeout(resultTimeout);
	}, 3000);
});

function initializeTablePagers(tableContainer)
{
	$(tableContainer).find("div.controls > button:first-child()").click(function(event)
	{
		let container = $(this).parent().parent();
		let tables = $(this).parent().siblings("table");
		
		let tableWidth = $(container).outerWidth();
		let tableCount = $(tables).length;
		
		let leftVal = parseInt($(tables).eq(0).css("left").substring(0, $(tables).eq(0).css("left").length - 2));
		let shiftCount = -1*Math.round(leftVal/tableWidth);  
		
		if(shiftCount > 0)
			$(tables).css("left", -(shiftCount - 1)*tableWidth + "px");
	});
	
	$(tableContainer).find("div.controls > button:last-child()").click(function(event)
	{
		let container = $(this).parent().parent();
		let tables = $(this).parent().siblings("table");
		
		let tableWidth = $(container).outerWidth();
		let tableCount = $(tables).length;
		
		let leftVal = parseInt($(tables).eq(0).css("left").substring(0, $(tables).eq(0).css("left").length - 2));
		let shiftCount = -1*Math.round(leftVal/tableWidth); 
		
		if(shiftCount < tableCount - 1)
			$(tables).css("left", -(shiftCount + 1)*tableWidth + "px");
	});
}