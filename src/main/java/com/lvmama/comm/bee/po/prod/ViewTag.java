package com.lvmama.comm.bee.po.prod;

import java.io.Serializable;

public class ViewTag implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6002213326841284897L;

	/**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column VIEW_TAG.TAG_ID
     *
     * @ibatorgenerated Wed Jun 02 13:02:17 CST 2010
     */
    private Long tagId;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column VIEW_TAG.NAME
     *
     * @ibatorgenerated Wed Jun 02 13:02:17 CST 2010
     */
    private String name;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column VIEW_TAG.TAG_ID
     *
     * @return the value of VIEW_TAG.TAG_ID
     *
     * @ibatorgenerated Wed Jun 02 13:02:17 CST 2010
     */
    public Long getTagId() {
        return tagId;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column VIEW_TAG.TAG_ID
     *
     * @param tagId the value for VIEW_TAG.TAG_ID
     *
     * @ibatorgenerated Wed Jun 02 13:02:17 CST 2010
     */
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column VIEW_TAG.NAME
     *
     * @return the value of VIEW_TAG.NAME
     *
     * @ibatorgenerated Wed Jun 02 13:02:17 CST 2010
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column VIEW_TAG.NAME
     *
     * @param name the value for VIEW_TAG.NAME
     *
     * @ibatorgenerated Wed Jun 02 13:02:17 CST 2010
     */
    public void setName(String name) {
        this.name = name;
    }
}