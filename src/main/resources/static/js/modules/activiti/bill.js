$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'bill/list',
        datatype: "json",
        colModel: [			
			{ label: 'ID', name: 'id', index: "id", width: 45, key: true },
			{ label: '商品名称', name: 'productName', width: 75 },
			{ label: '数量', name: 'num', width: 90 },
			{ label: '金额', name: 'amount', width: 90 },
			{ label: '日期', name: 'createDate', width: 90 },
			{ label: '备注', name: 'remark', width: 90 },
			{ label: '状态', name: 'state', width: 90, formatter: function(value, options, row){
				var htmlText =  '<span class="label label-danger">完成</span>';
				if (value === 0 ){
                    htmlText = '<span class="label label-success">初始</span>';
				}else if (value === 1 ){
                    htmlText = '<span class="label label-success">下单</span>';
				}else if (value === 2 ){
					htmlText = '<span class="label label-success">付款</span>';
				}else if (value === 3 ){
					htmlText = '<span class="label label-success">付款成功</span>';
				}else if (value === 4 ){
					htmlText = '<span class="label label-success">发货</span>';
				}else if (value === 5 ){
					htmlText = '<span class="label label-success">收货</span>';
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
			name:null,
			userId:null
		},
		showList: true,
		title:null,
		bill:{
            id:null,
            productName:null,
            num:null,
            amount:null,
            remark:null,
            state:null,
            userId:null
		}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.bill = {};
		},
		update: function () {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			//var ids = $('#jqGrid').getDataIDs();//返回数据表的ID数组
            var getRow = $('#jqGrid').getRowData(id);//获取当前的数据行
			vm.bill = getRow;
			vm.showList = false;
            vm.title = "修改";
		},
        startProc: function () {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			if(isBlank(vm.q.userId)){
                alert("请指定办理人");
                return ;
            }
			var json = new Object();
			json.id = id;
			json.userId = vm.q.userId;
			console.log(JSON.stringify(json));
			confirm('确定要办理选中的任务？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "bill/startProcess",
                    contentType: "application/json",
				    data: JSON.stringify(json),
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
       startProcMessage: function () {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }
            if(isBlank(vm.q.userId)){
                alert("请指定办理人");
                return ;
            }
            var json = new Object();
            json.id = id;
            json.userId = vm.q.userId;
            console.log(JSON.stringify(json));
            confirm('确定要办理选中的任务？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "bill/startProcMessage",
                    contentType: "application/json",
                    data: JSON.stringify(json),
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
			var url = vm.bill.id == null ? "bill/save" : "bill/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.bill),
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
            if(isBlank(vm.bill.productName)){
                alert("姓名不能为空");
                return true;
            }
           
        }
	}
});