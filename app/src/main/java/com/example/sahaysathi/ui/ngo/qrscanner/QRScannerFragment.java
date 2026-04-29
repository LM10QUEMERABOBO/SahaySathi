package com.example.sahaysathi.ui.ngo.qrscanner;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

public class QRScannerFragment extends Fragment {

    private CompoundBarcodeView barcodeView;
    private FirebaseFirestore db;
    private String ngoId;
    private boolean isScanning = true;

    // ✅ Modern permission handler
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startScanner();
                } else {
                    Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qr_scanner_ngo, container, false);

        barcodeView = view.findViewById(R.id.barcode_scanner);
        db = FirebaseFirestore.getInstance();
        SharedPreferences sp = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        ngoId = sp.getString(ConstantSp.userid, "");
        checkPermissionAndStart();

        return view;
    }

    // 🔐 Check permission
    private void checkPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            startScanner();

        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // 🎥 Start scanner
    private void startScanner() {

        barcodeView.resume();

        barcodeView.decodeContinuous(result -> {

            if (!isScanning) return;

            isScanning = false;

            String scannedData = result.getText();

            if (scannedData != null && !scannedData.isEmpty()) {
                showConfirmationDialog(scannedData);
            } else {
                Toast.makeText(getContext(), "Invalid QR", Toast.LENGTH_SHORT).show();
                resumeScanning();
            }
        });
    }
    private void showConfirmationDialog(String applicationId) {

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Attendance")
                .setMessage("Do you want to mark attendance for this volunteer?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    markAttendance(applicationId);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    resumeScanning();
                })
                .setCancelable(false)
                .show();
    }
    // 🔥 Firestore attendance update
    private void markAttendance(String applicationId) {

        db.collection("applications")
                .document(applicationId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(getContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        resumeScanning();
                        return;
                    }
                    String qrNgoId = doc.getString("ngoId");
                    String status = doc.getString("status");

                    if (!ngoId.equals(qrNgoId)) {
                        Toast.makeText(getContext(), "This QR is not for your NGO", Toast.LENGTH_SHORT).show();
                        resumeScanning();
                        return;
                    }

                    if (!"accepted".equalsIgnoreCase(status)) {
                        Toast.makeText(getContext(), "Volunteer is not accepted yet", Toast.LENGTH_SHORT).show();
                        resumeScanning();
                        return;
                    }

                    Boolean alreadyMarked = doc.getBoolean("attendanceMarked");

                    if (alreadyMarked != null && alreadyMarked) {
                        Toast.makeText(getContext(), "Already Marked", Toast.LENGTH_SHORT).show();
                        resumeScanning();
                        return;
                    }

                    db.collection("applications")
                            .document(applicationId)
                            .update(
                                    "attendanceMarked", true,
                                    "attendanceTime", FieldValue.serverTimestamp()
                            )
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Attendance Marked", Toast.LENGTH_SHORT).show();
                                resumeScanning();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                resumeScanning();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    resumeScanning();
                });
    }

    // 🔄 Resume scanning after one scan
    private void resumeScanning() {
        isScanning = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }
}