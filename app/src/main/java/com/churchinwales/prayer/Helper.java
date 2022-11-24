package com.churchinwales.prayer;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class Helper {


    String myLectionary ="";

    public Helper() {

    }
    public String[] getSeason()
    {
        return new Lectionary().getSeason();
    }

    public String[] getSeasonOld()
    {
        Calendar cal = Calendar.getInstance();
        Calendar easter = new Calendar.Builder()
                .setDate(2022, 4, 9)
                .build();

        if(cal.get(Calendar.YEAR) == 2022) {
            easter = new Calendar.Builder()
                    .setDate(2022, 3, 17)
                    .build();

        }
        if(cal.get(Calendar.YEAR) == 2023) {
            easter = new Calendar.Builder()
                    .setDate(2023, 4, 9)
                    .build();
        }

        String season="ADVENT";
        int weekOfSeason=1;
        String dayOfWeek="Monday";

        if( cal.compareTo(easter) > 0) {

            long weeks = cal.getTimeInMillis() - easter.getTimeInMillis();
            Calendar newCal = new Calendar.Builder().setInstant(weeks).build();
            int weeksSinceEaster = newCal.get(Calendar.WEEK_OF_YEAR);

            dayOfWeek = new Lectionary().getDayOfWeek();
            //dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

            // weeksSinceEaster = weeksSinceEaster / (24 * 60 * 60 * 1000);

            AppDebug.log("TAG","Season:"+season+" Week:"+weekOfSeason+ " Day:"+dayOfWeek + " Week of Year:" + weeksSinceEaster);

            if(weeksSinceEaster <= 6) {
                season = "EASTER";
                weekOfSeason = weeksSinceEaster;
            }
            else if (weeksSinceEaster >= 22) {
                season = "KINGDOM";
                weekOfSeason = weeksSinceEaster - 21;

                if (weekOfSeason >= 4) {
                    weekOfSeason =3;

                }
            }
            else if (weeksSinceEaster >= 26) {
                season = "ADVENT";
                weekOfSeason = weeksSinceEaster - 25;
            }
            else {
                season = "TRINITY";
                weekOfSeason = weeksSinceEaster -6;
            }
            if (cal.compareTo(easter) < 0) {
                AppDebug.log("TAG","Date is before Easter");
                if(cal.get(Calendar.WEEK_OF_YEAR) >= 0) {
                    season="NATIVITY";
                    weekOfSeason= cal.get(Calendar.WEEK_OF_YEAR);
                }
                if(cal.get(Calendar.WEEK_OF_YEAR) >=3) {
                    season="EPIPHANY";
                    weekOfSeason=cal.get(Calendar.WEEK_OF_YEAR)-2;
                }
            }
        }
        String[] returnData = new String[]{season, Integer.toString(weekOfSeason)};
        return returnData;
    }

    @Deprecated
    @RequiresApi(api = Build.VERSION_CODES.O)
    /*
     * Should Taake in a year
     *
     * Should perhaps return a JsonOBject?
     */
    /*
    public SpannableStringBuilder getLectionaryText(Context app_Context){

        SpannableStringBuilder contents = new SpannableStringBuilder("");

        try {

            if (myLectionary.equals("")) {
                myLectionary = this.readAsset(app_Context, "lectionary-YearTwo.json");
            }
            String myData = myLectionary;

            String season = "ADVENT";
            int weekOfSeason = 1;
            String dayOfWeek = "Monday";

            JSONObject jsonRootObject = new JSONObject(myData);

            Calendar cal = Calendar.getInstance();

            if (cal.compareTo(easter) > 0) {
                contents.append(Html.fromHtml("Date is after Easter<br>", Html.FROM_HTML_MODE_LEGACY));

                //   cal.add(Calendar.YEAR, - easter.get(Calendar.YEAR));
                //    cal.add(Calendar.MONTH, - easter.get(Calendar.MONTH));
                //    cal.add(Calendar.DAY_OF_MONTH, - easter.get(Calendar.DAY_OF_MONTH));
                contents.append(Html.fromHtml("Current Date: " + cal.get(Calendar.YEAR) + ":" + (cal.get(Calendar.MONTH) + 1) + ":" + cal.get(Calendar.DAY_OF_MONTH) + "<BR>", Html.FROM_HTML_MODE_LEGACY));


            dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

            contents.append(Html.fromHtml("Season: "+season+"<BR>",Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("Week:"+weekOfSeason+"<br>",Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("Day:"+dayOfWeek+"<BR>",Html.FROM_HTML_MODE_LEGACY));

                int weeksSinceEaster = newCal.get(Calendar.WEEK_OF_YEAR);

                dayOfWeek = new Lectionary().getDayOfWeek();

                // weeksSinceEaster = weeksSinceEaster / (24 * 60 * 60 * 1000);

                if (weeksSinceEaster <= 6) {
                    season = "EASTER";
                    weekOfSeason = weeksSinceEaster;
                } else {
                    season = "TRINITY";
                    weekOfSeason = weeksSinceEaster - 6;
                }

                contents.append(Html.fromHtml("Weeks Since Easter: " + weeksSinceEaster + "<br>", Html.FROM_HTML_MODE_LEGACY));
                contents.append(Html.fromHtml("Season: " + season + "<BR>", Html.FROM_HTML_MODE_LEGACY));
                contents.append(Html.fromHtml("Week:" + weekOfSeason + "<br>", Html.FROM_HTML_MODE_LEGACY));
                contents.append(Html.fromHtml("Day:" + dayOfWeek + "<BR>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                if (cal.compareTo(easter) < 0) {
                    contents.append("Date is before Easter");
                }
            }


            AppDebug.log("TAG", "Season:" + season + " Week:" + weekOfSeason + " Day:" + dayOfWeek);


            JSONObject jsonObject = jsonRootObject.optJSONObject(season);
            JSONObject week = jsonObject.optJSONObject(String.valueOf(weekOfSeason));

            if (dayOfWeek.equalsIgnoreCase("Sunday")) {
                dayOfWeek = "Saturday";
            }

            JSONObject day = week.optJSONObject(dayOfWeek);
            JSONObject prayer = day.optJSONObject("MorningPrayer");

            contents.append(Html.fromHtml("<br>", Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("Morning Prayer<br>", Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("Psalm: " + prayer.getString("Psalm") + "<BR>", Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("OT: " + prayer.getString("OT") + "<br>", Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("NT: " + prayer.getString("NT") + "<BR>", Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("<br><br>", Html.FROM_HTML_MODE_LEGACY));

            prayer = day.optJSONObject("EveningPrayer");

            contents.append(Html.fromHtml("Evening Prayer<br>", Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("Psalm: " + prayer.getString("Psalm") + "<BR>", Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("OT: " + prayer.getString("OT") + "<br>", Html.FROM_HTML_MODE_LEGACY));
            contents.append(Html.fromHtml("NT: " + prayer.getString("NT") + "<BR>", Html.FROM_HTML_MODE_LEGACY));
        }
        catch (NullPointerException e) {
            contents.append(e.getMessage());
        }
        catch (IOException e) {
                e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return contents;

    }
*/
    /*
     * This is designed to try and fix some of the errors
     * in the incoming verses.
     *
     * It can't fix everything, and it shows that the Lectionary still
     * needs work
     */
    protected String checkBibleReading(String theVerse) {

        String modifiedVerse = theVerse;

        if(theVerse.contains(")")) {
            //String result[] = modifiedVerse.split("b\\)");
            String result[] = modifiedVerse.split(",");
            String gettingA = result[0].trim().substring(2);
            String gettingB = "";
            if(result.length > 1) {
                gettingB = result[1].trim();
            }
            else {
                gettingB = "";
            }
            if(gettingA.contains("end")) {
                if(!gettingB.equalsIgnoreCase("")) {
                    modifiedVerse = gettingB;
                }
            }
            else {
                modifiedVerse = gettingA;
            }
        }

        if(theVerse.contains("[") && theVerse.toLowerCase().contains("ps")) {
            modifiedVerse = modifiedVerse.replace("Ps","");
            modifiedVerse = modifiedVerse.replace("PS","");
            String result[] = modifiedVerse.split("\\[");
            String gettingA = result[0].trim();
            gettingA = gettingA.replace("]",",");
            String gettingB = result[1].trim();
            gettingB = gettingB.replace("]",",");

            AppDebug.log("Psalm","Psalm B:"+gettingB);
            AppDebug.log("Psalm","Psalm A:"+gettingA);

            if(gettingA.equals("")) {
                if (!gettingB.contains("Ps")) {
                    gettingB = "Ps " + gettingB;
                    AppDebug.log("Psalm","Psalm B:"+gettingB);
                }
                modifiedVerse = gettingB;
            }
            else {
                if (!gettingA.contains("Ps")) {
                    gettingA = "Ps " + gettingA;
                    AppDebug.log("Psalm","Psalm A:"+gettingA);
                }
                modifiedVerse = gettingA;
            }
        }

        if(theVerse.contains("or")) {
            String result[] = modifiedVerse.split("or");
            String gettingA = result[0].trim();
            String gettingB = "";
            if (result.length > 1) {
               gettingB = result[1].trim();
            }
            if(gettingA.contains("end")) {
                modifiedVerse = gettingB;
            }
            else {
                if(modifiedVerse.equalsIgnoreCase("")) {
                    modifiedVerse = gettingA;
                }
            }

        }

        modifiedVerse = modifiedVerse.replace("or ","");
        modifiedVerse = modifiedVerse.replace(",","");

        AppDebug.log("Verse","Verse:"+modifiedVerse);


        return modifiedVerse;
    }

    /**
     * This should through an exception when there is no relevant Bible Reading!!!!
     * @param app_Context : The current app context
     * @param prayerTime : The MP (Morning Prayer) or EP (Evening Prayer)
     * @return : returns a JSON object of New and Old Testament readings
     */
    public JSONObject getLectionaryJson(Context app_Context, String prayerTime){

        JSONObject prayer = new JSONObject();

        try {

            String season="ADVENT";
            String dayOfWeek="Monday";
            String weekOfSeason="1";

            Calendar cal = Calendar.getInstance();

            dayOfWeek = new Lectionary().getDayOfWeek();

            if(cal.get(Calendar.YEAR) == 2022) {

                 myLectionary = this.readAsset(app_Context, "lectionary-YearTwo.json");
            }
            if(cal.get(Calendar.YEAR)  == 2023) {

                myLectionary = this.readAsset(app_Context, "lectionary-Year-A.json");
            }

            if(myLectionary.equals("")) {
                myLectionary = this.readAsset(app_Context, "lectionary-YearTwo.json");
            }


            String[] mySeasonData = this.getSeason();
            season = mySeasonData[Lectionary.SEASON];
            weekOfSeason = mySeasonData[Lectionary.WEEKOFSEASON];

            AppDebug.log("TAG-Helper","Season:"+season+" Week:"+weekOfSeason+ " Day:"+dayOfWeek );

            String myData = myLectionary;
            JSONObject jsonRootObject = new JSONObject(myData);
            JSONObject jsonObject = jsonRootObject.optJSONObject(season);
            JSONObject week =jsonObject.optJSONObject(weekOfSeason);
            if(dayOfWeek.equalsIgnoreCase("Sunday")) {
                dayOfWeek = "Saturday";
            }

            JSONObject day = week.optJSONObject(dayOfWeek);

            AppDebug.log("TAG", "Prayer Time:"+prayerTime);

            if((prayerTime.equalsIgnoreCase("MP")) || (prayerTime.equalsIgnoreCase("morningprayer"))) {
                prayer = day.optJSONObject("MorningPrayer");
            }
            else {
                prayer = day.optJSONObject("EveningPrayer");
            }
        }

        catch(IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        return prayer;


    }
/**
    protected String readFile(Context app_Context, String relativePath) throws IOException
    {
        StringBuilder myData = new StringBuilder();


        //InputStream fis = app_Context.getDataDir().open("Prayer/MorningPrayer/Confessional/EN_BasicConfessional.txt");

        String fileName = app_Context.getFilesDir().getPath()+relativePath;

        File file = new File(fileName);
        //InputStream fis = app_Context.getDataDir().open("Prayer/MorningPrayer/Confessional/EN_BasicConfessional.txt");

        FileInputStream fis = new FileInputStream(file);
        //FileInputStream fis = openFileInput(fileName);
        DataInputStream in = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        while((strLine = br.readLine())!= null) {
            myData.append(strLine);
        }
//
        br.close();
        in.close();
        fis.close();

        return myData.toString();
    }
**/

    public String readAsset(Context app_Context, String relativePath) throws IOException
    {
        String myData;


        //InputStream fis = app_Context.getDataDir().open("Prayer/MorningPrayer/Confessional/EN_BasicConfessional.txt");

        InputStream file= app_Context.getAssets().open(relativePath);

        int size = file.available();
        byte[] buffer = new byte[size];
        file.read(buffer);
        myData = new String(buffer);

        file.close();

        return myData;
    }
}
