$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	//根据按钮的样式进行判断 当前登录用户对该用户的关注状态
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.post(
			CONTEXT_PATH + "/follow",
			//通过异步请求发送的数据  entityType:ENTITY_TYPE_PEOPLE表示当前关注的实体类型为用户
			//entityId:$(btn).prev().val()} 表示entityId是从当前按钮的前一个标签中取
			//而在前端的页面中 设置了一个隐藏的输入框 其中的value为当前关注的用户id
			{"entityType":ENTITY_TYPE_PEOPLE,"entityId":$(btn).prev().val()},
			function(data) {
				data = $.parseJSON(data);
				if(data.code == 0) {
					window.location.reload();
				} else {
					alert(data.msg);
				}
			}
		);
		// $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
		$.post(
			CONTEXT_PATH + "/unfollow",
			{"entityType":ENTITY_TYPE_PEOPLE,"entityId":$(btn).prev().val()},
			function(data) {
				data = $.parseJSON(data);
				if(data.code == 0) {
					window.location.reload();
				} else {
					alert(data.msg);
				}
			}
		);
		//$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}