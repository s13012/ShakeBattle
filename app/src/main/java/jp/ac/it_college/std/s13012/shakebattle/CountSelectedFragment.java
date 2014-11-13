package jp.ac.it_college.std.s13012.shakebattle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CountSelectedFragment extends BaseFragment implements View.OnClickListener{

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_count_50:
                super.selectedTimeOrCount(SELECTED_COUNT, 50,
                        getActivity(), CountAttackActivity.class);
                break;
            case R.id.button_count_100:
                super.selectedTimeOrCount(SELECTED_COUNT, 100,
                        getActivity(), CountAttackActivity.class);
                break;
            case R.id.button_count_2000:
                super.selectedTimeOrCount(SELECTED_COUNT, 2000,
                        getActivity(), CountAttackActivity.class);
                break;
            case R.id.button_to_title:
                super.fragmentReplace(R.id.fragment_container, new ModeFragment());
                break;
        }
    }
}
