package com.example.sudoku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    CustomButton clickedButton; // 클릭된 버튼
    CustomButton[][] buttons = new CustomButton[9][9];
    BoardGenerator board = new BoardGenerator();
    boolean numberPadOn = false;

    // 버튼 초기화 숫자 최대 개수
    int maxNumber = (int)(9*9*0.6);
    // 확률 설정
    int percent = 6;

    // chance
    int chance = 5;
    int originChance = chance;
    TextView textViewChance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // declare
        TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
        table.setBackgroundColor(Color.parseColor("#A9A9A9"));
        TableLayout numberPad = (TableLayout) findViewById(R.id.numberPad);
        Button newGameButton = (Button) findViewById(R.id.reset);
        TextView textViewClear = (TextView) findViewById(R.id.textViewClear);
        TextView textViewLose = (TextView) findViewById(R.id.textViewLose);
        textViewClear.setTypeface(Typeface.DEFAULT_BOLD);
        textViewLose.setTypeface(Typeface.DEFAULT_BOLD);
        int maxNum = this.maxNumber;

        // 기회
        this.textViewChance = new TextView(this);
        textViewChance.setText("Chance : "+String.valueOf(chance)+" / "+originChance);
        textViewChance.setTypeface(null, Typeface.BOLD);
        textViewChance.setTextSize(20);
        table.addView(textViewChance);

        for (int i = 0; i < 9; i++) {
            TableRow tableRow = new TableRow(this);
            table.addView(tableRow);

            for (int j = 0; j < 9; j++) {
                buttons[i][j] = new CustomButton(this, i, j);
;
                // button 숫자 넣기 -> p값 확률, maxNum 최대 개수
                int p = (int) (Math.random()*10);

                if (p>=0 && p<percent && maxNum > 0){
                    buttons[i][j].value = board.get(i,j);
                    buttons[i][j].set(buttons[i][j].value);
                    maxNum--;
                }

                // 주어진 숫자 굵게 검정색 설정 !
                if(buttons[i][j].value != 0){
                    buttons[i][j].textView.setTypeface(null, Typeface.BOLD);
                    buttons[i][j].textView.setTextColor(Color.BLACK);
                }

                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f
                );

                // 간격 설정
                if(i%3==0 && j%3==0 && !(i==0 || j==0))
                    layoutParams.setMargins(20,20,5,5);
                else if(i%3==0 && i!=0)
                    layoutParams.setMargins(5, 20, 5, 5);
                else if(j%3==0 && j!=0)
                    layoutParams.setMargins(20,5,5,5);
                else
                    layoutParams.setMargins(5,5,5,5);

                buttons[i][j].setLayoutParams(layoutParams);
                tableRow.addView(buttons[i][j]);

                // 빈칸인 버튼 onclick
                if(buttons[i][j].value == 0){
                    // 버튼 클릭시 넘버패드
                    buttons[i][j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(numberPadOn == false)
                                clickedButton = (CustomButton) view;
                            // 게임 clear 하면 numberPad 클릭되지 않도록
                            if(textViewClear.getVisibility() == View.INVISIBLE && textViewLose.getVisibility() == View.INVISIBLE) {
                                numberPad.setVisibility(View.VISIBLE);
                                numberPadOn = true;
                            }
                        }
                    });
                    // 버튼 long 클릭시 메모다이얼로그
                    buttons[i][j].setOnLongClickListener(new View.OnLongClickListener(){
                        @Override
                        public boolean onLongClick(View view) {
                            if(numberPadOn == false)
                                clickedButton = (CustomButton) view;
                            if(clickedButton.value == 0 && numberPadOn == false &&
                            textViewClear.getVisibility() == View.INVISIBLE && textViewLose.getVisibility() == View.INVISIBLE) {
                                checkToggle();
                            }
                            return true;
                        }
                    });
                }
            }
        }

        // new game 버튼 앱 재시작
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                startActivity(mainIntent);
                System.exit(0);
            }
        });
    }

    // 게임 끝
    public int checkFinishGame(){
        if(chance == 0)
            return -1;
        else{
            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    if(!(buttons[i][j].conflict == 0 && buttons[i][j].value != 0)){
                        return 0;
                    }
                }
            }
            return 1;
        }
    }

    public void isFinishGame(){
        if(checkFinishGame() == 1){
            TextView textView1 = (TextView) findViewById(R.id.textViewClear);
            TextView textView2 = (TextView) findViewById(R.id.imoji);
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            Toast.makeText(this,"Congratulations",Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Play Again!",Toast.LENGTH_SHORT).show();
        }
        else if(checkFinishGame() == -1){
            TextView textView1 = (TextView) findViewById(R.id.textViewLose);
            textView1.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Play Again!",Toast.LENGTH_SHORT).show();
        }
    }

    // 충돌 확인
    public void setConflict(){
        int row = clickedButton.row;
        int col = clickedButton.col;
        int value = clickedButton.value;
        int checkConflict = 0;

        // 행에서 충돌 확인
        for(int i=0; i<9; i++){
            if(buttons[row][i].value == value && value!=0 && i != col){
                if(buttons[row][i].textView.getTypeface() == Typeface.DEFAULT)
                    buttons[row][i].setBackgroundColor(Color.parseColor("#FF1493"));
                else
                    buttons[row][i].setBackgroundColor(Color.parseColor("#FFFF00"));
                if(buttons[row][col].textView.getTypeface() == Typeface.DEFAULT)
                    buttons[row][col].setBackgroundColor(Color.parseColor("#FF1493"));
                else
                    buttons[row][col].setBackgroundColor(Color.parseColor("#FFFF00"));
                buttons[row][i].conflict = 24;
                buttons[row][col].conflict = 24;
                checkConflict = 1;
            }
        }

        // 열에서 충돌 확인
        for(int i=0; i<9; i++){
            if(buttons[i][col].value == value && value!=0 && i != row){
                if(buttons[i][col].textView.getTypeface() == Typeface.DEFAULT)
                    buttons[i][col].setBackgroundColor(Color.parseColor("#FF1493"));
                else
                    buttons[i][col].setBackgroundColor(Color.parseColor("#FFFF00"));
                if(buttons[row][col].textView.getTypeface() == Typeface.DEFAULT)
                    buttons[row][col].setBackgroundColor(Color.parseColor("#FF1493"));
                else
                    buttons[row][col].setBackgroundColor(Color.parseColor("#FFFF00"));
                buttons[i][col].conflict = 24;
                buttons[row][col].conflict = 24;
                checkConflict = 1;
            }
        }

        // 3*3 충돌 확인
        int checkRow = row / 3 * 3;
        int checkCol = col / 3 * 3;
        for(int i=checkRow; i<checkRow+3; i++){
            for(int j=checkCol; j<checkCol+3; j++){
                if(buttons[i][j].value == value && value!=0 && i != row && j != col){
                    if(buttons[i][j].textView.getTypeface() == Typeface.DEFAULT)
                        buttons[i][j].setBackgroundColor(Color.parseColor("#FF1493"));
                    else
                        buttons[i][j].setBackgroundColor(Color.parseColor("#FFFF00"));
                    if(buttons[row][col].textView.getTypeface() == Typeface.DEFAULT)
                        buttons[row][col].setBackgroundColor(Color.parseColor("#FF1493"));
                    else
                        buttons[row][col].setBackgroundColor(Color.parseColor("#FFFF00"));
                    buttons[i][j].conflict = 24;
                    buttons[row][col].conflict = 24;
                    checkConflict = 1;
                }
            }
        }

        if(checkConflict != 0) {
            chance--;
            textViewChance.setText("Chance : "+String.valueOf(chance)+" / "+originChance);
            Toast.makeText(this,"Wrong !",Toast.LENGTH_SHORT).show();
        }
        unsetConflict();
    }

    // 이미 충돌이 일어난 것 다시 검사
    public void unsetConflict() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (buttons[i][j].conflict != 0 && buttons[i][j].value == 0) {
                    buttons[i][j].conflict = 0;
                }

                if (buttons[i][j].conflict != 0 && buttons[i][j].value != 0) {
                    //행 확인 -> 8개 숫자가 다 다른지 확인
                    for (int k = 0; k < 9; k++) {
                        if ((buttons[i][k].value != buttons[i][j].value || buttons[i][k].value == 0) && k != j) {
                            buttons[i][j].conflict = buttons[i][j].conflict - 1;
                        }
                    }

                    //열 확인
                    for (int k = 0; k < 9; k++) {
                        if ((buttons[k][j].value != buttons[i][j].value || buttons[k][j].value == 0) && k != i) {
                            buttons[i][j].conflict = buttons[i][j].conflict - 1;
                        }
                    }
                    // 3*3 확인
                    int row = i / 3 * 3;
                    int col = j / 3 * 3;
                    for (int x = row; x < row + 3; x++) {
                        for (int y = col; y < col + 3; y++) {
                            if ((buttons[x][y].value != buttons[i][j].value || buttons[x][y].value == 0) &&
                                    !(x==i && y==j)) {
                                buttons[i][j].conflict = buttons[i][j].conflict - 1;
                            }
                        }
                    }
                }
            }
        }

        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if (buttons[i][j].conflict == 0) { // 충돌 x
                    buttons[i][j].setBackgroundResource(R.drawable.button_selector);
                } else { // 충돌 o
                    buttons[i][j].conflict = 24;
                }
            }
        }
    }

    // 메모 다이얼로그
    public void checkToggle(){
        View memoDialogView = (View)View.inflate(this, R.layout.dialog_memo, null);
        ToggleButton toggleButton1 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton1);
        ToggleButton toggleButton2 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton2);
        ToggleButton toggleButton3 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton3);
        ToggleButton toggleButton4 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton4);
        ToggleButton toggleButton5 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton5);
        ToggleButton toggleButton6 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton6);
        ToggleButton toggleButton7 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton7);
        ToggleButton toggleButton8 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton8);
        ToggleButton toggleButton9 = (ToggleButton) memoDialogView.findViewById(R.id.toggleButton9);

        //체크된 토글버튼
        int[] checkToggle = new int[9];

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Memo")
                .setView(memoDialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(int k=0; k<9; k++){
                            if(checkToggle[k] == 1){
                                clickedButton.memos[k].setVisibility(View.VISIBLE);
                            }
                            else{
                                clickedButton.memos[k].setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMemo();
                    }
                });

        //토글버튼 체크
        toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[0] = 1;
                else
                    checkToggle[0] = 0;
            }
        });
        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[1] = 1;
                else
                    checkToggle[1] = 0;
            }
        });
        toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[2] = 1;
                else
                    checkToggle[2] = 0;
            }
        });
        toggleButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[3] = 1;
                else
                    checkToggle[3] = 0;
            }
        });
        toggleButton5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[4] = 1;
                else
                    checkToggle[4] = 0;
            }
        });
        toggleButton6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[5] = 1;
                else
                    checkToggle[5] = 0;
            }
        });
        toggleButton7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[6] = 1;
                else
                    checkToggle[6] = 0;
            }
        });
        toggleButton8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[7] = 1;
                else
                    checkToggle[7] = 0;
            }
        });
        toggleButton9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    checkToggle[8] = 1;
                else
                    checkToggle[8] = 0;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        //메모 숫자가 visible 임에 따라 toggle checked true
        if(clickedButton.memos[0].getVisibility()==View.VISIBLE){
            toggleButton1.setChecked(true);
        }
        else if(clickedButton.memos[0].getVisibility()==View.INVISIBLE){
            toggleButton1.setChecked(false);
        }
        if(clickedButton.memos[1].getVisibility()==View.VISIBLE){
            toggleButton2.setChecked(true);
        }
        else if(clickedButton.memos[1].getVisibility()==View.INVISIBLE){
            toggleButton2.setChecked(false);
        }
        if(clickedButton.memos[2].getVisibility()==View.VISIBLE){
            toggleButton3.setChecked(true);
        }
        else if(clickedButton.memos[2].getVisibility()==View.INVISIBLE){
            toggleButton3.setChecked(false);
        }
        if(clickedButton.memos[3].getVisibility()==View.VISIBLE){
            toggleButton4.setChecked(true);
        }
        else if(clickedButton.memos[3].getVisibility()==View.INVISIBLE){
            toggleButton4.setChecked(false);
        }
        if(clickedButton.memos[4].getVisibility()==View.VISIBLE){
            toggleButton5.setChecked(true);
        }
        else if(clickedButton.memos[4].getVisibility()==View.INVISIBLE){
            toggleButton5.setChecked(false);
        }
        if(clickedButton.memos[5].getVisibility()==View.VISIBLE){
            toggleButton6.setChecked(true);
        }
        else if(clickedButton.memos[5].getVisibility()==View.INVISIBLE){
            toggleButton6.setChecked(false);
        }
        if(clickedButton.memos[6].getVisibility()==View.VISIBLE){
            toggleButton7.setChecked(true);
        }
        else if(clickedButton.memos[6].getVisibility()==View.INVISIBLE){
            toggleButton7.setChecked(false);
        }
        if(clickedButton.memos[7].getVisibility()==View.VISIBLE){
            toggleButton8.setChecked(true);
        }
        else if(clickedButton.memos[7].getVisibility()==View.INVISIBLE){
            toggleButton8.setChecked(false);
        }
        if(clickedButton.memos[8].getVisibility()==View.VISIBLE){
            toggleButton9.setChecked(true);
        }
        else if(clickedButton.memos[8].getVisibility()==View.INVISIBLE){
            toggleButton9.setChecked(false);
        }
    }

    //메모 삭제
    public void deleteMemo(){
        for(int i=0; i<9; i++){
            clickedButton.memos[i].setVisibility(View.INVISIBLE);
        }
    }
    //숫자 클릭시 실행 메소드
    public void clickNum(){
        TableLayout tableLayout = (TableLayout) findViewById(R.id.numberPad);
        tableLayout.setVisibility(View.INVISIBLE);
        setConflict();
        deleteMemo();
        isFinishGame();
        numberPadOn = false;
    }

    public void onClickNum1(View v) {
        clickedButton.set(1);
        clickNum();
    }

    public void onClickNum2(View v) {
        clickedButton.set(2);
        clickNum();
    }

    public void onClickNum3(View v) {
        clickedButton.set(3);
        clickNum();
    }

    public void onClickNum4(View v) {
        clickedButton.set(4);
        clickNum();
    }

    public void onClickNum5(View v) {
        clickedButton.set(5);
        clickNum();
    }

    public void onClickNum6(View v) {
        clickedButton.set(6);
        clickNum();
    }

    public void onClickNum7(View v) {
        clickedButton.set(7);
        clickNum();
    }

    public void onClickNum8(View v) {
        clickedButton.set(8);
        clickNum();
    }

    public void onClickNum9(View v) {
        clickedButton.set(9);
        clickNum();
    }

    public void onClickCancel(View v) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.numberPad);
        tableLayout.setVisibility(View.INVISIBLE);
        numberPadOn = false;
    }

    public void onClickDel(View v) {
        clickedButton.set(0);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.numberPad);
        tableLayout.setVisibility(View.INVISIBLE);
        setConflict();
        numberPadOn = false;
    }
}