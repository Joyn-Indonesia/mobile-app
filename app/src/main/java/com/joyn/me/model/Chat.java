package com.joyn.me.model;

import java.io.Serializable;

public class Chat implements Serializable {
    public int type = FCMType.CHAT;
    public String id_tujuan;
    public String nama_tujuan; // namaku
    public String reg_id_tujuan;
    public String isi_chat;
    public String waktu;
    public int status;
    public int chat_from;
    public transient String reg_id_from;
}
