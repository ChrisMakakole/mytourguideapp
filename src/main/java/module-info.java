module com.tourguide.tours {
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;
    requires javafx.media;


    opens com.tourguide.tours to javafx.fxml;
    exports com.tourguide.tours;
}