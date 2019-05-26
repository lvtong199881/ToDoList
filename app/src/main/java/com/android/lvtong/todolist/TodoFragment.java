package com.android.lvtong.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

public class TodoFragment extends Fragment {

    private static final String ARG_TODO_ID = "todo_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Todo mTodo;
    private EditText mTitleField;
    private EditText mBeizhuField;
    private Spinner mSpinner;
    private Button mDateButton;
    private Button mAddButton;
    private ArrayAdapter<String> mAdapter;

    Boolean isSpinner = false;

    public static TodoFragment newInstance(UUID todoID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, todoID);

        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID todoId = (UUID) getArguments().getSerializable(ARG_TODO_ID);
        mTodo = TodoLab.get(getActivity()).getTodo(todoId);
    }

    @Override
    public void onPause() {
        super.onPause();

        TodoLab.get(getActivity()).updateTodo(mTodo);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_todo, container, false);

        mAddButton = (Button)v.findViewById(R.id.btn_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mTitleField.getText())){
                    Toast.makeText(getActivity(), "标题为空！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //时间
        mDateButton = (Button)v.findViewById(R.id.button_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mTodo.getmDate());
                dialog.setTargetFragment(TodoFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSpinner = (Spinner)v.findViewById(R.id.spinner);
        mSpinner.setDropDownVerticalOffset(100);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinner){
                    String string = (String) mSpinner.getSelectedItem();
                    Toast.makeText(getActivity(), "选择了："+string, Toast.LENGTH_SHORT).show();
                    mTodo.setmImportance(Integer.valueOf(string));
                }
                isSpinner=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //标题
        mTitleField = (EditText)v.findViewById(R.id.editText_title);
        mTitleField.setText(mTodo.getmTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setmTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //备注
        mBeizhuField = (EditText)v.findViewById(R.id.ed_beizhu);
        mBeizhuField.setText(mTodo.getmBeizhu());
        mBeizhuField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setmBeizhu(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTodo.setmDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        if (mTodo.getmDate() != null){
            mDateButton.setText(mTodo.getmDate().toString());
        }
    }
}