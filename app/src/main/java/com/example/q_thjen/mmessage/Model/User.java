package com.example.q_thjen.mmessage.Model;

public class User {

    /** TODO: phải đặt tên biến giống tên child trong firebase để firebaseUi recyclerview có thể get được value **/
    private String name;
    private String image;
    private String status;
    private String thumb_image;

    public User() {
    }

    public User(String name, String image, String status, String thumb_image) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

}
