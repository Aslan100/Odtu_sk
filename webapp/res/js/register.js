var that = this;

$(document).ready(function()
{
	$("form#payment_data_form input[type='radio']").change(event =>
	{
		let payments = [];
		let dates = [];
		let monthlyPayment = 1000;
		
		if($(event.target).val() == 1)
		{
			let date = new Date();
	        let time = new Date(date.getTime());
	        time.setMonth(date.getMonth() + 1);
	        time.setDate(0);
	        let days = time.getDate() > date.getDate() ? time.getDate() - date.getDate() : 0;
			
			let months = 12 - (date.getMonth() + 1); 
			
			for(let i = 0; i < (days == 0 ? months : (months + 1)); i++)
			{
				if(i == 0 && days > 0)
				{
					let currentMonthPayment = monthlyPayment*(days/(time.getDate()));
					payments.push(currentMonthPayment.toFixed(2));
					dates.push(getDateString(date));
				}
				else
				{
					date.setDate(1);
					date.setMonth(date.getMonth() + 1);
					let nextPayment = monthlyPayment;
					payments.push(nextPayment);
					dates.push(getDateString(date));
				}
			}
		}
		else if($(event.target).val() == 2)
		{
			let date = new Date();
	        let time = new Date(date.getTime());
	        time.setMonth(date.getMonth() + 3);
	        time.setDate(0);
	        let days = time.getDate() > date.getDate() ? (time.getTime() - date.getTime())/(24*60*60*1000) : 0;
	        
	        let periods = 1;
	        
	        while(true)
	        {
	        	time.setMonth(time.getMonth() + 3);
	        	periods++;
	        	
	        	if(time.getYear() > date.getYear())
	        		break;
	        }
	        
			let periodPayment = monthlyPayment*3;
			
			for(let i = 0; i < periods; i++)
			{
				if(i == 0)
				{
					let currentPeriodPayment = periodPayment*(days/90);
					payments.push(currentPeriodPayment.toFixed(2));
					dates.push(getDateString(date));
				}
				else
				{
					date.setDate(1);
					date.setMonth(date.getMonth() + 3);
					let nextPayment = periodPayment;
					payments.push(nextPayment);
					dates.push(getDateString(date));
				}
			}
		}
		
		$("form#payment_data_form div.map_frame").html("");
		
		let paymentPlan = "";
		
		for(let i = 0; i < payments.length; i++)
			paymentPlan += "Tutar: " + payments[i] + " TL   Ödeme Tarihi: " + dates[i] + "<br>";
		
		$("form#payment_data_form div.map_frame").html(paymentPlan);
	});
});