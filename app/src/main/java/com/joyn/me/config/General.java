package com.joyn.me.config;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


public class General {
    public static final String FCM_KEY = "AIzaSyCQm8H3OzF6YcJsv2qRXPIuLrxO4X7IjZQ";
    public static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-7.216001, 0), // southwest
            new LatLng(0, 107.903316)); // northeast
}
