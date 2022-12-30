package com.project.letsnote.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.project.letsnote.MapsActivity;
import com.project.letsnote.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends Activity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private TextView btnLogin;
    private ProgressDialog progressDialog;
    public static User user;
    public boolean userFound = false;
    Firebase ref;
    public static String FIREBASEKEY;
    TextView textoLogo;
    public static String FOTOUSER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Le decimos en el ambito que estara Firebase (Lo inicializamos en la activity)
        Firebase.setAndroidContext(this);

        //Le decimos en el ambito que estara el FacebookSDK (Lo inicializamos en la activity)
        FacebookSdk.sdkInitialize(getApplicationContext());

        //Metodo para que nos de el debug HashKey de Facebook
        getFacebookDebugKeyHash();

        setContentView(R.layout.activity_login);
        textoLogo = (TextView)findViewById(R.id.textoLogo);
        Typeface trit = Typeface.createFromAsset(getAssets(),"fonts/quirlycues.regular.ttf");
        textoLogo.setTypeface(trit);



        if (PrefUtils.getCurrentUser(Login.this) != null) {
            User user = PrefUtils.getCurrentUser(this);
            getKey(user);

            Intent homeIntent = new Intent(Login.this, MapsActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions("public_profile", "email", "user_friends");

        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Para mostrar el ProgressDialog
                progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Cargando...");
                progressDialog.show();

                //Sets del LoginButton
                loginButton.performClick();
                loginButton.setPressed(true);
                loginButton.invalidate();
                loginButton.registerCallback(callbackManager, mCallBack);
                loginButton.setPressed(false);
                loginButton.invalidate();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        //Si to-do ha ido bien
        @Override
        public void onSuccess(LoginResult loginResult) {

            //Ocultamos el ProgressDialog
            progressDialog.dismiss();

            //Creamos la referencia donde guardará a los usuarios
            ref = new Firebase("https://letsnote.firebaseio.com//users");

            // App code
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(final JSONObject object, GraphResponse response) {

                    Log.e("Respuesta: ", response + "");
                    try {
                        try {
                            user = new User();
                          /*
                          Entra por parametro un object que es el JSON con la informacion de facebook del usuario
                          toda esa informacion la metemos en un Bundle para luego guardarla en Firebase
                          */
                            Bundle bFacebookData = getFacebookData(object);
                            user.setFacebookID(bFacebookData.getString("idFacebook"));
                            user.setEmail(bFacebookData.getString("email"));
                            user.setName(bFacebookData.getString("name"));
                            user.setGender(bFacebookData.getString("gender"));
                            user.setPictureUrl(bFacebookData.getString("profile_pic"));
                            user.setLocation(bFacebookData.getString("location"));
                            user.setDescripcion("Sin descripción");

                            //Recorremos los users defirebase para ver si ya exixte
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                                       /*Creamos un usuario para que coja las propiedas de cada uno y
                                        y compararemos la id del usuario que se esta logueando con las ya guardadas.*/
                                        User userSnapshot = postSnapshot.getValue(User.class);

                                        if (userSnapshot.getFacebookID().equals(user.getFacebookID())) {
                                            //Si existe ya el usuario en Firebase, lo ponemos a true y salimos
                                            userFound = true;
                                            FIREBASEKEY = postSnapshot.getKey();
                                            FOTOUSER = postSnapshot.getValue(User.class).getPictureUrl();
                                            Log.e("KEY ----------> ",FIREBASEKEY);
                                            break;
                                        }
                                    }
                                    //Si es falso es porque no lo ha encontrado y lo guardamos en Firebase
                                    if (!userFound) {
                                        addInfoFirebase(user);
                                        getKey(user);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }

                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        PrefUtils.setCurrentUser(user, Login.this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(Login.this, "Bienvenido " + user.name, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Login.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                }

            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, first_name, last_name, email,gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            progressDialog.dismiss();
        }

        @Override
        public void onError(FacebookException e) {
            progressDialog.dismiss();
        }
    };


    /**
     * Nos crea un objecto tipo Bundle, que guardará los datos del parámetro que le pasamos, que es un JSONObject
     * con las 'key' que le asignemos.
     * La recuperación se haría de la siguiente manera:
     * <p/>
     * Bundle nombreDelBundle = getFacebookData(object);
     * nombreDelBundle.getString("idFacebook").toString(); -> tiene key 'idFacebook'.
     *
     * @param object
     * @return
     * @throws JSONException
     */
    private Bundle getFacebookData(JSONObject object) throws JSONException {

        Bundle bundle = new Bundle();
        String id = object.getString("id");

        try {
            URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
            Log.i("profile_pic", profile_pic + "");
            bundle.putString("profile_pic", profile_pic.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        bundle.putString("idFacebook", id);
        if (object.has("name"))
            bundle.putString("name", object.getString("name"));
        if (object.has("first_name"))
            bundle.putString("first_name", object.getString("first_name"));
        if (object.has("last_name"))
            bundle.putString("last_name", object.getString("last_name"));
        if (object.has("email"))
            bundle.putString("email", object.getString("email"));
        if (object.has("gender"))
            bundle.putString("gender", object.getString("gender"));
        if (object.has("birthday"))
            bundle.putString("birthday", object.getString("birthday"));
        if (object.has("location"))
            bundle.putString("location", object.getJSONObject("location").getString("name"));

        return bundle;
    }

    /**
     * Método para convertir a bitmap la imagen referenciada por a URL.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static Bitmap getFacebookProfilePicture(String url) throws IOException {
        URL facebookProfileURL = new URL(url);
        Bitmap bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
        return bitmap;
    }

    /**
     * Este es el método para guardar los datos del usuario que se loguea en Firebase.
     * Se le pasa el parametro contador, que hemos declarado al principio de la clase y que vale cero de inicio,
     * pero dependiendo de comprobaciones anteriores puede seguir siendo cero, y no se gestionará ninguna acción de guardado,
     * o diferente a cero, en este último caso sí guardaremos los datos del usuario 'user', que es el otro
     * parámetro que se le pasa.
     *
     * @param user
     */
    public static void addInfoFirebase(User user) {
        final Firebase refUsers = new Firebase("https://letsnote.firebaseio.com/").child("users").push();
        refUsers.setValue(user);
    }

    /*
    Metodo nos devuelve el debug keyhash en un Log. No es necesario para la aplicación, pero si para el desarrollo
    y poder utilizar el login de facebook.
    */
    public void getFacebookDebugKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.letsnote.logins",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void getKey(final User user) {

        ref = new Firebase("https://letsnote.firebaseio.com//users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            User userFinal = user;

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (userFinal.getFacebookID().equals(postSnapshot.getValue(User.class).getFacebookID())) {
                        FIREBASEKEY = postSnapshot.getKey();
                        FOTOUSER = postSnapshot.getValue(User.class).getPictureUrl();
                        Log.e("KEY ----------> ",FIREBASEKEY);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });
    }
}