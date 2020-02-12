package com.kenova.store.Models;

/**
 * Created by kenova on 3/24/2019.
 */

public class UserModels {

        private String userId;
        private String userName;
        private String profileImage;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String id) {
            this.userId = id;
        }


        public String getUserName() {
            return userName;
        }

        public void setUserName(String name) {
            this.userName = name;
        }

        public String getProfileImage() {
            return profileImage;

        }

        public void setProfileImage(String image) {
            this.profileImage = image;
        }

    }
