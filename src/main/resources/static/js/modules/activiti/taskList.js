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
        showList: true,
        title:null,
        commentList:{},
        outcomeList:null,
        leaveBill:{
            days:null,
            content:null,
            leaveDate:null,
            remark:null
        },
        params:{
            id:null,
            taskId:null,
            outcome:null,
            comment:null
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
                        vm.leaveBill   = r.leaveBill;
                        vm.showList = false;
                        vm.title = "办理";
                        vm.params.id = r.leaveBill.id;
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
        selectpickerfunc:function(){
            $("#platformSelect").selectpicker({
                noneSelectedText : '请选择'
            });

            $.get(baseURL + "common/platformList", function(r){
                vm.platformEnum = r.platformEnum;
                for (var i = 0; i < r.platformEnum.length; i++) {
                    $("#platformSelect").append("<option value="+r.platformEnum[i]+">"+ r.platformEnum[i] + "</option>");
                }
            });
            //$("#platformSelect").selectpicker();
            $("#platformSelect").selectpicker('refresh');
            $("#platformSelect").selectpicker('render');
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            var platformEnum = $("#jqGrid").jqGrid('getGridParam','platformEnum');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'platform': vm.q.platform,'name':vm.q.name},
                page:page
            }).trigger("reloadGrid");
        },
        validator: function () {
            if(isBlank(vm.pact.platform)){
                alert("平台标识不能为空");
                return true;
            }
            if(isBlank(vm.pact.params)){
                alert("占位参数不能为空");
                return true;
            }
            if(vm.pact.pactName == null && isBlank(vm.pact.pactName)){
                alert("文档名称不能为空");
                return true;
            }
        },

        addContact:function(){

            //新增弹窗
            layer.open({
                type : 1,
                offset : '150px',
                skin : 'layui-layer-molv',
                title : "选择模板",
                area : [ '750px', '520px' ],
                shade : 0,
                shadeClose : false,
                content : jQuery("#addContacts"),
                btn : [ '确定', '取消' ],
                btn1 : function(index) {

                    var ids=$('#jqPactGrid').jqGrid('getGridParam','selrow');
                    if(ids == null ){
                        alert("请至少选中一条数据！");
                        return;
                    };
                    var rows = $('#jqPactGrid').jqGrid('getGridParam','selarrrow');
                    if(rows.length >1){
                        alert("请选中一条数据！");
                        return;
                    }
                    var rowData = $('#jqPactGrid').jqGrid('getRowData',ids);
                    console.log(rowData);
                    vm.$set(vm.pact,"platform",rowData.platform);
                    vm.$set(vm.pact,"pactName",rowData.name);
                    //实际保存ID
                    vm.$set(vm.pact,"pactTemplateId",ids);


                    layer.close(index);
                }.bind(this)
            });
            pactTemplate();
        },
        pactTemplateSearch :function () {
            var page = $("#jqPactGrid").jqGrid('getGridParam','page');
            $("#jqPactGrid").jqGrid('setGridParam',{
                postData:{'platform': vm.qa.platform,'name': vm.qa.name},
                page:page
            }).trigger("reloadGrid");
        }
    }
});