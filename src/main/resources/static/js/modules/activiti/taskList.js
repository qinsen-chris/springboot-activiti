$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'workFlow/taskList',
        datatype: "json",
        colModel: [
			{ label: 'ID', name: 'id', index: "id", width: 45, key: true },
			{ label: '任务名称', name: 'name', width: 75 },
			{ label: '办理人', name: 'assignee', width: 90 },
			{ label: '创建时间', name: 'createTime', width: 90 }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page",
            rows:"limit",
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });
});


var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            username: null
        },
        showList: true,
        title:null,
        commentList:{},
        outcomeList:null,
        processInfo:{
            days:null,
            content:null,
            leaveDate:null,
            remark:null
        },
        params:{
            id:null,
            taskId:null,
            outcome:null,
            comment:null,
            userid:null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.deploy = {};

        },
        complete: function () {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }
            $.ajax({
                type: "POST",
                url: baseURL + "workFlow/viewTask?taskId="+id,
                contentType: "application/json",
                data: {},
                success: function(r){
                    console.log(JSON.stringify(r));
                    if(r.code == 0){
                        vm.commentList = r.commentList;
                        vm.outcomeList = r.outcomeList;
                        vm.processInfo   = r.processInfo;
                        vm.showList = false;
                        vm.title = "办理";
                        vm.params.id = r.processInfo.id;
                        vm.params.taskId = id;
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        del: function () {
            var pactIds = getSelectedRows();
            if(pactIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "workFlow/delete",
                    contentType: "application/json",
                    data: JSON.stringify(pactIds),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function (arr) {
            if(isBlank(vm.params.userid)){
                alert("请指定办理人");
                return ;
            }
            vm.params.outcome = arr;
            var url =  "workFlow/submitTask";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.params),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },

        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');

            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'username': vm.q.username},
                page:page
            }).trigger("reloadGrid");
        },
        validator: function () {
            return true;
        }
    }
});