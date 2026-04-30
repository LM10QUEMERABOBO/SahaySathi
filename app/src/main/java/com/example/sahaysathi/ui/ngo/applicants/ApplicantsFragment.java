package com.example.sahaysathi.ui.ngo.applicants;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class ApplicantsFragment extends Fragment {

    RecyclerView recyclerView;
    ApplicantAdapter adapter;
    ArrayList<Applicant> applicantList;

    String ngoId;
    TextView noDataText;
    Button btnExport;
    FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_applicants, container, false);

        recyclerView = view.findViewById(R.id.recyclerApplicants);
        noDataText = view.findViewById(R.id.noApplicantText);
        btnExport = view.findViewById(R.id.btnExportExcel);

        SharedPreferences sp = requireActivity()
                .getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        ngoId = sp.getString(ConstantSp.userid, "");
        db = FirebaseFirestore.getInstance();

        applicantList = new ArrayList<>();
        adapter = new ApplicantAdapter(getContext(), applicantList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        noDataText.setVisibility(View.GONE);
        btnExport.setVisibility(View.GONE);

        btnExport.setOnClickListener(v -> showEventSelectionDialog());

        fetchApplicants();

        return view;
    }

    private void fetchApplicants() {
        db.collection("applications")
                .whereEqualTo("ngoId", ngoId)
                .get()
                .addOnSuccessListener(applicationSnapshots -> {

                    applicantList.clear();
                    btnExport.setVisibility(View.GONE);

                    if (applicationSnapshots.isEmpty()) {
                        noDataText.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    final int total = applicationSnapshots.size();
                    final int[] completed = {0};

                    for (DocumentSnapshot appDoc : applicationSnapshots) {
                        processApplicantDocument(appDoc, () -> {
                            completed[0]++;

                            if (completed[0] == total) {
                                adapter.notifyDataSetChanged();

                                if (applicantList.isEmpty()) {
                                    noDataText.setVisibility(View.VISIBLE);
                                    noDataText.setText("No Active Applicants Available");
                                } else {
                                    noDataText.setVisibility(View.GONE);
                                }

                                updateExportButtonVisibility();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    noDataText.setVisibility(View.VISIBLE);
                    noDataText.setText("Failed to load applicants");
                    btnExport.setVisibility(View.GONE);
                });
    }

    private void processApplicantDocument(DocumentSnapshot appDoc, Runnable onComplete) {

        String volunteerId = appDoc.getString("userId");
        String status = appDoc.getString("status");
        String appId = appDoc.getId();
        String eventId = appDoc.getString("eventId");
        String eventName = appDoc.getString("eventName");
        String location = appDoc.getString("location");

        String eventDate = appDoc.getString("date");

        if (!TextUtils.isEmpty(eventDate)) {
            if (isEventDatePassed(eventDate)) {
                onComplete.run();
                return;
            }

            addApplicant(appId, volunteerId, status, eventName, location, onComplete);
            return;
        }

        if (TextUtils.isEmpty(eventId)) {
            addApplicant(appId, volunteerId, status, eventName, location, onComplete);
            return;
        }

        db.collection("ngo_requests")
                .document(eventId)
                .get()
                .addOnSuccessListener(eventDoc -> {
                    String fetchedEventDate = eventDoc.getString("date");

                    if (isEventDatePassed(fetchedEventDate)) {
                        onComplete.run();
                        return;
                    }

                    addApplicant(appId, volunteerId, status, eventName, location, onComplete);
                })
                .addOnFailureListener(e ->
                        addApplicant(appId, volunteerId, status, eventName, location, onComplete)
                );
    }

    private void addApplicant(String appId,
                              String volunteerId,
                              String status,
                              String eventName,
                              String location,
                              Runnable onComplete) {

        if (TextUtils.isEmpty(volunteerId)) {
            onComplete.run();
            return;
        }

        db.collection("users")
                .document(volunteerId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String name = userDoc.getString("name");
                        String city = userDoc.getString("city");
                        String skill = userDoc.getString("skill");

                        applicantList.add(new Applicant(
                                appId,
                                volunteerId,
                                name != null ? name : "N/A",
                                city != null ? city : "N/A",
                                skill != null ? skill : "N/A",
                                status,
                                eventName != null ? eventName : "N/A",
                                location != null ? location : "N/A"
                        ));
                    }

                    onComplete.run();
                })
                .addOnFailureListener(e -> onComplete.run());
    }

    private void showEventSelectionDialog() {
        db.collection("applications")
                .whereEqualTo("ngoId", ngoId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(getContext(), "No accepted applicants found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Set<String> eventSet = new LinkedHashSet<>();

                    final int total = querySnapshot.size();
                    final int[] completed = {0};

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String eventName = doc.getString("eventName");
                        String eventId = doc.getString("eventId");
                        String eventDate = doc.getString("date");

                        Runnable finishOne = () -> {
                            completed[0]++;

                            if (completed[0] == total) {
                                showEventDialogFromSet(eventSet);
                            }
                        };

                        if (!TextUtils.isEmpty(eventDate)) {
                            if (!isEventDatePassed(eventDate) && !TextUtils.isEmpty(eventName)) {
                                eventSet.add(eventName);
                            }
                            finishOne.run();
                        } else if (!TextUtils.isEmpty(eventId)) {
                            db.collection("ngo_requests")
                                    .document(eventId)
                                    .get()
                                    .addOnSuccessListener(eventDoc -> {
                                        String fetchedDate = eventDoc.getString("date");

                                        if (!isEventDatePassed(fetchedDate) && !TextUtils.isEmpty(eventName)) {
                                            eventSet.add(eventName);
                                        }

                                        finishOne.run();
                                    })
                                    .addOnFailureListener(e -> finishOne.run());
                        } else {
                            if (!TextUtils.isEmpty(eventName)) {
                                eventSet.add(eventName);
                            }
                            finishOne.run();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void showEventDialogFromSet(Set<String> eventSet) {
        if (eventSet.isEmpty()) {
            Toast.makeText(getContext(), "No active accepted events found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] eventArray = eventSet.toArray(new String[0]);

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Event")
                .setItems(eventArray, (dialog, which) -> {
                    String selectedEvent = eventArray[which];
                    exportAcceptedApplicantsToCsv(selectedEvent);
                })
                .show();
    }

    private void exportAcceptedApplicantsToCsv(String selectedEventName) {
        db.collection("applications")
                .whereEqualTo("ngoId", ngoId)
                .whereEqualTo("status", "accepted")
                .whereEqualTo("eventName", selectedEventName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(getContext(), "No accepted applicants found for this event", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<String[]> rows = new ArrayList<>();

                    final int total = querySnapshot.size();
                    final int[] completed = {0};

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        processExportDocument(doc, rows, selectedEventName, () -> {
                            completed[0]++;

                            if (completed[0] == total) {
                                if (rows.isEmpty()) {
                                    Toast.makeText(getContext(), "No active accepted applicants found for this event", Toast.LENGTH_SHORT).show();
                                } else {
                                    createCsvFile(rows, selectedEventName);
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void processExportDocument(QueryDocumentSnapshot doc,
                                       ArrayList<String[]> rows,
                                       String selectedEventName,
                                       Runnable onComplete) {

        String eventDate = doc.getString("date");
        String eventId = doc.getString("eventId");

        if (!TextUtils.isEmpty(eventDate)) {
            if (isEventDatePassed(eventDate)) {
                onComplete.run();
                return;
            }

            addExportRow(doc, rows, selectedEventName, onComplete);
            return;
        }

        if (!TextUtils.isEmpty(eventId)) {
            db.collection("ngo_requests")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener(eventDoc -> {
                        String fetchedDate = eventDoc.getString("date");

                        if (isEventDatePassed(fetchedDate)) {
                            onComplete.run();
                            return;
                        }

                        addExportRow(doc, rows, selectedEventName, onComplete);
                    })
                    .addOnFailureListener(e -> addExportRow(doc, rows, selectedEventName, onComplete));
        } else {
            addExportRow(doc, rows, selectedEventName, onComplete);
        }
    }

    private void addExportRow(QueryDocumentSnapshot doc,
                              ArrayList<String[]> rows,
                              String selectedEventName,
                              Runnable onComplete) {

        String volunteerId = doc.getString("userId");

        if (TextUtils.isEmpty(volunteerId)) {
            rows.add(new String[]{
                    "N/A",
                    "N/A",
                    "N/A",
                    "N/A",
                    safe(selectedEventName),
                    "accepted",
                    "No"
            });

            onComplete.run();
            return;
        }

        db.collection("users")
                .document(volunteerId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    String name = userDoc.getString("name");
                    String city = userDoc.getString("city");
                    String phone = userDoc.getString("phone");
                    String email = userDoc.getString("email");

                    rows.add(new String[]{
                            safe(name),
                            safe(city),
                            safe(phone),
                            safe(email),
                            safe(selectedEventName),
                            "accepted",
                            "No"
                    });

                    onComplete.run();
                })
                .addOnFailureListener(e -> {
                    rows.add(new String[]{
                            "N/A",
                            "N/A",
                            "N/A",
                            "N/A",
                            safe(selectedEventName),
                            "accepted",
                            "No"
                    });

                    onComplete.run();
                });
    }

    private boolean isEventDatePassed(String eventDate) {
        try {
            if (eventDate == null || eventDate.trim().isEmpty()) {
                return false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            sdf.setLenient(false);

            Date parsedEventDate = sdf.parse(eventDate);
            Date today = sdf.parse(sdf.format(new Date()));

            return parsedEventDate != null && parsedEventDate.before(today);

        } catch (Exception e) {
            return false;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private void createCsvFile(ArrayList<String[]> rows, String eventName) {
        try {
            String safeEventName = eventName.replaceAll("[\\\\/:*?\"<>|]", "_");
            String fileName = "accepted_applicants_" + safeEventName + ".csv";

            File file = new File(requireContext().getExternalFilesDir(null), fileName);

            FileWriter writer = new FileWriter(file);
            writer.append("Name,City,Phone,Email,Event Name,Status,Attendance(Yes/No)\n");

            for (String[] row : rows) {
                writer.append(csvEscape(row[0])).append(",");
                writer.append(csvEscape(row[1])).append(",");
                writer.append(csvEscape(row[2])).append(",");
                writer.append(csvEscape(row[3])).append(",");
                writer.append(csvEscape(row[4])).append(",");
                writer.append(csvEscape(row[5])).append(",");
                writer.append(csvEscape(row[6])).append("\n");
            }
            writer.flush();
            writer.close();

            Toast.makeText(getContext(), "CSV exported successfully", Toast.LENGTH_SHORT).show();
            shareCsvFile(file);

        } catch (Exception e) {
            Toast.makeText(getContext(), "CSV creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareCsvFile(File file) {
        Uri uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Share CSV File"));
    }

    private void updateExportButtonVisibility() {
        boolean hasAcceptedApplicant = false;

        for (Applicant applicant : applicantList) {
            if ("accepted".equalsIgnoreCase(applicant.getStatus())) {
                hasAcceptedApplicant = true;
                break;
            }
        }

        btnExport.setVisibility(hasAcceptedApplicant ? View.VISIBLE : View.GONE);
    }
}