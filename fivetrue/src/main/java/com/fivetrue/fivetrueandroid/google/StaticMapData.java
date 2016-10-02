package com.fivetrue.fivetrueandroid.google;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kwonojin on 16. 9. 13..
 */
public class StaticMapData {

    private static final int DEFAULT_STATIC_MAP_ZOOM = 16;
    private static final int DEFAULT_STATIC_MAP_WIDTH = 200;
    private static final int DEFAULT_STATIC_MAP_HEIGHT = 200;
    private static final String DEFAULT_STATIC_MAP_MARKER_COLOR = "red";

    private static final String GOOGLE_STATIC_MAP_API = "https://maps.googleapis.com/maps/api/staticmap?" +
            "center=%s&zoom=%s&size=%s";

    private final double latitude;
    private final double longitude;
    private final int zoom;
    private final int width;
    private final int height;
    private final Markers[] markerses;

    public StaticMapData(LatLng latLng, Markers... markers){
        this(latLng, DEFAULT_STATIC_MAP_WIDTH, DEFAULT_STATIC_MAP_HEIGHT, markers);
    }

    public StaticMapData(LatLng latLng, int width, int height, Markers... markers){
        this(latLng.latitude, latLng.longitude, DEFAULT_STATIC_MAP_ZOOM, width, height, markers);
    }

    public StaticMapData(LatLng latLng, int zoom, int width, int height, Markers... markers){
        this(latLng.latitude, latLng.longitude, zoom, width, height, markers);
    }

    public StaticMapData(double latitude, double longitude, int width, int height, Markers... markers){
        this(latitude, longitude, DEFAULT_STATIC_MAP_ZOOM, width, height, markers);
    }

    public StaticMapData(double latitude, double longitude, int zoom, int width, int height, Markers... markers){
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
        this.width = width;
        this.height = height;
        this. markerses = markers;
    }

    public String toMapImageUrl(String apiKey){
//        String url =
        String center = latitude + "," + longitude;
        String size = width + "x" + height;
        String markers = null;
        if(markerses != null){
            for(Markers m : markerses){
                if(markers == null){
                    markers = m.toString();
                }else{
                    markers += "&" + m.toString();
                }
            }
        }

        String staticMapUrl = String.format(GOOGLE_STATIC_MAP_API, center, zoom, size);
        if(markers != null){
            staticMapUrl += "&"+ markers;
        }
        if(apiKey == null){
            throw new IllegalArgumentException("apiKey must be not null");
        }
        staticMapUrl += "&" + "key=" + apiKey;
        return staticMapUrl;
    }

    public static final class Markers{
        private final String color;
        private final String label;
        private final double latitude;
        private final double longitude;

        public Markers(String color, String label, double lat, double lng){
            this.color = color;
            this.label = label;
            this.latitude = lat;
            this.longitude = lng;
        }

        public Markers(String label, double lat, double lng){
            this(DEFAULT_STATIC_MAP_MARKER_COLOR, label, lat, lng);
        }

        public Markers(String color, String label, LatLng latlng){
            this(color, label, latlng.latitude, latlng.longitude);
        }

        public Markers(String label, LatLng latlng){
            this(DEFAULT_STATIC_MAP_MARKER_COLOR, label, latlng.latitude, latlng.longitude);
        }

        @Override
        public String toString() {
            return "markers=color:" + color + "|" + "label:" + label + "|" + latitude + "," +longitude;
        }
    }

}
