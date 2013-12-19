
$(document).ready(function(){
	
	toList();
	
	
	attachEvent($(".cusr"), function() {
		$("#listing").hide();
		$("#user-form").show();
		$(".cancel").show();
		$(".yes").show();
		$(".cusr").hide();
	});
	
	attachEvent($(".cancel"), function(){
		toList();
	});
	
	attachEvent($("#unlimited"), function(t) {
		if ($(t).parent().find("label").hasClass("checked")) {
			$(t).parent().find("label").removeClass("checked");
			$("#quota").removeAttr("disabled");
		} else {
			$(t).parent().find("label").addClass("checked");
			$("#quota").attr("disabled", "disabled");
		}
	});
	
	attachEvent($("autoGen"), function() {
		$("#password").val(Utils.genPass());
	});
	
	attachEvent($(".yes.user"), function() {
		postUser();
	});
});

function toList() {
	$(".cusr").show();
	$("#listing").show();
	$("#user-form").hide();	
	$(".cancel").hide();
	$(".yes").hide();
	
	$("#listing ul li.item").remove();
	$.getJSON("/service/person/list", {
		skip: 0,
		limit: -1
	}, function(data) {
		
		for ( var i = 0; i < data.length; i++) {
			var cloned = $(".file-template").clone();
			cloned.removeClass("file-template").addClass("item");
			cloned.find("span.name").html(data[i].name);
			cloned.find("p.ico").addClass("person");
			$("#listing ul").append(cloned);
		}
	});
}

function postUser() {
	$.post("/service/person/create", 
			{
				name: $("#nickname").val(),
				password: $("#password").val(),
				quota:parseInt( ($("#quota").attr("disabled")=="disabled")?-1:$("#quota").val())
			}, function() {
				
			}
	);
}