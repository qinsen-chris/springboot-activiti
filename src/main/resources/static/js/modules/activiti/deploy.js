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
        zipDeploy:false,
        title:null,
        deploy:{
        	fileName:null,
			processName:null,
			flag:null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add1: function(){
            vm.showList = false;
            vm.zipDeploy = false;
            vm.title = "部署流程模板";
            vm.deploy = {};

        },
        add2: function(){
            vm.showList = false;
            vm.zipDeploy = true;
            vm.title = "部署流程模板";
            vm.deploy = {};

        },
       saveOrUpdate: function () {
            if(vm.validator()){
                return ;
            }
            var url =  "workFlow/deployByFile" ;
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",

                data: JSON.stringify(vm.deploy),
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
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'platform': vm.q.platform,'name':vm.q.name},
                page:page
            }).trigger("reloadGrid");
        },
        validator: function () {
            if(isBlank(vm.deploy.fileName)){
                alert("模板文件名不能为空！");
                return true;
            }
            if(isBlank(vm.deploy.processName)){
                alert("流程中文名称不能为空！");
                return true;
            }
        },
        onUpload:function(e){
            if(isBlank(vm.deploy.processName)){
                alert("模板不能为空!");
                return false;
            }
            var fileName = e.target.files[0].name;
            var sufName = fileName.substring(fileName.lastIndexOf("."));
            if (!(sufName && /^.zip$/.test(sufName.toLowerCase()))){
                alert('只支持 .zip 格式的文件！');
                return;
            }
            eTarget = e.target;
            var formData = new FormData();
            formData.append('file', e.target.files[0]);
            formData.append('processName', vm.deploy.processName);
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
        }

    }
});