module com.example.iec61850goosegenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    requires org.pcap4j.core;
    requires org.reflections;


    opens com.example.iec61850goosegenerator to javafx.fxml;
    exports com.example.iec61850goosegenerator;
}