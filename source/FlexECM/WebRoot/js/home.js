var currentEntity = {};
var currentUser ;
var timer, delay = 500;

$(document).ready(function(){
	$("*[onclick]").each(function() {
		$(this).data("event", $(this).attr("onclick"));
		$(this).removeAttr("onclick");
		attachEvent($(this), function(t) {
			eval($(t).data("event"));
		});
	});
	initUi();
	initUploader();
	pageMyRepo();
});


function initUi() {
	$("div.oper").hide();
	clear();
	
	$('#shareToFilter').bind('keydown blur change', function(e) {
	    var _this = $(this);
	    clearTimeout(timer);
	    timer = setTimeout(function() {
	      onInputUserChange(_this.val());
	    }, delay );
	});
	
	$.getJSON("/service/person/current", null, function(user) {
		currentUser = user;
	});
}


function clear() {
	hideMessage();
	hideDialog();
	closeRight();
	$(".base .center>div").hide();
	$("div.head div.oper").hide();
}

var ROLE_LIST=["Admin", "Coordinator", "Contributor", "Creator", "Reader"];

function showButtons(role, selected) {
	$("div.head div.oper").show();
	$("a.head-btn").addClass("disabled");
	$("a.head-btn").each(function() {
		if ($(this).attr("selectonly")=="true") { //on for file selected  like copy/move/delete etc.
		} else {
			if (ROLE_LIST.indexOf(role)<=ROLE_LIST.indexOf($(this).attr("role"))) {
				$(this).removeClass("disabled");
			}
		}
	});
}

function onFileChecked() {
	var files = getCheckedFiles();
	$("div.oper").show();
	$("a.head-btn").addClass("disabled");
	//如果文件未选中，则将需要选择才能操作的按钮置灰
	$("a.head-btn").each(function() {
		if ($(this).attr("select")=="*") { //always show
			$(this).removeClass("disabled");
		}
		if ($(this).attr("select")==">0" && files.length>0) { //on for file selected  like copy/move/delete etc.
			$(this).removeClass("disabled");
		}
		if ($(this).attr("select")=="=1" && files.length==1) { //on for file selected  like copy/move/delete etc.
			$(this).removeClass("disabled");
		}
	});
}

function hideDialog(dialogname) {
	$("div.hide-bg").hide();
	if (dialogname==null) {
		$("div.dialog").hide();
	} else {
		$("#" + dialogname).hide();
	}
}

function showDialog(dialogname) {
	closeDialog();
	$("div.hide-bg").show();
	var left = ($(window).width() - parseInt($("#" + dialogname).css("width")))/2;
	var top = ($(window).height() - parseInt($("#" + dialogname).css("height")))/2;
	$("#" + dialogname).css("left", left);
	$("#" + dialogname).css("top", top);
	$("#" + dialogname).show();
}

function closeDialog() {
	$("div.hide-bg").hide();
	$(".dialog").fadeOut("fast");
}

function listClipboardFiles() {
	showClipboard();
}

function listUploading() {
	$(".base .right").css("right", 0);
	$(".base .right").css("opacity", 1);
	$(".base .center").css("right", 320);
	
	$(".right>div").hide();
	$("#uploading").show();
}


function addToClipboard() {
	var files = getCheckedFiles();
	
	for ( var i = 0; i < files.length; i++) {
		var file = files[i];
		if ($("#clip_"+file._id).length>0) continue;
		var li = $("li.cbitemTemplate").clone();
		li.data("file", file);
		li.attr("id", "clip_" + file._id);
		li.removeClass("cbitemTemplate").removeClass("hidden").addClass("cbitem");
		li.find(".filename").html(file.name);
		
		if (file.type=="s:folder") {
			li.find("p.ico").addClass("fd");
		} else {
			var endfix = file.name.substring(file.name.lastIndexOf(".")+1);
			li.find("p.ico").addClass(endfix);
		}
		
		attachEvent(li.find("a.mv"), function (d) {
			var fileData = $(d).parents("li.cbitem").data("file");
			if (fileData.pid==currentEntity._id) {
				showMessage("文件就在此目录中");
				return;
			}
			$.post("/service/file/move", {
				"srcPath": [fileData._id],
				"targetPath": currentEntity._id
			}, function() {
				refreshFolder("文件已经移动");
				$("#clip_"+fileData._id).remove();
			}).fail(function(error) {
				if (error.status==409) {
					showMessage("目录已经有同名文件");
				}
				if (error.status==406) {
					showMessage("不能移动到自己的子目录");
				}
			});
		});

		attachEvent(li.find("a.cp"), function(d) {
			var fileData = $(d).parents("li.cbitem").data("file");
			
			$.post("/service/file/copy", {
				"srcs": [fileData._id],
				"target": currentEntity._id
			}, function() {
				refreshFolder();
				$("#clip_"+fileData._id).remove();
			}).fail(function(error) {
				if (error.status==409 || error.status==400) {
					showMessage("目录已经有同名文件");
				}
				if (error.status==406) {
					showMessage("不能复制到自己的子目录");
				}
				
			});
		});
		
		$("#cblist").append(li);
	}
	
	unCheckFiles();
	showClipboard();
}

