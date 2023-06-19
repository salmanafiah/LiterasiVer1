package com.example.hopes.literasi;

public class UserRead {
    public String email;
    public int waktuBaca;

    public UserRead(String email, int waktuBaca) {
        this.email = email;
        this.waktuBaca = waktuBaca;
    }

    public UserRead() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getWaktuBaca() {
        return waktuBaca;
    }

    public void setWaktuBaca(int waktuBaca) {
        this.waktuBaca = waktuBaca;
    }
}
