package com.example.recipe_manager;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView textViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        textViewStatus = findViewById(R.id.textViewStatus);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        testAddData();
    }

    // Add test data
    private void testAddData() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello Firestore!");
        db.collection("testCollection")
                .document("testDoc")
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Document added successfully");
                    updateStatus("Document added successfully");
                    testReadData(); // Proceed to read after add
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                    updateStatus("Error adding document: " + e.getMessage());
                });
    }

    // Read data
    private void testReadData() {
        db.collection("testCollection")
                .document("testDoc")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("Firestore", "Read data: " + documentSnapshot.getData());
                        updateStatus("Read data: " + documentSnapshot.getData());
                    } else {
                        Log.d("Firestore", "No such document");
                        updateStatus("No such document");
                    }
                    testUpdateData(); // Proceed to update after read
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error reading document", e);
                    updateStatus("Error reading document: " + e.getMessage());
                });
    }

    // Update data
    private void testUpdateData() {
        db.collection("testCollection")
                .document("testDoc")
                .update("message", "Updated Firestore message!")
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Document updated successfully");
                    updateStatus("Document updated successfully");
                    testDeleteData(); // Proceed to delete after update
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating document", e);
                    updateStatus("Error updating document: " + e.getMessage());
                });
    }

    // Delete data
    private void testDeleteData() {
        db.collection("testCollection")
                .document("testDoc")
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Document deleted successfully");
                    updateStatus("Document deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error deleting document", e);
                    updateStatus("Error deleting document: " + e.getMessage());
                });
    }

    // Update the UI TextView status safely
    private void updateStatus(String message) {
        runOnUiThread(() -> textViewStatus.setText(message));
    }
}