function clearClipboard() {
	$("#cblist>li.cbitem").remove();
}

function copy() {
	showDialog("copy-move-dialog");
	$("#copy-move-dialog").data("action", "copy");
	addDialogTreeNode(null, "_user_root", "我的文件", "home", true);
	addDialogTreeNode(null, "_public_root", "公共目录", "public", true);
	addDialogTreeNode(null, "_shared_root", "分享的目录", "shared", true);
}
function move() {
	showDialog("copy-move-dialog");
	$("#copy-move-dialog").data("action", "move");
	addDialogTreeNode(null, "_user_root", "我的文件", "home", true);
	addDialogTreeNode(null, "_public_root", "公共目录", "public", true);
	addDialogTreeNode(null, "_shared_root", "分享的目录", "shared", true);
}


function addDialogTreeNode(parent, id, name,icon, hasChildren) {
	if ($("#target" + id).length>0) return;
	
	var li = $('<li><span><em></em><dfn></dfn><span class="name"></span></span><ul></ul></li>');
	var parentul;
	if(parent==null) {
		parentul = $("#copyToUL");
	} else {
		parentul = $("#target" + parent);	
	}
	
	
	li.find("ul").attr("id", "target" + id);
	li.find("span.name").html(name);
	if (hasChildren) {
		li.find("em").addClass("plus");
	} else {
		li.find("em").addClass("empty");
	}
	li.find("dfn").addClass(icon);
	li.find("em").data("pid", id);
	
	attachEvent(li.find("em"), function(em) {  // the open child events
		var parentId = $(em).data("pid");
		if (em.hasClass("plus")) {
			if (parentId=="_user_root") {
				$.getJSON("/service/repository/entity/childcontainer", {
					"skip":0,
					"limit":1000
				}, function(data) {
					handleList(data.list, parentId);
				});
			} else if (parentId=="_public_root") {
				$.getJSON("/service/repository/public/list", {
				}, function(list) {
					handleList(list, parentId);
				});
			} else if (parentId=="_shared_root") {
				$.getJSON("/service/share/list", {
				}, function(list) {
					handleList(list, parentId);
				});
			} else {
				$.getJSON("/service/repository/entity/childcontainer", {
					"pid": parentId,
					"skip": 0,
					"limit":1000
				}, function(data) {
					handleList(data.list, parentId);
				});
			}
			
			function handleList(list, ulId) {
				for ( var i = 0; i < list.length; i++) {
					if (list[i].fot>0) {
						addDialogTreeNode(ulId, list[i]._id,list[i].name, "closed",true);
					} else {
						addDialogTreeNode(ulId, list[i]._id,list[i].name, "closed",false);
					}
				}
				if (list.length==0) {
					em.removeClass("plus").addClass("empty");
				} else {
					em.removeClass("plus").addClass("minus");
				}
				if (em.next().hasClass("closed")) {
					em.next().removeClass("closed").addClass("open");
				}
			}
		} else if (em.hasClass("minus")) {
			$("#target" + parentId).find("li").remove();
			em.removeClass("minus").addClass("plus");
			if (em.next().hasClass("open")) {
				em.next().removeClass("open").addClass("closed");
			}
		}
	});
	
	attachEvent(li.find(">span"), function(span) {
		$('ul.tree li>span.checked').removeClass("checked");
		$(span).addClass("checked");
	});
	parentul.append(li);
}

