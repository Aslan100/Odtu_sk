const tablePageSize = 10;

function setCookie(name, value, expiryDays) 
{
	if(expiryDays != null && expiryDays != "")
	{
		var d = new Date();
		d.setTime(d.getTime() + (expiryDays*24*60*60*1000));
		var expires = "expires=" + d.toUTCString();
		
		document.cookie = name + "=" + value + "; " + expires;
	}
	else
		document.cookie = name + "=" + value;
} 

function getCookie(name) 
{
    var name = name + "=";
    var cookies = document.cookie.split(';');
    
	for(var i=0; i < cookies.length; i++) 
	{
        var nextCookie = cookies[i];
		
        while (nextCookie.charAt(0) == ' ')
			nextCookie = nextCookie.substring(1);
        
		if (nextCookie.indexOf(name) == 0)
			return nextCookie.substring(name.length, nextCookie.length);
    }
    
	return null;
}

function deleteCookie(name) 
{
	document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function getAjaxConnector()
{
	var activexmodes=["Msxml2.XMLHTTP", "Microsoft.XMLHTTP"]
	
	if (window.ActiveXObject)
	{
		for (var i=0; i < activexmodes.length; i++)
		{
			try
			{
				return new ActiveXObject(activexmodes[i])
			}
			catch(e){}
		}
	}
	else if (window.XMLHttpRequest)
		return new XMLHttpRequest();
	else
		return null;
}

function ellipsizeStr(str, maxLength)
{
	var result = null;
	
	if(str != null && str.length >= maxLength)
		result = str.substring(0, maxLength - 3) + "...";
	else if(str != null && str.length < maxLength)
		return str;
		
	return result;	
}

//Tarih metodları
function getDateString(date, mode)
{
	try
	{
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
			
		if((day + "").length == 1)
				day = "0" + day;
			
		if((month + "").length == 1)
			month = "0" + month;
				
		if(mode == null || mode == 0)
			return day + "/" + month + "/" + year;
		else if(mode == 1)
			return year + "-" + month + "-" + day;
		else if(mode == 2)
			return day + " " + months[month - 1] + " " + year;
		else
			return null;
	}
	catch(err)
	{
		return null;
	}
}

function getTimeString(date, mode)
{
	try
	{
		var hour = date.getHours();
		var min = date.getMinutes();
		
		if((hour + "").length == 1)
			hour = "0" + hour;
			
		if((min + "").length == 1)
			min = "0" + min;
				
		if(mode == null || mode == 0)
			return hour + ":" + min;
		else if(mode == 1)
			return hour + "-" + min;
		else
			return null;
	}
	catch(err)
	{
		return null;
	}
}

function getDateTimeString(date, mode)
{
	try
	{
		if(date == null)
			throw new Exception("");
			
		return getDateString(date, mode) + " " + getTimeString(date, mode);
	}
	catch(err)
	{
		return null;
	}
}

function getTimezoneOffsetString()
{
	let offset = new Date().getTimezoneOffset();
	
	let offsetHours = parseInt(Math.floor(Math.abs(offset)/60));
	offsetHours = offsetHours < 10 ? "0" + offsetHours : offsetHours + "";
	let offsetMins = parseInt(Math.abs(offset)%60);
	offsetMins = offsetMins < 10 ? "0" + offsetMins : offsetMins + "";
	let offsetSign = (offset + "").substring(0, 1) == "-" ? "+" : "-";
	
	return offsetSign + offsetHours + ":" + offsetMins;
}

function validateISODateTimeString(dateTimeStr)
{
	try
	{
		var dateObj = new Date(dateTimeStr);
		dateObj.setTime(dateObj.getTime() + dateObj.getTimezoneOffset()*60*1000);
		var dateStr = getDateString(dateObj, 1);
		
		return (dateTimeStr.indexOf(dateStr) >= 0);
	}
	catch(err)
	{
		return false;
	}
}

//Inputlar
function makeSelectInput(options, values, id, cssClass, selectedValue, isDisabled)
{
	var selectEl = "<select";
	
	if(id != null && id.length > 0)
		selectEl += " id='" + id + "'";
		
	if(name != null && name.length > 0)
		selectEl += " name='" + name + "'";
	
	if(cssClass != null && cssClass.length > 0)
		selectEl += " class='" + cssClass + "'";
	
	if(isDisabled != null && isDisabled == true)
		selectEl += " disabled='disabled'";
		
	selectEl += ">";
	
	for(var i = 0; i < options.length; i++)
	{
		if(values[i] != selectedValue)
			selectEl += "<option value='" + values[i] +"'>" + options[i] + "</option>";
		else
			selectEl += "<option value='" + values[i] +"' selected>" + options[i] + "</option>";
	}
		
	selectEl += "</select>";
	
	return selectEl;
}

//Array eşitlikleri
function areArraysEqual(array1, array2)
{
	try
	{
		if(array1 === array2)
			return true;
		if(array1 == null || array2 == null)
			return false;
		if(array1.length != array2.length)
			return false;
		
		for (var i = 0; i < array1.length; ++i)
		{
			if(!array1[i].equals(array2[i]))
				return false;
		}
		
		return true;
	}
	catch(err)
	{
		return false;
	}
}

function getUrlParameter(sParam) 
{
    let sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;
	
	for (i = 0; i < sURLVariables.length; i++) 
    {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) 
        	return sParameterName[1] === undefined ? null : decodeURIComponent(sParameterName[1]);
    }
    
    return null;
}

