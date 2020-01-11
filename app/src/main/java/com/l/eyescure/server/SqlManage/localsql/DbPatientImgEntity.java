package com.l.eyescure.server.SqlManage.localsql;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Look on 2017/1/14.
 * 病人信息
 */

@Table(name = "patient_img_table")
public class DbPatientImgEntity {
    @Column(name = "account", property = "UNIQUE")//唯一约束
    private String account;//账号
    @Column(name = "file_image")
    private File file_image; //图片内容
    @Column(name = "stream_image")
    private InputStream stream_image; //图片内容

    public DbPatientImgEntity(){
    }

    public DbPatientImgEntity(String account,File file_image){
        this.account = account;
        this.file_image = file_image;
    }

    public DbPatientImgEntity(String account,InputStream stream_image){
        this.account = account;
        this.stream_image = stream_image;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public File getFile_image() {
        return file_image;
    }

    public void setFile_image(File file_image) {
        this.file_image = file_image;
    }

    public InputStream getStream_image() {
        return stream_image;
    }

    public void setStream_image(InputStream stream_image) {
        this.stream_image = stream_image;
    }

}
