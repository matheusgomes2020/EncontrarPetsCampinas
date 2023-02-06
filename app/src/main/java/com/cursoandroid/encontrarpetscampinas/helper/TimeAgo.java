package com.cursoandroid.encontrarpetscampinas.helper;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeAgo {

    public static String timeAgoResultado( String dataFirebase ) {

        String timeA = "";

        try {
            PrettyTime prettyTime = new PrettyTime();
            Date dataPostagem = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" ).parse( dataFirebase );
            timeA = prettyTime.format( dataPostagem );
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return timeA;
    }
}