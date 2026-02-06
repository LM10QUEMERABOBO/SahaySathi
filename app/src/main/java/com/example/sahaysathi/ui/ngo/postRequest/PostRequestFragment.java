package com.example.sahaysathi.ui.ngo.postRequest;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.example.sahaysathi.databinding.FragmentSlideshowBinding;

public class PostRequestFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    SharedPreferences sharedPreferences;
    TextView textView;
    TextView dropEvent, dropLocation, dropVolunteer, dropMedia, dropDeadline;

    LinearLayout formEvent, formLocation, formVolunteer, formMedia, formDeadline,postRequestFrom;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postrequest, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        postRequestFrom = view.findViewById(R.id.postRequestForm);
        dropEvent = view.findViewById(R.id.dropEvent);
        formEvent = view.findViewById(R.id.formEvent);

        dropLocation = view.findViewById(R.id.dropLocation);
        formLocation = view.findViewById(R.id.formLocation);

        dropVolunteer = view.findViewById(R.id.dropVolunteer);
        formVolunteer = view.findViewById(R.id.formVolunteer);

        dropMedia = view.findViewById(R.id.dropMedia);
        formMedia = view.findViewById(R.id.formMedia);

        dropDeadline = view.findViewById(R.id.dropDeadline);
        formDeadline = view.findViewById(R.id.formDeadline);

        dropEvent.setOnClickListener(v -> toggle(formEvent));
        dropLocation.setOnClickListener(v -> toggle(formLocation));
        dropVolunteer.setOnClickListener(v -> toggle(formVolunteer));
        dropMedia.setOnClickListener(v -> toggle(formMedia));
        dropDeadline.setOnClickListener(v -> toggle(formDeadline));
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void toggle(View v){
        if(v.getVisibility() == View.GONE){
            v.setVisibility(View.VISIBLE);
        } else{
            v.setVisibility(View.GONE);
        }
    }
}