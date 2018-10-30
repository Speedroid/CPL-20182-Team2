//
//  ViewController.swift
//  com.ac.knu.helloworld
//
//  Created by CSP MOBILE LAB on 2018. 9. 11..
//  Copyright © 2018년 Test. All rights reserved.
//
// Navigation Design & UI design
/*
 유틸리티 기능은 SnailUtility.swift 파일 확인
 */

import UIKit
import WebKit
import Swift
import Foundation


class ViewController: UIViewController, WKUIDelegate, WKNavigationDelegate, UIApplicationDelegate, UIScrollViewDelegate {
    
    @IBOutlet weak var webView: WKWebView!
    @IBOutlet var containerView: UIView? = nil
    //var webView: WKWebView?
    @IBOutlet weak var backButton: UIBarButtonItem!
    @IBOutlet weak var forwardButton: UIBarButtonItem!
   // var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView()
    let firstLaunch = FinalLaunch()
    let didUrlLoaded = false
    @IBOutlet weak var contentsView: UIScrollView!
    //for debugging
    
    override func loadView()
    {
        //let webConfiguration = WKWebViewConfiguration() 왜 에러가 나는가?
        super.loadView()
        //javaScript 허용
        let preferences = WKPreferences()
        preferences.javaScriptEnabled = true
        let configuration = WKWebViewConfiguration()
        
        configuration.preferences = preferences
        
        self.webView = WKWebView(frame: CGRect(x:0, y:0,width:self.view.frame.width, height: self.view.frame.height + 100), configuration: configuration)// WKWebView 로드
        
        if firstLaunch.isFirstLaunch{
            print("It's first launched")
        }
        self.view = self.webView
        if(!isNetworkConnected())
        {
            performSegue(withIdentifier: "sendToNetworkError", sender: Any?.self)
            
        }
        /*codes from https://kinderas.com/technology/2014/6/7/getting-started-with-wkwebview-using-swift-in-ios-8 */
    }
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!){
        backButton.isEnabled = webView.canGoBack
        forwardButton.isEnabled = webView.canGoForward
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!){
        backButton.isEnabled = webView.canGoBack
        forwardButton.isEnabled = webView.canGoForward
        //self.activityIndicator.removeFromSuperview()
    }
    
    @IBAction func backButtonAction(_ sender: Any) {
        self.webView.goBack()
        self.webView.reload() // 필수! 없으면 안됨!
    }
    @IBAction func forwardButtonAction(_ sender: Any) {
        self.webView.goForward()
        self.webView.reload() // 필수! 없으면 안됨!
    }
    
    override func viewDidLoad() {
        /*
         뷰가 처음 로드 되었을 때 수행하는 동작들.
         사용자 에이전트, 웹 뷰의 주소, 상태 바 설정을 하는 곳이다.
         */
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        webView.scrollView.delegate = self
        
        webView.allowsBackForwardNavigationGestures = false
        let myURL = URL(string:snailPostHome)
        let myRequest = URLRequest(url: myURL!)
        self.webView!.load(myRequest)
        
        let nightColor = UIColor(red: 0.50, green: 0.494, blue: 0.662, alpha: 1) // R: 128, G: 125 B: 168
        guard let statusBar = UIApplication.shared.value(forKeyPath: "statusBarWindow.statusBar") as? UIView else { return }
        statusBar.backgroundColor = nightColor
        
        self.webView.customUserAgent = WK_snail_UserAgent
        self.webView.navigationDelegate = self
        self.webView.scrollView.isScrollEnabled = false
        self.webView.isUserInteractionEnabled = true
        self.webView.translatesAutoresizingMaskIntoConstraints = false
        
    }
    /*override var prefersStatusBarHidden: Bool{
        return true
    }//status bar 숨기기*/
    // hiding Navigation Bar
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.setNavigationBarHidden(false, animated: animated)
    }
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.navigationController?.setNavigationBarHidden(true, animated: animated)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    @available(iOS 8.0, *)
    public func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Swift.Void) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        let otherAction = UIAlertAction(title: "OK", style: .default, handler: {action in completionHandler()})
        alert.addAction(otherAction)
        
        self.present(alert, animated: true, completion: nil)
    }

    public func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Swift.Void) {
        let alert = UIAlertController(title: "", message: message, preferredStyle: .alert)
        let cancelAction = UIAlertAction(title: "CANCEL", style: .default, handler: {(action) in completionHandler(false)})
        let okAction = UIAlertAction(title: "OK", style: .default, handler: {(action) in completionHandler(true)})
        alert.addAction(cancelAction)
        alert.addAction(okAction)
        
        self.present(alert, animated: true, completion: nil)
    }
    
    /*@available(iOS 8.0, *)
    public func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!){
        activityIndicator = UIActivityIndicatorView(activityIndicatorStyle: .whiteLarge)
        activityIndicator.frame = CGRect(x: view.frame.midX-50, y: view.frame.midY-50, width: 100, height: 100)
        activityIndicator.color = UIColor.red
        activityIndicator.hidesWhenStopped = true
        activityIndicator.startAnimating() // 앱 로딩시 표시되는 애니메이션 호출
        self.view.addSubview(activityIndicator)
}*/

}

