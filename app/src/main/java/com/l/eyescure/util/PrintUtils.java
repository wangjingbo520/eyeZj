package com.l.eyescure.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.l.eyescure.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Author:admin
 * Version: V1.0
 * Description: 打印帮助类
 * Date: 2017/5/2
 */

public class PrintUtils {

    // for logging
    private static final String TAG = PrintUtils.class.getSimpleName();

    // Send 2 Printer package name
    private static final String PACKAGE_NAME = "com.rcreations.send2printer";

    // intent action to trigger printing
    public static final String PRINT_ACTION = "com.rcreations.send2printer.print";

    // content provider for accessing images on local sdcard from within html content
    // sample img src shoul be something like "content://s2p_localfile/sdcard/logo.gif"
    public static final String LOCAL_SDCARD_CONTENT_PROVIDER_PREFIX = "content://s2p_localfile";


    /**
     * Returns true if "Send 2 Printer" is installed.
     */
    public static boolean isSend2PrinterInstalled( Context context )
    {
        boolean output = false;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo( PACKAGE_NAME, 0 );
            if( pi != null )
            {
                output = true;
            }
        } catch (PackageManager.NameNotFoundException e) {}
        return output;
    }


    /**
     * Launches the Android Market page for installing "Send 2 Printer"
     * and calls "finish()" on the given activity.
     */
    public static void launchMarketPageForSend2Printer( final Activity context )
    {
        AlertDialog dlg = new AlertDialog.Builder( context, R.style.AlertDialogCustom )
                .setTitle("提示")
                .setMessage("如果需要打印功能，则必须安装打印程序")
                .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which )
                    {
                        // launch browser
                        Uri data = Uri.parse( "http://market.android.com/search?q=pname:" + PACKAGE_NAME );
                        Intent intent = new Intent( android.content.Intent.ACTION_VIEW, data );
                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        context.startActivity( intent );

                        // exit
                        context.finish();
                    }
                } )
                .show();
    }
