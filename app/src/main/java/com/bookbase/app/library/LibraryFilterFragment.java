package com.bookbase.app.library;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import com.bookbase.app.R;

public class LibraryFilterFragment extends DialogFragment {

    Spinner sortOptions;
    Spinner sortOrder;

    public interface FilterDialogListener {
        public void onCancel(LibraryFilterFragment dialog);
        public void onApply(LibraryFilterFragment dialog);
    }

    private FilterDialogListener listener;

    public LibraryFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Fragment parent = LibraryFilterFragment.this.getParentFragment();
        View view = parent.getLayoutInflater().inflate(R.layout.filter_options, null);
        builder.setTitle(R.string.lbl_filter_options)
                //.setView(R.layout.filter_options)
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onApply(LibraryFilterFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancel(LibraryFilterFragment.this);
                    }
                });
        sortOptions = view.findViewById(R.id.spinner_sort_options);
        sortOrder = view.findViewById(R.id.spinner_sort_order);
        return builder.create();
    }

    public static LibraryFilterFragment newInstance(String param1, String param2) {
        LibraryFilterFragment fragment = new LibraryFilterFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FilterDialogListener) LibraryFilterFragment.this.getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement FilterDialogListener");
        }
    }

    public String getSortOption() {
        int pos = sortOptions.getSelectedItemPosition();
        //String result = sortOptions.getItemAtPosition(pos).toString();
        return sortOptions.getSelectedItem().toString();
    }

    public boolean getSortOrder() {
        return sortOrder.getSelectedItem().toString().equals("Ascending");
    }
}
