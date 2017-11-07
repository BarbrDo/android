package com.barbrdo.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.User;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.LoginManager;
import com.barbrdo.app.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ForgotPasswordActivity extends AppActivity implements ServiceRedirection {

    private Context mContext;
    private EditText editTextEmail;
    private LoginManager loginManagerObj;
    private String email;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mContext = ForgotPasswordActivity.this;
        initData();
        bindControls();
    }

    @Override
    void initData() {
        setUpToolBar("");
        editTextEmail = getView(R.id.et_email);
        loginManagerObj = new LoginManager(this, this);
    }


    @Override
    void bindControls() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.FORGOT_PASSWORD:
                editTextEmail.setText("");
                getView(R.id.input_layout_email).setVisibility(View.GONE);
                getView(R.id.btn_forgot_password).setVisibility(View.GONE);
                getView(R.id.ll_success).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    public void forgotPassword(View view) {
        if (validatingRequired()) {
            utilObj.startLoader(mContext);
            User userObj = new User();
            userObj.email = email;
            loginManagerObj.forgotPassword(userObj);
        }

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("email", "jhon@gmail.com");
//            jsonObject.put("device_type", "iOS");
//            jsonObject.put("password", "123456");
//            jsonObject.put("device_id", "894679C3-628E-429D-AEA1-1797D8712B27");
//            jsonObject.put("device_token", "B44777563552882EC3139A0317E401B55D6FC699D0AC3D279F392927CAF9B566");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Gson gson = new Gson();
//        String json = gson.toJson(jsonObject);
////
////        String encryptData = encrypt(json);
////        Log.e("Encrypted" , encryptData);
////
////        String decryptData = decrypt(encryptData);
////        Log.e("Decrypted >>>>>>>> " , decryptData);
//
//        String password = "123456";
//        String message = "Ankush Sharma";
//        String encryptedMsg = "";
//        try {
//            encryptedMsg = AESCrypt.encrypt(password, message);
//            Log.e("Encrypted", encryptedMsg);
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            String messageAfterDecrypt = AESCrypt.decrypt(password, encryptedMsg);
//            Log.e("Decrypted >>>>>>>> ", messageAfterDecrypt);
//        } catch (GeneralSecurityException e) {
//            //handle error - could be due to incorrect password or tampered encryptedMsg
//            e.printStackTrace();
//        }
    }

    public void login(View view) {
        ForgotPasswordActivity.this.finish();
    }

    private boolean validatingRequired() {
        message = "";
        email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            message = getString(R.string.error_email_empty);
            utilObj.showError(message, editTextEmail);
        } else if (!utilObj.checkEmail(email)) {
            message = getString(R.string.error_email_invalid);
            utilObj.showError(message, editTextEmail);
        }

        if (message.equalsIgnoreCase("") || message == null) {
            return true;
        } else {
            return false;
        }
    }


    private String encrypt(String cipherText1) {
        String secret = "LSC@SD2017@ps";

        // Encrypt where jo is input, and query is output and ENCRPYTION_KEy is key\
        byte[] input = new byte[0];
        String query = null;
        try {
            input = cipherText1.getBytes();

            byte[] cipherData = Base64.decode(cipherText1.getBytes(), Base64.URL_SAFE);
            byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

            MessageDigest md5 = MessageDigest.getInstance("MD5");

            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes("UTF-8"), md5);


            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(secret.getBytes("UTF-8"));
            SecretKeySpec skc = new SecretKeySpec(keyAndIV[0], "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skc);

            byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
            int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);

            query = Base64.encodeToString(cipherText, Base64.URL_SAFE);

            System.out.println("NEw code\\n" + query);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (ShortBufferException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


        return query;
    }

    private String decrypt(String cipherText) {
        String secret = "LSC@SD2017@ps";
        MessageDigest md5 = null;
        try {

            byte[] cipherData = Base64.decode(cipherText.getBytes(), Base64.URL_SAFE);
            byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

            md5 = MessageDigest.getInstance("MD5");

            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes("UTF-8"), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance("AES/ECB/PKCS5PADDING");


            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(encrypted);
            return new String(decryptedData, "UTF-8");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {
        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            // Repeat process until sufficient data has been generated\
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)\
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds\
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }
                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays\
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

            return result;

        } catch (DigestException e) {
            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data\
            Arrays.fill(generatedData, (byte) 0);
        }
    }
}
