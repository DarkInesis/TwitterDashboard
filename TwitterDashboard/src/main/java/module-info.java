module TseInfo6.TwitterDashboard {
    requires transitive javafx.controls;
    requires javafx.fxml;
	requires transitive com.google.gson;
	requires java.net.http;
	requires transitive javafx.graphics;
	requires javafx.base;
	requires java.sql;
	requires jdk.httpserver;
	requires com.google.api.client;
	requires com.google.api.client.auth;
	requires org.apache.commons.codec;
	requires java.desktop;

	
    opens TseInfo6.TwitterDashboard to javafx.fxml;
    exports TseInfo6.TwitterDashboard;
    exports TseInfo6.TwitterDashboard.Model;
}