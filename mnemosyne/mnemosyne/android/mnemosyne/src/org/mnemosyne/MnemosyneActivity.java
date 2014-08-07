package org.mnemosyne;

import com.srplab.www.starcore.StarCoreFactory;
import com.srplab.www.starcore.StarCoreFactoryPath;
import com.srplab.www.starcore.StarObjectClass;
import com.srplab.www.starcore.StarServiceClass;
import com.srplab.www.starcore.StarSrvGroupClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MnemosyneActivity extends Activity {

    StarObjectClass python;
    StarObjectClass mnemosyne;
    StarObjectClass reviewController;

    static TextView questionLabel;
    static WebView question;
    static TextView answerLabel;
    static WebView answer;
    static Button showAnswerButton;
    static Button button0;
    static Button button1;
    static Button button2;
    static Button button3;
    static Button button4;
    static Button button5;
    static TextView statusbar;

    private void mergeApkFile(Activity c, ArrayList<String> partFileList, String dst)
            throws IOException {
        if (!new File(partFileList.get(0)).exists()) {
            //OutputStream out = new FileOutputStream(dst);
            OutputStream out = openFileOutput(dst, MODE_WORLD_READABLE );
            byte[] buffer = new byte[1024];
            InputStream in;
            int readLen = 0;
            for(int i = 0; i < partFileList.size(); i++){
                in = c.getAssets().open(partFileList.get(i));
                while ((readLen = in.read(buffer)) != -1) {
                    out.write(buffer, 0, readLen);
                }
                out.flush();
                in.close();
            }
            out.close();
        }
    }

    private void copyFile(Activity c, String Name,String desPath) throws IOException {
        File outfile = new File("/data/data/" + getPackageName() + "/files/" + desPath+Name);
        if (!outfile.exists()) {
            outfile.createNewFile();
            FileOutputStream out = new FileOutputStream(outfile);
            byte[] buffer = new byte[1024];
            InputStream in;
            int readLen = 0;
            in = c.getAssets().open(desPath + Name);
            while ((readLen = in.read(buffer)) != -1){
                out.write(buffer, 0, readLen);
            }
            out.flush();
            in.close();
            out.close();
        }
    }

    private boolean CreatePath(String Path){
        File destCardDir = new File(Path);
        if (!destCardDir.exists()) {
            int Index = Path.lastIndexOf(File.separator.charAt(0));
            if (Index < 0) {
                if( destCardDir.mkdirs() == false )
                    return false;
            } else {
                String ParentPath = Path.substring(0, Index);
                if (CreatePath(ParentPath) == false)
                    return false;
                if (destCardDir.mkdirs() == false)
                    return false;
            }
        }
        return true;
    }

    private boolean unzip(InputStream zipFileName, String outputDirectory, Boolean OverWriteFlag) {
        try {
            ZipInputStream in = new ZipInputStream(zipFileName);
            ZipEntry entry = in.getNextEntry();
            byte[] buffer = new byte[1024];
            while (entry != null) {
                File file = new File(outputDirectory);
                file.mkdir();
                if (entry.isDirectory()) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 1);
                    if (CreatePath(outputDirectory + File.separator + name) == false)
                        return false;
                } else {
                    String name = outputDirectory + File.separator + entry.getName();
                    int Index = name.lastIndexOf(File.separator.charAt(0));
                    if (Index < 0) {
                        file = new File(outputDirectory + File.separator + entry.getName());
                    } else {
                        String ParentPath = name.substring(0, Index);
                        if (CreatePath(ParentPath) == false)
                            return false;
                        file = new File(outputDirectory + File.separator + entry.getName());
                    }
                    if (!file.exists() || OverWriteFlag == true) {
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        int readLen = 0;
                        while ((readLen = in.read(buffer)) != -1) {
                            out.write(buffer, 0, readLen);
                        }
                        out.close();
                    }
                }
                entry = in.getNextEntry();
            }
            in.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setupMnemosyne() {
        java.io.File python_extras_r14File = new java.io.File("/data/data/" + getPackageName() +
                "/files/python_extras_r14.zip");
        if (!python_extras_r14File.exists()) {
            ArrayList<String> StarCoreFiles =  new ArrayList<String>();
            StarCoreFiles.add("python_extras_r14_aa");
            StarCoreFiles.add("python_extras_r14_ab");
            StarCoreFiles.add("python_extras_r14_ac");
            StarCoreFiles.add("python_extras_r14_ad");
            try {
                mergeApkFile(this,StarCoreFiles,"python_extras_r14.zip");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        File destDir = new File("/data/data/" + getPackageName() + "/files/lib-dynload");
        if (!destDir.exists())
            destDir.mkdirs();
        try {
            copyFile(this,"_bisect.so","lib-dynload/");
            copyFile(this,"_bytesio.so","lib-dynload/");
            copyFile(this,"_codecs_cn.so","lib-dynload/");
            copyFile(this,"_codecs_hk.so","lib-dynload/");
            copyFile(this,"_codecs_iso2022.so","lib-dynload/");
            copyFile(this,"_codecs_jp.so","lib-dynload/");
            copyFile(this,"_codecs_kr.so","lib-dynload/");
            copyFile(this,"_codecs_tw.so","lib-dynload/");
            copyFile(this,"_collections.so","lib-dynload/");
            copyFile(this,"_ctypes.so","lib-dynload/");
            copyFile(this,"_ctypes_test.so","lib-dynload/");
            copyFile(this,"_elementtree.so","lib-dynload/");
            copyFile(this,"_fileio.so","lib-dynload/");
            copyFile(this,"_functools.so","lib-dynload/");
            copyFile(this,"_heapq.so","lib-dynload/");
            copyFile(this,"_hotshot.so","lib-dynload/");
            copyFile(this,"_json.so","lib-dynload/");
            copyFile(this,"_lsprof.so","lib-dynload/");
            copyFile(this,"_md5.so","lib-dynload/");
            copyFile(this,"_multibytecodec.so","lib-dynload/");
            copyFile(this,"_multiprocessing.so","lib-dynload/");
            copyFile(this,"_random.so","lib-dynload/");
            copyFile(this,"_sha256.so","lib-dynload/");
            copyFile(this,"_sha512.so","lib-dynload/");
            copyFile(this,"_sha.so","lib-dynload/");
            copyFile(this,"_socket.so","lib-dynload/");
            copyFile(this,"_sqlite3.so","lib-dynload/");
            copyFile(this,"_ssl.so","lib-dynload/");
            copyFile(this,"_struct.so","lib-dynload/");
            copyFile(this,"_testcapi.so","lib-dynload/");
            copyFile(this,"_weakref.so","lib-dynload/");
            copyFile(this,"array.so","lib-dynload/");
            copyFile(this,"audioop.so","lib-dynload/");
            copyFile(this,"binascii.so","lib-dynload/");
            copyFile(this,"bz2.so","lib-dynload/");
            copyFile(this,"cmath.so","lib-dynload/");
            copyFile(this,"cPickle.so","lib-dynload/");
            copyFile(this,"crypt.so","lib-dynload/");
            copyFile(this,"cStringIO.so","lib-dynload/");
            copyFile(this,"datetime.so","lib-dynload/");
            copyFile(this,"fcntl.so","lib-dynload/");
            copyFile(this,"future_builtins.so","lib-dynload/");
            copyFile(this,"imageop.so","lib-dynload/");
            copyFile(this,"itertools.so","lib-dynload/");
            copyFile(this,"math.so","lib-dynload/");
            copyFile(this,"mmap.so","lib-dynload/");
            copyFile(this,"operator.so","lib-dynload/");
            copyFile(this,"parser.so","lib-dynload/");
            copyFile(this,"pyexpat.so","lib-dynload/");
            copyFile(this,"resource.so","lib-dynload/");
            copyFile(this,"select.so","lib-dynload/");
            copyFile(this,"strop.so","lib-dynload/");
            copyFile(this,"syslog.so","lib-dynload/");
            copyFile(this,"termios.so","lib-dynload/");
            copyFile(this,"time.so","lib-dynload/");
            copyFile(this,"unicodedata.so","lib-dynload/");
            copyFile(this,"zlib.so","lib-dynload/");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            AssetManager assetManager = getAssets();
            InputStream dataSource = assetManager.open("mnemosyne.zip");
            StarCoreFactoryPath.CreatePath(Runtime.getRuntime(),
                    "/data/data/" + getPackageName() + "/files");
            unzip(dataSource, "/data/data/" + getPackageName() + "/files", true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        StarCoreFactoryPath.StarCoreCoreLibraryPath = "/data/data/" + getPackageName() + "/lib";
        StarCoreFactoryPath.StarCoreShareLibraryPath = "/data/data/" + getPackageName() + "/lib";
        StarCoreFactoryPath.StarCoreOperationPath = "/data/data/" + getPackageName() + "/files";
        StarCoreFactory starcore = StarCoreFactory.GetFactory();

        StarSrvGroupClass SrvGroup = starcore._GetSrvGroup(0);
        StarServiceClass Service = SrvGroup._GetService("cle", "123");
        if (Service == null) {  // The service has not been initialized.
            Service = starcore._InitSimple("cle", "123", 0, 0);
            Service._CheckPassword(false);
            SrvGroup = (StarSrvGroupClass) Service._Get("_ServiceGroup");
            SrvGroup._InitRaw("python", Service);
            python = Service._ImportRawContext("python", "", false, "");

            // Set up extra paths.
            python._Call("import", "sys");
            StarObjectClass pythonSys = python._GetObject("sys");
            StarObjectClass pythonPath = (StarObjectClass) pythonSys._Get("path");
            String base = "/data/data/" + getPackageName();
            pythonPath._Call("insert", 0, base + "/files/python_extras_r14.zip");
            pythonPath._Call("insert", 0, base + "/lib");
            pythonPath._Call("insert", 0, base + "/files/lib-dynload");
            pythonPath._Call("insert", 0, base + "/files");

            // Start Mnemosyne.
            SrvGroup._LoadRawModule("python", "", "/data/data/" + getPackageName() +
                    "/files/mnemosyne/cle/mnemosyne_android.py", false);
            mnemosyne = python._GetObject("mnemosyne");

            String dataDir = "/sdcard/Mnemosyne/";
            String filename = "default.db";
            StarObjectClass activity = Service._New();
            activity._AttachRawObject(this, false);

            python._Call("start_mnemosyne", dataDir, filename, activity);

            reviewController = (StarObjectClass) mnemosyne._Call("review_controller");
        }
    }

    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        questionLabel = (TextView) this.findViewById(R.id.questionLabel);
        question = (WebView) this.findViewById(R.id.question);
        answerLabel = (TextView) this.findViewById(R.id.answerLabel);
        answer = (WebView) this.findViewById(R.id.answer);
        showAnswerButton = (Button) this.findViewById(R.id.showAnswerButton);
        button0 = (Button) this.findViewById(R.id.button0);
        button1 = (Button) this.findViewById(R.id.button1);
        button2 = (Button) this.findViewById(R.id.button2);
        button3 = (Button) this.findViewById(R.id.button3);
        button4 = (Button) this.findViewById(R.id.button4);
        button5 = (Button) this.findViewById(R.id.button5);
        statusbar = (TextView) this.findViewById(R.id.statusbar);

        handler = new Handler(Looper.getMainLooper());

        Thread t = new Thread(new Runnable() {
            public void run() {
                setupMnemosyne();
                //testProgress();
                Log.d("Mnemosyne", "done_thread ");
            }
        });
        t.start();

        //setupMnemosyne();
        //testProgress();

        showAnswerButton.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                Log.d("Mnemosyne", "show_answer clicked ");
                python._Call("calling_back");
                reviewController._Call("show_answer");
            }
        });

        button0.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                reviewController._Call("grade_answer", 0);
            }
        });

        button1.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                reviewController._Call("grade_answer", 1);
            }
        });

        button2.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                reviewController._Call("grade_answer", 2);
            }
        });

        button3.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                reviewController._Call("grade_answer", 3);
            }
        });

        button4.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                reviewController._Call("grade_answer", 4);
            }
        });

        button5.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                reviewController._Call("grade_answer", 5);
            }
        });

    }

    public void setQuestionLabel(String label) {
        questionLabel.setText(label);
    }

    public void setQuestion(String html) {
        question.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    public void setAnswer(String html) {
        answer.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    public void setQuestionBoxVisible(boolean isVisible) {
        if (isVisible) {
            question.setVisibility(android.view.View.VISIBLE);
            questionLabel.setVisibility(android.view.View.VISIBLE);
        }
        else {
            question.setVisibility(android.view.View.GONE);
            questionLabel.setVisibility(android.view.View.GONE);
        }
    }

    public void setAnswerBoxVisible(boolean isVisible) {
        if (isVisible) {
            answer.setVisibility(android.view.View.VISIBLE);
            answerLabel.setVisibility(android.view.View.VISIBLE);
        }
        else {
            answer.setVisibility(android.view.View.GONE);
            answerLabel.setVisibility(android.view.View.GONE);
        }
    }

    public void updateShowButton(String text, boolean isDefault, boolean isEnabled) {
        showAnswerButton.setText(text);
        if (isEnabled) {
            showAnswerButton.setVisibility(android.view.View.VISIBLE);
        }
        else {
            showAnswerButton.setVisibility(android.view.View.GONE);
        }
    }

    public void setGradesEnabled(boolean isEnabled) {
        if (isEnabled) {
            button0.setVisibility(android.view.View.VISIBLE);
            button1.setVisibility(android.view.View.VISIBLE);
            button2.setVisibility(android.view.View.VISIBLE);
            button3.setVisibility(android.view.View.VISIBLE);
            button4.setVisibility(android.view.View.VISIBLE);
            button5.setVisibility(android.view.View.VISIBLE);
        }
        else {
            button0.setVisibility(android.view.View.GONE);
            button1.setVisibility(android.view.View.GONE);
            button2.setVisibility(android.view.View.GONE);
            button3.setVisibility(android.view.View.GONE);
            button4.setVisibility(android.view.View.GONE);
            button5.setVisibility(android.view.View.GONE);
        }
    }

    String statusBarText;

    public void setStatusbarText(String text) {
        Log.d("Mnemosyne", "setstatusbartex " + text);
        statusBarText = text;
        handler.post(new Runnable() {
            public void run() {
                statusbar.setText(statusBarText);
            }});
    }

    public void showInformation(String text) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(text);
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });
        alert.show();
    }

    private int result = -1;

    public int showQuestion(String text, String option0, String option1, String option2) {

        // Make a handler that throws a runtime exception when a message is received.
        final Handler _handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(text);
        alert.setCancelable(false);
        alert.setPositiveButton(option0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                result = 0;
                _handler.sendMessage(_handler.obtainMessage());
            }
        });
        alert.setNeutralButton(option1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                result = 1;
                _handler.sendMessage(_handler.obtainMessage());
            }
        });
        alert.setNegativeButton(option2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                result = 2;
                _handler.sendMessage(_handler.obtainMessage());
            }

        });

        alert.show();
        // Loop until the user selects an answer and a runtime exception is triggered.
        try {
            Looper.loop();
        }
        catch (RuntimeException exception) {
        }
        return result;
    }

    private ProgressDialog progressDialog;

    private int progressValue = 0;

    public void setProgressText(String text) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(text);
        progressDialog.setProgress(0);
        progressDialog.show();
        Log.d("Mnemosyne", "done set progress text ");
    }

    public void setProgressRange(int maximum) {
        progressDialog.setMax(maximum);
    }

    public void setProgressValue(int value) {
        Log.d("Mnemosyne", "setProgressValue " + value);
        if (value >= progressDialog.getMax()) {
            closeProgress();
            return;
        }
        progressValue = value;
        //progressDialog.setProgress(progressValue);

        handler.post(new Runnable() {
            public void run() {
                progressDialog.setProgress(progressValue);
                Log.d("Mnemosyne", "handler " + progressValue);
            }
        });
    }

    public void closeProgress() {
        progressDialog.dismiss();
    }

    public void testProgress() {
        Log.d("Mnemosyne", "testProgress ");

        handler.post(new Runnable() {
            public void run() {
                Log.d("Mnemosyne", "runnable creating ui");
                setProgressText("progress2");
                setProgressRange(3);
            }
        });

        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i=1; i<4; i++) {
                    Log.d("Mnemosyne", "in thread " + i);
                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("Mnemosyne", "A " + i);
                    //mainWidget._Call("set_progress_value", i);
                    setProgressValue(i);
                    Log.d("Mnemosyne", "B " + i);
                }
            }
        });
        //t.start();

        Log.d("Mnemosyne", "starting_work");
        python._Call("do_work");

        if (false) {
            final StarObjectClass mainWidget = (StarObjectClass) mnemosyne._Call("main_widget");
            mainWidget._Call("set_progress_text", "progress3");
            mainWidget._Call("set_progress_range", 3);
            new Thread(new Runnable() {
                public void run() {
                    Log.d("Mnemosyne", "starting_work in thread");
                    python._Call("do_work");
                    Log.d("Mnemosyne", "done_work in thread");
                }
            }).start();}
    }

    private final   String TAG = "Activity";            //Log tag
    private         MyThread mThread;                   //spawned thread
    Bundle          myB = new Bundle();                 //used for creating the msgs
    public          Handler mHandler = new Handler(){   //handles the INcoming msgs
        @Override public void handleMessage(Message msg)
        {
            myB = msg.getData();
            Log.i(TAG, "Handler got message"+ myB.getInt("THREAD DELIVERY"));
        }
    };
    //Methods:
    //--------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThread = new MyThread(mHandler);
        mThread.start();
        sendMsgToThread();
    }
    //--------------------------
    void sendMsgToThread()
    {
        Message msg = mThread.getHandler().obtainMessage();
        myB.putInt("MAIN DELIVERY", 321);
        msg.setData(myB);
        mThread.getHandler().sendMessage(msg);
    }
}
//=========================================================================================
//=========================================================================================

