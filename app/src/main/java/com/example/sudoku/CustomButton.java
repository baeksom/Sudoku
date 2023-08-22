package com.example.sudoku;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Dimension;

public class CustomButton extends FrameLayout{
    int row;
    int col;
    int value = 0;
    TextView textView;
    TextView[] memos = new TextView[9];

    //숫자 충돌 확인 ( ==0 -> 충돌 x || 값이 x , !=0 -> 충돌 o )
    int conflict = 0;

    //생성자
    public CustomButton(Context context, int row, int col){
        super(context);
        this.row = row;
        this.col = col;

        textView = new TextView(context);
        addView(textView);

        //textview 사이즈 늘리기(버튼 세로 길이)
        textView.setTextSize(Dimension.SP, 19);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0,35,0,35);

        setClickable(true);
        setBackgroundResource(R.drawable.button_selector);

        // memo 레이아웃 추가
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout memo = (TableLayout) layoutInflater.inflate(R.layout.layout_memo, null);
        addView(memo);

        int k = 0;
        for(int i = 0; i < 3; i++) {
            TableRow tableRow = (TableRow) memo.getChildAt(i);
            for(int j = 0; j < 3; j++, k++) {
                memos[k] = (TextView) tableRow.getChildAt(j);
            }
        }
    }

    public void set(int a){
        value = a;
        if (value==0){
            //erase text attribute
            textView.setText(null);
        }
        else{
            textView.setText(String.valueOf(a));
        }
    }
}