function confirmCopyOrMove() {
	if ($('ul.tree li>span.checked').length==0) {
		return;
	}
	
	var targetId = $('ul.tree li>span.checked').find("em").data("pid");
	
	if (targetId.indexOf("_")==0) {
		if (targetId=="_user_root") {
			targetId=null;
		} else {
			return;
		}
	}
	
	var srcs = getCheckedFiles();
	var srcids = [];
	for ( var i = 0; i < srcs.length; i++) {
		srcids.push(srcs[i]._id);
	}
	
	if ($("#copy-move-dialog").data("action")=="copy") {
		hideDialog();
		showPenddingMsg("正在拷贝" + srcs.length + "个文件中");
		$.post("/service/file/copy", {
			"srcs" : srcids,
			"target": targetId
		}, function() {
			hideDialog();
			refreshFolder("文件已经拷贝完成");
		}).fail(function(error) {
			hideDialog();
			if (error.status==409) {
				showAlert("拷贝失败，原因：目的文件夹存在同名文件");
			} else if (error.status==406) {
				showAlert("拷贝失败，原因：无法将文件夹拷贝到自己的子目录下");
			}
		});
	} else if ($("#copy-move-dialog").data("action")=="move") {
		hideDialog();
		showPenddingMsg("正在移动" + srcs.length + "个文件中");
		$.post("/service/file/move", {
			"srcPath" : srcids,
			"targetPath": targetId
		}, function() {
			hideDialog();
			refreshFolder("文件已经移动完成");
		}).fail(function(error) {
			hideDialog();
			if (error.status==409) {
				showAlert("移动失败，原因：目的文件夹存在同名文件");
			} else if (error.status==406) {
				showAlert("移动失败，原因：无法将文件夹移动到自己的子目录下");
			}
		});
	}
}


function copyListItem() {
	if ($("#cblist>li.tocopy").length==0) return;
	
	var data = $("#cblist>li.tocopy").data("file");
	
	$("#cblist>li.tocopy").find("span.btns").hide();
	$("#cblist>li.tocopy").find("span.infos").show();
	
	$("#cblist>li.tocopy").find("span.infos").html("正在拷贝..");
	
	$.post("/service/file/copy", 
			{
				"srcs": [data._id],
				"target": currentEntity._id
			}, function() {
				refreshFolder();
				$("#cblist>li.tocopy").find("span.btns").show();
				$("#cblist>li.tocopy").find("span.infos").html("拷贝完成");
				
				if ($("#cblist>li.tocopy").next().length==0) {
					$("#cblist>li.tocopy").removeClass("tocopy");
					
					
				} else {
					$("#cblist>li.tocopy").removeClass("tocopy").next().addClass("tocopy");
					copyListItem();
				}
			}
	);
	
}


function showPenddingMsg(text) {
	showDialog("pendding-dialog");
	$("#pendding-dialog div.content").html(text);
}

function showAlert(text, func) {
	showDialog("alert-dialog");
	$("#alert-dialog div.content").html(text);
}

function showConfirm() {
	
}

function closeRight(div) {
	$(".base .right").css("right", -400);
	$(".base .center").css("right", 0);
	
	if (div!=null) {
		$("#" + div).hide();
	}
}

function showClipboard() {
	$(".base .right").css("right", 0);
	$(".base .right").css("opacity", 1);
	$(".base .center").css("right", 320);
	$(".right>div").hide();
	$("#clipboard").show();
}

function showTrash() {
	$(".base .right").css("right", 0);
	$(".base .right").css("opacity", 1);
	$(".base .center").css("right", 320);

	$(".right>div").hide();
	$("#trash").show();
}

function addFolderDialog() {
	$("#name-dialog").data("file", null);
	$("#name-dialog input.name").val("新建文件夹");
	showDialog("name-dialog");
}

