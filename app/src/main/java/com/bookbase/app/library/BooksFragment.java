package com.bookbase.app.library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookbase.app.R;
import com.bookbase.app.database.AppDatabase;
import com.bookbase.app.library.addBook.AddBookActivity;
import com.bookbase.app.library.viewBook.ViewBookFragment;
import com.bookbase.app.model.entity.Author;
import com.bookbase.app.model.entity.Book;
import com.bookbase.app.model.repository.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BooksFragment extends Fragment implements Runnable,
        android.support.v7.widget.SearchView.OnQueryTextListener,
        LibraryFilterFragment.FilterDialogListener {

    private OnFragmentInteractionListener mListener;
    private List<Book> books;
    private AppDatabase database;
    private RecyclerView bookList;
    private Repository repository;
    private TextView emptyView;
    private BooksAdapter adapter;
    private boolean sortAscending = true;

    private final Comparator<Book> COMPARATOR_TITLE = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            return sortAscending ? o1.getTitle().compareTo(o2.getTitle()) :
                    o2.getTitle().compareTo(o1.getTitle());
        }
    };

    private final Comparator<Book> COMPARATOR_AUTHOR = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            String author1 = o1.getAuthor().getName();
            String author2 = o2.getAuthor().getName();
            int result = sortAscending ? author1.compareTo(author2) : author2.compareTo(author1);
            Log.d("Comparator Testing", String.format(author1 + " vs " + author2 + " = %d", result));
            return sortAscending ? author1.compareTo(author2) : author2.compareTo(author1);
        }
    };

    private final Comparator<Book> COMPARATOR_GENRE = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            String genre1 = o1.getGenre().getGenreName();
            String genre2 = o2.getGenre().getGenreName();
            return sortAscending ? genre1.compareTo(genre2) : genre2.compareTo(genre1);
        }
    };

    private final Comparator<Book> COMPARATOR_RATING = new Comparator<Book>() {
        // TODO: Need to implement a more elegant solution here.
        @Override
        public int compare(Book o1, Book o2) {
            if(sortAscending) {
                if(o1.getRating() == o2.getRating()) {
                    return 0;
                } if (o1.getRating() > o2.getRating()) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                if(o1.getRating() == o2.getRating()) {
                    return 0;
                } if (o1.getRating() > o2.getRating()) {
                    return -1;
                } else {
                    return 1;
                }
            }

        }
    };

    public interface OnFragmentInteractionListener { void onFragmentInteraction(Uri uri); }

    public BooksFragment() {}

    public static BooksFragment newInstance(String param1, String param2) {
        return new BooksFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            run();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = Repository.getRepository();
        books = repository.getBookList();
    }



    @Override
    public void onResume() {
        super.onResume();
        books = repository.getBookList();
        setupAdapter(books);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.library_menu, menu);

        final TextView toolbarTitle = getActivity().findViewById(R.id.toolbar_title);
        final MenuItem searchMenuItem = menu.findItem(R.id.searchButton);
        final MenuItem filterMenuItem = menu.findItem(R.id.filterButton);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(this);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.findItem(R.id.filterButton).setVisible(false);
                toolbarTitle.setVisibility(View.GONE);
                searchView.requestFocus(View.FOCUS_DOWN, null);
            }
        });

        searchView.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                menu.findItem(R.id.filterButton).setVisible(true);
                toolbarTitle.setVisibility(View.VISIBLE  );
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Book> filteredModelList = filter(books, newText);
        adapter.replaceAll(filteredModelList);
        bookList.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.searchButton:
                TransitionManager.beginDelayedTransition((ViewGroup) getActivity().findViewById(R.id.toolbar));
                MenuItemCompat.expandActionView(item);
                return true;
            case R.id.filterButton:
                LibraryFilterFragment fragment = new LibraryFilterFragment();
                FragmentManager manager = getChildFragmentManager();
                manager.beginTransaction().add(fragment, "Filter").addToBackStack(null).commit();
                //fragment.show(getActivity().getSupportFragmentManager(), "Filter");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static List<Book> filter(List<Book> books, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Book> filteredBookList = new ArrayList<>();
        for (Book book : books) {
            final String text = book.getTitle().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredBookList.add(book);
            }
        }
        return filteredBookList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        bookList = view.findViewById(R.id.books_list);
        emptyView = view.findViewById(R.id.empty_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        bookList.setLayoutManager(layoutManager);
        FloatingActionButton fab = view.findViewById(R.id.add_book_fab);
        setupAdapter(books);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
            }
        });

        bookList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), bookList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Fragment fragment = (ViewBookFragment.class).newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("book", books.get(position));
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .commit();
                } catch(IllegalAccessException e){
                    e.printStackTrace();
                } catch(java.lang.InstantiationException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
                Snackbar.make(view, "Long touch", Snackbar.LENGTH_SHORT).show();
            }
        }));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void run(){ database = AppDatabase.getDatabase(this.getContext()); }


    private void setupAdapter(List<Book> books){
        if (adapter == null) {
            adapter = new BooksAdapter(getActivity(), books, COMPARATOR_TITLE);
        }
        adapter.add(books);
        if(books.isEmpty()) {
            bookList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.getRootView().setBackgroundColor(Color.WHITE);
        } else {
            bookList.setVisibility(View.VISIBLE);
            bookList.setAdapter(adapter);
            int currSize = adapter.getItemCount();
            adapter.notifyItemRangeInserted(currSize, books.size());
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCancel(LibraryFilterFragment dialog) {

    }

    @Override
    public void onApply(LibraryFilterFragment dialog) {
        sortAscending = dialog.getSortOrder();
        switch(dialog.getSortOption()) {
            case "Title":
                setupAdapter(books);
                break;
            case "Author":
                adapter.setComparator(COMPARATOR_AUTHOR);
                setupAdapter(books);
                break;
            case "Genre":
                setupAdapter(books);
                break;
            case "Rating":
                setupAdapter(books);
                break;
        }
    }
}
