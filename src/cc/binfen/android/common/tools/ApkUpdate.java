package cc.binfen.android.common.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import cc.binfen.android.member.R;

/**
 * @author Michael
 * Intention：Check update and download apk file
 * Description：it'll check version number and download the latest apk file and ask user if agree to install the new version
 */
public class ApkUpdate {
    private static final String APK_URL = "http://www.binfen.cc/apk/binfen.apk";
    private static final String VER_URL = "http://www.binfen.cc/apk/ver.txt";


    private static final String LOCAL_APK_PATH = "/sdcard/biobule/Biobule.apk";
    private static final String TAG = "ApkUpdate";


    Context mContext;
    ArrayList<String> list = new ArrayList<String>();
    private Updater mWork;


    public ApkUpdate(Context context) {
        this.mContext = context;
    }


    void checkUpdateAsync() {
        if(mWork==null){
            mWork = new Updater();
            mWork.start();
        }else{
            Log.w(TAG, "checkUpdateAsync has started a instance");
        }
        
    }


    void cancelUpdate() {
        if (mWork != null) {
            mWork.mCancel = true;
        }
    }


    class Updater extends Thread {
        boolean mCancel;


        @Override
        public void run() {
            checkUpdate();
        }


        void checkUpdate() {
            BufferedInputStream fin = null;
            try {
                URL url = new URL(VER_URL);
                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();


                InputStream in = con.getInputStream();


                BufferedReader bufferRd = new BufferedReader(
                        new InputStreamReader(in));
                String line;
                while (true) {
                    line = bufferRd.readLine();
                    if (line == null) {
                        break;
                    }
                    list.add(line);
                }


                if (list.size() < 1) {
                    return;
                }


                String packageName = mContext.getPackageName();
                PackageInfo info;
                try {
                    info = mContext.getPackageManager().getPackageInfo(
                            packageName, 0);
                    String verLocal = info.versionName;
                    String verOnServer = list.get(0);


                    float ver1 = Float.parseFloat(verLocal);
                    float ver2 = Float.parseFloat(verOnServer);
                    if (ver2 > ver1) {
                        // downLoad Newer Apk
                        downloadAndInstall(APK_URL, LOCAL_APK_PATH);
                    }


                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fin != null)
                    try {
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }


        }


        public void downloadAndInstall(String urlString, String filename) {
            InputStream is = null;
            OutputStream os = null;
            try {
                URL url = new URL(urlString);
                URLConnection con = url.openConnection();
                is = con.getInputStream();
                byte[] bs = new byte[1024];
                int len;


                File f = new File(filename);
                f.getParentFile().mkdirs();


                os = new FileOutputStream(filename);
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                os.flush();
                os.close();
                is.close();


                if (mCancel) {
                    return;
                }
                
                checkInstall(filename);


                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        private void checkInstall(final String filename) {
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    
                    build.setTitle(R.string.new_ver_avaliable).setMessage(R.string.check_if_install)
                    .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener (){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Intent.ACTION_VIEW);   
                            i.setDataAndType(Uri.parse("file://" + filename), "application/vnd.android.package-archive");
                            mContext.startActivity(i); 
                        }
                        
                    }).setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener (){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            
                        }
                        
                    });
                    
                    build.create().show();
                }
            };
            ((Activity)mContext).runOnUiThread(action);
            
            
        }


    };


}