function listTrashFiles() {
	$.getJSON("/service/entity/trash/list", {
		"repo": encodeURI(currentEntity.repo) 
	}, function(data) {
		$("#trashlist").data("repo", currentEntity.repo);
		$("#trashlist li.trashItem").remove();
		for ( var i = 0; i < data.length; i++) {
			var file = data[i];
			if ($("#trash_"+file._id).length>0) continue;
			var li = $("li.trashitemTemplate").clone();
			
			li.data("file", file);
			li.attr("id", "trash" + file._id);
			li.removeClass("trashitemTemplate").removeClass("hidden").addClass("trashItem");
			
			li.find(".filename").html(file.oname);
			
			li.find("div.content p").html("<i>" + Utils.formatTime(file.modified) + "</i>" + "被<i>" + file.modifier +  "</i>删除");
			
			li.find("div.btns").hide();
			
			attachEvent(li.find("a.remove"), function(t) {
				removeTrashItem(t);
			});
			attachEvent(li.find("a.recover"), function(t) { 
				recoverTrashItem(t);
			});
			
			li.hover(function() {
				$(this).find("div.btns").show();
			}, function() {
				$(this).find("div.btns").hide();
			});
			
			if (file.type=="s:folder") {
				li.find("p.ico").addClass("fd");
			} else {
				var endfix = file.oname.substring(file.oname.lastIndexOf(".")+1);
				li.find("p.ico").addClass(endfix);
			}
			$("#trashlist").append(li);
		}
		showTrash();
	});
}

function removeTrashItem(t) {
	$.post("/service/entity/remove", {
		"id": $(t).parent().parent().data('file')._id
	}, function() {
		listTrashFiles();
		refreshFolder();
	});
}

function recoverTrashItem(t) {
	$.post("/service/file/recover", {
		"ids": [$(t).parent().parent().data('file')._id]
	}, function() {
		listTrashFiles();
		refreshFolder("文件已恢复");
	}).fail(function() {
		showMessage("原来路径不存在或者文件冲突", 3000);
	});
}

function recoverAll() {
	$.post("/service/file/recoverAll", {
				"repo" : $("#trashlist").data("repo")
			}, 
			function(data) {
				listTrashFiles();
				refreshFolder("文件已经还原");
	});
}

function clearTrash() {
	$.post("/service/trash/clean", {
				"repo" : $("#trashlist").data("repo")
			}, 
			function(data) {
				$("#trashlist li.trashItem").remove();
				refreshFolder("回收站已清空");
	});
}



function confirmName() {
	var fileData = $("#name-dialog").data("file");
	var newName = $("#name-dialog input.name").val();
	if (newName=="") {
		$("name-dialog .msg").html("文件名称不能为空");
		return;
	}
	if (fileData==null) { //add Folder
		$("name-dialog .msg").html("正在创建文件夹");
		$.post("/service/folder/create", {
			"id": currentEntity._id,
			"name": newName
		}, function() {
			listFolder(currentEntity._id);
			closeDialog();
		}).fail(function(jqXHR, textStatus, errorThrown) {
			if (jqXHR.status==409) {
				$("name-dialog .msg").html("同名文件已经存在");
			}
		});
	} else {
		$("name-dialog .msg").html("正在重命名");
		$.post("/service/file/rename", {
			"src": fileData._id,
			"newName": newName
		}, function() {
			listFolder(currentEntity._id);
			closeDialog();
		}).fail(function(jqXHR, textStatus, errorThrown) {
			if (jqXHR.status==409) {
				$("name-dialog .msg").html("同名文件已经存在");
			}
		});
	}
}

function rename() {
	var files = getCheckedFiles();
	if (files.length==0) return;
	$("#name-dialog").data("file", files[0]);
	$("#name-dialog input.name").val(files[0].name);
	showDialog("name-dialog");
}

function remove() {
	var files =  Utils.arrayProject(getCheckedFiles(), "_id");
	if (files.length==0) return;
	
	$.post("/service/file/moveToTrash",
			{"files":files},
			function() {
				listFolder(currentEntity._id, "已经移除" + files.length + "个文件到回收站");
				if ($("#trash:visible").length>0) {
					listTrashFiles();
				}
			}
	);
}

var lastRemoved = [];

function recoverLastRemoved() {
	
	while(lastRemoved.length>0) {
		var id = lastRemoved.pop();
		$.post("/service/entity/recover", {
					"id": id
				}, 
				function() {
					
				});
	}
	if (lastRemoved.length>0) {
		for ( var i = 0; i < lastRemoved.length; i++) {
			
		}
	}
}


function sort(list, sortby) {
	if (sortby==null) {
		list.sort(function(a, b) {
			if (a.type==b.type) return 0;
			if (a.type=="s:folder") {
				return -1; 
			} else {
				return 1;
			}
		});
	}
}

