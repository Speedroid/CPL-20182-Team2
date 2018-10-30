//
//  SnailUitlity.swift
//  com.ac.knu.helloworld
//
//  Created by CSP MOBILE LAB on 2018. 10. 17..
//  Copyright © 2018년 Test. All rights reserved.
//

import Foundation
import SystemConfiguration
import Swift
import UIKit
/* classes */
final class FinalLaunch{
    let userDefaults: UserDefaults = .standard
    let wasLaunchedBefore: Bool
    var isFirstLaunch: Bool{
        return !wasLaunchedBefore
    }
    init()
    {
        let key = "com.ac.knu.helloworld.FinalLaunch.WasLaunchedBefore"
        let wasLaunchedBefore = userDefaults.bool(forKey: key)
        self.wasLaunchedBefore = wasLaunchedBefore
        if !wasLaunchedBefore{
            userDefaults.set(true, forKey: key)
        }
        
    }
}
/* functions */
func isNetworkConnected() -> Bool{
    /*
     네트워크 연결 상태를 확인하는 함수
     */
    var zeroAddress = sockaddr_in()
    zeroAddress.sin_len = UInt8(MemoryLayout<sockaddr_in>.size)
    zeroAddress.sin_family = sa_family_t(AF_INET)
    
    guard let defaultRouteReachability = withUnsafePointer(to: &zeroAddress, {
        $0.withMemoryRebound(to: sockaddr.self, capacity: 1){
            SCNetworkReachabilityCreateWithAddress(nil, $0)
        }
    })else
    {
        return false
    }
    var flags: SCNetworkReachabilityFlags = []
    
    if !SCNetworkReachabilityGetFlags(defaultRouteReachability, &flags) {
        return false
    }
    let isReachable = flags.contains(.reachable)
    let needsConnection = flags.contains(.connectionRequired)
    return (isReachable && !needsConnection)
}
