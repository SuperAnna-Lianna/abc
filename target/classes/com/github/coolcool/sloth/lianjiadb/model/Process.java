package com.github.coolcool.sloth.lianjiadb.model;

import javax.annotation.Generated;

@Generated(
	value = {
	"https://github.com/coolcooldee/sloth",
	"Sloth version:1.0"
	},
	comments = "This class is generated by Sloth"
)
public class Process {

	/**
	   
	 */
	private Integer id;
	/**
	   
	 */
	private String area;
	/**
	   
	 */
	private Integer pageNo=0;
	/**
	   
	 */
	private Integer finished=0;

	/**
	 * 1:在售房源任务类别; 2:已成交房源任务类别
	 */
	private Integer type=0;
	/**
	   
	 */
	private java.util.Date createtime;
	/**
	   
	 */
	private java.util.Date finishtime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	} 
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	} 
	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	} 
	public Integer getFinished() {
		return finished;
	}

	public void setFinished(Integer finished) {
		this.finished = finished;
	} 
	public java.util.Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	} 
	public java.util.Date getFinishtime() {
		return finishtime;
	}

	public void setFinishtime(java.util.Date finishtime) {
		this.finishtime = finishtime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