var currentSortBy = null;

function listFolder(id,msg) {
	if (msg) {
		showMessage(msg + ", 正在加载文件列表");
	} else {
		showMessage("正在加载文件列表");
	}
	$.getJSON("/service/repository/entity/list", {
		"skip": 0, 
		"limit": 100,
		"id": id
	}, function(result) {
		if (msg) {
			showMessage(msg, 3000);
		} else {
			hideMessage();
		}
		currentEntity = result;
		/*
		if (result.root) {
			$("#listing ul.list li.title div.ft").hide();
		} else {
			$("#listing ul.list li.title div.ft").show();
		}
		*/
		
		addCrumb(result.name, result._id, result.root);
		var list = result.list;
		$("#listing ul.files li.item").remove();
		
		sort(list,  currentSortBy);
		for ( var i = 0; i < list.length; i++) {
			var entity = list[i];
			var cloned = $("li.file-template").clone();
			cloned.removeClass("file-template");
			cloned.addClass("item");
			cloned.data("entity", entity);
			cloned.find("span.name").html(entity.name);
			
			if (entity.faceted!=null) {
				for (key in entity.faceted) {
					cloned.find("span.name").after("<span class='face'>" + entity.faceted[key] + "</span>");
				}
			}
			cloned.find("div.fs").html(entity.creator + "<br>" + Utils.formatTime(entity.created) + "创建");
			cloned.find("div.fm").html(Utils.formatFileSize(entity.size) +  "<br>"
					+  ((entity.fot!=null)?(entity.fot + "子文件夹，" + entity.fit + "子文件") : ""));
			
			if (entity.acl) {
				cloned.find("p.ico span.slot").addClass("shared");
			}
			
			if (entity.type=="s:folder") {
				cloned.find("p.ico").addClass("fd");
				attachEvent(cloned.find("span.name"), function(t) {
					var data = $(t).parent().parent().parent().data("entity");
					listFolder(data._id);
				});
			} else {
				cloned.find("p.ico").addClass(entity.ext);
				attachEvent(cloned.find("span.name"), function(t) {
					var data = $(t).parent().parent().parent().data("entity");
					window.open("/service/file/download?id=" + data._id);
				});
			}
			attachEvent(cloned, toggleFileChecked);
			$("#listing ul.files").append(cloned);
		}
		onFileChecked();
	});
}

function refreshFolder(msg) {
	listFolder(currentEntity._id, msg);
}

function parent() {
	listFolder(currentEntity.pid);
}

function listFolderByPath(file) {
	$(".base .center>div").hide();
	$("#listing").show();
	$("#fpath li.crumb").remove();
	listFolder(file._id);
}

function selectAll(t) {
	if ($("#listing ul.files li.item.checked").length==$("#listing ul.files li.item").length) {
		$("#listing ul.files li.item").removeClass("checked");
		$("#listing ul.files li.title").removeClass("checked");
	} else {
		$("#listing ul.files li.item").addClass("checked");
		$("#listing ul.files li.title").addClass("checked");
	}
	onFileChecked();
}

function toggleFileChecked(c) {
	if ($(c).hasClass("checked")) {
		$(c).removeClass("checked");
	} else {
		$(c).addClass("checked");
	}
	onFileChecked();
}

function getCheckedFiles() {
	var list = [];
	$("#listing ul.files li.checked").each(function() {
		if ($(this).data("entity")!=null) {
			list.push($(this).data("entity"));
		}
	});
	return list;
}

function getCheckedOneFile() {
	var files = getCheckedFiles();
	if (files.length==0) return null;
	 return files[0];
}


function unCheckFiles() {
	$("#listing ul.files li.checked").removeClass("checked");
}

function addCrumb(name, id, isRoot) {
	if ($("#fpath>li.crumb:last-child").data("eid")==id) return; //for refresh path
	if ($("#fpath>li.crumb:nth-last-child(2)").data("eid")==id) { // for nav to parent
		$("#fpath>li.crumb:last-child").remove();
		return;
	};
	
	var li =  $('<li class="crumb folder"><a href="javascript:void(0)" class=""><img src="../img/crumb.svg" ><span></span></a></li>');
	li.find("span").html(name);
	li.data("eid", id);
	
	$("#fpath li").removeClass("current");
	li.addClass("current");
	
	attachEvent(li, function(currentLi) {
		listFolder($(currentLi).data("eid"));
		$(currentLi).nextAll().remove();
	});
	
	if (isRoot) {
		$("#fpath li.crumb").remove();
		li.addClass("home");
		li.find("img").attr("src", "../img/home.svg");
	}
	$("#fpath").append(li);
}

