package com.lvmama.comm.vo.train.order;

import com.lvmama.comm.vo.train.RspVo;

public class OrderCreateRspVo extends RspVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8795462136L;
	
	/**
	 * 订单号（后续相关订单操作时的唯一标识）
	 */
	private String order_id;
	/**
	 * 订单状态码
	 */
	private int order_status;
	/**
	 * 订单状态说明
	 */
	private String order_msg;
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public int getOrder_status() {
		return order_status;
	}
	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}
	public String getOrder_msg() {
		return order_msg;
	}
	public void setOrder_msg(String order_msg) {
		this.order_msg = order_msg;
	}
	
	@Override
	public String toString(){
		return "order_id:" + order_id
			+ "|order_status:" + order_status
			+ "|order_msg:" + order_msg;
	}
}
