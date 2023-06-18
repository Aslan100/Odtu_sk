$(document).ready(function()
{
	$("input[type='radio'][name='registration_option']").change(function(event)
	{
		$("input[type='radio'][name='registration_option']").siblings("button").prop("disabled", "disabled");
		$(this).siblings("button").prop("disabled", false);
	});
	
	$("input[type='radio'][name='registration_option']").siblings("button").click(function(event)
	{
		window.location = $(this).siblings("input[type=radio]").val(); 
	});
});