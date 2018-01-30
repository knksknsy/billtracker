//
//  Bill.swift
//  Billtracker
//
//  Created by Kaan K. on 29.01.18.
//  Copyright © 2018 Valdet D.. All rights reserved.
//

import Foundation

class Bill {
    
    var id: String?
    
    var imageId: String?
    
    var downloadUrl: String?
    
    var category: String
    
    var date: UInt
    
    var sum: Double
    
    init(category: String, date: UInt, sum: Double) {
        self.category = category
        self.date = date
        self.sum = sum
    }
    
    init(category: String, date: UInt, sum: Double, downloadUrl: String, id: String, imageId: String) {
        self.category = category
        self.date = date
        self.sum = sum
        self.downloadUrl = downloadUrl
        self.id = id
        self.imageId = imageId
    }
    
    
    func printDate() -> String {
        let d = Date(timeIntervalSinceReferenceDate: TimeInterval(date))
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("dd.MM.yyyy")
        return formatter.string(from: d)
    }
    
    
}

