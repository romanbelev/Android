package belev.org.warface_app;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StartFragment extends Fragment implements View.OnClickListener {

    public BillingClient billingClient;
    public MainActivity mainActivity;
    public SkuDetails skuDetailsVipOne;
    public SkuDetails skuDetailsVipTwo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);
        TextView textView = (TextView) view.findViewById(R.id.start_textView);
        String text = getResources().getString(R.string.start_text);
        textView.setText(Html.fromHtml(text));

        mainActivity = (MainActivity) getActivity();

        Button buttonDonateVipOne = (Button) view.findViewById(R.id.button_donate_vip_one);
        buttonDonateVipOne.setOnClickListener(this);

        Button buttonDonateVipTwo = (Button) view.findViewById(R.id.button_donate_vip_two);
        buttonDonateVipTwo.setOnClickListener(this);

        initBillingClient();

        return view;
    }

    public void initBillingClient() {

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && list != null) {
                    for (Purchase purchase : list) {
                        handlePurchase(purchase);
                    }
                }
            }
        };

        billingClient = BillingClient.newBuilder(mainActivity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    System.out.println("Ok connect billing client -------------->");

                    List<String> skuList = new ArrayList<String>();
                    skuList.add("premium_level_1");
                    skuList.add("premium_level_2");

                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

                    billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                            System.out.println(list.get(0).getPrice());
                            System.out.println(list.get(1).getPrice());
                            skuDetailsVipOne = list.get(0);
                            skuDetailsVipTwo = list.get(1);
                        }
                    });
                } else {
                    System.out.println("Error connect billing client -------------->");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                System.out.println("Disconnected billing client -------------->");
            }
        });
    }

    public void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    System.out.println("The consume response ------->");
                }
            }
        };

        billingClient.consumeAsync(consumeParams, consumeResponseListener);
    }

    /*
    public View.OnClickListener buttonClickHandler(View v) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Button click ------------------------------------>");

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();

                int responseCode = billingClient.launchBillingFlow(mainActivity, billingFlowParams).getResponseCode();
            }
        };

        return onClickListener;
    }
    */

    @Override
    public void onClick(View view) {
        int responseCode;

        BillingFlowParams billingFlowParams;
        BillingFlowParams.Builder billingFlowParamsBuilder = BillingFlowParams.newBuilder();

        switch (view.getId()) {
            case R.id.button_donate_vip_one:
                billingFlowParams = billingFlowParamsBuilder.setSkuDetails(skuDetailsVipOne).build();
                break;
            case R.id.button_donate_vip_two:
                billingFlowParams = billingFlowParamsBuilder.setSkuDetails(skuDetailsVipTwo).build();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

        responseCode = billingClient.launchBillingFlow(mainActivity, billingFlowParams).getResponseCode();
    }
}