function openRepo(repoData) {
	$(".base .center>div").hide();
	$("#listing").show();
	if (repoData==null) {
		listFolder(null);
	} else {
		listFolder(repoData.root);
	}
}

function pageMyRepo() {
	$(".base .center>div").hide();
	$("#listing").show();
	listFolder(null);
}

function pagePublic() {
	clear();
	$("#repo-list").show();
	
	$.getJSON("/service/repository/public/list", null, function(repoList){
		$("#my-repo .cloned").remove();
		
		for ( var i = 0; i < repoList.length; i++) {
			$("#repo-list li.snapshot").remove();
			for ( var i = 0; i < repoList.length; i++) {
				var repo = repoList[i];
				$("#repo-list").append(fileSnapshot(repo.root));
			}
		}
	});
}

function pageShares() {
	clear();
	$("#share-list").show();
	/*
	$.getJSON("/service/share/mine", null, function(list) {
		$("#my-share li.snapshot").remove();
		for ( var i = 0; i < list.length; i++) {
			var file = list[i];
			$("#my-share").append(fileSnapshot(file));
		}
	});
	*/
	$.getJSON("/service/share/received", null, function(list) {
		$("#share-me li.snapshot").remove();
		for ( var i = 0; i < list.length; i++) {
			var file = list[i];
			$("#share-me").append(fileSnapshot(file));
		}
	});
}

function fileSnapshot(file) {
	var cloned = $("li.share.template").clone();
	cloned.removeClass("template").addClass("snapshot");
	
	cloned.find(".title span").html(file.name);
	
	if (file.type=="s:folder") {
		cloned.find(".title img").addClass("ico-folder");
		
		for ( var j = 0; j < file.list.length&&j<4; j++) {
			var subfile = file.list[j];
			var cloned_child = $("li.template.sharesub").clone();
			cloned_child.removeClass("template");
			cloned_child.find("span").html(subfile.name);
			
			if (subfile.type=="s:folder") {
				cloned_child.find("img").addClass("ico-folder");
			} else {
				cloned_child.find("img").addClass(Utils.getFileExt(subfile.name));
			}
			cloned.find("div.list ul").append(cloned_child);
		}
		
		if (file.list.length>=4) {
			cloned.find("div.list ul").append("<li>......</li>");
		} else if (file.list.length==0) {
			cloned.find("div.list ul").append("<li>没有文件</li>");
		} 
	} else {
		cloned.find(".title img").addClass(file.ext);
		cloned.find("div.list ul").append("<li></li>");
	}
	cloned.find("span.time").html(file.owner);
	/*
	if (file.owner==currentUser.name) {
		cloned.find(".owner").html(file.permissions.length);
	} else {
	}
	*/
	cloned.find(".owner").html("进入");
	cloned.find(".owner").data("file", file);
	attachEvent(cloned.find(".owner"), function(e) {
		var file = $(e).data('file');
		listFolderByPath(file);
	}) ;
	return cloned;
}



