package com.example.socialdistancingbluetoothapp;

public class DeviceModel {
    private String deviceName, location;
    private int strength;
    private boolean danger;

    public DeviceModel(String deviceName, String location, int strength, boolean danger) {
        this.deviceName = deviceName;
        this.location = location;
        this.strength = strength;
        this.danger = danger;
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
                '}';
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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
