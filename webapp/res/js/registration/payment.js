$(document).ready(function()
{
	let cardHolderPlaceHolder = $("div#card_visual p.card_holder").html();
	let panHtmlPlaceHolder = $("div#card_visual div.pan > p").html();
	let expiryDatePlaceHolder = $("div#card_visual p.expiry_date").html();
	let cvvPlaceHolder = $("div#card_visual div.cvv > p").html();
	
	$("input[name=pan]").on("input", event => 
	{
		$("div#card_visual").removeClass("flipped");
		
		$(event.target).val($(event.target).val().replace(/[^0-9-\s]/g, ""));
		let strippedVal = $(event.target).val().replace(/[^0-9]/g, "");
		let formattedVal = "";
		
		if(strippedVal.length > 12)
		{
			let part1 = strippedVal.substring(0, 4);
			let part2 = strippedVal.substring(4, 8);
			let part3 = strippedVal.substring(8, 12);
			let part4 = strippedVal.substring(12, 16);
			
			formattedVal = part1 + "-" + part2 + "-" + part3 + "-" + part4;
		}
		else if(strippedVal.length > 8)
		{
			let part1 = strippedVal.substring(0, 4);
			let part2 = strippedVal.substring(4, 8);
			let part3 = strippedVal.substring(8, 12);
			
			formattedVal = part1 + "-" + part2 + "-" + part3;
		}
		else if(strippedVal.length > 4)
		{
			let part1 = strippedVal.substring(0, 4);
			let part2 = strippedVal.substring(4);
			
			formattedVal = part1 + "-" + part2;	
		}
		else if(strippedVal.length > 0)
			formattedVal = strippedVal;
		
		$(event.target).val(formattedVal);
		let parts = formattedVal.split("-");
		
		if(parts.length == 0 || (parts.length == 1 && parts[0].trim().length == 0))
			$("div#card_visual div.pan > p").html(panHtmlPlaceHolder);
		else
		{
			while(parts.length < 4)
				parts.push("<span>****</span>");
				
			let panHtml = "";
			
			for(let i = 0; i < parts.length; i++)
			{
				let nextPart = parts[i];
				 
				while(nextPart.length < 4)
					nextPart += "*";
				
				panHtml += "<span>" + nextPart + "</span>";
			}
			
			$("div#card_visual div.pan > p").html(panHtml);
		}
	});
	
	$("input[name=card_holder]").on("input", event => 
	{
		$("div#card_visual").removeClass("flipped");
		
		let holderVal = $(event.target).val().toUpperCase();
		holderVal = holderVal.trim().length > 0 ? holderVal.trim() : cardHolderPlaceHolder;
		$("div#card_visual p.card_holder").html(holderVal);
	});
	
	$("select[name=Ecom_Payment_Card_ExpDate_Month], select[name=Ecom_Payment_Card_ExpDate_Year]").change(() => 
	{
		$("div#card_visual").removeClass("flipped");
		
		let month = $("select[name=expiry_month]").find("option:selected").val();
		let year = $("select[name=expiry_year]").find("option:selected").val();
		
		if(month != -1 && year != -1)
			$("div#card_visual p.expiry_date").html("<span>VALID<br>THRU</span>" + month + "/" + year.substring(2));
		else
			$("div#card_visual p.expiry_date").html(expiryDatePlaceHolder);
	});
	
	$("input[name=cv2]").on("input", event => 
	{
		$(event.target).val($(event.target).val().replace(/[^0-9-\s]/g, ""));
		
		$("div#card_visual").addClass("flipped");
		let cvvVal = $(event.target).val().length > 0 ? $(event.target).val() : cvvPlaceHolder;
		$("div#card_visual div.cvv > p").html(cvvVal);
	});
	
	$("button#flip_button").click(event =>  
	{
		event.preventDefault();
		$("div#card_visual").toggleClass("flipped");
	});
});