function getPriceString(price) 
{
	let formattedOutput = new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY", minimumFractionDigits: 0, maximumFractionDigits: 2 });
	return formattedOutput.format(price).replace("₺", "");
}


function setTablePagination(table)
{
	let isItemIncluded = (i, page) => { return (i >= page*tablePageSize && i < (page +1)*tablePageSize) };
	
	let t = $(table);
	let page = 0;
	let totalItems = $(t).find("tbody tr").length;
	let displayedItems = $(t).find("tbody tr:not(.filter_out)").length;
	let totalPages = Math.ceil(displayedItems/tablePageSize);
	
	if(displayedItems < totalItems)
		$(t).find("tfoot td.controls span#totals > span").html(totalItems + " kayıt içinden " + displayedItems);
	else	
		$(t).find("tfoot td.controls span#totals > span").html(displayedItems);
	
	setTablePaginationStates();
	
	if(totalPages > 1)
	{
		$(t).find("tfoot td.controls button").prop("disabled", false);
		
		$(t).find("tfoot td.controls button#first_page").click(function()
		{
			page = 0;
			setTablePaginationStates();
		});
		
		$(t).find("tfoot td.controls button#last_page").click(function()
		{
			page = totalPages - 1;
			setTablePaginationStates();
		});
		
		$(t).find("tfoot td.controls button#next_page").click(function()
		{
			page = page < (totalPages - 1) ? page+1 : page;
			setTablePaginationStates();
		});
		
		$(t).find("tfoot td.controls button#prev_page").click(function()
		{
			page = page > 0 ? page-1 : 0 ;
			setTablePaginationStates();
		});
	}
	else
		$(t).find("tfoot td.controls button").prop("disabled", true);
	
	function setTablePaginationStates()
	{
		$(t).find("tbody tr:not(.filter_out)").each(function(index)
		{
			$(this).addClass("page_out");
			let inPage = isItemIncluded(index, page);
			
			if(inPage) 
				$(this).removeClass("page_out");
		});
		
		let pgStr = totalPages > 0 ? "Sayfa " + (page + 1) + "/" + totalPages : "Sayfa: 0/0";
		
		$(t).find("tfoot td.controls span#page_data").html(pgStr);
	}
}

function filterTable(table, phrase)
{
	let t = $(table);
	phrase = phrase.trim().toLowerCase();
	
	$(t).find("tbody tr").each(function(index)
	{
		if(phrase.length == 0)
			$(this).removeClass("filter_out");
		else
		{
			let content = $(this).html().toLowerCase();
			
			if(content.toString().indexOf(phrase) >= 0)
				$(this).removeClass("filter_out");
			else
				$(this).addClass("filter_out");
		}
	});
}

//Quill Araçları
function formatQLEditorContent(editorDiv)
{
	$(editorDiv).find("p, li, h3").each(function()
	{
		let htmlContent = $(this).html().trim();
		
		if(htmlContent.length == 0 || htmlContent == "<br>")
			$(this).remove();
		else
			$(this).html(htmlContent);
	});
	
	$(editorDiv).find("ul").each(function()
	{
		let liElCount = $(this).find("li").length;
		
		if(liElCount > 7 && liElCount <= 14)
			$(this).addClass("two_column_list");
		else if (liElCount > 14)
			$(this).addClass("three_column_list");
	});
	
	let finalHTML = $(editorDiv).html().trim();
	 
	return finalHTML.length > 0 ? finalHTML : null;
}