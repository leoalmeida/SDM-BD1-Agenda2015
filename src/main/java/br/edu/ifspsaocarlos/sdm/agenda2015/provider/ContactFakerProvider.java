package br.edu.ifspsaocarlos.sdm.agenda2015.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by LeonardoAlmeida on 16/12/15.
 */
public class ContactFakerProvider {

    public static final String TAG = "ContactFakerProvider";


    //Faker
    private static final String FAKER_URL = "http://faker.hook.io/?property=";

    private static Uri FAKER_URI = Uri.parse(FAKER_URL);

    public enum LOCALE {
        DE("de", 0), DE_AT("de_AT", 1), DE_CH("de_CH", 2), EL_GR("el_GR", 3), EN("en", 4), EN_AU("en_AU", 5), en_BORK("en_BORK", 6), en_GB("en_GB", 8), en_IE("en_IE", 9), en_IND("en_IND", 10),
        en_US("en_US", 11), en_au_ocker("en_au_ocker", 12), es("es", 13), es_MX("es_MX", 14), fa("fa", 15), fr("fr", 16), fr_CA("fr_CA", 17), ge("ge", 18), it("it", 19), ja("ja", 20),
        ko("ko", 21), nb_NO("nb_NO", 22), nep("nep", 23), nl("nl", 24), pl("pl", 25), BR("pt_BR", 26), ru("ru", 27), sk("sk", 28), sv("sv", 29), tr("tr", 30),
        uk("uk", 31), vi("vi", 32), zh_CN("zh_CN", 33), zh_TW("zh_TW", 34);

        private String stringValue;
        private int intValue;

        private LOCALE(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return "&locale=" + stringValue;
        }
    }

    public enum ADDRESS {
        ZIPCODE("zipCode", 0),
        CITY ("city", 1),
        CITYPREFIX ("cityPrefix", 2),
        CITYSUFFIX("citySuffix", 3),
        STREETNAME("streetName", 4),
        STREETADDRESS("streetAddress", 5),
        STREETSUFFIX("streetSuffix", 6),
        STREETPREFIX("streetPrefix", 7),
        SECONDARYADDRESS("secondaryAddress", 8),
        COUNTY("county", 9),
        COUNTRY("country", 10),
        COUNTRYCODE("countryCode", 11),
        STATE("state", 12),
        STATEABBR("stateAbbr", 13),
        LATITUDE("latitude", 14),
        LONGITUDE("longitude", 15);

        private String stringValue;
        private int intValue;

