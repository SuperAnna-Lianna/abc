package com.github.coolcool.sloth.lianjiadb.model;

import javax.annotation.Generated;

@Generated(
		value = {
				"https://github.com/coolcooldee/sloth",
				"Sloth version:1.0"
		},
		comments = "This class is generated by Sloth"
)
public class Area {

	/**

	 */
	private Integer id;
	/**

	 */
	private String name;
	/**

	 */
	private String code;
	/**

	 */
	private String link;
	/**

	 */
	private Integer parentsId;
	/**

	 */
	private String parentsName;
	/**

	 */
	private String parentsCode;
	/**

	 */
	private Integer level;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	public Integer getParentsId() {
		return parentsId;
	}

	public void setParentsId(Integer parentsId) {
		this.parentsId = parentsId;
	}
	public String getParentsName() {
		return parentsName;
	}

	public void setParentsName(String parentsName) {
		this.parentsName = parentsName;
	}
	public String getParentsCode() {
		return parentsCode;
	}

	public void setParentsCode(String parentsCode) {
		this.parentsCode = parentsCode;
	}
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
}