public class MyThread extends Thread{
    //Properties:
    private final   String TAG = "MyThread";            //Log tag
    private         Handler outHandler;                 //handles the OUTgoing msgs
    Bundle          myB = new Bundle();                 //used for creating the msgs
    private         Handler inHandler = new Handler(){  //handles the INcoming msgs
        @Override public void handleMessage(Message msg)
        {
            myB = msg.getData();
            Log.i(TAG, "Handler got message"+ myB.getInt("MAIN DELIVERY"));
        }
    };

    //Methods:
    //--------------------------
    @Override
    public void run(){
        sendMsgToMainThread();  //send to the main activity a msg
        Looper.prepare();
        Looper.loop();
        //after this line nothing happens because of the LOOP!
        Log.i(TAG, "Lost message");
    }
    //--------------------------
    public MyThread(Handler mHandler) {
        //C-tor that get a reference object to the MainActivity handler.
        //this is how we know to whom we need to connect with.
        outHandler = mHandler;
    }
    //--------------------------
    public Handler getHandler(){
        //a Get method which return the handler which This Thread is connected with.
        return inHandler;
    }
    //--------------------------
    private void sendMsgToMainThread(){
        Message msg = outHandler.obtainMessage();
        myB.putInt("THREAD DELIVERY", 123);
        msg.setData(myB);
        outHandler.sendMessage(msg);
    }

};