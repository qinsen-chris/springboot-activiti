$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'workFlow/deploymentList',
        datatype: "json",
        colModel: [			
			{ label: 'ID', name: 'id', index: "id", width: 45, key: true },
			{ label: '部署名称', name: 'name', width: 75 },
			{ label: '部署种类', name: 'category', width: 90 },
			{ label: '部署时间', name: 'deploymentTime', width: 90 }
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
            platform: null,
            name: null
        },
        showList: true,
        title:null,
        deploy:{
        	name:null,
			file:null
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
        saveOrUpdate: function () {
            if(vm.validator()){
                return ;
            }

            var url =  "workFlow/upload";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.pact),
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
        onUpload:function(e){
            var fileName = e.target.files[0].name;
            var sufName = fileName.substring(fileName.lastIndexOf("."));
            if (!(sufName && /^.zip$/.test(sufName.toLowerCase()))){
                alert('只支持 .zip 格式的文件！');
                return;
            }
            eTarget = e.target;
            var formData = new FormData();
            formData.append('file', e.target.files[0]);
            $.ajax({
                url: baseURL + 'workFlow/upload?token=' + token,
                type: 'POST',
                dataType: 'json',
                cache: false,
                data: formData,
                processData: false,
                contentType: false,
                success: function(r){
                    if(r.code === 0){
                        alert('上传成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                },
                error: function(err) {

                    alert("网络错误");
                }
            });
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