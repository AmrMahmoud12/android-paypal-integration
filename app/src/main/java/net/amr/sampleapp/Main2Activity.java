package net.amr.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button b=findViewById(R.id.click);


        //______________we need to start PayPalService______________________________________________
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paypalPayment();
            }
        });
    }
    //__________________production ready to publish__________________________________________________
    //__________________Now to get Payment from PayPal we need a PayPal Configuration Object and a Request Code.__________________
    private int PAYPAL_REQUEST_CODE = 1;

    //_____________________Paypal Configuration Object____________________________________
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(paypalConfig.PAYPAL_CLIENT_ID);

    private void paypalPayment() {

        //__________________Creating a paypalpayment__________________
        PayPalPayment payment = new PayPalPayment(new BigDecimal(10.00), "USD", "pay",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //__________________crate paypal payment activity intent__________________
        Intent i = new Intent(this, PaymentActivity.class);


        //__________________putting the paypal configuration to the intent____________________________________
        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //__________________Puting paypal payment to the intent__________________
        i.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //__________________Starting the intent activity for result__________________
        //__________________the request code will be used on the method onActivityResult__________________
        startActivityForResult(i, PAYPAL_REQUEST_CODE);

    }

    //__________________The above method will invoke the onActivityResult() method after completion. So override onActivityResult() and write the following code.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //__________________If the result is from paypal__________________
        if (requestCode == PAYPAL_REQUEST_CODE) {
            //__________________If the result is OK i.e. user has not canceled the payment__________________
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                //__________________if confirmation is not null__________________
                if (confirm != null) {
                    try {
   //------------------------SERVERSIDE CODES-----------------------------
//____________________________________________________________________________________________________________
                        {
                            JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());

                            String paymentResponse = jsonObj.getJSONObject("response").getString("state");
//                            paymentId = confirm.toJSONObject().getJSONObject("response").getString("id");
                            // verifyPaymentOnServer(payment_id,confirm);
                            //displayResultText("PaymentConfirmation info received from PayPal");

                            if (paymentResponse.equals("approved")) {

//                                Toast.makeText(getContext(),"Payment successful",Toast.LENGTH_LONG).show();
//                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                                assert currentUser != null;
//                                String uid = currentUser.getUid();
//                                HashMap<String,Boolean> m=new HashMap<>();
//                                m.put("customerPaid",true);
//                                firestore.collection("Users")
//                                        .document(uid)
//                                        .set(m, SetOptions.merge());

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "payment unsuccessful", Toast.LENGTH_LONG).show();

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            }
        }
    }
    @Override
    public void onDestroy() {
        //_____________STOP SERVEICE_________________
       stopService(new Intent(this, PayPalService.class));
        super.onDestroy();

    }
}
