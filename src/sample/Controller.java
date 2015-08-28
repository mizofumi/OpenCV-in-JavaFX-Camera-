package sample;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import java.io.ByteArrayInputStream;

public class Controller {

    VideoCapture videoCapture;
    Service captureService;

    @FXML
    ImageView camera;

    @FXML
    Button start;

    @FXML
    TextField camid;

    public Controller() {
        captureService = new Service() {
            private final Mat image = new Mat();
            private final MatOfByte buf = new MatOfByte();
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Image call() throws Exception {
                        if (videoCapture.isOpened()) {
                            videoCapture.read(image);
                            if (!image.empty()) {
                                Highgui.imencode(".png", image, buf);
                                return new Image(new ByteArrayInputStream(buf.toArray()));
                            }
                        }
                        return null;
                    }
                };
            }
        };

        captureService.setOnSucceeded((event) -> {
            if (captureService.getValue() != null) {
                camera.setImage((Image) captureService.getValue());
            }
            captureService.restart();
        });
    }

    public void start(int camera){
        videoCapture = new VideoCapture(camera);
        captureService.start();
    }

    public void finish() {
        captureService.cancel();
        videoCapture.release();
    }

    public void push(ActionEvent event) {
        start(Integer.parseInt(camid.getText()));
        camid.setDisable(true);
        start.setDisable(true);
    }
}
