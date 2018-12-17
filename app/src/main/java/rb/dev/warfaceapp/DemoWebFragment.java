package rb.dev.warfaceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


public class DemoWebFragment extends Fragment {

    private static final String BUNDLE_LINK = "bundle_link";
    private String link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null && getArguments().containsKey(BUNDLE_LINK)) {
            link = getArguments().getString(BUNDLE_LINK);
        }

        View view = inflater.inflate(R.layout.fragment_web, container, false);
        WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.setBackgroundColor(0);
        webView.loadUrl("http://belev.org/android/wardocwarface/maps/spec_vulkan.html");
        //webView.loadUrl(link);
        return view;

    }
}