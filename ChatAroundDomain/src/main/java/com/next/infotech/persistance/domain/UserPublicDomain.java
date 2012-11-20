package com.next.infotech.persistance.domain;

import java.io.Serializable;
/*
 * This interface contains the information which need to be exposed to world 
 */
public interface UserPublicDomain extends Serializable{

	public enum Gender {
		Male("Male"), Female("Female"), Other ("Other");

        private String value;

        Gender(String value) { this.value = value; }    

        public String getValue() { return value; }

        public static Gender parse(String value) {
        	Gender gender = null; // Default
            for (Gender item : Gender.values()) {
                if (item.getValue().equals(value)) {
                	gender = item;
                    break;
                }
            }
            return gender;
        }

    };
	String getNickName();
	void setNickName(String nickName);

	String getStatusMessage();
	void setStatusMessage(String statusMessage);

	Double getLongitude();
	void setLongitude(Double longitude);
	
	Double getLattitude();
	void setLattitude(Double lattitude);
	
	String getUserId();
	void setUserId(String userId);
	
	Gender getGender();
	void setGender(String gender);
	void setGender(Gender gender);
	
}
