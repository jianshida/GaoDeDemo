package cn.cloudtop.gaodedemo.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import cn.cloudtop.gaodedemo.R;

public class LoaderProgressDialog extends Dialog {
	private Context context = null;
	private static LoaderProgressDialog customProgressDialog = null;
	
	public LoaderProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public LoaderProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static LoaderProgressDialog createDialog(Context context){
		customProgressDialog = new LoaderProgressDialog(context, R.style.dialog_untran2);
		customProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customProgressDialog.setContentView(R.layout.dialog_load_progress);
		//customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCancelable(false); //
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public LoaderProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }
    
    /**
     * 
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public LoaderProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return customProgressDialog;
    }
}