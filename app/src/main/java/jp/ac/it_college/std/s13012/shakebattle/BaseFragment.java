package jp.ac.it_college.std.s13012.shakebattle;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;

public abstract class BaseFragment extends Fragment {

    public static final String DESTINATION_CLASS = "destination_class";
    public static final String GOAL_VALUE = "goal";

    public void fragmentReplace(int id, Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(id, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void selectedTimeOrCount(Context context, Class destination, int goal) {
        Intent intent = new Intent(context, WaitOpponentActivity.class)
                .putExtra(DESTINATION_CLASS, destination)
                .putExtra(GOAL_VALUE, goal);
        startActivity(intent);
    }
}