function initUploader() {
	var uploader = new plupload.Uploader({
		runtimes : 'html5,html4',
		browse_button : 'pickFile',
		url : '/service/file/upload'
	});
	uploader.init();
	
	uploader.bind('FilesAdded', function(up, files) {  // fire when file is added to queue
		listUploading();
		for ( var i = 0; i < files.length; i++) {
			var file = files[i];
			var newli = $("li.uploadItemTemplate").clone();
			newli.attr("id", file.id);
			newli.removeClass("uploadItemTemplate").addClass("progress").removeClass("hidden");
			newli.show();
			
			newli.find("a.filename").html(file.name);
			newli.find("a.filename").attr("title", file.name);
			newli.find("p.btns").html("大小:<i>" + plupload.formatSize(file.size)  + "</i>, 等待传输中...");
			
			var endfix = file.name.substring(file.name.lastIndexOf(".")+1);
			newli.find("p.ico").addClass(endfix);
			
			var param = {};
			param.id = currentEntity._id;
			param.name = file.name;
			newli.data("param", param);
			up.settings.multipart_params = param; 
			$("#upload-list").append(newli);
		}
		up.start();
	});

	uploader.bind('UploadFile', function(up, file) { //before file upload start
		var newli = $("#" + file.id);
		up.settings.multipart_params = newli.data("param"); 
	});
	 uploader.bind('UploadProgress', function(up, file) { // on file upload progress
		 var newli = $("#" + file.id);
		 
		 newli.find("p.btns").html("大小:<i>" + plupload.formatSize(file.size)  + "</i>,已传输<i>" +  file.percent + "%</i>");
		
		 /*
		 newli.find(".text").html(Utils.shortenString(file.name, 20) + "(" + plupload.formatSize(file.size)  + ")" + "<br><span>"
				+ plupload.formatSize(up.total.bytesPerSec) + "/s  </span>");
		 newli.find(".percent").css("width", file.percent + "%");
		 $("#progress-bar .message").html("正在上传文件" + $("#progress-bar div.detail ul li.finished").length + "/" + $("#progress-bar div.detail ul li.progress").length);
		 */
	 });
		
	 uploader.bind('Error', function(up, err) {  // on file upload error
		 alert(err);
	 });

	 uploader.bind('FileUploaded', function(up, file, info) {  //on file upload finished
		 var newli = $("#" + file.id);
		
		 newli.find("p.btns").html("大小:<i>" + plupload.formatSize(file.size)  + "</i>,已传输完成");
		 
		 refreshFolder();
		 newli.addClass("finished");
	 });
}

function share() {
	var files = getCheckedFiles();
	if (files.length==0) return;
	if (files.length>1) return;
	$.getJSON("/service/share/list", {
		"id": files[0]._id
	}, function(data) {
		$("#shared-usr ul.inherited li.item").remove();
		for ( var i = 0; i < data.inhlist.length; i++) {
			addInheritAuh( data.inhlist[i].auth, data.inhlist[i].permission);
		}
		
		if (data.inh) {
			$("#share-inherit").addClass("checked");
			$("#shared-usr ul.inherited li.item").show();
		} else {
			$("#share-inherit").removeClass("checked");
			$("#shared-usr ul.inherited li.item").hide();
		}
		
		$("#shared-usr ul.current li.item").remove();
		for ( var i = 0; i < data.curlist.length; i++) {
			addSharedAuth(data.curlist[i].auth, data.curlist[i].permission);
		}
	});
	onInputUserChange();
	showDialog("share-dialog");
}


function pageFaceted() {
	$(".base .center>div").hide();
	$("#faceted-view").show();
	
	$.getJSON("/service/faceted/list", null, function(list) {
		$("#faceted-view dd div.added").remove();
		
		for ( var i = 0; i < list.length; i++) {
			var name = list[i];
			var tag = "文件标签:";
			var pos = name.indexOf(":"); 
			if (pos>-1) {
				tag = name.substring(0, pos+1);
				name = name.substring(pos+1);
			}
			
			if ($("#faceted-view dl.faceted-filter dt:contains('" + tag + "')").length==0) {
				var dl = $("#faceted-view dl:last").clone();
				dl.find("dt").html(tag);
				dl.find("dd div.added").remove();
				$("#faceted-view dl.faceted-filter:last").after(dl);
			}
			
			var div = $("<div>" + name + "</div>");
			div.addClass("added");
			$("#faceted-view dl.faceted-filter dt:contains('" + tag + "')").next().append(div);
		}
	});
}

function faceted() {
	var files = getCheckedFiles();
	if (files.length==0) return;
	
	$.getJSON("/service/faceted/list", null,  function(list) {
		
		$("#faceted-dialog ul.checktable-list li").remove();
		
		for ( var i = 0; i < list.length; i++) {
			var li  = $("<li>" + list[i] + "</li>");
			
			if (files.length==1) {
				var sets = files[0].faceted;
				if (sets!=null && sets.indexOf(list[i])>-1) {
					li.addClass("checked");
				}
			}
			attachEvent(li, function(t) {
				if ($(t).hasClass("checked")) {
					$(t).removeClass("checked");
				} else {
					$(t).addClass("checked");
				}
			});
			$("#faceted-dialog ul.checktable-list").append(li);
		}
	});
	
	showDialog("faceted-dialog");
}

