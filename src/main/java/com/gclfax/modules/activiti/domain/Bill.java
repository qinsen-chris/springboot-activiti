package com.gclfax.modules.activiti.domain;

import java.util.Date;

/**
 * 商品订单
 */
public class Bill {
	private Long id;//主键ID
	private String productName;// 商品名称
	private Integer num;// 商品数量
	private Double amount;//金额
	private Date createDate = new Date();// 请假时间
	private String remark;// 备注
	private Long userId;// 下单人
	
	private Integer state=0;// 定单状态 0初始录入,1.下单,2.付款，3.付款成功，4.发货，5、收货，6、完成

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	
}