        private ADDRESS(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public enum COMMERCE {

        COLOR("color", 0),
        DEPARTMENT ("department", 0),
        PRODUCTNAME ("productName", 0),
        PRICE ("price", 0),
        PRODUCTADJECTIVE ("productAdjective", 0),
        PRODUCTMATERIAL ("productMaterial", 0),
        PRODUCT ("product", 0);

        private String stringValue;
        private int intValue;

        private COMMERCE(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public enum COMPANY {

        SUFFIXES ("suffixes", 0),
        COMPANYNAME ("companyName", 1),
        COMPANYSUFFIX ("companySuffix", 2),
        CATCHPHRASE ("catchPhrase", 3),
        BS ("bs", 4),
        CATCHPHRASEADJECTIVE("catchPhraseAdjective", 5),
        CATCHPHRASEDESCRIPTOR("catchPhraseDescriptor", 6),
        CATCHPHRASENOUN("catchPhraseNoun", 7),
        BSADJECTIVE("bsAdjective", 8),
        BSBUZZ("bsBuzz", 9),
        BSNOUN("bsNoun", 10);

        private String stringValue;
        private int intValue;

        private COMPANY(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }


    public enum DATE {

        PAST ("past", 0),
        FUTURE ("future", 1),
        BETWEEN ("between", 2),
        RECENT ("recent", 3),
        MONTH ("month", 4),
        WEEKDAY ("weekday", 5);

        private String stringValue;
        private int intValue;

        private DATE(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static final String ROOT = "date";

    }

    public enum FAKE {}

    public enum FINANCE {

        ACCOUNT ("account", 0),
        ACCOUNTNAME ("accountName", 1),
        MASK ("mask", 2),
        AMOUNT ("amount", 3),
        TRANSACTIONTYPE ("transactionType", 4),
        CURRENCYCODE ("currencyCode", 5),
        CURRENCYNAME ("currencyName", 6),
        CURRENCYSYMBOL ("currencySymbol", 7);

        private String stringValue;
        private int intValue;

        private FINANCE(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public enum HACKER {

        ABBREVIATION ("abbreviation", 0),
        ADJECTIVE ("adjective", 1),
        NOUN ("noun", 2),
        VERB ("verb", 3),
        INGVERB ("ingverb", 4),
        PHRASE("phrase", 5);

        private String stringValue;
        private int intValue;

        private HACKER(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public enum HELPERS {

        RANDOMIZE ("randomize", 0),
        SLUGIFY ("slugify", 1),
        REPLACESYMBOLWITHNUMBER ("replaceSymbolWithNumber", 2),
        REPLACESYMBOLS ("replaceSymbols", 3),
        SHUFFLE ("shuffle", 4),
        MUSTACHE ("mustache", 5),
        CREATECARD ("createCard", 6),
        CONTEXTUALCARD ("contextualCard", 7),
        USERCARD ("userCard", 8),
        CREATETRANSACTION ("createTransaction", 9);

        private String stringValue;
        private int intValue;

        private HELPERS(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public enum IMAGE {

        IMAGE ("image", 0),
        AVATAR ("avatar", 1),
        IMAGEURL ("imageUrl", 2),
        ABSTRACT ("abstract", 3),
        ANIMALS ("animals", 4),
        BUSINESS ("business", 5),
        CATS ("cats", 6),
        CITY ("city", 7),
        FOOD ("food", 8),
        NIGHTLIFE ("nightlife", 9),
        FASHION ("fashion", 10),
        PEOPLE ("people", 11),
        NATURE ("nature", 12),
        SPORTS ("sports", 13),
        TECHNICS ("technics", 14),
        TRANSPORT ("transport", 15);

        private String stringValue;
        private int intValue;

        private IMAGE(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public enum INTERNET {

        AVATAR ("avatar", 0),
        EMAIL ("email", 1),
        USERNAME ("userName", 2),
        PROTOCOL ("protocol", 3),
        URL ("url", 4),
        DOMAINNAME ("domainName", 5),
        DOMAINSUFFIX ("domainSuffix", 6),
        DOMAINWORD ("domainWord", 7),
        IP ("ip", 8),
        USERAGENT ("userAgent", 9),
        COLOR ("color", 10),
        MAC ("mac", 11),
        PASSWORD ("password", 12);

        private String stringValue;
        private int intValue;

        private INTERNET(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static final String ROOT = "internet";

    }

    public enum LOREM {

        WORDS ("words", 0),
        SENTENCE ("sentence", 1),
        SENTENCES ("sentences", 2),
        PARAGRAPH ("paragraph", 3),
        PARAGRAPHS("paragraphs", 4);

        private String stringValue;
        private int intValue;

        private LOREM(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public enum PROPERTY {

        FIRSTNAME ("name.firstName", 0),
        LASTNAME ("name.lastName", 1),
        FINDNAME ("name.findName", 2),
        JOBTITLE ("name.jobTitle", 3),
        PREFIX ("name.prefix", 4),
        SUFFIX ("name.suffix", 5),
        TITLE ("name.title", 6),
        JOBDESCRIPTOR ("name.jobDescriptor", 7),
        JOBAREA ("name.jobArea", 8),
        JOBTYPE ("name.jobType", 9);

        private String stringValue;
        private int intValue;

        private PROPERTY(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static final String ROOT = "name";
    }

    public enum PHONE {

        PHONENUMBER  ("phone.phoneNumber", 0),
        PHONENUMBERFORMAT  ("phone.phoneNumberFormat", 1),
        PHONEFORMATS  ("phone.phoneFormats", 2);

        private String stringValue;
        private int intValue;

        private PHONE(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static final String ROOT = "phone";

    }

    public enum RANDOM {
        NUMBER ("number", 0),
        ARRAYELEMENT ("arrayElement", 1),
        OBJECTELEMENT ("objectElement", 2),
        UUID("uuid", 3),
        BOOLEAN("boolean", 4);

        private String stringValue;
        private int intValue;

        private RANDOM(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    private static int attrID = 0;

    private static String requestInfo(String property, String locale) throws IOException {
        InputStream input = null;
        // Only display the first 100 characters of the retrieved
        // web page content.
        int len = 100;

        try {
            URL url = new URL(FAKER_URI + property + locale);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            // Starts the query
            //connection.connect();
            int response = connection.getResponseCode();
                Log.d(TAG, "The response is: " + response);
            input = connection.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(input, len);
            connection.disconnect();
            return contentAsString;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (input != null) {
                input.close();
            }
        }
        return "";
    }

    private static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public static String generatePropertyValue(Enum<?> property, Context context){

        String retorno = null;

        try {
            retorno = requestInfo(property.toString(), LOCALE.BR.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retorno;
    }

}
