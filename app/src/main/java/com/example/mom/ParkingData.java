package com.example.mom;

import java.io.Serializable;

public class ParkingData implements Serializable {
        private String parking_x;
        private String parking_y;
        private String parking_name;
        private String parking_id;
        private String parking_basicFare;
        private String parking_basicTime;
        private String parking_unitFare;
        private String parking_unitTime;
        private String parking_maxFare;
        private String parking_averageRating;
        private String parking_number;


        public String getParking_number(){
        return parking_number;
    }

        public String getParking_name(){
        return parking_name;
    }

        public String getParking_id(){
        return parking_id;
    }

        public String getParking_x(){
        return parking_x;
    }

        public String getParking_y(){
        return parking_y;
    }

        public String getParking_basicFare() { return parking_basicFare;}

        public String getParking_basicTime() { return parking_basicTime;}

        public String getParking_unitFare() { return parking_unitFare;}

        public String getParking_unitTime() { return parking_unitTime;}

        public String getParking_maxFare() {return parking_maxFare;}

        public String getParking_averageRating() {return parking_averageRating;}





        public void setParking_name(String parking_name){
        this.parking_name=parking_name;
    }

        public void setParking_id(String parking_id){
        this.parking_id=parking_id;
    }

        public void setParking_x(String parking_x){
            this.parking_x=parking_x;
        }

        public void setParking_y(String parking_y){
            this.parking_y=parking_y;
        }

        public void setParking_basicFare(String parking_basicFare){this.parking_basicFare=parking_basicFare;}
        public void setParking_basicTime(String parking_basicTime){this.parking_basicTime=parking_basicTime;}
        public void setParking_unitFare(String parking_unitFare){this.parking_unitFare=parking_unitFare;}
        public void setParking_unitTime(String parking_unitTime){this.parking_unitTime=parking_unitTime;}
        public void setParking_maxFare(String parking_maxFare){this.parking_maxFare=parking_maxFare;}
        public void setParking_averageRating(String parking_averageRating){this.parking_averageRating=parking_averageRating;}
        public void setParking_number(String parking_number){this.parking_number=parking_number;}



    }

