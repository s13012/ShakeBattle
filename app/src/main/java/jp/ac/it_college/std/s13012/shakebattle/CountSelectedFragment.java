package jp.ac.it_college.std.s13012.shakebattle;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




public class CountSelectedFragment extends Fragment implements View.OnClickListener{

    public static final String SELECTED_COUNT = "selected_count";

    public CountSelectedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_count_selected, container, false);

        rootView.findViewById(R.id.button_count_50).setOnClickListener(this);
        rootView.findViewById(R.id.button_count_100).setOnClickListener(this);
        rootView.findViewById(R.id.button_count_2000).setOnClickListener(this);
        rootView.findViewById(R.id.button_to_title).setOnClickListener(this);

        return rootView;
    }

    private void countSelected(int count) {
        Intent intent = new Intent(getActivity(), CountAttackActivity.class);
        intent.putExtra(SELECTED_COUNT, count);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_count_50:
                countSelected(50);
                break;
            case R.id.button_count_100:
                countSelected(100);
                break;
            case R.id.button_count_2000:
                countSelected(2000);
                break;
            case R.id.button_to_title:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ModeFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
        }
    }
}
