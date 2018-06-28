$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'leaveBill/list',
        datatype: "json",
        colModel: [			
			{ label: 'ID', name: 'id', index: "id", width: 45, key: true },
			{ label: '请假天数', name: 'days', width: 75 },
			{ label: '请假内容', name: 'content', width: 90 },
			{ label: '日期', name: 'leaveDate', width: 90 },
			{ label: '备注', name: 'remark', width: 90 },
			{ label: '状态', name: 'state', width: 90, formatter: function(value, options, row){
				var htmlText =  '<span class="label label-danger">结束</span>';
				if (value === 0 ){
                    htmlText = '<span class="label label-danger">初始</span>';
				}else if (value === 1 ){
                    htmlText = '<span class="label label-danger">审核中</span>';
				}
                 return htmlText;
            }}
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
			name:null
		},
		showList: true,
		title:null,
		leaveBill:{
            id:null,
            days:null,
            content:null,
            remark:null,
            state:null,
            user_id:null
		}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.leaveBill = {};
		},
		update: function () {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			//var ids = $('#jqGrid').getDataIDs();//返回数据表的ID数组
            var getRow = $('#jqGrid').getRowData(id);//获取当前的数据行
			vm.leaveBill = getRow;
			vm.showList = false;
            vm.title = "修改";
		},
		del: function () {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "activiti/firstDemo",
                    contentType: "application/json",
				    data: JSON.stringify(ids),
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
        startProc: function () {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}

			confirm('确定要办理选中的任务？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "workFlow/startProcess?id="+id,
                    contentType: "application/json",
				    data: {},
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
		view: function () {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			var getRow = $('#jqGrid').getRowData(id);//获取当前的数据行
			var executionId = getRow.executionId;
			confirm('确定要办理选中的任务？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "activiti/viewTaskImage",
                    contentType: "application/json",
				    data: JSON.stringify(id),
				    success: function(r){
						if(r.code == 0){
							
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
			var url = vm.leaveBill.id == null ? "leaveBill/save" : "leaveBill/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.leaveBill),
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
                postData:{'name':vm.q.name},
                page:page
            }).trigger("reloadGrid");
		},
        validator: function () {
            if(isBlank(vm.leaveBill.days)){
                alert("姓名不能为空");
                return true;
            }
           
        }
	}
});