function newFaceted() {
	showDialog("new-faceted");
	$("#new-faceted input").val('');
}

function confirmFacetedSet() {
	var files = getCheckedFiles();
	var ids = [];
	for ( var i = 0; i < files.length; i++) {
		ids.push(files[i]._id);
	}
	
	var faceteds = [];
	
	$("#faceted-dialog ul.checktable-list li.checked").each(function() {
		faceteds.push($(this).html());
	}) ;

	$.post("/service/faceted/set", {
		"id": JSON.stringify(ids),
		"list": JSON.stringify(faceteds)
	}, function() {
		closeDialog();
		refreshFolder("已为" +files.length + "个文件设置标签" );
	});
}

function confirmAddFaceted() {
	var val =  $("#newFacetedField").val();
	
	if (faceted=="") {
		showAlert("请输入标签名称", newFaceted());
		return;
	}
	
	var parent = $("#facetedParentField").val();
	
	$.post("/service/faceted/add", {
		"parent": parent,
		"title": val
	}, function(data) {
		faceted();
	});
}

function switchInherit() {
	if ($("#share-inherit").hasClass("checked")) {
		$("#share-inherit").removeClass("checked");
		$("#shared-usr ul.inherited li.item").hide();
	} else {
		$("#share-inherit").addClass("checked");
		$("#shared-usr ul.inherited li.item").show();
	}
}


function addInheritAuh(auth, perm) {
	var li_cloned = $("#shared-usr ul.inherited li.template").clone();
	li_cloned.removeClass("template").addClass("item");
	li_cloned.find("span.name").html(auth);
	if (perm) {
		li_cloned.find(".permission").html(perm);
	}

	$("#shared-usr ul.inherited").append(li_cloned);
}

function addSharedAuth(auth,perm) {
	var added = false;
	
	$("#shared-usr ul.current li.item").each(function() {
		if (added) return;
		if ($(this).find("span.name").html()==auth) added = true;
	});
	if (added) {
		return;
	}
		
	var li_cloned = $("#shared-usr ul.current li.template").clone();
	li_cloned.removeClass("template").addClass("item");
	li_cloned.find("span.name").html(auth);
	if (perm) {
		li_cloned.find("select.permission").val(perm);
	}

	attachEvent(li_cloned.find("img"), function(t) {
		$(t).parents("li.item").remove();
	});
	$("#shared-usr ul.current").append(li_cloned);
}


function onInputUserChange(val) {
	$.getJSON("/service/user/filter", {
		"user": val
	}, function(list){
		$("#auth-tree ul li").remove();
		
		var names = Utils.arrayProject(list, "name");
		
		if (val!=null && val!="") {
			var li = $("<li></li>");
			if (names.indexOf(val)>-1) {
				li.html(val);
				li.addClass("green");
			} else {
				li.html(val + " <span class='red'>(尚不存在)</span>");
			} 
			li.data("auth",  {name:val});
			$("#auth-tree ul").append(li);
		}
		
		for ( var i = 0; i < list.length; i++) {
			if (list[i].name==val) continue;
			var li = $("<li>" + list[i].name + "</li>");
			li.data("auth",  list[i]);
			$("#auth-tree ul").append(li);
		}
		attachEvent($("#auth-tree ul li"), function(t) {
			addSharedAuth($(t).data("auth").name, null);
		});
	});
}


function confirmShare() {
	var file = getCheckedOneFile();
	
	var acelist = [];
	
	$("#shared-usr ul.current li.item").each(function() {
		acelist.push({
			"auth": $(this).find("span.name").html(),
			"perm": $(this).find("select.permission").val()
		});
	});
	
	$.post("/service/share/update", {
		"id": file._id,
		"inherit": $("#share-inherit").hasClass("checked"),
		"aces": JSON.stringify(acelist)
	}, function() {
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

function hideMessage() {
	$("#msg").slideUp();
}

function newRepo() {
	clear();
	$("#repo-edit-view").show();
	
	$("#repo-edit-view .shadow-box").hide();
	$("#repo-edit-view .new").show();
}

function confirmRepoName() {
	
	$.post("/service/repo/add", {
		'title': $("repo-name").val(),
		'desc': $("repo-desc").val()
	}, function() {
		pagePublic();
	});
}
