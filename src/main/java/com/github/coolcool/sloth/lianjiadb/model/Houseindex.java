package com.github.coolcool.sloth.lianjiadb.model;

import javax.annotation.Generated;
import java.util.Date;

@Generated(
	value = {
	"https://github.com/coolcooldee/sloth",
	"Sloth version:1.0"
	},
	comments = "This class is generated by Sloth"
)
public class Houseindex {

	public Houseindex() {

	}

	public Houseindex(String url) {
		this.url = url;
		String[] t = url.split("/");
		this.code = t[t.length-1].replace(".html","");
		//view-source:http://gz.lianjia.com/ershoufang/GZ0001384019.html
	}

	/**
	   
	 */
	private Long id;
	/**
	   
	 */
	private String code;
	/**
	   
	 */
	private String url;
	/**
	 * 0:未处理；1:在售；2:已成交；-1:已经下架；-2：信息异常；-301：找不到
	 */
	private Integer status;
	/**
	   
	 */
	private java.util.Date createtime;
	/**
	   
	 */
	private java.util.Date updatetime;

	private java.util.Date lastCheckDate;

	private int hasdetail;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	} 
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	} 
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	} 
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	} 
	public java.util.Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	} 
	public java.util.Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(java.util.Date updatetime) {
		this.updatetime = updatetime;
	}

	public Date getLastCheckDate() {
		return lastCheckDate;
	}

	public void setLastCheckDate(Date lastCheckDate) {
		this.lastCheckDate = lastCheckDate;
	}

	public int getHasdetail() {
		return hasdetail;
	}

	public void setHasdetail(int hasdetail) {
		this.hasdetail = hasdetail;
	}
}
