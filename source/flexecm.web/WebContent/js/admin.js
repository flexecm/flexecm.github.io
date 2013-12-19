
$(document).ready(function(){
	$("*[onclick]").each(function() {
		$(this).data("event", $(this).attr("onclick"));
		$(this).removeAttr("onclick");
		attachEvent($(this), function(t) {
			eval($(t).data("event"));
		});
	});
	initUi();
	pageBasic();
});

function clean() {
	$.getJSON("/service/reset", {
		pw : $("#confirm-clean-pwd").val()
	}, function() {
		location.href  = "boot.html";
	}).fail(function() {
		showAlert("密码不正确");
	});
}
function resetEmail () {
	var email = $("#email").val();
	var smtp = $("#smtp").val();
	var smtpport = $("#smtp-port").val();
	var smtppass= $("#smtp-password").val();
	
	if (email==""||smtp==""||smtpport==""||smtppass=="") {
		showAlert("输入不能为空");
		return;
	}

	if (!Utils.isEmail(email)) {
		showAlert("邮件地址格式不正确");
		return;
	}
	
	$.post("/service/person/email", {
		'email': email,
		'smtp': smtp,
		'port': smtpport,
		'emailpass': smtppass
	}, function() {
		pageBasic();
	});
}

function setSmtp() {
	pageBasic();
}

function pageBasic() {
	clear();
	$("#basic").show();
	
	$.getJSON("/service/admin/config", null, function (data) {
		if (data.defaultstore==null) {
			location.href = "boot.html";
		} else {
			if (data.adminemailok) {
				$("#admin-email").html(data.adminemail);
			} else {
				$("#admin-email").html(data.adminemail  +"<font color='red'>邮箱不可用</font>");
			}
			$("#default-store").html(data.defaultstore);
			$("#tomcat-path").html(data.BASEDIR);
		}
	});
}

function resetPwd() {
	if ($("#new-password").val()!=$("#re-password").val()) {
		closeDialog();
		showAlert("2次输入密码不一致");
		return;
	}
	if ($("#re-password").val()=="") {
		closeDialog();
		showAlert("新密码不能为空");
		return;
	}
	$.post("/service/password/modify", {
		"old": $("#old-password").val(),
		"new": $("#new-password").val()
	}, function() {
		closeDialog();
		showAlert("密码修改成功");
	}).fail(function() {
		closeDialog();
		showAlert("原密码提供错误");
	});
}

function showMessage(m, h) {
	if (typeof m === 'string') {
		$("#msg").html(m);
	} else {
		$("#msg").html("");
		$("#msg").append(m);
	}

	$("#msg").show();
	if (h) {
		setTimeout("hideMessage()", h);
	}
}

function pageUsers() {
	clear();
	$("#user-list").show();
	
	$.getJSON("/service/person/list", {
		skip: 0,
		limit: -1
	}, function(data) {
		$("#user-table tr.n").remove();
		for ( var i = 0; i < data.length; i++) {
			if (data[i].name=="admin") continue;
			var tr = $("#user-table tr.template").clone().removeClass("template").addClass("n");
			tr.data("user", data[i]);
			tr.find("td.name").html(data[i].name);
			tr.find("td.email").html(data[i].email);
			
			$("#user-table").append(tr);
			
			attachEvent(tr.find("td.remove a"), function(t) {
				var user = $(t).parent().parent().data("user");
				
				if (confirm("确认删除用户及其所有资料？") ) {
					$.post("/service/person/remove", {
						"id": user.name
					}, function() {
						$(t).parent().parent().remove();
					});
				}
			});
			
			attachEvent(tr.find("td.edit a"), function(t) {
				var user = $(t).parent().parent().data("user");
				editUser(user);
			});
		}
	});
}

function saveUser() {
	var userId = $("#userid").val();
	var password = $("#password").val();
	var email  = $("#user-email").val();
	
	if (userId=="") {
		showAlert("用户账号不能为空");
		return;
	}

	var u = $("#user-dialog").data("u");
	if (u==null) {
		$.post("/service/person/add", {
			"userId": userId,
			"password": password,
			"quota": -1,
			"email": email
		}, function(data) {
			pageUsers();
		}).fail(function() {
			alert("用户账户冲突");
		});
	} else {
		$.post("/service/person/modify", {
			"userId": userId,
			"password": password,
			"email": email
		}, function(data) {
			pageUsers();
		}).fail(function() {
		});
	}
}

function genPassword() {
	$("#password").val(Utils.genPass());
}

function editUser(u) {
	$("#user-dialog").data("u", u);
	if (u!=null) {
		$("#userid").val(u.name);
		$("#userid").attr("readonly", 1);
		$("#userid").css("background", "#ccc");
		
		$("#password").val("");
		$("#user-email").val(u.email);
	} else {
		$("#userid").val("");
		$("#userid").removeAttr("readonly");
		$("#userid").css("background", "white");
		$("#password").val("");
		$("#user-email").val("");
	}
	showDialog("user-dialog");
}

function main() {
	location.href = "/web/home.html";
}