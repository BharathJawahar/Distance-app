package com.example.socialdistancingbluetoothapp;

public class DeviceModel {
    private String deviceName, location, time;
    private int strength;
    private boolean danger;

    public DeviceModel(String deviceName, String location, int strength, boolean danger, String time) {
        this.deviceName = deviceName;
        this.location = location;
        this.strength = strength;
        this.danger = danger;
        this.time = time;
    }

    public DeviceModel() {

    }

    @Override
    public String toString() {
        return "DeviceModel{" +
                "deviceName='" + deviceName + '\'' +
                ", location='" + location + '\'' +
                ", strength=" + strength +
                ", danger=" + danger +
                ", time=" + time +
                '}';
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public boolean isDanger() {
        return danger;
    }

    public void setDanger(boolean danger) {
        this.danger = danger;
    }
}
