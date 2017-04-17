package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class MainController implements Initializable{
	private long id;
	private boolean success = false;
	@FXML
	Button btngetid;
	@FXML
	private Text Qremind;	
	@FXML
	private GridPane  frontpage;
	@FXML
	private BorderPane backpage;
	@FXML
	private Text dlremind;
	@FXML
	private ImageView imgWebCamCapturedImage;
	@FXML
	ComboBox<WebCamInfo> cbCameraOptions;
	private comprint com;
	
	private class WebCamInfo {

		private String webCamName;
		private int webCamIndex;

		public String getWebCamName() {
			return webCamName;
		}

		public void setWebCamName(String webCamName) {
			this.webCamName = webCamName;
		}

		public int getWebCamIndex() {
			return webCamIndex;
		}

		public void setWebCamIndex(int webCamIndex) {
			this.webCamIndex = webCamIndex;
		}

		@Override
		public String toString() {
			return webCamName;
		}
	}

	private BufferedImage grabbedImage;
	private Webcam selWebCam = null;
	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
	private Result result = null;
	private String cameraListPromptText = "Choose Camera";
	private int webcomindex;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
		int webCamCounter = 0;
		for (Webcam webcam : Webcam.getWebcams()) {
			WebCamInfo webCamInfo = new WebCamInfo();
			webCamInfo.setWebCamIndex(webCamCounter);
			webCamInfo.setWebCamName(webcam.getName());
			options.add(webCamInfo);
			webCamCounter++;
		}
		cbCameraOptions.setItems(options);
		cbCameraOptions.setPromptText(cameraListPromptText);
		cbCameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {

			@Override
			public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
				if (arg2 != null) {
					System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
					webcomindex = arg2.getWebCamIndex();
					//initializeWebCam(arg2.getWebCamIndex());
				}
			}
		});
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				setImageViewSize();
			}
		});

	}

	protected void setImageViewSize() {
		imgWebCamCapturedImage.setPreserveRatio(true);
	}

	protected void initializeWebCam(final int webCamIndex) {

		Task<Void> webCamIntilizer = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				if (selWebCam == null) {
					selWebCam = Webcam.getWebcams().get(webCamIndex);
					selWebCam.open();
				} else {
					closeCamera();
					selWebCam = Webcam.getWebcams().get(webCamIndex);
					selWebCam.open();
				}
				startWebCamStream();
				return null;
			}

		};

		new Thread(webCamIntilizer).start();

	}

	protected void startWebCamStream() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				while (!success) {
					
					try {
						if ((grabbedImage = selWebCam.getImage()) != null) {

							Platform.runLater(new Runnable() {

								@Override
								public void run() {

									/*try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}*/
									final Image mainiamge = SwingFXUtils
										.toFXImage(grabbedImage, null);
									imageProperty.set(mainiamge);
									LuminanceSource source = new BufferedImageLuminanceSource(grabbedImage);
									BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

									try {
										result = new MultiFormatReader().decode(bitmap);
										success = true;
									} catch (NotFoundException e) {
										success = false;
									}
								}
							});

							grabbedImage.flush();

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				id = Long.valueOf(result.getText()).longValue();
				closeCamera();
				frontpage.setVisible(false);
				frontpage.setDisable(true);
				backpage.setVisible(true);
				backpage.setDisable(false);
				dlremind.setText("’˝‘⁄¥Ú”°£¨«Î…‘∫Ú...");
				PostDl postdl = new PostDl();
				File file = postdl.DownLoad(String.valueOf(id));
				com.convert2PDF(file.getAbsolutePath());
				return null;
			}

		};
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		imgWebCamCapturedImage.imageProperty().bind(imageProperty);
	}
	private void closeCamera() {
		if (selWebCam != null) {
			selWebCam.close();
		}
	}
	@FXML
	public void startget(ActionEvent event) {
		initializeWebCam(webcomindex);
	}
	public void backtomain(ActionEvent event){
		frontpage.setVisible(true);
		frontpage.setDisable(false);
		backpage.setVisible(false);
		backpage.setDisable(true);
	}
}