//
//    public static void launchMarketPageForSend2Printer( final Activity context )
//    {
//        AlertDialog dlg = new AlertDialog.Builder( context )
//                .setTitle("提示")
//                .setMessage("如果需要打印功能，则必须安装打印程序")
//                .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick( DialogInterface dialog, int which )
//                    {
//                        // launch browser
//                        Uri data = Uri.parse( "http://market.android.com/search?q=pname:" + PACKAGE_NAME );
//                        Intent intent = new Intent( android.content.Intent.ACTION_VIEW, data );
//                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                        context.startActivity( intent );
//
//                        // exit
//                        context.finish();
//                    }
//                } )
//                .show();
//    }


    /**
     * Save the given picture (contains canvas draw commands) to a file for printing.
     */
    public static File saveCanvasPictureToTempFile(Picture picture )
    {
        File tempFile = null;

        // save to temporary file
        File dir = getTempDir();
        if( dir != null )
        {
            FileOutputStream fos = null;
            try
            {
                File f = File.createTempFile( "picture", ".stream", dir );
                fos = new FileOutputStream( f );
                picture.writeToStream( fos );
                tempFile = f;
            }
            catch( IOException e )
            {
                Log.e( TAG, "failed to save picture", e );
            }
            finally
            {
                close( fos );
            }
        }

        return tempFile;
    }


    /**
     * Sends the given picture file (returned from {@link #saveCanvasPictureToTempFile}) for printing.
     */
    public static boolean queuePictureStreamForPrinting( Context context, File f )
    {
        // send to print activity
        Uri uri = Uri.fromFile( f );
        Intent i = new Intent( PRINT_ACTION );
        i.setDataAndType( uri, "application/x-android-picture-stream" );
        i.putExtra( "scaleFitToPage", true );
        context.startActivity( i );

        return true;
    }


    /**
     * Save the given Bitmap to a file for printing.
     * Note: Bitmap can be result of canvas draw commands.
     */
    public static File saveBitmapToTempFile(Bitmap b, Bitmap.CompressFormat format )
            throws IOException, UnknownFormatException
    {
        File tempFile = null;

        // save to temporary file
        File dir = getTempDir();
        if( dir != null )
        {
            FileOutputStream fos = null;
            try
            {
                String strExt = null;
                switch( format )
                {
                    case PNG:
                        strExt = ".pngx";
                        break;

                    case JPEG:
                        strExt = ".jpgx";
                        break;

                    default:
                        throw new UnknownFormatException( "unknown format: " + format );
                }
                File f = File.createTempFile( "bitmap", strExt, dir );
                fos = new FileOutputStream( f );
                b.compress( format, 100, fos );
                tempFile = f;
            }
            finally
            {
                close( fos );
            }
        }

        return tempFile;
    }


    /**
     * Sends the given image file for printing.
     */
    public static boolean queueBitmapForPrinting( Context context, File f, Bitmap.CompressFormat format )
            throws UnknownFormatException
    {
        String strMimeType = null;
        switch( format )
        {
            case PNG:
                strMimeType = "image/png";
                break;

            case JPEG:
                strMimeType = "image/jpeg";
                break;

            default:
                throw new UnknownFormatException( "unknown format: " + format );
        }

        // send to print activity
        Uri uri = Uri.fromFile( f );
        Intent i = new Intent( PRINT_ACTION );
        i.setDataAndType( uri, strMimeType );
        i.putExtra( "scaleFitToPage", true );
        i.putExtra( "deleteAfterPrint", true );
        context.startActivity( i );

        return true;
    }


    /**
     * Sends the given text for printing.
     */
    public static boolean queueTextForPrinting( Context context, String strContent )
    {
        // send to print activity
        Intent i = new Intent( PRINT_ACTION );
        i.setType( "text/plain" );
        i.putExtra( Intent.EXTRA_TEXT, strContent );
        context.startActivity( i );

        return true;
    }


    /**
     * Save the given text to a file for printing.
     */
    public static File saveTextToTempFile( String text )
            throws IOException
    {
        File tempFile = null;

        // save to temporary file
        File dir = getTempDir();
        if( dir != null )
        {
            FileOutputStream fos = null;
            try
            {
                File f = File.createTempFile( "text", ".txt", dir );
                fos = new FileOutputStream( f );
                fos.write( text.getBytes() );
                tempFile = f;
            }
            finally
            {
                close( fos );
            }
        }

        return tempFile;
    }


    /**
     * Sends the given text file for printing.
     */
    public static boolean queueTextFileForPrinting( Context context, File f )
    {
        // send to print activity
        Uri uri = Uri.fromFile( f );
        Intent i = new Intent( PRINT_ACTION );
        i.setDataAndType( uri, "text/plain" );
        i.putExtra( "deleteAfterPrint", true );
        context.startActivity( i );

        return true;
    }


    /**
     * Sends the given html for printing.
     *
     * You can also reference a local image on your sdcard using the "content://s2p_localfile" provider.
     * For example: <img src="content://s2p_localfile/sdcard/logo.gif">
     */
    public static boolean queueHtmlForPrinting( Context context, String strContent )
    {
        // send to print activity
        Intent i = new Intent( PRINT_ACTION );
        i.setType( "text/html" );
        i.putExtra( Intent.EXTRA_TEXT, strContent );
        context.startActivity( i );

        return true;
    }


    /**
     * Sends the given html URL for printing.
     *
     * You can also reference a local file on your sdcard using the "content://s2p_localfile" provider.
     * For example: "content://s2p_localfile/sdcard/test.html"
     */
    public static boolean queueHtmlUrlForPrinting( Context context, String strUrl )
    {
        // send to print activity
        Intent i = new Intent( PRINT_ACTION );
        //i.setDataAndType( Uri.parse(strUrl), "text/html" );// this crashes!
        i.setType( "text/html" );
        i.putExtra( Intent.EXTRA_TEXT, strUrl );
        context.startActivity( i );

        return true;
    }


    /**
     * Save the given html content to a file for printing.
     */
    public static File saveHtmlToTempFile( String html )
            throws IOException
    {
        File tempFile = null;

        // save to temporary file
        File dir = getTempDir();
        if( dir != null )
        {
            FileOutputStream fos = null;
            try
            {
                File f = File.createTempFile( "html", ".html", dir );
                fos = new FileOutputStream( f );
                fos.write( html.getBytes() );
                tempFile = f;
            }
            finally
            {
                close( fos );
            }
        }

        return tempFile;
    }


    /**
     * Sends the given html file for printing.
     */
    public static boolean queueHtmlFileForPrinting( Context context, File f )
    {
        // send to print activity
        Uri uri = Uri.fromFile( f );
        Intent i = new Intent( PRINT_ACTION );
        i.setDataAndType( uri, "text/html" );
        i.putExtra( "deleteAfterPrint", true );
        context.startActivity( i );

        return true;
    }


    /**
     * Return a temporary directory on the sdcard where files can be saved for printing.
     * @return null if temporary directory could not be created.
     */
    public static File getTempDir()
    {
        File dir = new File( Environment.getExternalStorageDirectory(), "temp" );
        if( dir.exists() == false && dir.mkdirs() == false )
        {
            Log.e( TAG, "failed to get/create temp directory" );
            return null;
        }
        return dir;
    }


    /**
     * Helper method to close given output stream ignoring any exceptions.
     */
    public static void close( OutputStream os )
    {
        if( os != null )
        {
            try
            {
                os.close();
            }
            catch( IOException e ) {}
        }
    }


    /**
     * Thrown by bitmap methods where the given Bitmap.CompressFormat value is unknown.
     */
    public static class UnknownFormatException extends Exception
    {
        public UnknownFormatException( String msg )
        {
            super( msg );
        }
    }
}
