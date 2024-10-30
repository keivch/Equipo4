package com.example.project1



import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view)

        val webView: WebView = findViewById(R.id.webview)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        // Obtén la URL de la intención
        val url = intent.getStringExtra("URL")
        if (url != null) {
            webView.loadUrl(url)
        }
    }
}
