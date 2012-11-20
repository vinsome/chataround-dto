package com.next.infotech.persistance.jpa.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.next.infotech.persistance.UserEntity;

@Entity
@Table(name="users")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="User", include="all")
public class User implements UserEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,  generator="USER_GEN")
	@SequenceGenerator(
    name="USER_GEN",
    sequenceName="user_seq",
    allocationSize=20
	)
	private Long id;
	@Version
	@Column(name="ver")
	private int ver;
	
	@Column(name="date_created")
	private Date dateCreated;
	@Column(name="date_modified")
	private Date dateModified;
	@Column(name="creator_id")
	private Long creatorId;
	@Column(name="modifier_id")
	private Long modifierId;
	@Column(name="email")
	private String email;
	@Column(name="password")
	private String password;
	@Column(name="nick_name")
	private String nickName;
	@Column(name="longitude")
	private Double longitude;
	@Column(name="lattitude")
	private Double lattitude;
	@Column(name="status_message")
	private String statusMessage;
	@Column(name="device_id")
	private String deviceId;
	@Column(name="user_id")
	private String userId;
	@Column(name="gender")
	private String gender;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getVer() {
		return ver;
	}
	public void setVer(int ver) {
		this.ver = ver;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateModified() {
		return dateModified;
	}
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public Long getModifierId() {
		return modifierId;
	}
	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLattitude() {
		return lattitude;
	}
	public void setLattitude(Double lattitude) {
		this.lattitude = lattitude;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Gender getGender() {
		return Gender.parse(gender);
	}
	public void setGender(String gender) {
		Gender gendreEnum = Gender.parse(gender);
		if(gendreEnum == null){
			throw new RuntimeException(gender + " is not a correct value for Gender");
		}
		this.gender = gender;
	}
	public void setGender(Gender gender) {
		if(gender == null){
			this.gender = null;
		}else{
			this.gender = gender.getValue();	
		}
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [id=");
		builder.append(id);
		builder.append(", ver=");
		builder.append(ver);
		builder.append(", dateCreated=");
		builder.append(dateCreated);
		builder.append(", dateModified=");
		builder.append(dateModified);
		builder.append(", creatorId=");
		builder.append(creatorId);
		builder.append(", modifierId=");
		builder.append(modifierId);
		builder.append(", email=");
		builder.append(email);
		builder.append(", password=");
		builder.append(password);
		builder.append(", nickName=");
		builder.append(nickName);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", lattitude=");
		builder.append(lattitude);
		builder.append(", statusMessage=");
		builder.append(statusMessage);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", gender=");
		builder.append(gender);
		builder.append("]");
		return builder.toString();
	}
	
		
}
