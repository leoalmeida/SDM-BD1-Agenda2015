package br.edu.ifspsaocarlos.sdm.agenda2015.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import br.edu.ifspsaocarlos.sdm.agenda2015.adapter.FakerInterface;


/**
 * Created by LeonardoAlmeida on 16/12/15.
 */
public class FakerProvider extends AsyncTask<String, Void, String> {

    public static final String TAG = "FakerProvider";


    //Faker
    private static final String FAKER_URL = "http://faker.hook.io/?property=";

    private static Uri FAKER_URI = Uri.parse(FAKER_URL);
    private static String DEF_LOCALE = LOCALE.BR.toString();
    private static String DEF_PROPERTY = PROPERTY.FINDNAME.toString();

    private Context appContext = null;
    private Map<String,String> atributos = null;
    private FakerInterface listener;

    @Override
    protected String doInBackground(String... params) {
        // Only display the first 100 characters of the retrieved
        // web page content.
        int len = 100;



        for (String param:params) {

            try {
                URL url = new URL(FAKER_URI + param + DEF_LOCALE);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000 /* milliseconds */);
                connection.setConnectTimeout(15000 /* milliseconds */);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                // Starts the query
                //connection.connect();
                                                                                                                                                                                                                                                                                                                        int response = connection.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                InputStream input = connection.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(input, len);

                connection.disconnect();
                input.close();

                atributos.put(param, contentAsString);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    protected void onPostExecute(String property) {
        listener.fakeContactResponse(this.atributos);
    }

    private static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8), len);
        String buffer = reader.readLine().replace("\"","");
        return buffer;
    }

    public FakerProvider(FakerInterface listener, Context context){
        this.listener = listener;
        this.appContext = context;
        this.atributos = new HashMap<String,String>();
    }

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

    public enum PROPERTY {

        ZIPCODE("address.zipCode", 0),
        CITY("address.city", 1),
        CITYPREFIX("address.cityPrefix", 2),
        CITYSUFFIX("address.citySuffix", 3),
        STREETNAME("address.streetName", 4),
        STREETADDRESS("address.streetAddress", 5),
        STREETSUFFIX("address.streetSuffix", 6),
        STREETPREFIX("address.streetPrefix", 7),
        SECONDARYADDRESS("address.secondaryAddress", 8),
        COUNTY("address.county", 9),
        COUNTRY("address.country", 10),
        COUNTRYCODE("address.countryCode", 11),
        STATE("address.state", 12),
        STATEABBR("address.stateAbbr", 13),
        LATITUDE("address.latitude", 14),
        LONGITUDE("address.longitude", 15),
        COMMCOLOR("commerce.color", 0),
        DEPARTMENT("commerce.department", 0),
        PRODUCTNAME("commerce.productName", 0),
        PRICE("commerce.price", 0),
        PRODUCTADJECTIVE("commerce.productAdjective", 0),
        PRODUCTMATERIAL("commerce.productMaterial", 0),
        PRODUCT("commerce.product", 0),
        SUFFIXES("company.suffixes", 0),
        COMPANYNAME("company.companyName", 1),
        COMPANYSUFFIX("company.companySuffix", 2),
        CATCHPHRASE("company.catchPhrase", 3),
        BS("company.bs", 4),
        CATCHPHRASEADJECTIVE("company.catchPhraseAdjective", 5),
        CATCHPHRASEDESCRIPTOR("company.catchPhraseDescriptor", 6),
        CATCHPHRASENOUN("company.catchPhraseNoun", 7),
        BSADJECTIVE("company.bsAdjective", 8),
        BSBUZZ("company.bsBuzz", 9),
        BSNOUN("company.bsNoun", 10),
        PAST("date.past", 0),
        FUTURE("date.future", 1),
        BETWEEN("date.between", 2),
        RECENT("date.recent", 3),
        MONTH("date.month", 4),
        WEEKDAY("date.weekday", 5),
        ACCOUNT("finance.account", 0),
        ACCOUNTNAME("finance.accountName", 1),
        MASK("finance.mask", 2),
        AMOUNT("finance.amount", 3),
        TRANSACTIONTYPE("finance.transactionType", 4),
        CURRENCYCODE("finance.currencyCode", 5),
        CURRENCYNAME("finance.currencyName", 6),
        CURRENCYSYMBOL("finance.currencySymbol", 7),
        ABBREVIATION("hacker.abbreviation", 0),
        ADJECTIVE("hacker.adjective", 1),
        NOUN("hacker.noun", 2),
        VERB("hacker.verb", 3),
        INGVERB("hacker.ingverb", 4),
        PHRASE("hacker.phrase", 5),
        RANDOMIZE("helpers.randomize", 0),
        SLUGIFY("helpers.slugify", 1),
        REPLACESYMBOLWITHNUMBER("helpers.replaceSymbolWithNumber", 2),
        REPLACESYMBOLS("helpers.replaceSymbols", 3),
        SHUFFLE("helpers.shuffle", 4),
        MUSTACHE("helpers.mustache", 5),
        CREATECARD("helpers.createCard", 6),
        CONTEXTUALCARD("helpers.contextualCard", 7),
        USERCARD("helpers.userCard", 8),
        CREATETRANSACTION("helpers.createTransaction", 9),
        IMAGE("image.image", 0),
        IMGAVATAR("image.avatar", 1),
        IMAGEURL("image.imageUrl", 2),
        ABSTRACT("image.abstract", 3),
        ANIMALS("image.animals", 4),
        BUSINESS("image.business", 5),
        CATS("image.cats", 6),
        IMGCITY("image.city", 7),
        FOOD("image.food", 8),
        NIGHTLIFE("image.nightlife", 9),
        FASHION("image.fashion", 10),
        PEOPLE("image.people", 11),
        NATURE("image.nature", 12),
        SPORTS("image.sports", 13),
        TECHNICS("image.technics", 14),
        TRANSPORT("image.transport", 15),
        AVATAR("internet.avatar", 0),
        EMAIL("internet.email", 1),
        USERNAME("internet.userName", 2),
        PROTOCOL("internet.protocol", 3),
        URL("internet.url", 4),
        DOMAINNAME("internet.domainName", 5),
        DOMAINSUFFIX("internet.domainSuffix", 6),
        DOMAINWORD("internet.domainWord", 7),
        IP("internet.ip", 8),
        USERAGENT("internet.userAgent", 9),
        COLOR("internet.color", 10),
        MAC("internet.mac", 11),
        PASSWORD("internet.password", 12),
        WORDS("lorem.words", 0),
        SENTENCE("lorem.sentence", 1),
        SENTENCES("lorem.sentences", 2),
        PARAGRAPH("lorem.paragraph", 3),
        PARAGRAPHS("lorem.paragraphs", 4),
        FIRSTNAME("name.firstName", 0),
        LASTNAME("name.lastName", 1),
        FINDNAME("name.findName", 2),
        JOBTITLE("name.jobTitle", 3),
        PREFIX("name.prefix", 4),
        SUFFIX("name.suffix", 5),
        TITLE("name.title", 6),
        JOBDESCRIPTOR("name.jobDescriptor", 7),
        JOBAREA("name.jobArea", 8),
        JOBTYPE("name.jobType", 9),
        PHONENUMBER("phone.phoneNumber",0),
        PHONENUMBERFORMAT("phone.phoneNumberFormat",1),
        PHONEFORMATS("phone.phoneFormats",2),
        NUMBER("random.number",0),
        ARRAYELEMENT("random.arrayElement",1),
        OBJECTELEMENT("random.objectElement",2),
        UUID("urandom.uid",3),
        BOOLEAN("random.boolean",4);

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

        public static final String ROOT = "property";
    }

    public enum FAKE {}

}
