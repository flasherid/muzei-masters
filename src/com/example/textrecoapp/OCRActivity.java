package com.example.textrecoapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.textrecoapp.ar.CameraPreview;
import com.example.textrecoapp.ar.ScanningResult;
import com.example.textrecoapp.wizards.AbstractWizardActivity;

public class OCRActivity extends Activity implements ScanningResult {

	// settings
	public static final String PHOTO_TAKEN = "photo_taken";
	public static final String LOG_TAG = "OCRActivity";

	private Camera camera;
	// ui
	private FrameLayout surfaceViewContainer;
	private Button scanBtn;
	private View progressBar;

	private Bitmap bitmap;
	private Bitmap source;

	private ImageView scannedImage;
	private TextView scannedResult;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		initUI();
	}
	
	private void initUI() {
		bitmap = null;
		camera = Camera.open();
		CameraPreview preview = new CameraPreview(this, camera);
		surfaceViewContainer = (FrameLayout) findViewById(R.id.surface_view_container);
		surfaceViewContainer.addView(preview);

		scanBtn = (Button) findViewById(R.id.scan_btn);
		progressBar = findViewById(R.id.progress_overlay);

		final int scanAreaWidth = getResources().getDimensionPixelOffset(
				R.dimen.scan_area_width);
		final int scanAreaHeight = getResources().getDimensionPixelOffset(
				R.dimen.scan_area_height);

		scanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				camera.takePicture(null, null, new PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						prepareBitmapFromCamera(data, scanAreaWidth,
								scanAreaHeight);
						prepareScanningDialog();
					}
				});
			}
		});
	}

	private void prepareScanningDialog() {

		View view = getLayoutInflater().inflate(R.layout.scan_dialog_layout,
				null);

		scannedResult = (TextView) view.findViewById(R.id.scan_result_tv);
		scannedImage = (ImageView) view.findViewById(R.id.scanned_imag);
		scannedImage.setImageBitmap(bitmap);

		ImageOcrProcessing task = new ImageOcrProcessing(progressBar, App
				.getInstance().getOCR_API(), OCRActivity.this);
		task.execute(bitmap);

		String title = getResources().getString(R.string.title_scan_dialog);
		String posBtnText = getResources().getString(R.string.submit);
		String negBtnText = getResources().getString(R.string.retry);

		CustomDialogClickListener posListener = new CustomDialogClickListener() {
			@Override
			public void onClick() {
				String scannedText = String.valueOf(scannedResult.getText());
				Intent intent = new Intent();
				intent.putExtra(AbstractWizardActivity.EXTRAS_MISSION_STATUS,
						scannedText);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		};

		CustomDialogClickListener negListener = new CustomDialogClickListener() {
			@Override
			public void onClick() {
				camera.startPreview();
			}
		};

		dialog = UiUtils.createDialogWithView(this, title, posBtnText,
				negBtnText, view, posListener, negListener);
	}

	private Bitmap prepareBitmapFromCamera(byte[] data, int scanAreaWidth,
			int scanAreaHeight) {
		source = BitmapFactory.decodeByteArray(data, 0, data.length);

		float wfact = source.getWidth()
				/ (float) surfaceViewContainer.getWidth();
		float hfact = source.getHeight()
				/ (float) surfaceViewContainer.getHeight();

		int picScanWidth = (int) (wfact * scanAreaWidth);
		int picScanHeight = (int) (hfact * scanAreaHeight);

		int x = source.getWidth() / 2 - picScanWidth / 2;
		int y = source.getHeight() / 2 - picScanHeight / 2;

		bitmap = Bitmap.createBitmap(source, x, y, picScanWidth,
				picScanHeight);
		source.recycle();
		return bitmap;
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (camera == null) {
			camera = Camera.open();
		}
	}

	@Override
	public void onScanningFinished(String resultString) {
		String beautifiedResult = resultString.replace("\n", " ");
		beautifiedResult.trim();
		scannedResult.setText(beautifiedResult);
		dialog.show();
	}

}
