package com.wiline.domain;

import javax.persistence.Column;
import javax.persistence.Id;

//@Entity(name="poller_mibs")
//@Table(name = "poller_mibs",schema="voiceco")
public class PollerMib {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "mib")
    private String mib;

    @Column(name = "mib_prefix")
    private String mib_prefix;

    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
	return "PollerMib [id=" + id + ", mib=" + mib + ", mib_prefix=" + mib_prefix + ", description=" + description
		+ "]";
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getMib() {
	return mib;
    }

    public void setMib(String mib) {
	this.mib = mib;
    }

    public String getMib_prefix() {
	return mib_prefix;
    }

    public void setMib_prefix(String mib_prefix) {
	this.mib_prefix = mib_prefix;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

}
